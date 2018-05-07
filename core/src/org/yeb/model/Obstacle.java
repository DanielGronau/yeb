package org.yeb.model;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;

public interface Obstacle {

    void render(ShapeRenderer sr);

    boolean intersectsLine(Vector2 start, Vector2 end);

    static Obstacle of(Circle circle) {
        return new Obstacle() {

            private Vector2 center = new Vector2(circle.x, circle.y);

            public void render(ShapeRenderer sr) {
                sr.circle(circle.x, circle.y, circle.radius, 50);
            }

            @Override
            public boolean intersectsLine(Vector2 start, Vector2 end) {
                return Intersector.intersectSegmentCircle(start, end, center,circle.radius * circle.radius);
            }
        };
    }

    static Obstacle of(Rectangle rect) {
        return new Obstacle() {

            Polygon polygon = new Polygon(new float[]{
                    rect.x, rect.y,
                    rect.x + rect.width, rect.y,
                    rect.x + rect.width, rect.y + rect.height,
                    rect.x, rect.y + rect.height});

            public void render(ShapeRenderer sr) {
                sr.rect(rect.x, rect.y, rect.width, rect.height);
            }

            @Override
            public boolean intersectsLine(Vector2 start, Vector2 end) {
                return Intersector.intersectSegmentPolygon(start, end, polygon);
            }
        };
    }

}
