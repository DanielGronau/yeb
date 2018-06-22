package org.yeb.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.yeb.YebGame;

import java.util.Random;
import java.util.Set;

public class Droplet {

    private final static Random RANDOM = new Random();

    private float animationTime = 0F;
    private float liveTime = 3F + 4F * RANDOM.nextFloat();
    private float minRadius = 3F + 10F * RANDOM.nextFloat();
    private float maxRadius = 50F + 80F * RANDOM.nextFloat();
    private float x = RANDOM.nextInt(1000);
    private float y = RANDOM.nextInt(800);

    public void render(float delta, ShapeRenderer sr) {
        animationTime += delta;

        float age = animationTime / liveTime;
        float outerRadius = (1 - age) * minRadius + age * (maxRadius / 0.81F);
        if(minRadius < outerRadius && outerRadius <= maxRadius) {
            sr.circle(x, y, outerRadius, (int) outerRadius * 2);
        }
        float middleRadius = outerRadius * 0.9f;
        if (minRadius < middleRadius && middleRadius <= maxRadius) {
            sr.circle(x, y, middleRadius, (int) outerRadius * 2);
        }
        float innerRadius = middleRadius * 0.9f;
        if (minRadius < innerRadius && innerRadius <= maxRadius) {
            sr.circle(x, y, innerRadius, (int) outerRadius * 2);
        }
    }

    private boolean isDead() {
        return animationTime > liveTime;
    }

    public static Set<Droplet> generateAndRemove(float delta, Set<Droplet> droplets) {
        if (RANDOM.nextFloat() < delta) {
            droplets.add(new Droplet());
        }
        droplets.removeIf(Droplet::isDead);
        return droplets;
    }

    public static void render(OrthographicCamera camera, ShapeRenderer sr, Set<Droplet> droplets, float delta) {
        sr.setAutoShapeType(true);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(1);
        sr.setColor(new Color(YebGame.instance().background).mul(0.9F));
        droplets.forEach(droplet -> droplet.render(delta, sr));
        sr.end();
    }
}
