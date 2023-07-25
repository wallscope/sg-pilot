package com.sg.app.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

// Define a data class for the JSON

// Detailed graph
data class Node(val name: String? = "", val children: List<Node>? = null)

fun stringToNode(str: String): List<Node> = listToNodes(listOf(str))
fun listToNodes(list: List<String?>): List<Node> {
    return list.map { Node(it) }
}fun bpComListToNodes(list: List<BPCom>): List<Node> {
    return list.map { Node(it.commitment) }
}

// Sankey
data class SankeyGraph(val nodes: List<SankeyNode>? = listOf(SankeyNode(name = "")), val links: List<SankeyLink>? = null)
data class SankeyNode(val name: String? = "")
data class SankeyLink(val source: String? = "", val target: String? = "", val value: Int? = 1, val label: String? = "")

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