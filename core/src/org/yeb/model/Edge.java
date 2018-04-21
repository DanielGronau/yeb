package org.yeb.model;

public class Edge {

    public final int id1;
    public final int id2;

    Edge(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public int hashCode() {
        return id1 * id2;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Edge) {
            Edge that = (Edge) other;
            return (id1 == that.id1 && id2 == that.id2)
                           || (id1 == that.id2 && id2 == that.id1);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Edge{" + id1 +", " + id2 + '}';
    }
}