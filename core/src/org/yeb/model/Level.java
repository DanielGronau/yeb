package org.yeb.model;

import com.badlogic.gdx.math.Vector2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Level {

    private static Random random = new Random();

    public final Set<Node> nodes;
    public final Set<Edge> edges;
    public final float winLength;


    public Level(Set<Node> nodes, Set<Edge> edges, float winLength) {
        this.nodes = nodes;
        this.edges = edges;
        this.winLength = winLength;
    }

    public Node nodeById(int id) {
        return nodes.stream()
                       .filter(node -> node.id == id)
                       .findFirst()
                       .orElseThrow(() -> new RuntimeException("unknown id"));
    }

    private Pair<Node, Node> nodesOfEdge(Edge edge) {
        return Pair.of(nodeById(edge.id1), nodeById(edge.id2));
    }

    private float edgeLength(Edge edge) {
        Pair<Node, Node> tuple = nodesOfEdge(edge);
        float dx = tuple._1.x - tuple._2.x;
        float dy = tuple._1.y - tuple._2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public Optional<Level> createEdge(int id1, int id2) {
        if (id1 == id2 || edges.contains(new Edge(id1, id2))) return Optional.empty();
        Set<Edge> newEdges = new HashSet<>(edges);
        newEdges.add(new Edge(id1, id2));
        Level newLevel = new Level(nodes, newEdges, winLength);
        return newLevel.hasCycle() ? Optional.empty() : Optional.of(newLevel);
    }

    public Pair<Level, Integer> splitEdge(Edge edge) {
        Pair<Node, Node> nodePair = nodesOfEdge(edge);
        Vector2 middle = middle(edge);
        int id = newId();
        Node node = new Node(id, middle.x, middle.y, false);
        Set<Node> newNodes = new HashSet<>(nodes);
        newNodes.add(node);
        Edge edge1 = new Edge(nodePair._1.id, id);
        Edge edge2 = new Edge(nodePair._2.id, id);
        Set<Edge> newEdges = new HashSet<>(edges);
        newEdges.remove(edge);
        newEdges.add(edge1);
        newEdges.add(edge2);
        return Pair.of(new Level(newNodes, newEdges, winLength), id);
    }

    public Vector2 middle(Edge edge) {
        Pair<Node, Node> nodes = nodesOfEdge(edge);
        return new Vector2((nodes._1.x + nodes._2.x) / 2F, (nodes._1.y + nodes._2.y) / 2F);
    }

    private int newId() {
        return 1 + nodes.stream().map(node -> node.id).reduce(Math::max).orElse(0);
    }

    public float totalEdgeLength() {
        return edges.stream().map(this::edgeLength).reduce(0F, (a, b) -> a + b);
    }

    public Level wiggle() {
        List<Node> innerNodes = nodes.stream().filter(node -> !node.leaf).collect(Collectors.toList());
        if (!innerNodes.isEmpty()) {
            Collections.shuffle(innerNodes);
            Node node = innerNodes.get(0);
            float oldDist = totalEdgeLength();
            Node newNode = new Node(node.id,
                    node.x + random.nextInt(21) - 10,
                    node.y + random.nextInt(21) - 10,
                    false);
            Set<Node> newSet = new HashSet<>(nodes);
            newSet.removeIf(n -> n.id == node.id);
            newSet.add(newNode);
            Level newLevel = new Level(newSet, edges, winLength);
            float newDist = newLevel.totalEdgeLength();
            if (newDist < oldDist) return newLevel;
        }
        return this;
    }

    public boolean allConnected() {
        List<Node> nodeList = new ArrayList<>(nodes);
        Set<Integer> done = new HashSet<>();
        done.add(nodeList.get(0).id);
        Set<Integer> todo = nodeList.subList(1, nodeList.size()).stream().map(node -> node.id).collect(Collectors.toSet());
        Set<Edge> unused = edges;
        Set<Edge> connected;
        do {
            Set<Integer> doneCopy = done;
            Set<Integer> todoCopy = todo;
            //throw away edges between done nodes
            unused = unused.stream().filter(edge -> !doneCopy.contains(edge.id1) || !doneCopy.contains(edge.id2)).collect(Collectors.toSet());
            //remaining edges with done endpoint must connect to a to-do node
            connected = unused.stream().filter(edge -> doneCopy.contains(edge.id1) || doneCopy.contains(edge.id2)).collect(Collectors.toSet());
            Set<Integer> nodesToMove =
                    connected.stream()
                            .flatMap(edge -> Stream.of(edge.id1, edge.id2).filter(todoCopy::contains))
                            .collect(Collectors.toSet());
            done.addAll(nodesToMove);
            todo.removeAll(nodesToMove);
            unused.removeAll(connected);
        } while (!connected.isEmpty());
        return todo.isEmpty();
    }

    public boolean hasCycle() {
       List<Set<Integer>> sets = nodes.stream().map(node -> Collections.singleton(node.id)).collect(Collectors.toList());
       for(Edge edge : edges) {
           Set<Integer> set1 = sets.stream().filter(set -> set.contains(edge.id1)).findFirst().get();
           Set<Integer> set2 = sets.stream().filter(set -> set.contains(edge.id2)).findFirst().get();
           if (set1 == set2) {
               return true;
           }
           Set<Integer> newSet = new HashSet<>(set1);
           newSet.addAll(set2);
           sets.remove(set1);
           sets.remove(set2);
           sets.add(newSet);
       }
       return false;
    }

    public static class Builder {
        private Set<Node> nodes = new HashSet<>();
        private final float winLength;
        private int index = 1;

        public Builder(float winLength) {
            this.winLength = winLength;
        }

        public Builder node(float x, float y) {
            nodes.add(new Node(index++, x, y, true));
            return this;
        }

        public Level build() {
            return new Level(nodes, new HashSet<>(), winLength);
        }
    }
}