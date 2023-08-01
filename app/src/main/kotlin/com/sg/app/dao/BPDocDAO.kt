package com.sg.app.dao

import com.sg.app.model.BPDoc
import com.sg.app.rdf.*
import org.slf4j.LoggerFactory
import com.sg.app.rdf.RDF.SG.BPDoc as Doc

object BPDocDAO {
    private val log = LoggerFactory.getLogger(javaClass)
    private val BPDOC_SUB_QUERY = """
        ?bpdoc a <${Doc.type}> ;
          <${RDF.DCAT.resource}> ?bpdocFilename ;
         # <${Doc.hasCommitment}> ?bpdocCom ;
        .

        OPTIONAL { ?bpdoc <${RDF.ORG.Organization}> ?bpdocDG . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.ORG.OrganizationalUnit}> ?bpdocDirectorate . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.ORG.headOf}> ?bpdocDirector . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.SCHEMA.contactPoint}> ?bpdocKeyContact . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.SCHEMA.email}> ?bpdocContactEmail . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.ORG.hasSubOrganization}> ?bpdocDivisions . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.ORG.hasMember}> ?bpdocDivisionLeads . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.FRAPO.FundingProgramme}> ?bpdocResProgramme . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.FRAPO.Expenditure}> ?bpdocResTotalOperatingCosts . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.FRAPO.ProjectBudget}> ?bpdocResCorporateRunningCosts . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.FRAPO.BudgetedAmount}> ?bpdocResTotal . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.FRAPO.Funding}> ?bpdocResCapital . }#END OPTIONAL
        OPTIONAL { ?bpdoc <${RDF.FRAPO.ExpenditureToDate}> ?bpdocResFinancialTransactions . }#END OPTIONAL
                
        # Expand BPDoc related keywords
        OPTIONAL { ?bpdoc <${RDF.DCAT.keyword}> ?bpdocKeyword . }#END OPTIONAL
        
    """.trimIndent()
    // last part of the query to fetch BPComs, kept outside to comment it out correctly because # didn't work in the string.
    // ${BPComDAO.BPCOM_SUB_QUERY}
    private val BPDOC_QUERY = PREFIXES + """
        CONSTRUCT {
          ${BPDOC_SUB_QUERY.trimOptionals()}
        } WHERE {
          $BPDOC_SUB_QUERY
        }
    """.trimIndent()

    fun getAll(): List<BPDoc> {
        log.debug("getting all BPDocs")
        val query = pss {
            commandText = BPDOC_QUERY
        }
        log.debug("Get all bpdocs query: $query")
        val model = TriplestoreUtil.sendConstruct(query)
        return model?.let { BPDoc.buildAllFromModel(model) }?.also { log.debug("BPDoc objects built") }
            ?: emptyList()
    }

    fun getOne(uri: URI): BPDoc? {
        log.debug("getting BPDoc $uri")
        val query = pss {
            commandText = BPDOC_QUERY
            setParam("bpdoc", uri.res)
        }
        log.debug("Get one bpdoc query: $query")
        val model = TriplestoreUtil.sendConstruct(query)
        return model?.let { BPDoc.buildOneFromModel(it) }
    }
}
