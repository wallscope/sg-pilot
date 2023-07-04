package com.sg.app.controller

import com.sg.app.config.WebConfigurer
import com.sg.app.dao.OrganisationDAO
import com.sg.app.rdf.RDF
import com.sg.app.rdf.TriplestoreUtil
import com.sg.app.model.Organisation
import com.sg.app.model.Workgroup
import com.sg.app.rdf.asURI
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@CrossOrigin
@RestController
class OrganisationController {
    private val log = LoggerFactory.getLogger(OrganisationController::class.java)

    // List
    @GetMapping("/api/organisation/list")
    @ResponseBody
    fun getOrganisations(): List<Organisation> {
        return OrganisationDAO.getAll()
    }

    @GetMapping("/api/organisation/haveAnswers")
    @ResponseBody
    fun getOrganisationsWithAnswers(): List<Organisation> {
        return OrganisationDAO.getHaveAnswers()
    }

    // Create
    @PostMapping("/api/organisation/new")
    @ResponseBody
    fun createOrganisation(@RequestBody body: String) {
        val o = RESTUtil.receiveAsValue<Organisation>(body)
        val uri = RDF.SG.Organisation.id + "${UUID.randomUUID()}"
        o.orgType = o.orgType.value.trim().replace(" ", "_").asURI()

        OrganisationDAO.saveOrganisation(o.copy(uri = uri))
    }

    // Get an organisation
    @GetMapping("/api/organisation/{id}")
    @ResponseBody
    fun getOrganisation(@PathVariable id: String): Organisation? {
        val uri = RDF.SG.Organisation.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no Organisation with id: $id")
        }

        return OrganisationDAO.getOne(uri)
    }

    // Update
    @PutMapping("/api/organisation/{id}")
    @ResponseBody
    fun updateOrganisation(@PathVariable id: String, @RequestBody body: String) {
        val o = RESTUtil.receiveAsValue<Organisation>(body)
        val uri = RDF.SG.Organisation.id + id

       o.orgType = RDF.SG.uri + "OrgType/" + o.orgType.value.replace(" ", "_")

        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no Organisation with id: $id")
        }

        OrganisationDAO.delete(uri)
        OrganisationDAO.saveOrganisation(o)
    }

    // Delete
    @DeleteMapping("/api/organisation/{id}")
    @ResponseBody
    fun deleteOrganisation(@PathVariable id: String) {
        val uri = RDF.SG.Organisation.id + id
        if (!TriplestoreUtil.checkIfExists(uri)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no Organisation with id: $id")
        }

        OrganisationDAO.delete(uri)
    }
}
