package org.yeb.model;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.yeb.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Level {

    private static final Random RANDOM = new Random();

    public final Map<Integer, Node> nodes;
    public final Set<Edge> edges;
    public final float winLength;
    public final List<Obstacle> obstacles;

    private Boolean allNodesConnected = null;
    private Boolean hasCycle = null;

    public Level(Map<Integer, Node> nodes, Set<Edge> edges, float winLength, List<Obstacle> obstacles) {
        this.nodes = nodes;
        this.edges = edges;
        this.winLength = winLength;
        this.obstacles = obstacles;
    }

    private Level copy(Map<Integer, Node> newNodes, Set<Edge> newEdges) {
        return new Level(newNodes, newEdges, winLength, obstacles);
    }

    public Node nodeById(int id) {
        return Objects.requireNonNull(nodes.get(id));
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
        Map<Integer, Node> newNodes = new HashMap<>(nodes);
        newNodes.put(node.id, node);
        Edge edge1 = new Edge(edge.id1, node.id);
        Edge edge2 = new Edge(edge.id2, node.id);
        Set<Edge> newEdges = new HashSet<>(edges);
        newEdges.remove(edge);
        newEdges.add(edge1);
        newEdges.add(edge2);
        return Pair.of(copy(newNodes, newEdges), node.id);
    }

    public Level removeEdge(Edge edge) {
        Set<Edge> newEdges = new HashSet<>(edges);
        newEdges.remove(edge);
        return copy(new HashMap<>(nodes), newEdges).mergeEdge(edge.id1).mergeEdge(edge.id2);
    }

    private Level mergeEdge(int id) {
        Node node = nodeById(id);
        if (node.leaf) {
            return this;
        }
        List<Edge> commonEdges = edges.stream().filter(edge -> edge.id1 == id || edge.id2 == id).collect(Collectors.toList());
        if (commonEdges.size() == 2) {
            Set<Edge> newEdges = new HashSet<>(edges);
            newEdges.removeAll(commonEdges);
            List<Integer> newIds = commonEdges.stream().map(edge -> edge.id1 == id ? edge.id2 : edge.id1).collect(Collectors.toList());
            newEdges.add(new Edge(newIds.get(0), newIds.get(1)));
            Map<Integer, Node> newNodes = new HashMap<>(nodes);
            newNodes.remove(id);
            return copy(newNodes, newEdges);
        }
        return this;
    }

    public Vector2 middle(Edge edge) {
        Node first = edgeNode1(edge);
        Node second = edgeNode2(edge);
        return new Vector2(first.pos).add(second.pos).scl(0.5F);
    }

    private int newId() {
        return 1 + nodes.keySet().stream().reduce(Math::max).orElse(0);
    }

    public float totalEdgeLength() {
        return edges.stream().map(this::edgeLength).reduce(0F, (a, b) -> a + b);
    }

    public Level wiggle() {
        List<Node> innerNodes = nodes.values().stream().filter(node -> !node.leaf).collect(Collectors.toList());
        if (!innerNodes.isEmpty()) {
            Collections.shuffle(innerNodes);
            Node node = innerNodes.get(0);
            float oldDist = totalEdgeLength();
            Vector2 rnd = new Vector2(RANDOM.nextInt(21) - 10, RANDOM.nextInt(21) - 10);
            Node newNode = new Node(node.id, rnd.add(node.pos), false);
            Map<Integer, Node> newNodes = new HashMap<>(nodes);
            newNodes.put(newNode.id, newNode);
            Level newLevel = copy(newNodes, edges);
            float newDist = newLevel.totalEdgeLength();
            if (newDist < oldDist) {
                newLevel.hasCycle = hasCycle;
                newLevel.allNodesConnected = allNodesConnected;
                return newLevel;
            }
        }
        return this;
    }

    public boolean hasWon() {
        return totalEdgeLength() < winLength
                       && allNodesConnected()
                       && !hasObstacleIntersections();
    }

    public boolean allNodesConnected() {
        if (allNodesConnected == null) {
            List<Set<Integer>> sets = singletonNodeIdSets();
            for (Edge edge : edges) {
                Set<Integer> set1 = findSet(sets, edge.id1);
                Set<Integer> set2 = findSet(sets, edge.id2);
                if (set1 == set2) {
                    continue;
                }
                mergeSets(sets, set1, set2);
            }
            allNodesConnected = sets.size() == 1;
        }
        return allNodesConnected;
    }

    private boolean hasCycle() {
        if (hasCycle == null) {
            hasCycle = false;
            List<Set<Integer>> sets = singletonNodeIdSets();
            for (Edge edge : edges) {
                Set<Integer> set1 = findSet(sets, edge.id1);
                Set<Integer> set2 = findSet(sets, edge.id2);
                if (set1 == set2) {
                    hasCycle = true;
                    break;
                }
                mergeSets(sets, set1, set2);
            }
        }
        return hasCycle;
    }

    private List<Set<Integer>> singletonNodeIdSets() {
        return nodes.values().stream().map(node -> Collections.singleton(node.id)).collect(Collectors.toList());
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
        private Map<Integer, Node> nodes = new HashMap<>();
        private List<Obstacle> obstacles = new ArrayList<>();
        private final float winLength;
        private int index = 1;

        public Builder(float winLength) {
            this.winLength = winLength;
        }

        public Builder node(float x, float y) {
            nodes.put(index, new Node(index, new Vector2(x, y), true));
            index++;
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