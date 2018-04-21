package org.yeb.model;

import com.badlogic.gdx.math.Vector2;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Set;
import io.vavr.control.Option;

import java.util.Collections;
import java.util.Random;

public class Level {

    private static Random random = new Random();

    public final Set<Node> nodes;
    public final Set<Edge> edges;
    public final float winDistance;


    public Level(Set<Node> nodes, Set<Edge> edges, float winDistance) {
        this.nodes = nodes;
        this.edges = edges;
        this.winDistance = winDistance;
    }

    public Node nodeById(int id) {
        return nodes.find(node -> node.id == id).getOrElseThrow(() -> new RuntimeException("unknown id"));
    }

    private Tuple2<Node, Node> nodesOfEdge(Edge edge) {
        return Tuple.of(nodeById(edge.id1), nodeById(edge.id2));
    }

    private float edgeLength(Edge edge) {
        Tuple2<Node, Node> tuple = nodesOfEdge(edge);
        float dx = tuple._1.x - tuple._2.x;
        float dy = tuple._1.y - tuple._2.y;
        return (float) Math.sqrt(dx*dx + dy*dy);
    }

    public Option<Level> createEdge(int id1, int id2) {
        if (id1 == id2 || edges.contains(new Edge(id1, id2))) return Option.none();
        //TODO test for cycles
        return Option.of(new Level(nodes, edges.add(new Edge(id1, id2)), winDistance));
    }

    public Tuple2<Level, Integer> splitEdge(Edge edge) {
        Tuple2<Node, Node> nodes = nodesOfEdge(edge);
        Vector2 middle = middle(edge);
        int id = newId();
        Node node = new Node(id, middle.x, middle.y, false);
        Set<Node> newNodes = this.nodes.add(node);
        Edge edge1 = new Edge(nodes._1.id, id);
        Edge edge2 = new Edge(nodes._2.id, id);
        Set<Edge> newEdges = edges.remove(edge).add(edge1).add(edge2);
        return Tuple.of(new Level(newNodes, newEdges, winDistance), id);
    }

    public Vector2 middle(Edge edge) {
        Tuple2<Node, Node> nodes = nodesOfEdge(edge);
        return new Vector2((nodes._1.x + nodes._2.x) / 2F, (nodes._1.y + nodes._2.y) / 2F);
    }

    private int newId() {
        return 1 + nodes.map(node -> node.id).reduce(Math::max);
    }

    public float distanceEdges() {
        return edges.toList().map(this::edgeLength).fold(0F, (a, b) -> a + b);
    }

    public Level wiggle() {
        java.util.List<Node> javaNodes = this.nodes.filter(node -> !node.leaf).toJavaList();
        if (!javaNodes.isEmpty()) {
            Collections.shuffle(javaNodes);
            Node node = javaNodes.get(0);
            float oldDist = distanceEdges();
            Node newNode = new Node(node.id,
                    node.x + random.nextInt(21) - 10,
                    node.y + random.nextInt(21) - 10,
                    false);
            Set<Node> newSet = nodes.filter(n -> n.id != node.id).add(newNode);
            Level newLevel = new Level(newSet, edges, winDistance);
            float newDist = newLevel.distanceEdges();
            if (newDist < oldDist) return newLevel;
        }
        return this;
    }
}