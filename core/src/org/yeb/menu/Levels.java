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
            new Level.Builder(1998F)
                    .node(100F, 100F)
                    .node(900F, 100F)
                    .node(900F, 700F)
                    .node(100F, 700F)
                    .node(500F,250F)
                    .node(500F, 550F).build(),
            new Level.Builder(1893F)
                    .node(100F, 100F)
                    .node(100F, 500F)
                    .node(500F, 300F)
                    .node(500F, 700F)
                    .node(900F,100F)
                    .node(900F, 500F).build(),
            new Level.Builder(1853F)
                    .node(200F, 100F)
                    .node(800F, 100F)
                    .node(800F, 700F)
                    .node(200F, 700F)
                    .node(100F, 400F)
                    .node(900F, 400F).build()
    );

}
