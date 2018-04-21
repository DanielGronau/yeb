package org.yeb.model;

public class Node {

    public final int id;
    public final float x;
    public final float y;
    public final boolean leaf;

    public Node(int id, float x, float y, boolean leaf) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.leaf = leaf;
    }

    @Override
    public String toString() {
        return "Node{" +
                       "id=" + id +
                       ", x=" + x +
                       ", y=" + y +
                       ", leaf=" + leaf +
                       '}';
    }
}