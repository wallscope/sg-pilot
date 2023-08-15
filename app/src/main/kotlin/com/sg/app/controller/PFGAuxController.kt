package com.sg.app.controller

import com.sg.app.dao.PFGAuxDAO
import com.sg.app.rdf.RDF
import com.sg.app.rdf.TriplestoreUtil
import com.sg.app.model.PFGAux
import com.sg.app.model.PFGAux.Companion.toDetailedGraphJSON
import com.sg.app.model.mapper
import com.sg.app.rdf.URI
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

data class PFGAuxOverview(var uri: URI?, var title: String? = "", var docType: String? = "", var dG: String? = "", var directorate: String? = "", var primaryOutcomes: List<String>? = emptyList(), var secondaryOutcomes: List<String>? = emptyList(), var keywords: List<String>? = emptyList())

@CrossOrigin
@RestController
class PFGAuxController {
    private val log = LoggerFactory.getLogger(PFGAuxController::class.java)

    // List
    @GetMapping("/api/pfgaux/list")
    @ResponseBody
    fun getPFGAuxs(): List<PFGAux> {
        return PFGAuxDAO.getAll()
    }

    // Get a PFGAux as-is
    @GetMapping("/api/pfgaux/{id}")
    @ResponseBody
    fun getPFGAux(@PathVariable id: String): PFGAux? {
        val uri = RDF.SG.PFGAux.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no PFGAux with id: $id")
        }

        return PFGAuxDAO.getOne(uri)
    }

    // List table overview
    @GetMapping("/api/pfgaux/overview/list")
    @ResponseBody
    fun getPFGAuxsOverview(): List<PFGAuxOverview> {
        return PFGAuxDAO.getAll().map {
            PFGAuxOverview(
                uri = it.uri,
                title = it.policyTitle.getOrElse(0) { "" },
                docType = it.uri.value.split("/SG/")[1].split("/")[0],
                dG = it.dG.getOrElse(0) { "" },
                directorate = it.directorate.getOrElse(0) { "" },
                primaryOutcomes = it.primaryOutcomes,
                secondaryOutcomes = it.secondaryOutcomes,
                keywords = it.keywords
            )
        }
    }

    // Get a PFGAux processed detailed graph data
    @GetMapping("/api/pfgaux/graph-detailed/{id}")
    @ResponseBody
    fun getPFGAuxDetailedGraphData(@PathVariable id: String): String {
        val uri = RDF.SG.PFGAux.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no PFGAux with id: $id")
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(PFGAuxDAO.getOne(uri)
            ?.let { toDetailedGraphJSON(it) })
    }

    // Forced graph
    // PFGAuxs - forced graph, all docs
    @GetMapping("/api/pfgaux/forcedgraph/list")
    @ResponseBody
    fun getPFGAuxsForcedList(): String {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            PFGAux.toForcedGraphJSONAll(PFGAuxDAO.getAll(), "",15)
        )
    }

    // PFGAuxs - forced graph, one doc
    @GetMapping("/api/pfgaux/forcedgraph/{id}")
    @ResponseBody
    fun getPFGAuxForcedGraphData(@PathVariable id: String): String {
        val uri = RDF.SG.PFGAux.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no PFGAux with id: $id")
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            PFGAuxDAO.getOne(uri)
            ?.let { PFGAux.toForcedGraphJSON(it) })
    }
}
