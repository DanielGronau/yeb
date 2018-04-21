package org.yeb

import java.util.*

class Level(val nodes: Set<Node>, val edges: Set<Edge>, val winDistance: Float) {

    fun nodeById(id: Int): Node = nodes.find { it.id == id } ?: error("unknown id")

    fun nodesOfEdge(edge: Edge) = nodeById(edge.id1) to nodeById(edge.id2)

    fun Edge.length(): Float {
        val (n1, n2) = nodesOfEdge(this)
        return n1.toVector().dst(n2.toVector())
    }

    fun createEdge(id1: Int, id2: Int): Level {
        if (id1 == id2) error("Nodes can't be identical")
        if (edges.contains(Edge(id1, id2))) {
            return this
        }
        val newId = (nodes.map { it.id }.max() ?: 0) + 1
        val node1 = nodeById(id1)
        val node2 = nodeById(id2)
        val newNode = Node(newId, (node1.x + node2.x) / 2F, (node1.y + node2.y) / 2F, false)
        return Level(nodes.plus(newNode), edges.plus(Edge(id1, newId)).plus(Edge(id2, newId)), winDistance)
    }

    fun distanceEdges(): Float = edges.map { it.length() }.sum()

    fun wiggle() : Level {
        val node = nodes.shuffled().firstOrNull { ! it.leaf }
        if (node != null) {
            val oldDist = distanceEdges()
            val newNode = node.copy(x = node.x + random.nextInt(3)-1, y = node.y + random.nextInt(3)-1)
            val newSet = nodes.filter { it.id != node.id }.plus(newNode).toSet()
            val newLevel = Level(newSet, edges, winDistance)
            val newDist = newLevel.distanceEdges()
            if (newDist < oldDist) return newLevel
        }
        return this
    }

    private val random = Random()
}