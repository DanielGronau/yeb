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
            new Level.Builder(1858F)
                    .node(100F, 100F)
                    .node(100F, 500F)
                    .node(500F, 300F)
                    .node(500F, 700F)
                    .node(900F,100F)
                    .node(900F, 500F).build(),
            new Level.Builder(2493F)
                    .node(100F, 100F)
                    .node(500F, 100F)
                    .node(900F, 100F)
                    .node(100F, 300F)
                    .node(500F, 300F)
                    .node(900F, 300F)
                    .node(100F, 500F)
                    .node(500F, 500F)
                    .node(900F, 500F)
                    .node(100F, 700F)
                    .node(500F, 700F)
                    .node(900F, 700F).build(),
            new Level.Builder(1853F)
                    .node(200F, 100F)
                    .node(800F, 100F)
                    .node(800F, 700F)
                    .node(200F, 700F)
                    .node(100F, 400F)
                    .node(900F, 400F)
                    .circle(500,150,80)
                    .circle(300,500,50)
                    .circle(700,400,40).build(),
            new Level.Builder(1455F)
                    .node(400F, 100F)
                    .node(800F, 600F)
                    .node(100F, 500F)
                    .node(200F, 700F)
                    .node(900F, 400F)
                    .rect(200,150,80, 100)
                    .rect(300,500,50, 120)
                    .circle(700,400,40).build()
    );

}
