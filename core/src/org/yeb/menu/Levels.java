package org.yeb.menu;

import io.vavr.collection.List;
import org.yeb.model.Level;

class Levels {

    static List<Level> LEVELS = List.of(
            new Level.Builder(1840F)
                    .node(100F, 100F)
                    .node(900F, 100F)
                    .node(900F, 700F)
                    .node(100F, 700F).build(),
            new Level.Builder(1853F)
                    .node(200F, 100F)
                    .node(800F, 100F)
                    .node(800F, 700F)
                    .node(200F, 700F)
                    .node(100F, 400F)
                    .node(900F, 400F).build()
    );


}
