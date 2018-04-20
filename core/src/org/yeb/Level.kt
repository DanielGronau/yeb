package org.yeb

class Level(val nodes: Set<Node>, val edges: Set<Edge>, val winDistance: Float) {

    fun nodeById(id: Int): Node = nodes.find { it.id == id } ?: error("unknown id")

    fun createEdge(id1: Int, id2: Int): Level {
        if (id1 == id2) error("Nodes can't be identical")
        val newId = (nodes.map { it.id }.max() ?: 0) + 1
        val node1 = nodeById(id1)
        val node2 = nodeById(id2)
        val newNode = Node(newId, (node1.x + node2.x) / 2F, (node1.y + node2.y) / 2F, false)
        return Level(nodes.plus(newNode), edges.plus(Edge(id1, newId)).plus(Edge(id2, newId)), winDistance)
    }
}