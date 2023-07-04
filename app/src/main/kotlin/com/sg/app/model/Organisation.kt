package com.sg.app.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.sg.app.rdf.*
import org.apache.jena.rdf.model.Model
import org.slf4j.LoggerFactory
import java.util.*

data class Organisation(
    @JsonProperty("uri") override var uri: URI,
    var name: String,
    var description: String,
    var orgType: URI,
    var lat: String,
    var long: String,
    var locality: String,
    var postalCode: String,
    var streetAddress: String,
    var homepage: String,
    var workgroups: List<LabeledURI>,
    var labels: List<URI>
) : RDFWritable {
    fun withURI(uri: URI): Organisation = Organisation(uri, this.name, this.description, this.orgType, this.lat, this.long, this.locality, this.postalCode, this.streetAddress ,this.homepage, this.workgroups, this.labels)

    override fun toRDF() = jenaModel {
        add(uri.res, RDF.type.prop, RDF.SG.Organisation.type.res)

        add(uri.res, RDF.RDFS.label.prop, name.toRDFLiteral())
        add(uri.res, RDF.SCHEMA.description.prop, description)
        add(uri.res, RDF.SG.Organisation.orgType.prop, orgType.res)
        add(uri.res, RDF.GEOPOS.lat.prop, lat.toRDFLiteral())
        add(uri.res, RDF.GEOPOS.long.prop, long.toRDFLiteral())
        add(uri.res, RDF.VCARD.locality.prop, locality.toRDFLiteral())
        add(uri.res, RDF.VCARD.postalCode.prop, postalCode.toRDFLiteral())
        add(uri.res, RDF.VCARD.streetAddress.prop, streetAddress.toRDFLiteral())
        add(uri.res, RDF.FOAF.homepage.prop, homepage.toRDFLiteral())
        workgroups.forEach { add(uri, RDF.SG.Workgroup.type, it.uri.res) }
        labels.forEach { add(uri, RDF.SKOS.related, it.res) }
    }

    companion object : RDFDeserializer<Organisation> {
        private val log = LoggerFactory.getLogger(Organisation::class.java)
        override val RDFType = RDF.SG.Organisation.type

        override fun fromModel(uri: URI, model: Model): Organisation {
            val name = model.getOneObjectOrFail(uri, RDF.RDFS.label).asLiteral().string
            val description = model.getOneObjectOrFail(uri.res, RDF.SCHEMA.description.prop).asLiteral().string
            val orgType = model.getOneObjectOrFail(uri.res, RDF.SG.Organisation.orgType.prop).asURI()
            val lat = model.getOneObjectOrFail(uri, RDF.GEOPOS.lat).asLiteral().string
            val long = model.getOneObjectOrFail(uri, RDF.GEOPOS.long).asLiteral().string
            val locality = model.getOneObjectOrFail(uri, RDF.VCARD.locality).asLiteral().string
            val postalCode = model.getOneObjectOrFail(uri, RDF.VCARD.postalCode).asLiteral().string
            val streetAddress = model.getOneObjectOrFail(uri, RDF.VCARD.streetAddress).asLiteral().string
            val homepage = model.getOneObjectOrFail(uri, RDF.FOAF.homepage).asLiteral().string
            val workgroups = model.getAllObjectsOrFail(uri, RDF.SG.Workgroup.type).map {
                val label = model.getOneObjectOrFail(it.asResource(), RDF.RDFS.label.prop).asLiteral().string
                LabeledURI(it.asURI(), label)
            }
            val labels = model.getAllObjectsOrFail(uri, RDF.SKOS.related).map { it.asURI() }

            return Organisation(uri, name, description, orgType, lat, long, locality, postalCode, streetAddress, homepage, workgroups, labels)
        }
    }
}

data class Workgroup(
    override var uri: URI,
    var name: String,
    var description: String,
    var organisation: LabeledURI,
    var moderators: List<String>,
    var participants: List<String>,
    //var datasets: List<URI>,
    var labels: List<URI>,
) : RDFWritable {
    override fun toRDF() = jenaModel {
        add(uri.res, RDF.type.prop, RDF.SG.Workgroup.type.res)
        add(uri.res, RDF.RDFS.label.prop, name)
        add(uri.res, RDF.SCHEMA.description.prop, description)
        add(uri.res, RDF.SG.Organisation.type.prop, organisation.uri.res)
        moderators.forEach { add(uri, RDF.SG.Workgroup.moderators, it.toRDFLiteral()) }
        participants.forEach { add(uri, RDF.SG.Workgroup.participants, it.toRDFLiteral()) }
        //datasets.forEach { add(uri, RDF.SG.Dataset.type, it.res) }
        labels.forEach { add(uri, RDF.SKOS.related, it.res) }
    }

    companion object : RDFDeserializer<Workgroup> {
        override val RDFType = RDF.SG.Workgroup.type

        override fun fromModel(uri: URI, model: Model): Workgroup {
            val name = model.getOneObjectOrFail(uri.res, RDF.RDFS.label.prop).asLiteral().string
            val description = model.getOneObjectOrFail(uri.res, RDF.SCHEMA.description.prop).asLiteral().string
            //val organisation = model.getOneObjectOrFail(uri.res, RDF.SG.Organisation.type.prop).asURI()
            val organisation = model.getOneObjectOrFail(uri, RDF.SG.Organisation.type).let {
                val label = model.getOneObjectOrFail(it.asResource(), RDF.RDFS.label.prop).asLiteral().string
                LabeledURI(it.asURI(), label)
            }
            val moderators = model.getAllObjectsOrFail(uri, RDF.SG.Workgroup.moderators).map { it.asLiteral().string }
            val participants = model.getAllObjectsOrFail(uri, RDF.SG.Workgroup.participants).map { it.asLiteral().string }
            //val datasets = model.getAllObjectsOrFail(uri, RDF.SG.Dataset.type).map { it.asURI() }
            val labels = model.getAllObjectsOrFail(uri, RDF.SKOS.related).map { it.asURI() }
            //return Workgroup(uri, name, description, organisation, moderators, participants, datasets, labels)
            return Workgroup(uri, name, description, organisation, moderators, participants, labels)
        }
    }
}