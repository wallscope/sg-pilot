package com.sg.app.dao

import com.sg.app.model.PFGAux
import com.sg.app.rdf.*
import org.slf4j.LoggerFactory
import com.sg.app.rdf.RDF.DCAT.Catalog as Doc

object PFGAuxDAO {
    private val log = LoggerFactory.getLogger(javaClass)
    private val PFGAUX_SUB_QUERY = """
        ?pfgaux a <${Doc.type}> ;
          <${RDF.DCAT.identifier}> ?id ;
          <${RDF.DCTERMS.Period}> ?pfgauxPeriod ;
        .

        OPTIONAL { ?pfgaux <${RDF.DCAT.accessURL}> ?pfgauxAccessURL . }#END OPTIONAL
        OPTIONAL { ?pfgaux <${RDF.DBPEDIA.portfolio}> ?pfgauxMinisterialPortfolio . }#END OPTIONAL
        OPTIONAL { ?pfgaux <${RDF.ORG.OrganizationalUnit}> ?pfgauxDirectorate . }#END OPTIONAL
        OPTIONAL { ?pfgaux <${RDF.ORG.Organization}> ?pfgauxDG . }#END OPTIONAL
        OPTIONAL { ?pfgaux <${RDF.ORG.headOf}> ?pfgauxLeadOfficial . }#END OPTIONAL
        OPTIONAL { ?pfgaux <${RDF.SKOS.subject}> ?pfgauxPrimaryOutcomes . }#END OPTIONAL
        OPTIONAL { ?pfgaux <${RDF.SKOS.related}> ?pfgauxSecondaryOutcomes . }#END OPTIONAL
        OPTIONAL { ?pfgaux <${RDF.DCTERMS.title}> ?pfgauxPolicyTitle . }#END OPTIONAL
                
        # Expand PFGAux related keywords
        OPTIONAL { ?pfgaux <${RDF.SG.keywords}> ?pfgauxKeywords . }#END OPTIONAL
    """.trimIndent()

    private val PFGAUX_QUERY = PREFIXES + """
        CONSTRUCT {
          ${PFGAUX_SUB_QUERY.trimOptionals()}
        } WHERE {
          $PFGAUX_SUB_QUERY
        }
    """.trimIndent()

    fun getAll(): List<PFGAux> {
        log.debug("getting all PFGAuxs")
        val query = pss {
            commandText = PFGAUX_QUERY
        }
        log.debug("Get all pfgauxs query: $query")
        val model = TriplestoreUtil.sendConstruct(query)
        return model?.let { PFGAux.buildAllFromModel(model) }?.also { log.debug("PFGAux objects built") }
            ?: emptyList()
    }

    fun getOne(uri: URI): PFGAux? {
        log.debug("getting PFGAux $uri")
        val query = pss {
            commandText = PFGAUX_QUERY
            setParam("pfgaux", uri.res)
        }
        log.debug("Get one pfgaux query: $query")
        val model = TriplestoreUtil.sendConstruct(query)
        return model?.let { PFGAux.buildOneFromModel(it) }
    }
}
