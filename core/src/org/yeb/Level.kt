package org.yeb

import com.badlogic.gdx.math.Vector2
import java.util.*

class Level(val nodes: Set<Node>, val edges: Set<Edge>, val winDistance: Float) {

    fun nodeById(id: Int): Node = nodes.find { it.id == id } ?: error("unknown id")

    fun nodesOfEdge(edge: Edge) = nodeById(edge.id1) to nodeById(edge.id2)

    fun Edge.length(): Float {
        val (n1, n2) = nodesOfEdge(this)
        return n1.toVector().dst(n2.toVector())
    }

    fun createEdge(id1: Int, id2: Int): Level? {
        if (id1 == id2) return null
        if (edges.contains(Edge(id1, id2))) return null
        //TODO test for cycles
        return Level(nodes, edges.plus(Edge(id1, id2)), winDistance)
    }

    fun splitEdge(edge: Edge):Pair<Level, Int> {
        val (n1, n2) = nodesOfEdge(edge)
        val middle = middle(edge)
        val id = newId()
        val node = Node(id, middle.x, middle.y, false)
        val newNodes = nodes.plus(node)
        val newEdges = edges.minus(edge).plus(Edge(n1.id, id)).plus(Edge(n2.id, id))
        return Level(newNodes, newEdges, winDistance) to id
    }

    fun middle(edge: Edge):Vector2 {
        val (n1, n2) = nodesOfEdge(edge)
        return Vector2((n1.x + n2.x)/2F, (n1.y + n2.y)/2F)
    }

    fun newId() = (nodes.map { it.id }.max() ?: 0) + 1

    fun distanceEdges(): Float = edges.map { it.length() }.sum()

    fun wiggle() : Level {
        val node = nodes.shuffled().firstOrNull { ! it.leaf }
        if (node != null) {
            val oldDist = distanceEdges()
            val newNode = node.copy(x = node.x + random.nextInt(21)-10, y = node.y + random.nextInt(21)-10)
            val newSet = nodes.filter { it.id != node.id }.plus(newNode).toSet()
            val newLevel = Level(newSet, edges, winDistance)
            val newDist = newLevel.distanceEdges()
            if (newDist < oldDist) return newLevel
        }
        return this
    }

    private val random = Random()
}