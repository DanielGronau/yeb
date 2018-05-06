package org.yeb.menu;

import org.yeb.model.Level;

import java.util.Arrays;
import java.util.List;

class Levels {

    static List<Level> LEVELS = Arrays.asList(
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
