package org.yeb.menu;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import org.yeb.model.Level;
import org.yeb.model.Node;

class Levels {

    static List<Level> LEVELS = List.of(
            new Builder(1840F)
                    .node(100F, 100F)
                    .node(900F, 100F)
                    .node(900F, 700F)
                    .node(100F, 700F).build(),
            new Builder(1940F)
                    .node(300F, 100F)
                    .node(700F, 100F)
                    .node(700F, 700F)
                    .node(300F, 700F)
                    .node(100F, 400F)
                    .node(900F,400F).build()
    );


    private static class Builder {
        private Set<Node> nodes = HashSet.empty();
        private final float winDistance;
        private int index = 1;

        private Builder(float winDistance) {
            this.winDistance = winDistance;
        }

        private Builder node(float x, float y) {
            nodes = nodes.add(new Node(index++, x, y, true));
            return this;
        }

        private Level build() {
            return new Level(nodes, HashSet.empty(), winDistance);
        }
    }
}
