package com.sg.app.controller

import com.sg.app.dao.PFGDocDAO
import com.sg.app.rdf.RDF
import com.sg.app.rdf.TriplestoreUtil
import com.sg.app.model.PFGDoc
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

    // Get a PFGDoc
    @GetMapping("/api/pfgdoc/{id}")
    @ResponseBody
    fun getPFGDoc(@PathVariable id: String): PFGDoc? {
        val uri = RDF.SG.PFGDoc.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no PFGDoc with id: $id")
        }

        return PFGDocDAO.getOne(uri)
    }
}
