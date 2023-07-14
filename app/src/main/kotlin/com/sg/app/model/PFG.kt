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

// Define a data class for the JSON
data class Node(val name: String, val children: List<Node>? = null)

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
    var completionDate: LocalDateTime?,
    var keywords: List<String>,
) : RDFWritable {
    fun withURI(uri: URI): PFGDoc = PFGDoc(uri, this.filename, this.ministerialPortfolio, this.directorate, this.dG, this.unitBranch, this.leadOfficial, this.scsClearance, this.fbpClearance, this.primaryOutcomes, this.secondaryOutcomes, this.portfolioCoordinator, this.policyTitle, this.completionDate,/* this.npfList,*/ this.keywords)

    override fun toRDF() = jenaModel {
        add(uri.res, RDF.type.prop, RDF.SG.PFGDoc.type.res)

        add(uri.res, RDF.DCAT.Resource.prop, filename.toRDFLiteral())
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
        policyTitle.forEach { add(uri, RDF.DCTERMS.title, it.toRDFLiteral()) }
        add(uri.res, RDF.DBPEDIA.completionDate.prop, DateTimeFormatter.ISO_DATE_TIME.format(completionDate).toRDFLiteral(
            XSDDatatype.XSDdateTime))
        keywords.forEach { add(uri, RDF.SG.keywords, it.toRDFLiteral()) }
    }

    companion object : RDFDeserializer<PFGDoc> {
        private val log = LoggerFactory.getLogger(PFGDoc::class.java)
        override val RDFType = RDF.SG.PFGDoc.type

        override fun fromModel(uri: URI, model: Model): PFGDoc {
            val filename = model.getOneObjectOrFail(uri.res, RDF.DCAT.Resource.prop).asLiteral().string
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
            val policyTitle = model.getAllObjectsOrFail(uri, RDF.DCTERMS.title).map { it.asLiteral().string }
            val completionDate = try {
                model.getOneObjectOrFail(uri, RDF.DBPEDIA.completionDate)
                    .asLiteral().string.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }
            } catch (e: Exception) {
                null
            }
            val keywords = model.getAllObjectsOrFail(uri, RDF.SG.keywords).map { it.asLiteral().string }

            return PFGDoc(uri, filename, ministerialPortfolio, directorate, dG, unitBranch, leadOfficial, scsClearance, fbpClearance, primaryOutcomes, secondaryOutcomes, portfolioCoordinator, policyTitle, completionDate, keywords)
        }

        // Create an instance of the ObjectMapper with the Kotlin module
        val mapper: ObjectMapper = ObjectMapper().registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )

        // Define a function to convert a list of strings to a list of nodes
        private fun listToNodes(list: List<String>): List<Node> {
            return list.map { Node(it) }
        }

        // Define a function to convert the object to the JSON
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
                            Node(name = "completionDate", children = listOf(Node(pfgDoc.completionDate!!.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))))),
                            Node(name = "keywords", children = listToNodes(pfgDoc.keywords))
                        )
                    )
                )
            )
        }
    }
}
