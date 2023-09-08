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

            val dG = model.getAllObjectsOrFail(uri, RDF.ORG.Organization).map { it.asLiteral().string.trim().lowercase() }
            val directorate = model.getAllObjectsOrFail(uri, RDF.ORG.OrganizationalUnit).map { it.asLiteral().string.trim().lowercase() }
            val director = model.getAllObjectsOrFail(uri, RDF.ORG.headOf).map { it.asLiteral().string.trim().lowercase() }
            val keyContact = model.getAllObjectsOrFail(uri, RDF.SCHEMA.contactPoint).map { it.asLiteral().string.trim().lowercase() }
            val contactEmail = model.getAllObjectsOrFail(uri, RDF.SCHEMA.email).map { it.asLiteral().string.trim().lowercase() }
            val divisions = model.getAllObjectsOrFail(uri, RDF.ORG.hasSubOrganization).map { it.asLiteral().string.trim().lowercase() }
            val divisionLeads = model.getAllObjectsOrFail(uri, RDF.ORG.hasMember).map { it.asLiteral().string.trim().lowercase() }

            val resProgramme = model.getOneObjectOrNull(uri.res, RDF.FRAPO.FundingProgramme.prop)?.asLiteral()?.string?.trim()?.lowercase()
            val resTotalOperatingCosts = model.getOneObjectOrNull(uri.res, RDF.FRAPO.Expenditure.prop)?.asLiteral()?.string?.trim()?.lowercase()
            val resCorporateRunningCosts = model.getOneObjectOrNull(uri.res, RDF.FRAPO.ProjectBudget.prop)?.asLiteral()?.string?.trim()?.lowercase()
            val resTotal = model.getOneObjectOrNull(uri.res, RDF.FRAPO.BudgetedAmount.prop)?.asLiteral()?.string?.trim()?.lowercase()
            val resCapital = model.getOneObjectOrNull(uri.res, RDF.FRAPO.Funding.prop)?.asLiteral()?.string?.trim()?.lowercase()
            val resFinancialTransactions = model.getOneObjectOrNull(uri.res, RDF.FRAPO.ExpenditureToDate.prop)?.asLiteral()?.string?.trim()?.lowercase()
//            val commitments = model.getAllObjectsOrFail(uri, RDF.DCAT.keyword).map { it.asLiteral().string }
            val commitmentsUris = model.getAllObjectsOrFail(uri, RDF.SG.BPDoc.hasCommitment)
                .map { it.asResource() }
            val commitments = BPCom.buildFromModel(model, commitmentsUris)

            val keywords = model.getAllObjectsOrFail(uri, RDF.DCAT.keyword).map { it.asLiteral().string.trim().lowercase() }

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

        // Forced graph
        fun toForcedGraphJSONAll(bpDocs: List<BPDoc>, searchDirs: List<String>? = emptyList(), searchTerms: List<String>? = emptyList(), limit: Int? = null): ForcedGraph {
            val docs = limit?.let { bpDocs.take(it) } ?: bpDocs
            val allGraphs = docs.map { toForcedGraphJSON(it, searchDirs, searchTerms) }
            return ForcedGraph(
                nodes = allGraphs.flatMap { it?.nodes ?: emptyList() }.filter { it?.name?.isNotEmpty() == true }.distinct(),
                links = allGraphs.flatMap { it?.links ?: emptyList() }.distinct(),
                categories = allGraphs.flatMap { it?.categories ?: emptyList() }.distinct()
            )
        }
        fun toForcedGraphJSON(bpDoc: BPDoc, searchDirs: List<String>? = emptyList(), searchTerms: List<String>? = emptyList()): ForcedGraph? {

            // Nodes
            val mainName = bpDoc.directorate.firstOrNull()?.trim()?.lowercase() ?: "***NO DIRECTORATE***"

            // Continue ONLY if the user wants to see this directorate
            if (!searchDirs.orEmpty().any { mainName.contains(it, ignoreCase = true) }) return null

            val mainId = "Directorate|$mainName"
            val main = ForcedNode(id = mainId, name = mainName, symbolSize = 65, value = "Directorate")
            val docIdDG = bpDoc.dG.firstOrNull()?.trim()?.lowercase() ?: "***NO DG***"
            val docId = "$mainName | $docIdDG"
            val doc = ForcedNode(id = docId, name = docId, symbolSize = 55, value = "Directorate | DG")
            val dG = ForcedNode(
                id = "${BPDoc::dG.name}|${bpDoc.dG}",
                name = bpDoc.dG.firstOrNull()?.trim()?.lowercase(),
                symbolSize = 20,
                value = BPDoc::dG.name,
                category = 0
            )
//            val directorate = ForcedNode(
//                id = "${BPDoc::directorate.name}|${bpDoc.directorate}",
//                name = bpDoc.directorate.firstOrNull(),
//                symbolSize = 40,
//                value = BPDoc::directorate.name,
//                category = 3
//            )
            val director = ForcedNode(
                id = "Lead|${bpDoc.director.firstOrNull()?.trim()?.lowercase()}",
                // id = "${BPDoc::director.name}|${bpDoc.director}",
                name = bpDoc.director.firstOrNull()?.trim()?.lowercase(),
                symbolSize = 20,
                value = "Lead",
//                value = BPDoc::director.name,
                category = 1
            )
            val keyContact = ForcedNode(
                id = "Lead|${bpDoc.keyContact.firstOrNull()?.trim()?.lowercase()}",
//                id = "${BPDoc::keyContact.name}|${bpDoc.keyContact}",
                name = bpDoc.keyContact.firstOrNull()?.trim()?.lowercase(),
                symbolSize = 20,
                value = "Lead",
//                value = BPDoc::keyContact.name,
                category = 1
            )
            val contactEmail = ForcedNode(
                id = "${BPDoc::contactEmail.name}|${bpDoc.contactEmail}",
                name = bpDoc.contactEmail.firstOrNull()?.trim()?.lowercase(),
                symbolSize = 20,
                value = BPDoc::contactEmail.name,
                category = 0
            )
            val resProgramme = ForcedNode(
                id = "${BPDoc::resProgramme.name}|${bpDoc.resProgramme}",
                name = bpDoc.resProgramme?.trim()?.lowercase(),
                symbolSize = 20,
                value = BPDoc::resProgramme.name,
                category = 0
            )
            val resTotalOperatingCosts = ForcedNode(
                id = "${BPDoc::resTotalOperatingCosts.name}|${bpDoc.resTotalOperatingCosts}",
                name = bpDoc.resTotalOperatingCosts?.trim()?.lowercase(),
                symbolSize = 20,
                value = BPDoc::resTotalOperatingCosts.name,
                category = 0
            )
            val resCorporateRunningCosts = ForcedNode(
                id = "${BPDoc::resCorporateRunningCosts.name}|${bpDoc.resCorporateRunningCosts}",
                name = bpDoc.resCorporateRunningCosts?.trim()?.lowercase(),
                symbolSize = 20,
                value = BPDoc::resCorporateRunningCosts.name,
                category = 0
            )
            val resTotal = ForcedNode(
                id = "${BPDoc::resTotal.name}|${bpDoc.resTotal}",
                name = bpDoc.resTotal?.trim()?.lowercase(),
                symbolSize = 20,
                value = BPDoc::resTotal.name,
                category = 0
            )
            val resCapital = ForcedNode(
                id = "${BPDoc::resCapital.name}|${bpDoc.resCapital}",
                name = bpDoc.resCapital?.trim()?.lowercase(),
                symbolSize = 20,
                value = BPDoc::resCapital.name,
                category = 0
            )
            val resFinancialTransactions = ForcedNode(
                id = "${BPDoc::resFinancialTransactions.name}|${bpDoc.resFinancialTransactions}",
                name = bpDoc.resFinancialTransactions?.trim()?.lowercase(),
                symbolSize = 20,
                value = BPDoc::resFinancialTransactions.name,
                category = 0
            )

            val divisionsNodes = bpDoc.divisions.map { division ->
                ForcedNode(
                    id = "${BPDoc::divisions.name}|${division.split("|")[1].trim().lowercase()}",
                    name = division.split("|")[1].trim().lowercase(),
                    symbolSize = 30,
                    value = "Division",
                    category = 1,
                    itemStyle = ItemStyle(color = "blue")
                )
            }
            val divisionLeadsNodes = bpDoc.divisionLeads.map { divisionLead ->
                ForcedNode(
                    id = "Lead|${divisionLead.split("|")[1].trim().lowercase()}",
//                    id = "${BPDoc::divisionLeads.name}|${divisionLead.split("|")[1].trim().lowercase()}",
                    name = divisionLead.split("|")[1].trim().lowercase(),
                    symbolSize = 20,
                    value = "Lead",
//                    value = "Division Lead",
                    category = 1
                )
            }
            // Buggy
//            val commitmentsNodes = bpDoc.commitments.filter { commitment -> commitment.filename != "empty" }
//                .map { commitment ->
//                    ForcedNode(
//                        id = "${BPDoc::commitments.name}|${commitment}",
//    //                    id = "npfOutcome|${primaryOutcome}",
//                        name = commitment.commitment,
//                        symbolSize = 20,
//                        value = "Commitment",
//                        category = 0
//                    )
//            }
            val keywordsNodes = bpDoc.keywords.map { keyword ->
                ForcedNode(
                    id = "${BPDoc::keywords.name}|${keyword}",
                    name = keyword.trim().lowercase(),
                    symbolSize = 31,
                    value = "keyword",
                    category = 2
                )
            }

            // Links
            val doc2propertyLinks = listOf(
                ForcedLink(target = mainId, source = docId),
                ForcedLink(target = docId, source = dG.id),
//                ForcedLink(source = docId, target = directorate.id),
                ForcedLink(target = docId, source = director.id),
                ForcedLink(target = docId, source = keyContact.id),
                ForcedLink(target = docId, source = contactEmail.id),
                ForcedLink(target = docId, source = resProgramme.id),
                ForcedLink(target = docId, source = resTotalOperatingCosts.id),
                ForcedLink(target = docId, source = resCorporateRunningCosts.id),
                ForcedLink(target = docId, source = resTotal.id),
                ForcedLink(target = docId, source = resCapital.id),
                ForcedLink(target = docId, source = resFinancialTransactions.id),
            )
            val divisionsLinks = bpDoc.divisions.flatMap { division ->
                val (divisionIndex, divisionName) = division.split("|")
                val divisionId = "${BPDoc::divisions.name}|${divisionName.trim().lowercase()}"

                val matchingDivisionLead = bpDoc.divisionLeads.find { it.startsWith("$divisionIndex|") }
                    ?.split("|")?.get(1)
                    ?.trim()
                    ?.lowercase()

                listOf(
                    ForcedLink(target = docId, source = divisionId),
//                    ForcedLink(target = divisionId, source = "${BPDoc::divisionLeads.name}|${matchingDivisionLead}")
                    ForcedLink(target = divisionId, source = "Lead|${matchingDivisionLead}")
                )
            }
//            val divisionLeadsLinks = bpDoc.divisionLeads.flatMap { divisionLead ->
//
//                val (divisionLeadIndex, divisionLeadName) = divisionLead.split("|")
//                val divisionLeadId = "${BPDoc::divisionLeads.name}|${divisionLeadName}"
//
//                listOf(
//                    ForcedLink(source = docId, target = divisionLeadId),
//                    ForcedLink(source = divisionLeadId, target = "${BPDoc::divisionLeads.name}|${divisionLead}")
//
//                )
//            }
            //Buggy
//            val commitmentsLinks = bpDoc.commitments.flatMap { commitment ->
//                listOf(
//                    ForcedLink(source = docId, target = "${BPDoc::commitments.name}|${commitment.commitment}")
//                )
//            }
            val keywordsLinks = bpDoc.keywords.flatMap { keyword ->
                listOf(
                    ForcedLink(target = docId, source = "${BPDoc::keywords.name}|${keyword}")
                )
            }
            val nodes = listOf(
                main,
                doc,
                dG,
//                directorate,
                director,
                keyContact,
                contactEmail,
                resProgramme,
                resTotalOperatingCosts,
                resCorporateRunningCosts,
                resTotal,
                resCapital,
                resFinancialTransactions) + divisionsNodes + divisionLeadsNodes + /*commitmentsNode +*/ keywordsNodes
            val links = doc2propertyLinks + divisionsLinks + /*commitmentsLinks +*/ keywordsLinks

            if (!searchTerms.isNullOrEmpty()) {
                val containsSearchTerms = searchTerms.all { searchTerm ->
                    nodes.any { node ->
                        node.name?.contains(searchTerm, ignoreCase = true) == true
                    } || links.any { link ->
                        link.source?.contains(searchTerm, ignoreCase = true) == true ||
                                link.target?.contains(searchTerm, ignoreCase = true) == true
                    }
                }

                return if (containsSearchTerms) {
                    ForcedGraph(
                        nodes = nodes,
                        links = links,
                        categories = categories
                    )
                } else {
                    null
                }
            }

            return ForcedGraph(
                nodes = nodes,
                links = links,
                categories = categories
            )
        }
    }
}data class BPCom(
    @JsonProperty("uri") override var uri: URI,
    var filename: String?,
//    var comment: String, // rdfs:comment
    var commitment: String?, // itsmo:Project
    var priority: String?, // itsmo:Priority
    var commitmentLead: String?, // itsmo:hasProjectOwner
    var projectBudget: String?, // frapo:ProjectBudget
    var budgetSufficient: String?, // sg:BudgetSufficient
    var deliveryPartners: List<String>, // sg:DeliveryPartner
    var primaryOutcomes: List<String>, // skos:subject
    var secondaryOutcomes: List<String>, // skos:related
    var keywords: List<String>, // dcat:keyword
) : RDFWritable {
    fun withURI(uri: URI): BPCom = BPCom(uri, this.filename, /*this.comment,*/ this.commitment, this.priority, this.commitmentLead, this.projectBudget, this.budgetSufficient, this.deliveryPartners, this.primaryOutcomes, this.secondaryOutcomes, this.keywords)

    override fun toRDF() = jenaModel {
        add(uri, RDF.type, RDF.SG.BPCom.type.res)

        add(uri, RDF.DCAT.resource, filename?.toRDFLiteral())
//        add(uri, RDF.RDFS.comment, comment.toRDFLiteral())
        add(uri, RDF.DCAT.title, commitment?.toRDFLiteral())
        add(uri, RDF.ITSMO.Priority, priority?.toRDFLiteral())
        add(uri, RDF.ITSMO.hasProjectOwner, commitmentLead?.toRDFLiteral())
        add(uri, RDF.FRAPO.ProjectBudget, projectBudget?.toRDFLiteral())
        add(uri, RDF.SG.BudgetSufficient, budgetSufficient?.toRDFLiteral())
        deliveryPartners.forEach { add(uri, RDF.SG.DeliveryPartner, it.toRDFLiteral()) }
        primaryOutcomes.forEach { add(uri, RDF.SKOS.subject, it.toRDFLiteral()) }
        secondaryOutcomes.forEach { add(uri, RDF.SKOS.related, it.toRDFLiteral()) }
        keywords.forEach { add(uri, RDF.DCAT.keyword, it.toRDFLiteral()) }
    }

    companion object : RDFDeserializer<BPCom> {
        private val log = LoggerFactory.getLogger(BPCom::class.java)
        override val RDFType = RDF.SG.BPCom.type

        override fun fromModel(uri: URI, model: Model): BPCom {
            val filename = model.getOneObjectOrNull(uri, RDF.DCAT.resource)?.asLiteral()?.string?.trim()?.lowercase()
//            val comment = model.getOneObjectOrFail(uri, RDF.RDFS.comment).asLiteral().string
            val commitment = model.getOneObjectOrNull(uri, RDF.DCAT.title)?.asLiteral()?.string?.trim()?.lowercase()
            val projectBudget = model.getOneObjectOrNull(uri, RDF.FRAPO.ProjectBudget)?.asLiteral()?.string
            val budgetSufficient = model.getOneObjectOrNull(uri, RDF.SG.BudgetSufficient)?.asLiteral()?.string?.trim()?.lowercase()
            val priority = model.getOneObjectOrNull(uri, RDF.ITSMO.Priority)?.asLiteral()?.string?.trim()?.lowercase()
            val commitmentLead = model.getOneObjectOrNull(uri, RDF.ITSMO.hasProjectOwner)?.asLiteral()?.string?.trim()?.lowercase()
            val deliveryPartners = model.getAllObjectsOrFail(uri, RDF.SG.DeliveryPartner).map { it.asLiteral().string.trim().lowercase() }
            val primaryOutcomes = model.getAllObjectsOrFail(uri, RDF.SKOS.subject).map { it.asLiteral().string.trim().lowercase() }
            val secondaryOutcomes = model.getAllObjectsOrFail(uri, RDF.SKOS.related).map { it.asLiteral().string.trim().lowercase() }
            val keywords = model.getAllObjectsOrFail(uri, RDF.DCAT.keyword).map { it.asLiteral().string.trim().lowercase() }

            return BPCom(uri, filename, /*comment,*/ commitment, priority, commitmentLead, projectBudget, budgetSufficient, deliveryPartners, primaryOutcomes, secondaryOutcomes, keywords)
        }

        // To detailed graph JSON
        fun toDetailedGraphJSON(bpCom: BPCom): Node {

            return Node(
                name = bpCom.uri.value,
                children = listOf(
                    Node(
                        name = bpCom.filename,
                        children = listOf(
//                            Node(name = "comment", children = stringToNode(bpCom.comment)),
                            Node(name = "commitment", children = stringToNode(bpCom.commitment)),
                            Node(name = "priority", children = stringToNode(bpCom.priority)),
                            Node(name = "commitmentLead", children = stringToNode(bpCom.commitmentLead)),
                            Node(name = "projectBudget", children = stringToNode(bpCom.projectBudget)),
                            Node(name = "budgetSufficient", children = stringToNode(bpCom.budgetSufficient)),
                            Node(name = "deliveryPartners", children = listToNodes(bpCom.deliveryPartners)),
                            Node(name = "primaryOutcomes", children = listToNodes(bpCom.primaryOutcomes)),
                            Node(name = "secondaryOutcomes", children = listToNodes(bpCom.secondaryOutcomes)),
                            Node(name = "keywords", children = listToNodes(bpCom.keywords)),
                        )
                    )
                )
            )
        }

        // Forced graph
        fun toForcedGraphJSONAll(bpComs: List<BPCom>, searchDirs: List<String>? = emptyList(), searchTerms: List<String>? = emptyList(), bpDocsFilenamesDirectorates: Map<String?,String?>, limit: Int? = null): ForcedGraph {
            val docs = limit?.let { bpComs.take(it) } ?: bpComs
            val allGraphs = docs.map { toForcedGraphJSON(it, searchDirs, searchTerms, bpDocsFilenamesDirectorates) }
            return ForcedGraph(
                nodes = allGraphs.flatMap { it?.nodes ?: emptyList() }.filter { it?.name?.isNotEmpty() == true }.distinct(),
                links = allGraphs.flatMap { it?.links ?: emptyList() }.distinct(),
                categories = allGraphs.flatMap { it?.categories ?: emptyList() }.distinct()
            )
        }
        fun toForcedGraphJSON(bpCom: BPCom, searchDirs: List<String>? = emptyList(), searchTerms: List<String>? = emptyList(), bpDocsFilenamesDirectorates: Map<String?,String?>): ForcedGraph? {

            // Nodes
            val mainName = bpDocsFilenamesDirectorates[bpCom.filename]?.trim()?.lowercase() ?: "***NO DIRECTORATE***"

            // Continue ONLY if the user wants to see this directorate
            if (!searchDirs.orEmpty().any { mainName.contains(it, ignoreCase = true) }) return null

            val mainId = "Directorate|$mainName"
            val main = ForcedNode(id = mainId, name = mainName, symbolSize = 65, value = "Directorate")
            val docName = bpCom.commitment?.trim()?.lowercase() ?: "***NO COMMITMENT TITLE***"
            val docId = "Commitment|$docName"
            val doc = ForcedNode(id = docId, name = docName, symbolSize = 55, value = "Commitment title")
//            val commitment = ForcedNode(
//                id = "${BPCom::commitment.name}|${bpCom.commitment}",
//                name = bpCom.commitment,
//                symbolSize = 20,
//                value = BPCom::commitment.name,
//                category = 0
//            )
            val priority = ForcedNode(
                id = "${BPCom::priority.name}|${bpCom.priority}",
                name = bpCom.priority?.trim()?.lowercase(),
                symbolSize = 20,
                value = BPCom::priority.name,
                category = 0
            )
            val commitmentLead = ForcedNode(
                id = "Lead|${bpCom.commitmentLead?.trim()?.lowercase()}",
//                id = "${BPCom::commitmentLead.name}|${bpCom.commitmentLead}",
                name = bpCom.commitmentLead?.trim()?.lowercase(),
                symbolSize = 20,
                value = "Lead",
//                value = BPCom::commitmentLead.name,
                category = 1
            )
            val projectBudget = ForcedNode(
                id = "${BPCom::projectBudget.name}|${bpCom.projectBudget}",
                name = bpCom.projectBudget?.trim()?.lowercase(),
                symbolSize = 20,
                value = BPCom::projectBudget.name,
                category = 0
            )
            val budgetSufficient = ForcedNode(
                id = "${BPCom::budgetSufficient.name}|${bpCom.budgetSufficient}",
                name = bpCom.budgetSufficient?.trim()?.lowercase(),
                symbolSize = 20,
                value = BPCom::budgetSufficient.name,
                category = 0
            )

            val primaryOutcomesNodes = bpCom.primaryOutcomes.map { primaryOutcome ->
                ForcedNode(
//                    id = "${BPCom::primaryOutcomes.name}|${primaryOutcome}",
                    id = "Outcome|${primaryOutcome}",
                    name = primaryOutcome.trim()?.lowercase(),
                    symbolSize = 36,
                    value = "Outcome",
                    category = getOutcomeCategory(primaryOutcome.trim().lowercase())
                )
            }
            val secondaryOutcomesNodes = bpCom.secondaryOutcomes.map { secondaryOutcome ->
                ForcedNode(
//                    id = "${BPCom::secondaryOutcomes.name}|${secondaryOutcome}",
                    id = "Outcome|${secondaryOutcome}",
                    name = secondaryOutcome.trim()?.lowercase(),
                    symbolSize = 36,
                    value = "Outcome",
                    category = getOutcomeCategory(secondaryOutcome.trim().lowercase())
                )
            }

            val keywordsNodes = bpCom.keywords.map { keyword ->
                ForcedNode(
                    id = "${BPCom::keywords.name}|${keyword}",
                    name = keyword.trim()?.lowercase(),
                    symbolSize = 31,
                    value = "keyword",
                    category = 2
                )
            }

            // Links
            val doc2propertyLinks = listOf(
                ForcedLink(target = mainId, source = docId),
//                ForcedLink(source = docId, target = bpCom.filename),
                ForcedLink(target = bpCom.filename, source = docId),
//                ForcedLink(source = docId, target = commitment.id),
                ForcedLink(target = docId, source = priority.id),
                ForcedLink(target = docId, source = commitmentLead.id),
                ForcedLink(target = docId, source = projectBudget.id),
                ForcedLink(target = docId, source = budgetSufficient.id),
            )

            val primaryOutcomesLinks = bpCom.primaryOutcomes.flatMap { primaryOutcome ->
                listOf(
//                    ForcedLink(target = docId, source = "${BPCom::primaryOutcomes.name}|${primaryOutcome}")
                    ForcedLink(target = docId, source = "Outcome|${primaryOutcome}")
                )
            }
            val secondaryOutcomesLinks = bpCom.secondaryOutcomes.flatMap { secondaryOutcome ->
                listOf(
//                    ForcedLink(target = docId, source = "${BPCom::secondaryOutcomes.name}|${secondaryOutcome}")
                    ForcedLink(target = docId, source = "Outcome|${secondaryOutcome}")
                )
            }
            val keywordsLinks = bpCom.keywords.flatMap { keyword ->
                listOf(
                    ForcedLink(target = docId, source = "${BPCom::keywords.name}|${keyword}")
                )
            }
            val nodes = listOf(
                main,
                doc,
//                    commitment,
                priority,
                commitmentLead,
                projectBudget,
                budgetSufficient
            ) + primaryOutcomesNodes + secondaryOutcomesNodes + keywordsNodes
            val links = doc2propertyLinks + primaryOutcomesLinks + secondaryOutcomesLinks + keywordsLinks

            if (!searchTerms.isNullOrEmpty()) {
                val containsSearchTerms = searchTerms.all { searchTerm ->
                    nodes.any { node ->
                        node.name?.contains(searchTerm, ignoreCase = true) == true
                    } || links.any { link ->
                        link.source?.contains(searchTerm, ignoreCase = true) == true ||
                                link.target?.contains(searchTerm, ignoreCase = true) == true
                    }
                }

                return if (containsSearchTerms) {
                    ForcedGraph(
                        nodes = nodes,
                        links = links,
                        categories = categories
                    )
                } else {
                    null
                }
            }

            return ForcedGraph(
                nodes = nodes,
                links = links,
                categories = categories
            )
        }
    }
}

