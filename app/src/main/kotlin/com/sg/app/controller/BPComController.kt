package com.sg.app.controller

import com.sg.app.dao.BPComDAO
import com.sg.app.dao.BPDocDAO
import com.sg.app.rdf.RDF
import com.sg.app.rdf.TriplestoreUtil
import com.sg.app.model.BPCom
import com.sg.app.model.BPCom.Companion.toDetailedGraphJSON
import com.sg.app.model.mapper
import com.sg.app.rdf.URI
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

data class BPComOverview(var uri: URI?, var title: String? = "", var docType: String? = "", var dG: String? = "", var directorate: String? = "", var primaryOutcomes: List<String>? = emptyList(), var secondaryOutcomes: List<String>? = emptyList(), var keywords: List<String>? = emptyList())

@CrossOrigin
@RestController
class BPComController {
    private val log = LoggerFactory.getLogger(BPComController::class.java)

    // List
    @GetMapping("/api/bpcom/list")
    @ResponseBody
    fun getBPComs(): List<BPCom> {
        return BPComDAO.getAll()
    }

    // Get a BPCom as-is
    @GetMapping("/api/bpcom/{id}")
    @ResponseBody
    fun getBPCom(@PathVariable id: String): BPCom? {
        val uri = RDF.SG.BPCom.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no BPCom with id: $id")
        }

        return BPComDAO.getOne(uri)
    }

    // List table overview
    @GetMapping("/api/bpcom/overview/list")
    @ResponseBody
    fun getBPComsOverview(): List<BPComOverview> {
        val bpDocs = BPDocDAO.getAll()

        return BPComDAO.getAll().map {
            val bpDoc = bpDocs.firstOrNull { bpDoc -> bpDoc.filename == it.filename }

            BPComOverview(
                uri = it.uri,
                title = it.commitment,
                docType = it.uri.value.split("/SG/")[1].split("/")[0],
                dG = bpDoc?.dG?.getOrElse(0) { "" },
                directorate = bpDoc?.directorate?.getOrElse(0) { "" },
                primaryOutcomes = it.primaryOutcomes,
                secondaryOutcomes = it.secondaryOutcomes,
                keywords = it.keywords
            )
        }
    }

    // Get a BPCom processed detailed graph data
    @GetMapping("/api/bpcom/graph-detailed/{id}")
    @ResponseBody
    fun getBPComDetailedGraphData(@PathVariable id: String): String {
        val uri = RDF.SG.BPCom.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no BPCom with id: $id")
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(BPComDAO.getOne(uri)
            ?.let { toDetailedGraphJSON(it) })
    }

    // Forced graph
    // BPComs - forced graph, all docs
    @GetMapping("/api/bpcom/forcedgraph/list")
    @ResponseBody
    fun getBPComsForcedList(): String {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            BPCom.toForcedGraphJSONAll(BPComDAO.getAll())
        )
    }

    // BPComs - forced graph, one doc
    @GetMapping("/api/bpcom/forcedgraph/{id}")
    @ResponseBody
    fun getBPComForcedGraphData(@PathVariable id: String): String {
        val uri = RDF.SG.BPCom.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no BPCom with id: $id")
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            BPComDAO.getOne(uri)
            ?.let { BPCom.toForcedGraphJSON(it) })
    }
}
