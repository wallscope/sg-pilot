package com.sg.app.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.sg.app.rdf.*
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.Model
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

data class BPDoc(
    @JsonProperty("uri") override var uri: URI,
    var filename: String,
    var dG: List<String>,
    var directorate: List<String>,
    var director: List<String>,
    var keyContact: List<String>,
    var contactEmail: List<String>,
    var divisions: List<String>,
    var divisionLeads: List<String>,
    var resProgramme: String,
    var resTotalOperatingCosts: String,
    var resCorporateRunningCosts: String,
    var resTotal: String,
    var resCapital: String,
    var resFinancialTransactions: String,
    var keywords: List<String>,
) : RDFWritable {
    fun withURI(uri: URI): BPDoc = BPDoc(uri, this.filename, this.dG, this.directorate, this.director, this.keyContact, this.contactEmail, this.divisions, this.divisionLeads, this.resProgramme, this.resTotalOperatingCosts, this.resCorporateRunningCosts, this.resTotal, this.resCapital, this.resFinancialTransactions, this.keywords)

    override fun toRDF() = jenaModel {
        add(uri.res, RDF.type.prop, RDF.SG.BPDoc.type.res)

        add(uri.res, RDF.DCAT.Resource.prop, filename.toRDFLiteral())
        dG.forEach { add(uri, RDF.ORG.Organization, it.toRDFLiteral()) }
        directorate.forEach { add(uri, RDF.ORG.OrganizationalUnit, it.toRDFLiteral()) }
        director.forEach { add(uri, RDF.ORG.headOf, it.toRDFLiteral()) }
        keyContact.forEach { add(uri, RDF.SCHEMA.contactPoint, it.toRDFLiteral()) }
        contactEmail.forEach { add(uri, RDF.SCHEMA.email, it.toRDFLiteral()) }
        divisions.forEach { add(uri, RDF.ORG.hasSubOrganization, it.toRDFLiteral()) }
        divisionLeads.forEach { add(uri, RDF.ORG.hasMember, it.toRDFLiteral()) }
        add(uri.res, RDF.FRAPO.FundingProgramme.prop, resProgramme.toRDFLiteral())
        add(uri.res, RDF.FRAPO.Expenditure.prop, resTotalOperatingCosts.toRDFLiteral())
        add(uri.res, RDF.FRAPO.ProjectBudget.prop, resCorporateRunningCosts.toRDFLiteral())
        add(uri.res, RDF.FRAPO.BudgetedAmount.prop, resTotal.toRDFLiteral())
        add(uri.res, RDF.FRAPO.Funding.prop, resCapital.toRDFLiteral())
        add(uri.res, RDF.FRAPO.ExpenditureToDate.prop, resFinancialTransactions.toRDFLiteral())
        keywords.forEach { add(uri, RDF.SG.keywords, it.toRDFLiteral()) }
    }

    companion object : RDFDeserializer<BPDoc> {
        private val log = LoggerFactory.getLogger(BPDoc::class.java)
        override val RDFType = RDF.SG.BPDoc.type

        override fun fromModel(uri: URI, model: Model): BPDoc {
            val filename = model.getOneObjectOrFail(uri.res, RDF.DCAT.Resource.prop).asLiteral().string

            val dG = model.getAllObjectsOrFail(uri, RDF.ORG.Organization).map { it.asLiteral().string }
            val directorate = model.getAllObjectsOrFail(uri, RDF.ORG.OrganizationalUnit).map { it.asLiteral().string }
            val director = model.getAllObjectsOrFail(uri, RDF.ORG.headOf).map { it.asLiteral().string }
            val keyContact = model.getAllObjectsOrFail(uri, RDF.SCHEMA.contactPoint).map { it.asLiteral().string }
            val contactEmail = model.getAllObjectsOrFail(uri, RDF.SCHEMA.email).map { it.asLiteral().string }
            val divisions = model.getAllObjectsOrFail(uri, RDF.ORG.hasSubOrganization).map { it.asLiteral().string }
            val divisionLeads = model.getAllObjectsOrFail(uri, RDF.ORG.hasMember).map { it.asLiteral().string }

            val resProgramme = model.getOneObjectOrFail(uri.res, RDF.FRAPO.FundingProgramme.prop).asLiteral().string
            val resTotalOperatingCosts = model.getOneObjectOrFail(uri.res, RDF.FRAPO.Expenditure.prop).asLiteral().string
            val resCorporateRunningCosts = model.getOneObjectOrFail(uri.res, RDF.FRAPO.ProjectBudget.prop).asLiteral().string
            val resTotal = model.getOneObjectOrFail(uri.res, RDF.FRAPO.BudgetedAmount.prop).asLiteral().string
            val resCapital = model.getOneObjectOrFail(uri.res, RDF.FRAPO.Funding.prop).asLiteral().string
            val resFinancialTransactions = model.getOneObjectOrFail(uri.res, RDF.FRAPO.ExpenditureToDate.prop).asLiteral().string

            val keywords = model.getAllObjectsOrFail(uri, RDF.SG.keywords).map { it.asLiteral().string }

            return BPDoc(uri, filename, dG, directorate, director, keyContact, contactEmail, divisions, divisionLeads, resProgramme, resTotalOperatingCosts, resCorporateRunningCosts, resTotal, resCapital, resFinancialTransactions, keywords)
        }

        // To detailed graph JSON
        fun toDetailedGraphJSON(bpDoc: BPDoc): Node {
            val divisionLeads = bpDoc.divisionLeads.sorted().groupBy { it.substringBefore('|') }
            val divisionsWithLeads = bpDoc.divisions.sorted().map { division ->
                Node(
                    name = division.substringAfter('|'),
                    children = divisionLeads[division.substringBefore('|')]?.map { it.substringAfter('|') }?.let { listToNodes(it) }
                )
            }

            return Node(
                name = bpDoc.uri.value,
                children = listOf(
                    Node(
                        name = bpDoc.filename,
                        children = listOf(
                            Node(name = "dG", children = listToNodes(bpDoc.dG)),
                            Node(name = "directorate", children = listToNodes(bpDoc.directorate)),
                            Node(name = "director", children = listToNodes(bpDoc.director)),
                            Node(name = "keyContact", children = listToNodes(bpDoc.keyContact)),
                            Node(name = "contactEmail", children = listToNodes(bpDoc.contactEmail)),
                            Node(name = "divisions", children = divisionsWithLeads),
                            Node(name = "resProgramme", children = listToNodes(listOf(bpDoc.resProgramme))),
                            Node(name = "resTotalOperatingCosts", children = listToNodes(listOf(bpDoc.resTotalOperatingCosts))),
                            Node(name = "resCorporateRunningCosts", children = listToNodes(listOf(bpDoc.resCorporateRunningCosts))),
                            Node(name = "resTotal", children = listToNodes(listOf(bpDoc.resTotal))),
                            Node(name = "resCapital", children = listToNodes(listOf(bpDoc.resCapital))),
                            Node(name = "resFinancialTransactions", children = listToNodes(listOf(bpDoc.resFinancialTransactions))),
                            Node(name = "keywords", children = listToNodes(bpDoc.keywords))
                        )
                    )
                )
            )
        }
    }
}

