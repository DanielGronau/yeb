package org.yeb

class Edge(val id1: Int, val id2: Int) {

    override fun hashCode(): Int {
        return id1 * id2
    }

    override fun equals(other: Any?): Boolean {
        if (other is Edge) {
           return  (id1 == other.id1 && id2 == other.id2)
            || (id1 == other.id2 && id2 == other.id1)
        }
        return false
    }
}