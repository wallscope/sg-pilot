package com.sg.app.controller

import com.sg.app.dao.PFGAuxDAO
import com.sg.app.rdf.RDF
import com.sg.app.rdf.TriplestoreUtil
import com.sg.app.model.PFGAux
import com.sg.app.model.PFGAux.Companion.toDetailedGraphJSON
import com.sg.app.model.mapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

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
}
