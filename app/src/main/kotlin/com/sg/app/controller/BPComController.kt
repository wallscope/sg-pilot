package com.sg.app.controller

import com.sg.app.dao.BPComDAO
import com.sg.app.rdf.RDF
import com.sg.app.rdf.TriplestoreUtil
import com.sg.app.model.BPCom
import com.sg.app.model.BPCom.Companion.toDetailedGraphJSON
import com.sg.app.model.mapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

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
}
