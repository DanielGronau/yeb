package org.yeb.model;

import com.badlogic.gdx.math.Vector2;

public class Node {

    public final int id;
    public final Vector2 pos;
    public final boolean leaf;

    public Node(int id, Vector2 pos, boolean leaf) {
        this.id = id;
        this.pos = pos;
        this.leaf = leaf;
    }

    @Override
    public String toString() {
        return "Node{" +
                       "id=" + id +
                       ", x=" + pos.x +
                       ", y=" + pos.y +
                       ", leaf=" + leaf +
                       '}';
    }
}