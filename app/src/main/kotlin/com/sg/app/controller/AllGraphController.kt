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
    suspend fun getDocsForcedList(): String = coroutineScope {
        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            mergeForcedGraphs(
                async { PFGDoc.toForcedGraphJSONAll(PFGDocDAO.getAll(), 15) }.await(),
                async { PFGAux.toForcedGraphJSONAll(PFGAuxDAO.getAll(), 15) }.await(),
                async { BPDoc.toForcedGraphJSONAll(BPDocDAO.getAll(), 15) }.await(),
                async { BPCom.toForcedGraphJSONAll(BPComDAO.getAll(), 15) }.await()
            )
        )
    }
    // Forced graph, docs with NPF relationships

    @PostMapping("/api/alldocs/forcedgraph-npf/list")
    suspend fun getDocsForcedNpfList(@RequestBody outcomes: List<String>): String = coroutineScope {

        val pfgDocs = PFGDocDAO.getAll().filter { myDoc ->
                myDoc.primaryOutcomes.any { it in outcomes } || myDoc.secondaryOutcomes.any { it in outcomes }
        }
        val pfgAuxs = PFGAuxDAO.getAll().filter { myDoc ->
                myDoc.primaryOutcomes.any { it in outcomes } || myDoc.secondaryOutcomes.any { it in outcomes }
        }

        val bpComs = BPComDAO.getAll().filter { myDoc ->
                myDoc.primaryOutcomes.any { it in outcomes } || myDoc.secondaryOutcomes.any { it in outcomes }
        }

        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            mergeForcedGraphs(
                async { PFGDoc.toForcedGraphJSONAll(pfgDocs) }.await(),
                async { PFGAux.toForcedGraphJSONAll(pfgAuxs) }.await(),
                async { BPCom.toForcedGraphJSONAll(bpComs) }.await()
            )
        )
    }

    // TODO
//    @GetMapping("/api/alldocs/overview/list")
//    @ResponseBody
//    suspend fun getDocsOverviewList(): String = coroutineScope {
//        mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
//            mergeForcedGraphs(
//                async { PFGDoc.toForcedGraphJSONAll(PFGDocDAO.getAll(), 15) }.await(),
//                async { PFGAux.toForcedGraphJSONAll(PFGAuxDAO.getAll(), 15) }.await(),
//                async { BPDoc.toForcedGraphJSONAll(BPDocDAO.getAll(), 15) }.await(),
//                async { BPCom.toForcedGraphJSONAll(BPComDAO.getAll(), 15) }.await()
//            )
//        )
//    }

    // Forced graph, all docs
//    @GetMapping("/api/alldocs/forcedgraph/list")
//    @ResponseBody
//    fun getDocsForcedList(): String {
//        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
//            mergeForcedGraphs(
//                PFGDoc.toForcedGraphJSONAll(PFGDocDAO.getAll()),
//                PFGAux.toForcedGraphJSONAll(PFGAuxDAO.getAll()),
//                BPDoc.toForcedGraphJSONAll(BPDocDAO.getAll()),
//                BPCom.toForcedGraphJSONAll(BPComDAO.getAll())
//            )
//        )
//    }
}
