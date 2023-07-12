package com.sg.app.dao

import com.sg.app.model.PFGDoc
import com.sg.app.rdf.*
import org.slf4j.LoggerFactory
import com.sg.app.rdf.RDF.SG.PFGDoc as Doc

object PFGDocDAO {
    private val log = LoggerFactory.getLogger(javaClass)
    private val PFGDOC_SUB_QUERY = """
        ?pfgdoc a <${Doc.type}> ;
          <${RDF.DCAT.Resource}> ?filename ;
        .

        OPTIONAL { ?pfgdoc <${RDF.DBPEDIA.portfolio}> ?pfgdocMinisterialPortfolio . }#END OPTIONAL
        OPTIONAL { ?pfgdoc <${RDF.ORG.OrganizationalUnit}> ?pfgdocDirectorate . }#END OPTIONAL
        OPTIONAL { ?pfgdoc <${RDF.ORG.Organization}> ?pfgdocDG . }#END OPTIONAL
        OPTIONAL { ?pfgdoc <${RDF.ORG.hasUnit}> ?pfgdocUnitBranch . }#END OPTIONAL
        OPTIONAL { ?pfgdoc <${RDF.ORG.headOf}> ?pfgdocLeadOfficial . }#END OPTIONAL
        OPTIONAL { ?pfgdoc <${RDF.SG.PFGDoc.scsClearance}> ?pfgdocScsClearance . }#END OPTIONAL
        OPTIONAL { ?pfgdoc <${RDF.SG.PFGDoc.fbpClearance}> ?pfgdocFbpClearance . }#END OPTIONAL
        OPTIONAL { ?pfgdoc <${RDF.SKOS.subject}> ?pfgdocPrimaryOutcomes . }#END OPTIONAL
        OPTIONAL { ?pfgdoc <${RDF.SKOS.related}> ?pfgdocSecondaryOutcomes . }#END OPTIONAL
        OPTIONAL { ?pfgdoc <${RDF.DBPEDIA.projectCoordinator}> ?pfgdocPortfolioCoordinator . }#END OPTIONAL
        OPTIONAL { ?pfgdoc <${RDF.DCTERMS.title}> ?pfgdocPolicyTitle . }#END OPTIONAL
        OPTIONAL { ?pfgdoc <${RDF.DBPEDIA.completionDate}> ?pfgdocCompletionDate . }#END OPTIONAL
                
        # Expand PFGDoc related keywords
        OPTIONAL { ?pfgdoc <${RDF.SG.keywords}> ?pfgdocKeywords . }#END OPTIONAL
    """.trimIndent()

    private val PFGDOC_QUERY = PREFIXES + """
        CONSTRUCT {
          ${PFGDOC_SUB_QUERY.trimOptionals()}
        } WHERE {
          $PFGDOC_SUB_QUERY
        }
    """.trimIndent()

    fun getAll(): List<PFGDoc> {
        log.debug("getting all PFGDocs")
        val query = pss {
            commandText = PFGDOC_QUERY
        }
        log.debug("Get all pfgdocs query: $query")
        val model = TriplestoreUtil.sendConstruct(query)
        return model?.let { PFGDoc.buildAllFromModel(model) }?.also { log.debug("PFGDoc objects built") }
            ?: emptyList()
    }

    fun getOne(uri: URI): PFGDoc? {
        log.debug("getting PFGDoc $uri")
        val query = pss {
            commandText = PFGDOC_QUERY
            setParam("pfgdoc", uri.res)
        }
        log.debug("Get one pfgdoc query: $query")
        val model = TriplestoreUtil.sendConstruct(query)
        return model?.let { PFGDoc.buildOneFromModel(it) }
    }
}
