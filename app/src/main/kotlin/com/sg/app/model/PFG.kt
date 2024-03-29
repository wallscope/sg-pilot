package com.sg.app.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.sg.app.rdf.*
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.Model
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    }
}

