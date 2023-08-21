package com.sg.app.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.sg.app.rdf.*
import org.apache.jena.rdf.model.Model
import org.slf4j.LoggerFactory
import java.util.*

data class PFGDoc(
    @JsonProperty("uri") override var uri: URI,
    var filename: String,
    var ministerialPortfolio: List<String>,
    var directorate: List<String>,
    var dG: List<String>,
    var unitBranch: List<String>,
    var leadOfficial: List<String>,
    var scsClearance: List<String>,
    var fbpClearance: List<String>,
    var primaryOutcomes: List<String>,
    var secondaryOutcomes: List<String>,
    var portfolioCoordinator: List<String>,
    var policyTitle: List<String>,
//    var completionDate: LocalDateTime?,
    var completionDate: String?,
    var keywords: List<String>,
) : RDFWritable {
    fun withURI(uri: URI): PFGDoc = PFGDoc(uri, this.filename, this.ministerialPortfolio, this.directorate, this.dG, this.unitBranch, this.leadOfficial, this.scsClearance, this.fbpClearance, this.primaryOutcomes, this.secondaryOutcomes, this.portfolioCoordinator, this.policyTitle, this.completionDate,/* this.npfList,*/ this.keywords)

    override fun toRDF() = jenaModel {
        add(uri.res, RDF.type.prop, RDF.SG.PFGDoc.type.res)

        add(uri.res, RDF.DCAT.resource.prop, filename.toRDFLiteral())

        ministerialPortfolio.forEach { add(uri, RDF.DBPEDIA.portfolio, it.toRDFLiteral()) }
        directorate.forEach { add(uri, RDF.ORG.OrganizationalUnit, it.toRDFLiteral()) }
        dG.forEach { add(uri, RDF.ORG.Organization, it.toRDFLiteral()) }
        unitBranch.forEach { add(uri, RDF.ORG.hasUnit, it.toRDFLiteral()) }
        leadOfficial.forEach { add(uri, RDF.ORG.headOf, it.toRDFLiteral()) }
        scsClearance.forEach { add(uri, RDF.SG.PFGDoc.scsClearance, it.toRDFLiteral()) }
        fbpClearance.forEach { add(uri, RDF.SG.PFGDoc.fbpClearance, it.toRDFLiteral()) }
        primaryOutcomes.forEach { add(uri, RDF.SKOS.subject, it.toRDFLiteral()) }
        secondaryOutcomes.forEach { add(uri, RDF.SKOS.related, it.toRDFLiteral()) }
        portfolioCoordinator.forEach { add(uri, RDF.DBPEDIA.projectCoordinator, it.toRDFLiteral()) }
        policyTitle.forEach { add(uri, RDF.DCAT.title, it.toRDFLiteral()) }
//        add(uri.res, RDF.DBPEDIA.completionDate.prop, DateTimeFormatter.ISO_DATE_TIME.format(completionDate).toRDFLiteral(
//            XSDDatatype.XSDdateTime))
        add(uri, RDF.DBPEDIA.completionDate, completionDate?.toRDFLiteral())
        keywords.forEach { add(uri, RDF.DCAT.keyword, it.toRDFLiteral()) }
    }

    companion object : RDFDeserializer<PFGDoc> {
        private val log = LoggerFactory.getLogger(PFGDoc::class.java)
        override val RDFType = RDF.SG.PFGDoc.type

        override fun fromModel(uri: URI, model: Model): PFGDoc {
            val filename = model.getOneObjectOrFail(uri.res, RDF.DCAT.resource.prop).asLiteral().string
            val ministerialPortfolio = model.getAllObjectsOrFail(uri, RDF.DBPEDIA.portfolio).map { it.asLiteral().string }
            val directorate = model.getAllObjectsOrFail(uri, RDF.ORG.OrganizationalUnit).map { it.asLiteral().string }
            val dG = model.getAllObjectsOrFail(uri, RDF.ORG.Organization).map { it.asLiteral().string }
            val unitBranch = model.getAllObjectsOrFail(uri, RDF.ORG.hasUnit).map { it.asLiteral().string }
            val leadOfficial = model.getAllObjectsOrFail(uri, RDF.ORG.headOf).map { it.asLiteral().string }
            val scsClearance = model.getAllObjectsOrFail(uri, RDF.SG.PFGDoc.scsClearance).map { it.asLiteral().string }
            val fbpClearance = model.getAllObjectsOrFail(uri, RDF.SG.PFGDoc.fbpClearance).map { it.asLiteral().string }
            val primaryOutcomes = model.getAllObjectsOrFail(uri, RDF.SKOS.subject).map { it.asLiteral().string }
            val secondaryOutcomes = model.getAllObjectsOrFail(uri, RDF.SKOS.related).map { it.asLiteral().string }
            val portfolioCoordinator = model.getAllObjectsOrFail(uri, RDF.DBPEDIA.projectCoordinator).map { it.asLiteral().string }
            val policyTitle = model.getAllObjectsOrFail(uri, RDF.DCAT.title).map { it.asLiteral().string }
//            val completionDate = try {
//                model.getOneObjectOrFail(uri, RDF.DBPEDIA.completionDate)
//                    .asLiteral().string.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }
//            } catch (e: Exception) {
//                null
//            }
            val completionDate = model.getOneObjectOrNull(uri, RDF.DBPEDIA.completionDate)?.asLiteral()?.string
            val keywords = model.getAllObjectsOrFail(uri, RDF.DCAT.keyword).map { it.asLiteral().string }

            return PFGDoc(uri, filename, ministerialPortfolio, directorate, dG, unitBranch, leadOfficial, scsClearance, fbpClearance, primaryOutcomes, secondaryOutcomes, portfolioCoordinator, policyTitle, completionDate, keywords)
        }

        // To detailed graph JSON
        fun toDetailedGraphJSON(pfgDoc: PFGDoc): Node {
            return Node(
                name = pfgDoc.uri.value,
                children = listOf(
                    Node(
                        name = pfgDoc.filename,
                        children = listOf(
                            Node(name = "ministerialPortfolio", children = listToNodes(pfgDoc.ministerialPortfolio)),
                            Node(name = "directorate", children = listToNodes(pfgDoc.directorate)),
                            Node(name = "dG", children = listToNodes(pfgDoc.dG)),
                            Node(name = "unitBranch", children = listToNodes(pfgDoc.unitBranch)),
                            Node(name = "leadOfficial", children = listToNodes(pfgDoc.leadOfficial)),
                            Node(name = "scsClearance", children = listToNodes(pfgDoc.scsClearance)),
                            Node(name = "fbpClearance", children = listToNodes(pfgDoc.fbpClearance)),
                            Node(name = "primaryOutcomes", children = listToNodes(pfgDoc.primaryOutcomes)),
                            Node(name = "secondaryOutcomes", children = listToNodes(pfgDoc.secondaryOutcomes)),
                            Node(name = "portfolioCoordinator", children = listToNodes(pfgDoc.portfolioCoordinator)),
                            Node(name = "policyTitle", children = listToNodes(pfgDoc.policyTitle)),
//                            Node(name = "completionDate", children = listOf(Node(pfgDoc.completionDate!!.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))),
                            Node(name = "completionDate", children = stringToNode(pfgDoc.completionDate ?: "")),
                            Node(name = "keywords", children = listToNodes(pfgDoc.keywords))
                        )
                    )
                )
            )
        }

        fun toSankeyGraphJSONAll(pfgDocs: List<PFGDoc>, limit: Int? = null): SankeyGraph {
            val docs = limit?.let { pfgDocs.take(it) } ?: pfgDocs
            val allGraphs = docs.map { toSankeyGraphJSON(it) }

            return SankeyGraph(
                nodes = allGraphs.flatMap { it.nodes!! }.distinct(),
                links = allGraphs.flatMap { it.links!! }
            )
        }
        // To sankey graph JSON
        fun toSankeyGraphJSON(pfgDoc: PFGDoc): SankeyGraph {

            // Nodes
            val primaryOutcomesNodes = pfgDoc.primaryOutcomes.map { primaryOutcome ->
                SankeyNode(name = primaryOutcome)
            }
            val directorateNode = SankeyNode(name = pfgDoc.directorate[0])
            val policyTitleNode = SankeyNode(name = pfgDoc.policyTitle[0])
            val secondaryOutcomesNodes = pfgDoc.secondaryOutcomes.map { secondaryOutcome ->
                SankeyNode(name = secondaryOutcome)
            }

            // Links
            val primaryOutcomesLinks = pfgDoc.primaryOutcomes.flatMap { primaryOutcome ->
                listOf(
                    SankeyLink(source = pfgDoc.policyTitle[0], target = primaryOutcome, value = 1, label = "Primary")
                )
            }

            val secondaryOutcomesLinks = pfgDoc.secondaryOutcomes.flatMap { secondaryOutcome ->
                listOf(
                    SankeyLink(source = pfgDoc.policyTitle[0], target = secondaryOutcome, value = 1, label = "Secondary")
                )
            }

            val dir2policyLink = listOf(
                SankeyLink(source = pfgDoc.directorate[0], target = pfgDoc.policyTitle[0], value = primaryOutcomesLinks.size + secondaryOutcomesLinks.size, label = pfgDoc.filename)
            )

            return SankeyGraph(
                nodes = primaryOutcomesNodes + secondaryOutcomesNodes + directorateNode + policyTitleNode,
                links = primaryOutcomesLinks + secondaryOutcomesLinks + dir2policyLink
            )
        }

        // Forced graph
        fun toForcedGraphJSONAll(pfgDocs: List<PFGDoc>, searchTerms: List<String>? = emptyList(), limit: Int? = null): ForcedGraph {
            val docs = limit?.let { pfgDocs.take(it) } ?: pfgDocs
            val allGraphs = docs.map { toForcedGraphJSON(it, searchTerms) }
            return ForcedGraph(
                nodes = allGraphs.flatMap { it?.nodes ?: emptyList() }.filter { it?.name?.isNotEmpty() == true }.distinct(),
                links = allGraphs.flatMap { it?.links ?: emptyList() }.distinct(),
                categories = allGraphs.flatMap { it?.categories ?: emptyList() }.distinct()
            )
        }
        fun toForcedGraphJSON(pfgDoc: PFGDoc, searchTerms: List<String>? = emptyList()): ForcedGraph? {

            // Nodes
            val docId = pfgDoc.directorate.firstOrNull() ?: "***NO DIRECTORATE***"
            val doc = ForcedNode(id = docId, name = docId, symbolSize = 55, value = "Directorate")
            val ministerialPortfolio = ForcedNode(
                id = "${PFGDoc::ministerialPortfolio.name}|${pfgDoc.ministerialPortfolio}",
                name = pfgDoc.ministerialPortfolio.firstOrNull(),
                symbolSize = 20,
                value = PFGDoc::ministerialPortfolio.name,
                category = 0
            )
//            val directorate = ForcedNode(
//                id = "${PFGDoc::directorate.name}|${pfgDoc.directorate}",
//                name = pfgDoc.directorate.firstOrNull(),
//                symbolSize = 40,
//                value = PFGDoc::directorate.name,
//                category = 3
//            )
            val leadOfficial = ForcedNode(
                id = "${PFGDoc::leadOfficial.name}|${pfgDoc.leadOfficial}",
                name = pfgDoc.leadOfficial.firstOrNull(),
                symbolSize = 20,
                value = PFGDoc::leadOfficial.name,
                category = 0
            )
            val unitBranch = ForcedNode(
                id = "${PFGDoc::unitBranch.name}|${pfgDoc.unitBranch}",
                name = pfgDoc.unitBranch.firstOrNull(),
                symbolSize = 20,
                value = PFGDoc::unitBranch.name,
                category = 0
            )
            val policyTitle = ForcedNode(
                id = "${PFGDoc::policyTitle.name}|${pfgDoc.policyTitle}",
                name = pfgDoc.policyTitle.firstOrNull(),
                symbolSize = 20,
                value = PFGDoc::policyTitle.name,
                category = 0
            )
            val scsClearance = ForcedNode(
                id = "${PFGDoc::scsClearance.name}|${pfgDoc.scsClearance}",
                name = pfgDoc.scsClearance.firstOrNull(),
                symbolSize = 20,
                value = PFGDoc::scsClearance.name,
                category = 0
            )
            val fbpClearance = ForcedNode(
                id = "${PFGDoc::fbpClearance.name}|${pfgDoc.fbpClearance}",
                name = pfgDoc.fbpClearance.firstOrNull(),
                symbolSize = 20,
                value = PFGDoc::fbpClearance.name,
                category = 0
            )
            val dG = ForcedNode(
                id = "${PFGDoc::dG.name}|${pfgDoc.dG}",
                name = pfgDoc.dG.firstOrNull(),
                symbolSize = 20,
                value = PFGDoc::dG.name,
                category = 0
            )
            val portfolioCoordinator = ForcedNode(
                id = "${PFGDoc::portfolioCoordinator.name}|${pfgDoc.portfolioCoordinator}",
                name = pfgDoc.portfolioCoordinator.firstOrNull(),
                symbolSize = 20,
                value = PFGDoc::portfolioCoordinator.name,
                category = 0
            )
            val completionDate = ForcedNode(
                id = "${PFGDoc::completionDate.name}|${pfgDoc.completionDate}",
                name = pfgDoc.completionDate,
                symbolSize = 20,
                value = PFGDoc::completionDate.name,
                category = 0
            )

            val primaryOutcomesNodes = pfgDoc.primaryOutcomes.map { primaryOutcome ->
                ForcedNode(
                    id = "${PFGDoc::primaryOutcomes.name}|${primaryOutcome}",
//                    id = "npfOutcome|${primaryOutcome}",
                    name = primaryOutcome,
                    symbolSize = 36,
                    value = "Primary Outcome",
                    category = 1
                )
            }
            val secondaryOutcomesNodes = pfgDoc.secondaryOutcomes.map { secondaryOutcome ->
                ForcedNode(
                    id = "${PFGDoc::secondaryOutcomes.name}|${secondaryOutcome}",
//                    id = "npfOutcome|${secondaryOutcome}",
                    name = secondaryOutcome,
                    symbolSize = 36,
                    value = "Secondary Outcome",
                    category = 2
                )
            }
            val keywordsNodes = pfgDoc.keywords.map { keyword ->
                ForcedNode(
                    id = "${PFGDoc::keywords.name}|${keyword}",
                    name = keyword,
                    symbolSize = 31,
                    value = "keyword",
                    category = 3
                )
            }

            // Links
            val doc2propertyLinks = listOf(
                ForcedLink(source = docId, target = ministerialPortfolio.id),
//                ForcedLink(source = docId, target = directorate.id),
                ForcedLink(source = docId, target = leadOfficial.id),
                ForcedLink(source = docId, target = unitBranch.id),
                ForcedLink(source = docId, target = policyTitle.id),
                ForcedLink(source = docId, target = scsClearance.id),
                ForcedLink(source = docId, target = fbpClearance.id),
                ForcedLink(source = docId, target = dG.id),
                ForcedLink(source = docId, target = portfolioCoordinator.id),
                ForcedLink(source = docId, target = completionDate.id),
            )
            val primaryOutcomesLinks = pfgDoc.primaryOutcomes.flatMap { primaryOutcome ->
                listOf(
                    ForcedLink(source = docId, target = "${PFGDoc::primaryOutcomes.name}|${primaryOutcome}")
                )
            }

            val secondaryOutcomesLinks = pfgDoc.secondaryOutcomes.flatMap { secondaryOutcome ->
                listOf(
                    ForcedLink(source = docId, target = "${PFGDoc::secondaryOutcomes.name}|${secondaryOutcome}")
                )
            }
            val keywordsLinks = pfgDoc.keywords.flatMap { keyword ->
                listOf(
                    ForcedLink(source = docId, target = "${PFGDoc::keywords.name}|${keyword}")
                )
            }

            val nodes = listOf(
                doc,
                ministerialPortfolio,
//                directorate,
                leadOfficial,
                unitBranch,
                policyTitle,
                scsClearance,
                fbpClearance,
                dG,
                portfolioCoordinator,
                completionDate
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
                        categories = listOf(
                            ForcedCategory(name = "Other"),
                            ForcedCategory(name = "Primary outcomes"),
                            ForcedCategory(name = "Secondary outcomes"),
                            ForcedCategory(name = "Keywords"),
                        )
                    )
                } else {
                    null
                }
            }
            return ForcedGraph(
                nodes = nodes,
                links = links,
                categories = listOf(
                    ForcedCategory(name = "Other"),
                    ForcedCategory(name = "Primary outcomes"),
                    ForcedCategory(name = "Secondary outcomes"),
//                    ForcedCategory(name = "Directorate"),
                    ForcedCategory(name = "Keywords"),
                )
            )
        }
    }
}
data class PFGAux(
    @JsonProperty("uri") override var uri: URI,
    var id: String?,
    var period: String?,
    var accessURL: String?,
    var strategicPriority: String?,
    var legislativeProposal: String?,
    var buteHouseAgreementLink: String?,
    var completionDate: String?,
    var ministerialPortfolio: List<String>,
    var directorate: List<String>,
    var dG: List<String>,
    var leadOfficial: List<String>,
    var primaryOutcomes: List<String>,
    var secondaryOutcomes: List<String>,
    var policyTitle: List<String>,
    var keywords: List<String>,
) : RDFWritable {
    fun withURI(uri: URI): PFGAux = PFGAux(uri, this.id, this.period, this.accessURL, this.strategicPriority, this.legislativeProposal, this.buteHouseAgreementLink, this.completionDate, this.ministerialPortfolio, this.directorate, this.dG, this.leadOfficial, this.primaryOutcomes, this.secondaryOutcomes, this.policyTitle, this.keywords)

    override fun toRDF() = jenaModel {
        add(uri, RDF.type, RDF.SG.PFGAux.type.res)

        add(uri, RDF.DCAT.identifier, id?.toRDFLiteral())
        add(uri, RDF.DCTERMS.Period, period?.toRDFLiteral())
        add(uri, RDF.DCAT.accessURL, accessURL?.toRDFLiteral())
        add(uri, RDF.ITSMO.Priority, strategicPriority?.toRDFLiteral())
        add(uri, RDF.DCTERMS.Standard, legislativeProposal?.toRDFLiteral())
        add(uri, RDF.DCTERMS.policy, buteHouseAgreementLink?.toRDFLiteral())
        add(uri, RDF.DBPEDIA.completionDate, completionDate?.toRDFLiteral())
        ministerialPortfolio.forEach { add(uri, RDF.DBPEDIA.portfolio, it.toRDFLiteral()) }
        directorate.forEach { add(uri, RDF.ORG.OrganizationalUnit, it.toRDFLiteral()) }
        dG.forEach { add(uri, RDF.ORG.Organization, it.toRDFLiteral()) }
        leadOfficial.forEach { add(uri, RDF.ORG.headOf, it.toRDFLiteral()) }
        primaryOutcomes.forEach { add(uri, RDF.SKOS.subject, it.toRDFLiteral()) }
        secondaryOutcomes.forEach { add(uri, RDF.SKOS.related, it.toRDFLiteral()) }
        policyTitle.forEach { add(uri, RDF.DCAT.title, it.toRDFLiteral()) }
        keywords.forEach { add(uri, RDF.DCAT.keyword, it.toRDFLiteral()) }
    }

    companion object : RDFDeserializer<PFGAux> {
        private val log = LoggerFactory.getLogger(PFGAux::class.java)
        override val RDFType = RDF.SG.PFGAux.type

        override fun fromModel(uri: URI, model: Model): PFGAux {
            val id = model.getOneObjectOrNull(uri, RDF.DCAT.identifier)?.asLiteral()?.string
            val period = model.getOneObjectOrNull(uri, RDF.DCTERMS.Period)?.asLiteral()?.string
            val accessURL = model.getOneObjectOrNull(uri, RDF.DCAT.accessURL)?.asLiteral()?.string
            val strategicPriority = model.getOneObjectOrNull(uri, RDF.ITSMO.Priority)?.asLiteral()?.string
            val legislativeProposal = model.getOneObjectOrNull(uri, RDF.DCTERMS.Standard)?.asLiteral()?.string
            val buteHouseAgreementLink = model.getOneObjectOrNull(uri, RDF.DCTERMS.policy)?.asLiteral()?.string
            val completionDate = model.getOneObjectOrNull(uri, RDF.DBPEDIA.completionDate)?.asLiteral()?.string
            val ministerialPortfolio = model.getAllObjectsOrFail(uri, RDF.DBPEDIA.portfolio).map { it.asLiteral().string }
            val directorate = model.getAllObjectsOrFail(uri, RDF.ORG.OrganizationalUnit).map { it.asLiteral().string }
            val dG = model.getAllObjectsOrFail(uri, RDF.ORG.Organization).map { it.asLiteral().string }
            val leadOfficial = model.getAllObjectsOrFail(uri, RDF.ORG.headOf).map { it.asLiteral().string }
            val primaryOutcomes = model.getAllObjectsOrFail(uri, RDF.SKOS.subject).map { it.asLiteral().string }
            val secondaryOutcomes = model.getAllObjectsOrFail(uri, RDF.SKOS.related).map { it.asLiteral().string }
            val policyTitle = model.getAllObjectsOrFail(uri, RDF.DCAT.title).map { it.asLiteral().string }
            val keywords = model.getAllObjectsOrFail(uri, RDF.DCAT.keyword).map { it.asLiteral().string }

            return PFGAux(uri, id, period, accessURL, completionDate, strategicPriority, legislativeProposal, buteHouseAgreementLink, ministerialPortfolio, directorate, dG, leadOfficial, primaryOutcomes, secondaryOutcomes, policyTitle, keywords)
        }

        // To detailed graph JSON
        fun toDetailedGraphJSON(pfgAux: PFGAux): Node {
            return Node(
                name = pfgAux.uri.value,
                children = listOf(
                    Node(
                        name = "${pfgAux.period}_${pfgAux.id}",
                        children = listOf(
                            Node(name = "id", children = stringToNode(pfgAux.id)),
                            Node(name = "period", children = stringToNode(pfgAux.period)),
                            Node(name = "accessURL", children = stringToNode(pfgAux.accessURL)),
                            Node(name = "strategicPriority", children = stringToNode(pfgAux.strategicPriority)),
                            Node(name = "legislativeProposal", children = stringToNode(pfgAux.legislativeProposal)),
                            Node(name = "buteHouseAgreementLink", children = stringToNode(pfgAux.buteHouseAgreementLink)),
                            Node(name = "completionDate", children = stringToNode(pfgAux.completionDate)),
                            Node(name = "ministerialPortfolio", children = listToNodes(pfgAux.ministerialPortfolio)),
                            Node(name = "directorate", children = listToNodes(pfgAux.directorate)),
                            Node(name = "dG", children = listToNodes(pfgAux.dG)),
                            Node(name = "leadOfficial", children = listToNodes(pfgAux.leadOfficial)),
                            Node(name = "primaryOutcomes", children = listToNodes(pfgAux.primaryOutcomes)),
                            Node(name = "secondaryOutcomes", children = listToNodes(pfgAux.secondaryOutcomes)),
                            Node(name = "policyTitle", children = listToNodes(pfgAux.policyTitle)),
                            Node(name = "keywords", children = listToNodes(pfgAux.keywords))
                        )
                    )
                )
            )
        }

        // Forced graph
        fun toForcedGraphJSONAll(pfgAuxs: List<PFGAux>, searchTerms: List<String>? = emptyList(), limit: Int? = null): ForcedGraph {
            val docs = limit?.let { pfgAuxs.take(it) } ?: pfgAuxs
            val allGraphs = docs.map { toForcedGraphJSON(it, searchTerms) }
            return ForcedGraph(
                nodes = allGraphs.flatMap { it?.nodes ?: emptyList() }.filter { it?.name?.isNotEmpty() == true }.distinct(),
                links = allGraphs.flatMap { it?.links ?: emptyList() }.distinct(),
                categories = allGraphs.flatMap { it?.categories ?: emptyList() }.distinct()
            )
        }
        fun toForcedGraphJSON(pfgAux: PFGAux, searchTerms: List<String>? = emptyList()): ForcedGraph? {

            // Nodes
            val docId = pfgAux.directorate.firstOrNull() ?: "***NO DIRECTORATE***"
            val doc = ForcedNode(id = docId, name = docId, symbolSize = 55, value = "Directorate")
            val ministerialPortfolio = ForcedNode(
                id = "${PFGAux::ministerialPortfolio.name}|${pfgAux.ministerialPortfolio}",
                name = pfgAux.ministerialPortfolio.firstOrNull(),
                symbolSize = 20,
                value = PFGAux::ministerialPortfolio.name,
                category = 0
            )
//            val directorate = ForcedNode(
//                id = "${PFGAux::directorate.name}|${pfgAux.directorate}",
//                name = pfgAux.directorate.firstOrNull(),
//                symbolSize = 40,
//                value = PFGAux::directorate.name,
//                category = 3
//            )
            val leadOfficial = ForcedNode(
                id = "${PFGAux::leadOfficial.name}|${pfgAux.leadOfficial}",
                name = pfgAux.leadOfficial.firstOrNull(),
                symbolSize = 20,
                value = PFGAux::leadOfficial.name,
                category = 0
            )
            val period = ForcedNode(
                id = "${PFGAux::period.name}|${pfgAux.period}",
                name = pfgAux.period,
                symbolSize = 20,
                value = PFGAux::period.name,
                category = 0
            )
            val policyTitle = ForcedNode(
                id = "${PFGAux::policyTitle.name}|${pfgAux.policyTitle}",
                name = pfgAux.policyTitle.firstOrNull(),
                symbolSize = 20,
                value = PFGAux::policyTitle.name,
                category = 0
            )
            val strategicPriority = ForcedNode(
                id = "${PFGAux::strategicPriority.name}|${pfgAux.strategicPriority}",
                name = pfgAux.strategicPriority,
                symbolSize = 20,
                value = PFGAux::strategicPriority.name,
                category = 0
            )
            val legislativeProposal = ForcedNode(
                id = "${PFGAux::legislativeProposal.name}|${pfgAux.legislativeProposal}",
                name = pfgAux.legislativeProposal,
                symbolSize = 20,
                value = PFGAux::legislativeProposal.name,
                category = 0
            )
            val dG = ForcedNode(
                id = "${PFGAux::dG.name}|${pfgAux.dG}",
                name = pfgAux.dG.firstOrNull(),
                symbolSize = 20,
                value = PFGAux::dG.name,
                category = 0
            )
            val buteHouseAgreementLink = ForcedNode(
                id = "${PFGAux::buteHouseAgreementLink.name}|${pfgAux.buteHouseAgreementLink}",
                name = pfgAux.buteHouseAgreementLink,
                symbolSize = 20,
                value = PFGAux::buteHouseAgreementLink.name,
                category = 0
            )
            val completionDate = ForcedNode(
                id = "${PFGAux::completionDate.name}|${pfgAux.completionDate}",
                name = pfgAux.completionDate,
                symbolSize = 20,
                value = PFGAux::completionDate.name,
                category = 0
            )

            val primaryOutcomesNodes = pfgAux.primaryOutcomes.map { primaryOutcome ->
                ForcedNode(
                    id = "${PFGAux::primaryOutcomes.name}|${primaryOutcome}",
//                    id = "npfOutcome|${primaryOutcome}",
                    name = primaryOutcome,
                    symbolSize = 36,
                    value = "Primary Outcome",
                    category = 1
                )
            }
            val secondaryOutcomesNodes = pfgAux.secondaryOutcomes.map { secondaryOutcome ->
                ForcedNode(
                    id = "${PFGAux::secondaryOutcomes.name}|${secondaryOutcome}",
//                    id = "npfOutcome|${secondaryOutcome}",
                    name = secondaryOutcome,
                    symbolSize = 36,
                    value = "Secondary Outcome",
                    category = 2
                )
            }
            val keywordsNodes = pfgAux.keywords.map { keyword ->
                ForcedNode(
                    id = "${PFGAux::keywords.name}|${keyword}",
                    name = keyword,
                    symbolSize = 31,
                    value = "keyword",
                    category = 3
                )
            }

            // Links
            val doc2propertyLinks = listOf(
                ForcedLink(source = docId, target = ministerialPortfolio.id),
//                ForcedLink(source = docId, target = directorate.id),
                ForcedLink(source = docId, target = leadOfficial.id),
                ForcedLink(source = docId, target = period.id),
                ForcedLink(source = docId, target = policyTitle.id),
                ForcedLink(source = docId, target = strategicPriority.id),
                ForcedLink(source = docId, target = legislativeProposal.id),
                ForcedLink(source = docId, target = dG.id),
                ForcedLink(source = docId, target = buteHouseAgreementLink.id),
                ForcedLink(source = docId, target = completionDate.id),
            )
            val primaryOutcomesLinks = pfgAux.primaryOutcomes.flatMap { primaryOutcome ->
                listOf(
                    ForcedLink(source = docId, target = "${PFGAux::primaryOutcomes.name}|${primaryOutcome}")
                )
            }

            val secondaryOutcomesLinks = pfgAux.secondaryOutcomes.flatMap { secondaryOutcome ->
                listOf(
                    ForcedLink(source = docId, target = "${PFGAux::secondaryOutcomes.name}|${secondaryOutcome}")
                )
            }
            val keywordsLinks = pfgAux.keywords.flatMap { keyword ->
                listOf(
                    ForcedLink(source = docId, target = "${PFGAux::keywords.name}|${keyword}")
                )
            }

            val nodes = listOf(
                doc,
                ministerialPortfolio,
//                directorate,
                leadOfficial,
                period,
                policyTitle,
                strategicPriority,
                legislativeProposal,
                dG,
                buteHouseAgreementLink,
                completionDate
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
                        categories = listOf(
                            ForcedCategory(name = "Other"),
                            ForcedCategory(name = "Primary outcomes"),
                            ForcedCategory(name = "Secondary outcomes"),
                            ForcedCategory(name = "Keywords"),
                        )
                    )
                } else {
                    null
                }
            }

            return ForcedGraph(
                nodes = nodes,
                links = links,
                categories = listOf(
                    ForcedCategory(name = "Other"),
                    ForcedCategory(name = "Primary outcomes"),
                    ForcedCategory(name = "Secondary outcomes"),
//                    ForcedCategory(name = "Directorate"),
                    ForcedCategory(name = "Keywords"),
                )
            )
        }
    }
}

