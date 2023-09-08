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
data class ForcedNode(val id: String? = "", val name: String? = "", val symbolSize: Int? = null, val value: String? = "", val category: Int? = null, val itemStyle: ItemStyle? = null )
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
    ForcedCategory(name = "More"),                     //  0
    ForcedCategory(name = "Leads"),                    //  1
    ForcedCategory(name = "Keywords"),                 //  2
    ForcedCategory(name = "Child poverty"),            //  3
    ForcedCategory(name = "Children and young people"),//  4
    ForcedCategory(name = "Climate change"),           //  5
    ForcedCategory(name = "Communities"),              //  6
    ForcedCategory(name = "Constitution"),             //  7
    ForcedCategory(name = "Cost of living"),           //  8
    ForcedCategory(name = "Covid recovery"),           //  9
    ForcedCategory(name = "Culture"),                  // 10
    ForcedCategory(name = "Economy"),                  // 11
    ForcedCategory(name = "Education"),                // 12
    ForcedCategory(name = "Environment"),              // 13
    ForcedCategory(name = "Fair work and business"),   // 14
    ForcedCategory(name = "Health"),                   // 15
    ForcedCategory(name = "Human rights"),             // 16
    ForcedCategory(name = "International"),            // 17
    ForcedCategory(name = "Poverty")                   // 18
)

fun getOutcomeCategory(outcome: String): Int {
    return when (outcome.lowercase()) {
        "child poverty" -> 3
        "children and young people" -> 4
        "climate change" -> 5
        "communities" -> 6
        "constitution" -> 7
        "cost of living" -> 8
        "covid recovery" -> 9
        "culture" -> 10
        "economy" -> 11
        "education" -> 12
        "environment" -> 13
        "fair work and business" -> 14
        "health" -> 15
        "human rights" -> 16
        "international" -> 17
        "poverty" -> 18
        else -> -1
    }
}
