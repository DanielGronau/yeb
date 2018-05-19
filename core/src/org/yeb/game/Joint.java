package org.yeb.game;

import com.badlogic.gdx.math.Vector2;
import org.yeb.model.Edge;
import org.yeb.model.Level;
import org.yeb.model.Node;
import org.yeb.util.Pair;

import java.util.Objects;
import java.util.Optional;

/**
 * A joint is a clickable (and with other joints connectable) part of the net,
 * which may be a node of the level, or an edge center.
 */
abstract class Joint {

    public abstract Node asNode(Level level);
    public abstract Pair<Level, Integer> withNode(Level level);
    public abstract Optional<Edge> asEdge();

    private Joint() {
    }

    static Joint ofNodeId(int id) {
        return new NodeJoint(id);
    }

    static Joint ofEdge(Edge edge){
        return new EdgeJoint(edge);
    }

    private static class NodeJoint extends Joint {
        private final int id;

        private NodeJoint(int id) {
            this.id = id;
        }

        @Override
        public Node asNode(Level level) {
            return level.nodeById(id);
        }

        @Override
        public Pair<Level, Integer> withNode(Level level) {
            return Pair.of(level, id);
        }

        @Override
        public Optional<Edge> asEdge() {
            return Optional.empty();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof NodeJoint && id == ((NodeJoint) obj).id;
        }
    }

    private static class EdgeJoint extends Joint {
        private final Edge edge;

        private EdgeJoint(Edge edge) {
            this.edge = edge;
        }

        @Override
        public Node asNode(Level level) {
            Vector2 middle = level.middle(edge);
            return new Node(0, middle, false);
        }

        @Override
        public Pair<Level, Integer> withNode(Level level) {
            return level.splitEdge(edge);
        }

        @Override
        public Optional<Edge> asEdge() {
            return Optional.of(edge);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EdgeJoint && edge.equals(((EdgeJoint) obj).edge);
        }
    }
}
