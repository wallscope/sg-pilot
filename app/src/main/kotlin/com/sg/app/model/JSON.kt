package com.sg.app.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

// Define a data class for the JSON

// Detailed graph
data class Node(val name: String? = "", val children: List<Node>? = null)

fun stringToNode(str: String?): List<Node> = listToNodes(listOf(str))
fun listToNodes(list: List<String?>): List<Node> {
    return list.map { Node(it) }
}fun bpComListToNodes(list: List<BPCom>): List<Node> {
    return list.map { Node(it.commitment) }
}

// Sankey
data class SankeyGraph(val nodes: List<SankeyNode>? = listOf(SankeyNode(name = "")), val links: List<SankeyLink>? = null)
data class SankeyNode(val name: String? = "")
data class SankeyLink(val source: String? = "", val target: String? = "", val value: Int? = 1, val label: String? = "")

// ForcedGraph
data class ForcedGraph(var nodes: List<ForcedNode>? = listOf(ForcedNode(name = "")), var links: List<ForcedLink>? = null, var categories: List<ForcedCategory>? = null)
data class ForcedNode(val id: String? = "", val name: String? = "", val symbolSize: Int? = null, val value: String? = "", val category: Int? = null, val itemStyle: ItemStyle? = null, val uriList: List<String>? = emptyList() )
data class ItemStyle(val color: String? = null)
data class ForcedLink(val source: String? = "", val target: String? = "")
data class ForcedCategory(val name: String? = "")

fun mergeForcedGraphs(vararg forcedGraphs: ForcedGraph): ForcedGraph {
    val allNodes = forcedGraphs.flatMap { it.nodes!! }.filter { it.name?.isNotEmpty() == true }.distinct()
    val allLinks = forcedGraphs.flatMap { it.links!! }.distinct()
    val allCategories = forcedGraphs.flatMap { it.categories!! }.distinct()

    return ForcedGraph(nodes = allNodes, links = allLinks, categories = allCategories)
}

// Mapper setup
val mapper: ObjectMapper = ObjectMapper().registerModule(
    KotlinModule.Builder()
        .withReflectionCacheSize(512)
        .configure(KotlinFeature.NullToEmptyCollection, false)
        .configure(KotlinFeature.NullToEmptyMap, false)
        .configure(KotlinFeature.NullIsSameAsDefault, false)
        .configure(KotlinFeature.SingletonSupport, false)
        .configure(KotlinFeature.StrictNullChecks, false)
        .build()
)

// Categories
val categories = listOf(
    ForcedCategory(name = "Child poverty"),            //  0
    ForcedCategory(name = "Children and young people"),//  1
    ForcedCategory(name = "Climate change"),           //  2
    ForcedCategory(name = "Communities"),              //  3
    ForcedCategory(name = "Constitution"),             //  4
    ForcedCategory(name = "Cost of living"),           //  5
    ForcedCategory(name = "Covid recovery"),           //  6
    ForcedCategory(name = "Culture"),                  //  7
    ForcedCategory(name = "Economy"),                  //  8
    ForcedCategory(name = "Education"),                //  9
    ForcedCategory(name = "Environment"),              // 10
    ForcedCategory(name = "Fair work and business"),   // 11
    ForcedCategory(name = "Health"),                   // 12
    ForcedCategory(name = "Human rights"),             // 13
    ForcedCategory(name = "International"),            // 14
    ForcedCategory(name = "Poverty"),                  // 15
    ForcedCategory(name = "Leads"),                    // 16
    ForcedCategory(name = "Keywords"),                 // 17
    ForcedCategory(name = "More"),                     // 18
)

fun getOutcomeCategory(outcome: String): Int {
    return when (outcome.lowercase()) {
        "child poverty" -> 0
        "children and young people" -> 1
        "climate change" -> 2
        "communities" -> 3
        "constitution" -> 4
        "cost of living" -> 5
        "covid recovery" -> 6
        "culture" -> 7
        "economy" -> 8
        "education" -> 9
        "environment" -> 10
        "fair work and business" -> 11
        "health" -> 12
        "human rights" -> 13
        "international" -> 14
        "poverty" -> 15
        else -> -1
    }
}
