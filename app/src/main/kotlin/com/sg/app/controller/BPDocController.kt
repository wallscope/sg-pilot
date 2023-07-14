package com.sg.app.controller

import com.sg.app.dao.BPDocDAO
import com.sg.app.rdf.RDF
import com.sg.app.rdf.TriplestoreUtil
import com.sg.app.model.BPDoc
import com.sg.app.model.BPDoc.Companion.mapper
import com.sg.app.model.BPDoc.Companion.toDetailedGraphJSON
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@CrossOrigin
@RestController
class BPDocController {
    private val log = LoggerFactory.getLogger(BPDocController::class.java)

    // List
    @GetMapping("/api/bpdoc/list")
    @ResponseBody
    fun getBPDocs(): List<BPDoc> {
        return BPDocDAO.getAll()
    }

    // Get a BPDoc as-is
    @GetMapping("/api/bpdoc/{id}")
    @ResponseBody
    fun getBPDoc(@PathVariable id: String): BPDoc? {
        val uri = RDF.SG.BPDoc.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no BPDoc with id: $id")
        }

        return BPDocDAO.getOne(uri)
    }

    // Get a BPDoc processed detailed graph data
    @GetMapping("/api/bpdoc/graph-detailed/{id}")
    @ResponseBody
    fun getBPDocDetailedGraphData(@PathVariable id: String): String {
        val uri = RDF.SG.BPDoc.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no BPDoc with id: $id")
        }

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(BPDocDAO.getOne(uri)
            ?.let { toDetailedGraphJSON(it) })
    }
}
