package com.sg.app.controller

import com.sg.app.dao.PFGAuxDAO
import com.sg.app.dao.PFGDocDAO
import com.sg.app.dao.BPDocDAO
import com.sg.app.dao.BPComDAO
import com.sg.app.model.PFGAux
import com.sg.app.rdf.RDF
import com.sg.app.rdf.TriplestoreUtil
import com.sg.app.model.PFGDoc
import com.sg.app.model.BPDoc
import com.sg.app.model.BPCom
import com.sg.app.model.mapper
import com.sg.app.model.mergeForcedGraphs
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

@CrossOrigin
@RestController
class AllGraphController {
    private val log = LoggerFactory.getLogger(AllGraphController::class.java)

    // TODO
    // List
//    @GetMapping("/api/pfgdoc/list")
//    @ResponseBody
//    fun getPFGDocs(): List<PFGDoc> {
//        return PFGDocDAO.getAll()
//    }

    // TODO
    // Sankey chart
    // PFGDocs - sankey chart, all docs
//    @GetMapping("/api/alldocs/sankey/list")
//    @ResponseBody
//    fun getPFGDocsSankeyList(): String {
//        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(PFGDoc.toSankeyGraphJSONAll(PFGDocDAO.getAll(), 15))
//    }

    // Forced graph, all docs
    @GetMapping("/api/alldocs/forcedgraph/list")
    @ResponseBody
    suspend fun getDocsForcedList(@RequestParam searchString: String? = ""): String = coroutineScope {
        val bpDocs = BPDocDAO.getAll()
        val searchTerms = searchString
            ?.split('|')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }

        val bpDocsFilenamesDirectorates = bpDocs
            .distinctBy { it.filename }
            .associate { bpDoc ->
                bpDoc.filename?.lowercase().takeIf { it.isNotEmpty() } to bpDoc.directorate.getOrNull(0)
            }

        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            mergeForcedGraphs(
                async { PFGDoc.toForcedGraphJSONAll(PFGDocDAO.getAll(), searchTerms,) }.await(),
                async { PFGAux.toForcedGraphJSONAll(PFGAuxDAO.getAll(), searchTerms, ) }.await(),
                async { BPDoc.toForcedGraphJSONAll(bpDocs, searchTerms, ) }.await(),
                async { BPCom.toForcedGraphJSONAll(BPComDAO.getAll(), searchTerms, bpDocsFilenamesDirectorates) }.await()
            ).apply {
                nodes?.filter { it.name?.isNotEmpty() == true }?.distinctBy { it.name to it.id }
                links?.distinct()
                categories?.distinct()
            }
        )
    }

    // Forced graph, docs with NPF relationships
    @PostMapping("/api/alldocs/forcedgraph-npf/list")
    suspend fun getDocsForcedNpfList(@RequestBody outcomes: List<String>, @RequestParam searchString: String? = ""): String = coroutineScope {
        val searchTerms = searchString
            ?.split('|')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }

        val bpDocs = BPDocDAO.getAll()
        val bpDocsFilenamesDirectorates = bpDocs
            .distinctBy { it.filename }
            .associate { bpDoc ->
                bpDoc.filename?.lowercase().takeIf { it.isNotEmpty() } to bpDoc.directorate.getOrNull(0)
            }

        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            mergeForcedGraphs(
                async { PFGDoc.toForcedGraphJSONAll(PFGDocDAO.getAll().filter { myDoc ->
                    myDoc.primaryOutcomes.any { it in outcomes } || myDoc.secondaryOutcomes.any { it in outcomes }
                }, searchTerms) }.await(),
                async { PFGAux.toForcedGraphJSONAll(PFGAuxDAO.getAll().filter { myDoc ->
                    myDoc.primaryOutcomes.any { it in outcomes } || myDoc.secondaryOutcomes.any { it in outcomes }
                }, searchTerms) }.await(),
                async { BPCom.toForcedGraphJSONAll(BPComDAO.getAll().filter { myDoc ->
                    myDoc.primaryOutcomes.any { it in outcomes } || myDoc.secondaryOutcomes.any { it in outcomes }
                }, searchTerms, bpDocsFilenamesDirectorates = bpDocsFilenamesDirectorates) }.await()
            ).apply {
                nodes?.filter { it.name?.isNotEmpty() == true }?.distinctBy { it.name to it.id }
                links?.distinct()
                categories?.distinct()
            }
        )
    }
}
