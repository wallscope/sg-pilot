package com.sg.app.dao

import com.sg.app.model.BPCom
import com.sg.app.rdf.*
import org.slf4j.LoggerFactory
import com.sg.app.rdf.RDF.SG.BPCom as Doc

object BPComDAO {
    private val log = LoggerFactory.getLogger(javaClass)
    val BPCOM_SUB_QUERY = """
        ?bpdocCom a <${Doc.type}> ;
          <${RDF.DCAT.resource}> ?bpdocComFilename ;
        .

        OPTIONAL { ?bpdocCom <${RDF.FRAPO.ProjectBudget}> ?bpdocProjectBudget . }#END OPTIONAL
        OPTIONAL { ?bpdocCom <${RDF.SG.BudgetSufficient}> ?bpdocBudgetSufficient . }#END OPTIONAL
        OPTIONAL { ?bpdocCom <${RDF.ITSMO.Project}> ?bpdocComCommitment . }#END OPTIONAL
        OPTIONAL { ?bpdocCom <${RDF.ITSMO.Priority}> ?bpdocComPriority . }#END OPTIONAL
        OPTIONAL { ?bpdocCom <${RDF.ITSMO.hasProjectOwner}> ?bpdocComCommitmentLead . }#END OPTIONAL
        OPTIONAL { ?bpdocCom <${RDF.SG.DeliveryPartner}> ?bpdocComDeliveryPartner . }#END OPTIONAL
        OPTIONAL { ?bpdocCom <${RDF.SKOS.subject}> ?bpdocComPrimaryOutcomes . }#END OPTIONAL
        OPTIONAL { ?bpdocCom <${RDF.SKOS.related}> ?bpdocComSecondaryOutcomes . }#END OPTIONAL
                
        # Expand BPCom related keywords
        OPTIONAL { ?bpdocCom <${RDF.DCAT.keyword}> ?bpdocComKeyword . }#END OPTIONAL
    """.trimIndent()

    private val BPCOM_QUERY = PREFIXES + """
        CONSTRUCT {
          ${BPCOM_SUB_QUERY.trimOptionals()}
        } WHERE {
          $BPCOM_SUB_QUERY
        }
    """.trimIndent()

    fun getAll(): List<BPCom> {
        log.debug("getting all BPComs")
        val query = pss {
            commandText = BPCOM_QUERY
        }
        log.debug("Get all bpdocComs query: $query")
        val model = TriplestoreUtil.sendConstruct(query)
        return model?.let { BPCom.buildAllFromModel(model) }?.also { log.debug("BPCom objects built") }
            ?: emptyList()
    }

    fun getOne(uri: URI): BPCom? {
        log.debug("getting BPCom $uri")
        val query = pss {
            commandText = BPCOM_QUERY
            setParam("bpdocCom", uri.res)
        }
        log.debug("Get one bpcom query: $query")
        val model = TriplestoreUtil.sendConstruct(query)
        return model?.let { BPCom.buildOneFromModel(it) }
    }
}
