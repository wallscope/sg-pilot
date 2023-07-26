package com.sg.app.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.sg.app.rdf.*
import org.apache.jena.rdf.model.Model
import org.slf4j.LoggerFactory
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
    var resProgramme: String?,
    var resTotalOperatingCosts: String?,
    var resCorporateRunningCosts: String?,
    var resTotal: String?,
    var resCapital: String?,
    var resFinancialTransactions: String?,
    var commitments: List<BPCom>,
    var keywords: List<String>,
) : RDFWritable {
    fun withURI(uri: URI): BPDoc = BPDoc(uri, this.filename, this.dG, this.directorate, this.director, this.keyContact, this.contactEmail, this.divisions, this.divisionLeads, this.resProgramme, this.resTotalOperatingCosts, this.resCorporateRunningCosts, this.resTotal, this.resCapital, this.resFinancialTransactions, this.commitments, this.keywords)

    override fun toRDF() = jenaModel {
        add(uri.res, RDF.type.prop, RDF.SG.BPDoc.type.res)

        add(uri.res, RDF.DCAT.resource.prop, filename.toRDFLiteral())
        dG.forEach { add(uri, RDF.ORG.Organization, it.toRDFLiteral()) }
        directorate.forEach { add(uri, RDF.ORG.OrganizationalUnit, it.toRDFLiteral()) }
        director.forEach { add(uri, RDF.ORG.headOf, it.toRDFLiteral()) }
        keyContact.forEach { add(uri, RDF.SCHEMA.contactPoint, it.toRDFLiteral()) }
        contactEmail.forEach { add(uri, RDF.SCHEMA.email, it.toRDFLiteral()) }
        divisions.forEach { add(uri, RDF.ORG.hasSubOrganization, it.toRDFLiteral()) }
        divisionLeads.forEach { add(uri, RDF.ORG.hasMember, it.toRDFLiteral()) }
        add(uri.res, RDF.FRAPO.FundingProgramme.prop, resProgramme?.toRDFLiteral())
        add(uri.res, RDF.FRAPO.Expenditure.prop, resTotalOperatingCosts?.toRDFLiteral())
        add(uri.res, RDF.FRAPO.ProjectBudget.prop, resCorporateRunningCosts?.toRDFLiteral())
        add(uri.res, RDF.FRAPO.BudgetedAmount.prop, resTotal?.toRDFLiteral())
        add(uri.res, RDF.FRAPO.Funding.prop, resCapital?.toRDFLiteral())
        add(uri.res, RDF.FRAPO.ExpenditureToDate.prop, resFinancialTransactions!!.toRDFLiteral())
        commitments.forEach {
            add(uri, RDF.SG.BPDoc.hasCommitment, it.uri.res )
            add(it.toRDF())
        }
        keywords.forEach { add(uri, RDF.DCAT.keyword, it.toRDFLiteral()) }
    }

    companion object : RDFDeserializer<BPDoc> {
        private val log = LoggerFactory.getLogger(BPDoc::class.java)
        override val RDFType = RDF.SG.BPDoc.type

        override fun fromModel(uri: URI, model: Model): BPDoc {
            val filename = model.getOneObjectOrFail(uri.res, RDF.DCAT.resource.prop).asLiteral().string

            val dG = model.getAllObjectsOrFail(uri, RDF.ORG.Organization).map { it.asLiteral().string }
            val directorate = model.getAllObjectsOrFail(uri, RDF.ORG.OrganizationalUnit).map { it.asLiteral().string }
            val director = model.getAllObjectsOrFail(uri, RDF.ORG.headOf).map { it.asLiteral().string }
            val keyContact = model.getAllObjectsOrFail(uri, RDF.SCHEMA.contactPoint).map { it.asLiteral().string }
            val contactEmail = model.getAllObjectsOrFail(uri, RDF.SCHEMA.email).map { it.asLiteral().string }
            val divisions = model.getAllObjectsOrFail(uri, RDF.ORG.hasSubOrganization).map { it.asLiteral().string }
            val divisionLeads = model.getAllObjectsOrFail(uri, RDF.ORG.hasMember).map { it.asLiteral().string }

            val resProgramme = model.getOneObjectOrNull(uri.res, RDF.FRAPO.FundingProgramme.prop)?.asLiteral()?.string
            val resTotalOperatingCosts = model.getOneObjectOrNull(uri.res, RDF.FRAPO.Expenditure.prop)?.asLiteral()?.string
            val resCorporateRunningCosts = model.getOneObjectOrNull(uri.res, RDF.FRAPO.ProjectBudget.prop)?.asLiteral()?.string
            val resTotal = model.getOneObjectOrNull(uri.res, RDF.FRAPO.BudgetedAmount.prop)?.asLiteral()?.string
            val resCapital = model.getOneObjectOrNull(uri.res, RDF.FRAPO.Funding.prop)?.asLiteral()?.string
            val resFinancialTransactions = model.getOneObjectOrNull(uri.res, RDF.FRAPO.ExpenditureToDate.prop)?.asLiteral()?.string
//            val commitments = model.getAllObjectsOrFail(uri, RDF.DCAT.keyword).map { it.asLiteral().string }
            val commitmentsUris = model.getAllObjectsOrFail(uri, RDF.SG.BPDoc.hasCommitment)
                .map { it.asResource() }
            val commitments = BPCom.buildFromModel(model, commitmentsUris)

            val keywords = model.getAllObjectsOrFail(uri, RDF.DCAT.keyword).map { it.asLiteral().string }

            return BPDoc(uri, filename, dG, directorate, director, keyContact, contactEmail, divisions, divisionLeads, resProgramme, resTotalOperatingCosts, resCorporateRunningCosts, resTotal, resCapital, resFinancialTransactions, commitments, keywords)
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
                            Node(name = "commitments", children = bpComListToNodes(bpDoc.commitments)),
                            Node(name = "keywords", children = listToNodes(bpDoc.keywords))
                        )
                    )
                )
            )
        }
    }
}data class BPCom(
    @JsonProperty("uri") override var uri: URI,
    var filename: String,
    var comment: String, // rdfs:comment
    var commitment: String, // itsmo:Project
    var priority: String, // itsmo:Priority
    var commitmentLead: String, // itsmo:hasProjectOwner
    var primaryOutcomes: List<String>,
    var secondaryOutcomes: List<String>,
    var keywords: List<String>,
) : RDFWritable {
    fun withURI(uri: URI): BPCom = BPCom(uri, this.filename, this.comment, this.commitment, this.priority, this.commitmentLead, this.primaryOutcomes, this.secondaryOutcomes, this.keywords)

    override fun toRDF() = jenaModel {
        add(uri, RDF.type, RDF.SG.BPCom.type.res)

        add(uri, RDF.DCAT.resource, filename.toRDFLiteral())
        add(uri, RDF.RDFS.comment, comment.toRDFLiteral())
        add(uri, RDF.ITSMO.Project, commitment.toRDFLiteral())
        add(uri, RDF.ITSMO.Priority, priority.toRDFLiteral())
        add(uri, RDF.ITSMO.hasProjectOwner, commitmentLead.toRDFLiteral())
        primaryOutcomes.forEach { add(uri, RDF.SKOS.subject, it.toRDFLiteral()) }
        secondaryOutcomes.forEach { add(uri, RDF.SKOS.related, it.toRDFLiteral()) }
        keywords.forEach { add(uri, RDF.DCAT.keyword, it.toRDFLiteral()) }
    }

    companion object : RDFDeserializer<BPCom> {
        private val log = LoggerFactory.getLogger(BPCom::class.java)
        override val RDFType = RDF.SG.BPCom.type

        override fun fromModel(uri: URI, model: Model): BPCom {
            val filename = model.getOneObjectOrFail(uri, RDF.DCAT.resource).asLiteral().string
            val comment = model.getOneObjectOrFail(uri, RDF.RDFS.comment).asLiteral().string
            val commitment = model.getOneObjectOrFail(uri, RDF.ITSMO.Project).asLiteral().string
            val priority = model.getOneObjectOrFail(uri, RDF.ITSMO.Priority).asLiteral().string
            val commitmentLead = model.getOneObjectOrFail(uri, RDF.ITSMO.hasProjectOwner).asLiteral().string

            val primaryOutcomes = model.getAllObjectsOrFail(uri, RDF.SKOS.subject).map { it.asLiteral().string }
            val secondaryOutcomes = model.getAllObjectsOrFail(uri, RDF.SKOS.related).map { it.asLiteral().string }
            val keywords = model.getAllObjectsOrFail(uri, RDF.DCAT.keyword).map { it.asLiteral().string }

            return BPCom(uri, filename, comment, commitment, priority, commitmentLead, primaryOutcomes, secondaryOutcomes, keywords)
        }

        // To detailed graph JSON
        fun toDetailedGraphJSON(bpCom: BPCom): Node {

            return Node(
                name = bpCom.uri.value,
                children = listOf(
                    Node(
                        name = bpCom.filename,
                        children = listOf(
                            Node(name = "comment", children = stringToNode(bpCom.comment)),
                            Node(name = "commitment", children = stringToNode(bpCom.commitment)),
                            Node(name = "priority", children = stringToNode(bpCom.priority)),
                            Node(name = "commitmentLead", children = stringToNode(bpCom.commitmentLead)),
                            Node(name = "primaryOutcomes", children = listToNodes(bpCom.primaryOutcomes)),
                            Node(name = "secondaryOutcomes", children = listToNodes(bpCom.secondaryOutcomes)),
                            Node(name = "keywords", children = listToNodes(bpCom.keywords)),
                        )
                    )
                )
            )
        }
    }
}

