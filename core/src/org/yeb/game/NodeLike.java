package org.yeb.game;

import com.badlogic.gdx.math.Vector2;
import org.yeb.model.Edge;
import org.yeb.model.Level;
import org.yeb.model.Node;
import org.yeb.model.Pair;

import java.util.Optional;

public interface NodeLike {
    Node asNode(Level level);
    Pair<Level, Integer> withNode(Level level);
    Optional<Edge> asEdge();

    static NodeLike ofEdge(Edge edge){
        return new NodeLike() {
            @Override
            public Node asNode(Level level) {
                Vector2 middle = level.middle(edge);
                return new Node(0, middle.x, middle.y, false);
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

    static NodeLike ofNodeId(int id) {
        return new NodeLike() {
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
