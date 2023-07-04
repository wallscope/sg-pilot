package com.sg.app.rdf

import org.apache.jena.query.ParameterizedSparqlString
import org.apache.jena.query.QuerySolution
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.rdfconnection.RDFConnectionRemote
import org.apache.jena.rdfconnection.RDFConnectionRemoteBuilder
import org.apache.jena.riot.RDFFormat
import org.apache.jena.update.UpdateRequest
import org.slf4j.LoggerFactory

private val endpoint = System.getenv("TRIPLESTORE") ?: "http://localhost:8030/ds"
//private val endpoint = System.getenv("TRIPLESTORE") ?: "http://dev.verinote.net:8030/ds"
private val defaultGraph = System.getenv("GRAPH") ?: RDF.SG.graph.value
private val conn: RDFConnectionRemoteBuilder = RDFConnectionRemote.create()
    .destination(endpoint)
    .queryEndpoint("sparql")
    .updateEndpoint("update")
    .gspEndpoint("data")
    .triplesFormat(RDFFormat.TURTLE)

object TriplestoreUtil {
    private val log = LoggerFactory.getLogger(TriplestoreUtil::class.java)

    fun uploadIfNotExists(ent: RDFWritable, graph: String? = defaultGraph, prop: URI? = null, obj: RDFNode? = null) {
        try {
            if (!checkIfExists(ent.uri, prop, obj)) {
                conn.build().load(graph, ent.toRDF())
            }
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }

    fun upload(ent: RDFWritable, graph: String? = defaultGraph) = try {
        conn.build().load(graph, ent.toRDF())
    } catch (e: Exception) {
        log.error(e.stackTraceToString())
    }

    fun upload(model: Model, graph: String? = defaultGraph) = try {
        conn.build().load(graph, model)
    } catch (e: Exception) {
        log.error(e.stackTraceToString())
    }

    fun checkIfExists(ent: URI, prop: URI? = null, obj: RDFNode? = null): Boolean {
        val query = pss {
            commandText = "ASK { ?entity ?prop ?obj}"
            setParam("entity", ent.res)
            // This can be set in case the ASK needs to be customised
            // If left blank the query will match for any triple associated with ?ent
            prop?.let { setParam("prop", it.prop) }
            obj?.let { setParam("obj", it) }
        }
        return conn.build().queryAsk(query.asQuery())
    }

    fun sendAsk(q: ParameterizedSparqlString): Boolean {
        return conn.build().queryAsk(q.asQuery())
    }

    fun sendUpdate(upd: UpdateRequest) {
        try {
            conn.build().update(upd)
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
    }

    fun sendConstruct(q: ParameterizedSparqlString): Model? {
        try {
            return conn.build().queryConstruct(q.asQuery())
        } catch (e: Exception) {
            log.error(e.stackTraceToString())
        }
        return null
    }

    fun sendSelect(query: ParameterizedSparqlString, callback: (QuerySolution) -> Unit) {
        return conn.build().querySelect(query.asQuery(), callback)
    }
}
