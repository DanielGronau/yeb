package org.yeb.model;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.yeb.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Level {

    private static Random random = new Random();

    public final Set<Node> nodes;
    public final Set<Edge> edges;
    public final float winLength;
    public final List<Obstacle> obstacles;

    public Level(Set<Node> nodes, Set<Edge> edges, float winLength, List<Obstacle> obstacles) {
        this.nodes = nodes;
        this.edges = edges;
        this.winLength = winLength;
        this.obstacles = obstacles;
    }

    private Level copy(Set<Node> newNodes, Set<Edge> newEdges) {
        return new Level(newNodes, newEdges, winLength, obstacles);
    }

    public Node nodeById(int id) {
        return nodes.stream()
                       .filter(node -> node.id == id)
                       .findFirst()
                       .orElseThrow(() -> new RuntimeException("unknown id"));
    }

    private Node edgeNode1(Edge edge) {
        return nodeById(edge.id1);
    }

    private Node edgeNode2(Edge edge) {
        return nodeById(edge.id2);
    }

    private float edgeLength(Edge edge) {
        Node first = edgeNode1(edge);
        Node second = edgeNode2(edge);
        return first.pos.dst(second.pos);
    }

    public boolean edgeIntersectsObstacle(Edge edge) {
        Node first = edgeNode1(edge);
        Node second = edgeNode2(edge);
        return obstacles.stream().anyMatch(obstacle -> obstacle.intersectsLine(first.pos, second.pos));
    }

    private boolean hasObstacleIntersections() {
        return edges.stream().anyMatch(this::edgeIntersectsObstacle);
    }

    public Optional<Level> createEdge(int id1, int id2) {
        if (id1 == id2 || edges.contains(new Edge(id1, id2))) return Optional.empty();
        Set<Edge> newEdges = new HashSet<>(edges);
        newEdges.add(new Edge(id1, id2));
        Level newLevel = copy(nodes, newEdges);
        return newLevel.hasCycle() ? Optional.empty() : Optional.of(newLevel);
    }

    public Pair<Level, Integer> splitEdge(Edge edge) {
        Vector2 middle = middle(edge);
        Node node = new Node(newId(), middle, false);
        Set<Node> newNodes = new HashSet<>(nodes);
        newNodes.add(node);
        Edge edge1 = new Edge(edge.id1, node.id);
        Edge edge2 = new Edge(edge.id2, node.id);
        Set<Edge> newEdges = new HashSet<>(edges);
        newEdges.remove(edge);
        newEdges.add(edge1);
        newEdges.add(edge2);
        return Pair.of(copy(newNodes, newEdges), node.id);
    }

    public Vector2 middle(Edge edge) {
        Node first = edgeNode1(edge);
        Node second = edgeNode2(edge);
        return new Vector2(first.pos).add(second.pos).scl(0.5F);
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
            Vector2 rnd = new Vector2(random.nextInt(21) - 10, random.nextInt(21) - 10);
            Node newNode = new Node(node.id, rnd.add(node.pos), false);
            Set<Node> newSet = new HashSet<>(nodes);
            newSet.removeIf(n -> n.id == node.id);
            newSet.add(newNode);
            Level newLevel = copy(newSet, edges);
            float newDist = newLevel.totalEdgeLength();
            if (newDist < oldDist) return newLevel;
        }
        return this;
    }

    public boolean hasWon() {
        return totalEdgeLength() < winLength
                       && allNodesConnected()
                       && !hasObstacleIntersections();
    }

    private boolean allNodesConnected() {
        List<Set<Integer>> sets = singletonNodeIdSets();
        for (Edge edge : edges) {
            Set<Integer> set1 = findSet(sets, edge.id1);
            Set<Integer> set2 = findSet(sets, edge.id2);
            if (set1 == set2) {
                continue;
            }
            mergeSets(sets, set1, set2);
        }
        return sets.size() == 1;
    }

    private boolean hasCycle() {
        List<Set<Integer>> sets = singletonNodeIdSets();
        for (Edge edge : edges) {
            Set<Integer> set1 = findSet(sets, edge.id1);
            Set<Integer> set2 = findSet(sets, edge.id2);
            if (set1 == set2) {
                return true;
            }
            mergeSets(sets, set1, set2);
        }
        return false;
    }

    private List<Set<Integer>> singletonNodeIdSets() {
        return nodes.stream().map(node -> Collections.singleton(node.id)).collect(Collectors.toList());
    }

    private Set<Integer> findSet(List<Set<Integer>> sets, int id) {
        return sets.stream().filter(set -> set.contains(id)).findFirst().get();
    }

    private void mergeSets(List<Set<Integer>> sets, Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> newSet = new HashSet<>(set1);
        newSet.addAll(set2);
        sets.remove(set1);
        sets.remove(set2);
        sets.add(newSet);
    }

    public static class Builder {
        private Set<Node> nodes = new HashSet<>();
        private List<Obstacle> obstacles = new ArrayList<>();
        private final float winLength;
        private int index = 1;

        public Builder(float winLength) {
            this.winLength = winLength;
        }

        public Builder node(float x, float y) {
            nodes.add(new Node(index++, new Vector2(x, y), true));
            return this;
        }

        public Builder rect(float x, float y, float w, float h) {
            obstacles.add(Obstacle.of(new Rectangle(x, y, w, h)));
            return this;
        }

        public Builder circle(float x, float y, float r) {
            obstacles.add(Obstacle.of(new Circle(x, y, r)));
            return this;
        }

        public Level build() {
            return new Level(nodes, new HashSet<>(), winLength, obstacles);
        }
    }

    @Override
    public String toString() {
        return "Level{" +
                       "nodes=" + nodes +
                       ", edges=" + edges +
                       ", winLength=" + winLength +
                       '}';
    }
}