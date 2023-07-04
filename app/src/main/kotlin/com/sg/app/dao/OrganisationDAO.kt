package com.sg.app.dao

import com.sg.app.model.Organisation
import com.sg.app.rdf.*
import org.slf4j.LoggerFactory
import com.sg.app.rdf.RDF.SG.Organisation as Org

object OrganisationDAO {
    private val log = LoggerFactory.getLogger(javaClass)
    private val ORGANISATION_SUB_QUERY = """
        ?org a <${Org.type}> ;
          <${RDF.RDFS.label}> ?name ;
          <${Org.orgType}> ?orgType ;
          <${RDF.VCARD.locality}> ?locality ;
          <${RDF.VCARD.postalCode}> ?postalCode ;
          <${RDF.VCARD.streetAddress}> ?streetAddress ;
          <${RDF.FOAF.homepage}> ?homepage ;
        .

        OPTIONAL { ?org <${RDF.SCHEMA.description}> ?orgDescription . }#END OPTIONAL
        OPTIONAL { ?org <${RDF.GEOPOS.lat}> ?orgLat . }#END OPTIONAL
        OPTIONAL { ?org <${RDF.GEOPOS.long}> ?orgLong . }#END OPTIONAL
                
        # Expand Organisation related labels
        OPTIONAL { ?org <${RDF.SKOS.related}> ?orgLabels . }#END OPTIONAL
    """.trimIndent()

    // IMPORTANT - leaving this as part of the ORGANISATION_SUB_QUERY means it's passed to delete queries
    // - causing the workgroup label to be removed from the triplestore
    private val ORGANISATION_SUB_QUERY_ORG_LABEL = """
        # Expand Organisation workgroups
        OPTIONAL { ?org <${RDF.SG.Workgroup.type}> ?orgWorkgroups .
          ?orgWorkGroups <${RDF.RDFS.label}> ?orgWorkGroupsLabel .
        }#END OPTIONAL
    """.trimIndent()

    // Required to delete organisations in full
    private val ORGANISATION_SUB_QUERY_DELETE_WORKGROUPS = """
        OPTIONAL { ?org <${RDF.SG.Workgroup.type}> ?orgWorkgroups . }#END OPTIONAL
    """.trimIndent()

    // In order to not slow the query down with fetching all the answers related to a workgroup,
    // this should be included in the WHERE Clause, but not in the CONSTRUCT
    // FILTER EXISTS here also speeded up the query, so with that, the query can't even be included in the CONSTRUCT clause
    // Without throwing up an error or removing the FILTER EXISTS
    private val ORGANISATION_SUB_QUERY_HAS_ANSWERS = """
        FILTER EXISTS { ?org <${RDF.SG.Workgroup.type}> ?wg . ?answers <${RDF.SG.Answer.workgroup}> ?wg . }
    """.trimIndent()

    private val ORGANISATION_QUERY = PREFIXES + """
        CONSTRUCT {
          ${ORGANISATION_SUB_QUERY.trimOptionals()}
          ${ORGANISATION_SUB_QUERY_ORG_LABEL.trimOptionals()}
        } WHERE {
          $ORGANISATION_SUB_QUERY
          $ORGANISATION_SUB_QUERY_ORG_LABEL
        }
    """.trimIndent()

    private val ORGANISATION_DELETE_QUERY = PREFIXES + WITH_STATEMENT + """
        DELETE {
          ${ORGANISATION_SUB_QUERY.trimOptionals()}
          ${ORGANISATION_SUB_QUERY_DELETE_WORKGROUPS.trimOptionals()}
        } WHERE {
          $ORGANISATION_SUB_QUERY
          $ORGANISATION_SUB_QUERY_DELETE_WORKGROUPS
        }
    """.trimIndent()

    private val ORGANISATION_QUERY_HAS_ANSWERS = PREFIXES + """
        CONSTRUCT {
          ${ORGANISATION_SUB_QUERY.trimOptionals()}
          ${ORGANISATION_SUB_QUERY_ORG_LABEL.trimOptionals()}
        } WHERE {
          $ORGANISATION_SUB_QUERY
          $ORGANISATION_SUB_QUERY_ORG_LABEL
          $ORGANISATION_SUB_QUERY_HAS_ANSWERS
        }
    """.trimIndent()

    fun getAll(): List<Organisation> {
        log.debug("getting all Organisations")
        val query = pss {
            commandText = ORGANISATION_QUERY
        }

        val model = TriplestoreUtil.sendConstruct(query)
        return model?.let { Organisation.buildAllFromModel(model) }?.also { log.debug("Organisation objects built") }
            ?: emptyList()
    }

    fun getHaveAnswers(): List<Organisation> {
        log.debug("getting Organisations that have questionnaire answers")
        val query = pss {
            commandText = ORGANISATION_QUERY_HAS_ANSWERS
        }

        val model = TriplestoreUtil.sendConstruct(query)
        return model?.let { Organisation.buildAllFromModel(model) }?.also { log.debug("Organisation objects built") }
            ?: emptyList()
    }

    fun getOne(uri: URI): Organisation? {
        log.debug("getting Organisation $uri")
        val query = pss {
            commandText = ORGANISATION_QUERY
            setParam("org", uri.res)
        }
        log.debug("Get one organisation query: $query")
        val model = TriplestoreUtil.sendConstruct(query)
        return model?.let { Organisation.buildOneFromModel(it) }
    }

    fun delete(uri: URI) {
        log.debug("deleting Organisation $uri")
        val query = pss {
            commandText = ORGANISATION_DELETE_QUERY
            setParam("org", uri.res)
        }
        log.debug("Delete organisation query: $query")
        TriplestoreUtil.sendUpdate(query.asUpdate())
        log.debug("Organisation deleted successfully")
    }

    fun saveOrganisation(q: Organisation) =
        TriplestoreUtil.uploadIfNotExists(q).also { log.debug("Organisation object saved") }
}
