package com.sg.app.controller

import com.sg.app.dao.PFGDocDAO
import com.sg.app.rdf.RDF
import com.sg.app.rdf.TriplestoreUtil
import com.sg.app.model.PFGDoc
import com.sg.app.model.PFGDoc.Companion.toDetailedGraphJSON
import com.sg.app.model.PFGDoc.Companion.toForcedGraphJSON
import com.sg.app.model.PFGDoc.Companion.toForcedGraphJSONAll
import com.sg.app.model.PFGDoc.Companion.toSankeyGraphJSON
import com.sg.app.model.PFGDoc.Companion.toSankeyGraphJSONAll
import com.sg.app.model.mapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@CrossOrigin
@RestController
class PFGDocController {
    private val log = LoggerFactory.getLogger(PFGDocController::class.java)

    // List
    @GetMapping("/api/pfgdoc/list")
    @ResponseBody
    fun getPFGDocs(): List<PFGDoc> {
        return PFGDocDAO.getAll()
    }

    // Get a PFGDoc as-is
    @GetMapping("/api/pfgdoc/{id}")
    @ResponseBody
    fun getPFGDoc(@PathVariable id: String): PFGDoc? {
        val uri = RDF.SG.PFGDoc.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no PFGDoc with id: $id")
        }

        return PFGDocDAO.getOne(uri)
    }

    // Sankey chart
    // PFGDocs - sankey chart, all docs
    @GetMapping("/api/pfgdoc/sankey/list")
    @ResponseBody
    fun getPFGDocsSankeyList(): String {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(toSankeyGraphJSONAll(PFGDocDAO.getAll(), 15))
    }

    // PFGDocs - sankey chart, one doc
    @GetMapping("/api/pfgdoc/sankey/{id}")
    @ResponseBody
    fun getPFGDocSankeyGraphData(@PathVariable id: String): String {
        val uri = RDF.SG.PFGDoc.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no PFGDoc with id: $id")
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(PFGDocDAO.getOne(uri)
            ?.let { toSankeyGraphJSON(it) })
    }

    // Forced graph
    // PFGDocs - forced graph, all docs
    @GetMapping("/api/pfgdoc/forcedgraph/list")
    @ResponseBody
    fun getPFGDocsForcedList(): String {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(toForcedGraphJSONAll(PFGDocDAO.getAll()))
    }

    // PFGDocs - forced graph, one doc
    @GetMapping("/api/pfgdoc/forcedgraph/{id}")
    @ResponseBody
    fun getPFGDocForcedGraphData(@PathVariable id: String): String {
        val uri = RDF.SG.PFGDoc.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no PFGDoc with id: $id")
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(PFGDocDAO.getOne(uri)
            ?.let { toForcedGraphJSON(it) })
    }

    // Get a PFGDoc processed detailed graph data
    @GetMapping("/api/pfgdoc/graph-detailed/{id}")
    @ResponseBody
    fun getPFGDocDetailedGraphData(@PathVariable id: String): String {
        val uri = RDF.SG.PFGDoc.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no PFGDoc with id: $id")
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(PFGDocDAO.getOne(uri)
            ?.let { toDetailedGraphJSON(it) })
    }
}
