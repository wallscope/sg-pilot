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

data class AllGraphData(val bpDocs: List<BPDoc>? = emptyList(),
                        val pfgDocs: List<PFGDoc>? = emptyList(),
                        val pfgAuxs: List<PFGAux>? = emptyList(),
                        val bpComs: List<BPCom>? = emptyList(),
                        val graph: String? = "")

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
    suspend fun getDocsForcedList(@RequestParam searchDirs: String? = "", @RequestParam searchString: String? = ""): AllGraphData = coroutineScope {
        val findDirs = searchDirs
            ?.split('|')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        val searchTerms = searchString
            ?.split('|')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        if (findDirs.isNotEmpty() || searchTerms.isNotEmpty()) {
            val bpDocs = BPDocDAO.getAll()
            val pfgDocs = PFGDocDAO.getAll()
            val pfgAuxs = PFGAuxDAO.getAll()
            val bpComs = BPComDAO.getAll()

            val bpDocsFilenamesDirectorates = bpDocs
                .distinctBy { it.filename }
                .associate { bpDoc ->
                    bpDoc.filename?.lowercase().takeIf { it.isNotEmpty() } to bpDoc.directorate.getOrNull(0)
                }

            val graph = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                mergeForcedGraphs(
                    async { PFGDoc.toForcedGraphJSONAll(pfgDocs, findDirs, searchTerms) }.await(),
                    async { PFGAux.toForcedGraphJSONAll(pfgAuxs, findDirs, searchTerms) }.await(),
                    async { BPDoc.toForcedGraphJSONAll(bpDocs, findDirs, searchTerms) }.await(),
                    async {
                        BPCom.toForcedGraphJSONAll(
                            bpComs,
                            findDirs,
                            searchTerms,
                            bpDocsFilenamesDirectorates
                        )
                    }.await()
                ).apply {
                    nodes = nodes?.filter { it.name?.isNotEmpty() == true }
                        ?.groupBy { it.name to it.id }
                        ?.map { (_, nodesWithSameNameAndId) ->
                            nodesWithSameNameAndId.reduce { acc, node ->
                                // Merge the uriLists into a single list
                                val mergedUriList = (acc.uriList ?: emptyList()) + (node.uriList ?: emptyList())

                                // Return a new ForcedNode with merged uriList and other properties retained
                                acc.copy(uriList = mergedUriList)
                            }
                        }
                    links?.distinct()
                    categories?.distinct()
                }
            )
            AllGraphData(bpDocs, pfgDocs, pfgAuxs, bpComs, graph)
        } else AllGraphData()

    }

    // Forced graph, docs with NPF relationships
    @PostMapping("/api/alldocs/forcedgraph-npf/list")
    suspend fun getDocsForcedNpfList(@RequestBody outcomes: List<String>, @RequestParam searchDirs: String? = "", @RequestParam searchString: String? = ""): AllGraphData = coroutineScope {
        val findDirs = searchDirs
            ?.split('|')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        val searchTerms = searchString
            ?.split('|')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        val bpDocs = BPDocDAO.getAll()
        val bpDocsFilenamesDirectorates = bpDocs
            .distinctBy { it.filename }
            .associate { bpDoc ->
                bpDoc.filename?.lowercase().takeIf { it.isNotEmpty() } to bpDoc.directorate.getOrNull(0)
            }
        val pfgDocs = PFGDocDAO.getAll()
        val pfgAuxs = PFGAuxDAO.getAll()
        val bpComs = BPComDAO.getAll()

        val graph = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
            mergeForcedGraphs(
                async { PFGDoc.toForcedGraphJSONAll(pfgDocs.filter { myDoc ->
                    myDoc.primaryOutcomes.any { it in outcomes } || myDoc.secondaryOutcomes.any { it in outcomes }
                }, findDirs, searchTerms) }.await(),
                async { PFGAux.toForcedGraphJSONAll(pfgAuxs.filter { myDoc ->
                    myDoc.primaryOutcomes.any { it in outcomes } || myDoc.secondaryOutcomes.any { it in outcomes }
                }, findDirs, searchTerms) }.await(),
                async { BPDoc.toForcedGraphJSONAll(bpDocs, findDirs, searchTerms) }.await(),
                async { BPCom.toForcedGraphJSONAll(bpComs.filter { myDoc ->
                    myDoc.primaryOutcomes.any { it in outcomes } || myDoc.secondaryOutcomes.any { it in outcomes }
                }, findDirs, searchTerms, bpDocsFilenamesDirectorates = bpDocsFilenamesDirectorates) }.await()
            ).apply {
                nodes = nodes?.filter { it.name?.isNotEmpty() == true }
                    ?.groupBy { it.name to it.id }
                    ?.map { (_, nodesWithSameNameAndId) ->
                        nodesWithSameNameAndId.reduce { acc, node ->
                            // Merge the uriLists into a single list
                            val mergedUriList = (acc.uriList ?: emptyList()) + (node.uriList ?: emptyList())

                            // Return a new ForcedNode with merged uriList and other properties retained
                            acc.copy(uriList = mergedUriList)
                        }
                    }
                links?.distinct()
                categories?.distinct()
            }
        )
        AllGraphData(bpDocs, pfgDocs, pfgAuxs, bpComs, graph)
    }
}
