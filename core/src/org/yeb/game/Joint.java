package org.yeb.game;

import com.badlogic.gdx.math.Vector2;
import org.yeb.model.Edge;
import org.yeb.model.Level;
import org.yeb.model.Node;
import org.yeb.util.Pair;

import java.util.Optional;

/**
 * A joint is a clickable (and with other joints connectable) part of the net,
 * which may be a node of the level, or an edge center.
 */
interface Joint {
    Node asNode(Level level);
    Pair<Level, Integer> withNode(Level level);
    Optional<Edge> asEdge();

    static Joint ofEdge(Edge edge){
        return new Joint() {
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
        };
    }

    static Joint ofNodeId(int id) {
        return new Joint() {
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
        };
    }
}
