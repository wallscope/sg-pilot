package com.sg.app.rdf

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.apache.jena.datatypes.RDFDatatype
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.query.ParameterizedSparqlString
import org.apache.jena.rdf.model.*
import org.slf4j.LoggerFactory

// Define prefixes to make it easier to use them in the code
// Hierarchy based on the uris
// Types are capitalised
// properties are lower case
object RDF {
    val uri = URI("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    val type = uri + "type"

    object XSD {
        val uri = URI("http://www.w3.org/2001/XMLSchema#")
    }

    object RDFS {
        val uri = URI("http://www.w3.org/2000/01/rdf-schema#")
        val label = uri + "label"
        val comment = uri + "comment"
    }

    object WALLS {
        val uri = URI("http://wallscope.co.uk/ontology/")
        val resource = URI("http://wallscope.co.uk/resource/")
        val graph = URI("http://wallscope.co.uk/graph")
    }

    object DCT {
        val uri = URI("http://purl.org/dc/terms/")
        val subject = uri + "subject"
        val relation = uri + "relation"
        val created = uri + "created"
        val creator = uri + "creator"
        val modified = uri + "modified"
    }

    object FOAF {
        val uri = URI("http://xmlns.com/foaf/0.1/")
        val birthday = uri + "birthday"
        val firstName = uri + "first_name"
        val lastName = uri + "last_name"
        val gender = uri + "gender"
        val depiction = uri + "depiction"
        val homepage = uri + "homepage"
    }

    object GEOPOS {
        val uri = URI("http://www.w3.org/2003/01/geo/wgs84_pos#")
        val lat = uri + "lat"
        val long = uri + "long"
    }

    object SCHEMA {
        val uri = URI("http://schema.org/")
        val Person = uri + "Person"
        val Place = uri + "Place"
        val Organization = uri + "Organization"
        val text = uri + "text"
        val address = uri + "address"
        val contactPoint = uri + "contactPoint"
        val email = uri + "email"
        val legalName = uri + "legalName"
        val numberOfEmployees = uri + "numberOfEmployees"
        val telephone = uri + "telephone"
        val description = uri + "description"
        val url = uri + "url"
        val skills = uri + "skills"
        val video = uri + "video"
        val logo = uri + "logo"
        val foundingDate = uri + "foundingDate"
        val jobTitle = uri + "jobTitle"
        val seeks = uri + "seeks"
    }

    object DBPEDIA {
        val uri = URI("http://dbpedia.org/ontology/")
        val code = uri + "code"
        val city = uri + "city"
        val region = uri + "region"
        val country = uri + "country"
        val focus = uri + "focus"
        val certification = uri + "certification"
        val established = uri + "established"
        val councilArea = uri + "councilArea"
        val headquarters = uri + "headquarter"
        val dateLastUpdated = uri + "dateLastUpdated"
        val completionDate = uri + "completionDate"
        val portfolio = uri + "portfolio"
        val projectCoordinator = uri + "projectCoordinator"
    }

    object ORG {
        val uri = URI("http://www.w3.org/ns/org#")
        val Organization = uri + "Organization"
        val OrganizationalUnit = uri + "OrganizationalUnit"
        val hasSubOrganization = uri + "hasSubOrganization"
        val hasMember = uri + "hasMember"
        val hasUnit = uri + "hasUnit"
        val headOf = uri + "headOf"
    }

    object DCTERMS {
        val uri = URI("http://purl.org/dc/terms/")
        val issued = uri + "issued"
        val title = uri + "title"
        val Period = uri + "Period"
        val Standard = uri + "Standard"
    }

    object DCAT {
        val uri = URI("http://www.w3.org/ns/dcat#")
        val Resource = uri + "Resource"
        val title = uri + "title"

        val accessURL = uri + "accessURL"
        val identifier = uri + "identifier"
        val theme = uri + "theme"
        val keyword = uri + "keyword"

        object Catalog {
            val type = uri + "Catalog"
        }
    }

    object FRAPO {
        val uri = URI("http://purl.org/cerif/frapo#")
        val BudgetedAmount = uri + "BudgetedAmount"
        val Expenditure = uri + "Expenditure"
        val ExpenditureToDate = uri + "ExpenditureToDate"
        val Funding = uri + "Funding"
        val FundingProgramme = uri + "FundingProgramme"
        val ProjectBudget = uri + "ProjectBudget"
    }

    object ITSMO {
        val uri = URI("http://ontology.it/itsmo/v1#")
        val Priority = uri + "Priority"
        val Project = uri + "Project"
        val hasProjectOwner = uri + "hasProjectOwner"
    }

    object SKOS {
        val uri = URI("http://www.w3.org/2004/02/skos/core#")
        val Concept = uri + "Concept"
        val Collection = uri + "Collection"
        val prefLabel = uri + "prefLabel"
        val broader = uri + "broader"
        val narrower = uri + "narrower"
        val related = uri + "related"
        val subject = uri + "subject"
        val memberList = uri + "memberList"
    }

    object VCARD {
        val uri = URI("http://www.w3.org/2006/vcard/ns#")
        val fn = uri + "fn"
        val locality = uri + "locality"
        val postalCode = uri + "postalcode"
        val streetAddress = uri + "streetaddress"
    }

    object SG {
        val uri = WALLS.uri + "SG/"
        val id = WALLS.resource + "SG/"
        val graph = WALLS.graph + "/SG"

        val keywords = SG.uri + "keywords"

        object Organisation {
            val type = SG.uri + "Organisation"
            val uri = SG.uri + "Organisation."
            val id = SG.id + "Organisation/"

            val orgType = uri + "orgType"
        }
        object PFGDoc {
            val type = SG.uri + "PFGDoc"
            val uri = SG.uri + "PFGDoc."
            val id = SG.id + "PFGDoc/"

            val fbpClearance = uri + "fbpClearance"
            val scsClearance = uri + "scsClearance"
        }
        object PFGAux {
            val type = SG.uri + "PFGAux"
            val uri = SG.uri + "PFGAux."
            val id = SG.id + "PFGAux/"
        }

        object BPDoc {
            val type = SG.uri + "BPDoc"
            val uri = SG.uri + "BPDoc."
            val id = SG.id + "BPDoc/"

        }
        object BPCom {
            val type = SG.uri + "BPCom"
            val uri = SG.uri + "BPCom."
            val id = SG.id + "BPCom/"

        }
    }
}

val PREFIXES = mapOf(
    "rdfs" to RDF.RDFS.uri,
    "sg" to RDF.SG.uri,
    "skos" to RDF.SKOS.uri,
    "geo" to RDF.GEOPOS.uri
).map { "PREFIX ${it.key}: <${it.value}>" }.joinToString("\n")

val WITH_STATEMENT = "\nWITH <${RDF.SG.graph}>\n"
val GRAPH_STATEMENT = "\nGRAPH <${RDF.SG.graph}>\n"

class LabeledURI(override val uri: URI, val label: String) : RDFWritable {

    override fun toRDF() = jenaModel {
        add(uri, RDF.RDFS.label, label.toRDFLiteral())
    }

    companion object : RDFDeserializer<LabeledURI?> {
        override val RDFType: URI = RDF.SG.uri + "LabeledURI"

        override fun fromModel(uri: URI, model: Model): LabeledURI {
            val label = model.getOneObjectOrFail(uri.res, RDF.RDFS.label.prop).asLiteral().string
            return LabeledURI(uri, label)
        }
    }
}

// Extension functions / helper classes
@JsonSerialize(using = URISerializer::class)
@JsonDeserialize(using = URIDeserializer::class)
class URI(val value: String) {
    constructor(res: Resource) : this(res.uri)

    operator fun plus(rs: String) = URI(value + rs)
    operator fun plus(rs: URI) = URI(value + rs.value)
    val res
        get() = ResourceFactory.createResource(value)!!
    val prop
        get() = ResourceFactory.createProperty(value)!!

    override fun toString() = value
}

class URISerializer : JsonSerializer<URI>() {
    override fun serialize(src: URI?, gen: JsonGenerator, serializers: SerializerProvider?) {
        src?.let { gen.writeString(it.value) }
    }
}

class URIDeserializer : JsonDeserializer<URI>() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): URI {
        return URI(p.readValueAs(String::class.java))
    }
}

data class RDFTriple(val s: Resource? = null, val p: Property? = null, val o: RDFNode? = null) {
    val hasNulls
        get() = s == null || p == null || o == null

    fun toStatement(): Statement? {
        if (!this.hasNulls) {
            return ResourceFactory.createStatement(s, p, o)
        }
        return null
    }
}

fun Resource.asURI() = URI(this.uri)
fun Property.asURI() = URI(this.uri)
fun String.asURI() = URI(this)
fun RDFNode.asURI() = URI(this.asResource())

fun jenaModel(block: Model.() -> Unit): Model = ModelFactory.createDefaultModel().apply(block)
fun pss(block: ParameterizedSparqlString.() -> Unit) = ParameterizedSparqlString().apply(block)

class RDFPropertyNotFoundException(override val message: String) : Exception(message)

fun Model.getOneObjectOrFail(uri: URI? = null, prop: URI? = null): RDFNode = getOneObjectOrFail(uri?.res, prop?.prop)
fun Model.getOneObjectOrFail(uri: Resource? = null, prop: Property? = null): RDFNode = try {
    this.listObjectsOfProperty(uri, prop).toList().first()
} catch (e: Exception) {
    throw RDFPropertyNotFoundException("property <${prop?.uri}> not found for URI: ${uri?.uri}")
}

fun Model.getAllObjectsOrFail(uri: URI? = null, prop: URI? = null): List<RDFNode> =
    getAllObjectsOrFail(uri?.res, prop?.prop)

fun Model.getAllObjectsOrFail(uri: Resource? = null, prop: Property? = null): List<RDFNode> = try {
    this.listObjectsOfProperty(uri, prop).toList()
} catch (e: Exception) {
    throw RDFPropertyNotFoundException("property <${prop?.uri}> not found for URI: ${uri?.uri}")
}

fun Model.getOneObjectOrNull(uri: URI? = null, prop: URI? = null): RDFNode? = getOneObjectOrNull(uri?.res, prop?.prop)
fun Model.getOneObjectOrNull(uri: Resource? = null, prop: Property? = null): RDFNode? {
    return this.listObjectsOfProperty(uri, prop).toList().firstOrNull()
}

fun Model.add(s: URI?, p: URI?, o: RDFNode?): Model = this.add(s?.res, p?.prop, o)

fun Model.list(s: Resource? = null, p: Property? = null, o: RDFNode? = null): List<Statement> =
    this.listStatements(s, p, o).toList()

interface RDFWritable {
    val uri: URI
    fun toRDF(): Model
}

interface RDFDeserializer<ToType> {
    val RDFType: URI
    fun fromModel(uri: URI, model: Model): ToType?
}

fun <T> RDFDeserializer<T>.buildAllFromModel(m: Model, type: URI? = this.RDFType): List<T> =
    m.listSubjectsWithProperty(RDF.type.prop, type?.res).toList().mapNotNull { this.fromModel(URI(it), m) }

fun <T> RDFDeserializer<T>.buildOneFromModel(m: Model, type: URI? = this.RDFType): T? =
    m.listSubjectsWithProperty(RDF.type.prop, type?.res).toList().firstOrNull()?.let { this.fromModel(URI(it), m) }

fun <T> RDFDeserializer<T>.buildFromModel(m: Model, uris: List<Resource>, type: URI? = this.RDFType): List<T> =
    m.listSubjectsWithProperty(RDF.type.prop, type?.res).toList().filter { uris.contains(it) }
        .mapNotNull { this.fromModel(URI(it), m) }

fun String.toRDFLiteral() = ResourceFactory.createStringLiteral(this)!!
fun Boolean.toRDFLiteral() = ResourceFactory.createTypedLiteral(this.toString(), XSDDatatype.XSDboolean)!!
fun String.toRDFLiteral(type: RDFDatatype) = ResourceFactory.createTypedLiteral(this, type)!!
fun Int.toRDFLiteral() = ResourceFactory.createTypedLiteral(this)

fun String.trimOptionals() = this.replace(Regex("OPTIONAL\\s?\\{"), "").replace("}#END OPTIONAL", "")
