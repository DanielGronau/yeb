package org.yeb.util;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Random;
import java.util.Set;

public class Droplet {

    private final static Random RANDOM = new Random();

    private float animationTime = 0F;
    private float liveTime = 3F + 3F * RANDOM.nextFloat();
    private float minRadius = 3F + 10F * RANDOM.nextFloat();
    private float maxRadius = 50F + 100F * RANDOM.nextFloat();
    private float x = RANDOM.nextInt(1000);
    private float y = RANDOM.nextInt(800);

    public void render(float delta, ShapeRenderer sr) {
        animationTime += delta;

        float age = animationTime / liveTime;
        float radius = (1 - age) * minRadius + age * maxRadius;
        sr.circle(x, y, radius, (int)radius*2);

        if (radius > 5) {
            sr.circle(x, y, radius - 5, (int)radius*2);
        }
        if (radius > 10) {
            sr.circle(x, y, radius - 10, (int)radius*2);
        }
    }

    public boolean isDead() {
        return animationTime > liveTime;
    }

    public static Set<Droplet> generateAndRemove (float delta, Set<Droplet> droplets) {
        if (RANDOM.nextFloat() * 2 < delta) {
            droplets.add(new Droplet());
        }
        droplets.removeIf(Droplet::isDead);
        return droplets;
    }
}
