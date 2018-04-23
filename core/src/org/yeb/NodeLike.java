package org.yeb;

import com.badlogic.gdx.math.Vector2;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import org.yeb.model.Edge;
import org.yeb.model.Level;
import org.yeb.model.Node;

public interface NodeLike {
    Node asNode(Level level);
    Tuple2<Level, Integer> withNode(Level level);
    Option<Edge> asEdge();

    static NodeLike ofEdge(Edge edge){
        return new NodeLike() {
            @Override
            public Node asNode(Level level) {
                Vector2 middle = level.middle(edge);
                return new Node(0, middle.x, middle.y, false);
            }

            @Override
            public Tuple2<Level, Integer> withNode(Level level) {
                return level.splitEdge(edge);
            }

            @Override
            public Option<Edge> asEdge() {
                return Option.of(edge);
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
            public Tuple2<Level, Integer> withNode(Level level) {
                return Tuple.of(level, id);
            }

            @Override
            public Option<Edge> asEdge() {
                return Option.none();
            }
        };
    }

}
