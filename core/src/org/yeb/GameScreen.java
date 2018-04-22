package org.yeb;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import io.vavr.Tuple2;
import org.yeb.model.Edge;
import org.yeb.model.Level;
import org.yeb.model.Node;


class GameScreen implements Screen {

    private int markedNodeId;
    private Edge markedEdge;
    private final YebGame game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private Level level;

    GameScreen(YebGame game, Level level) {
        this.game = game;
        this.level = level;
        camera.setToOrtho(false, 1000F, 800F);

        Gdx.input.setInputProcessor(new InputAdapter(){
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                mouseDown(screenX, screenY, button);
                return true;
            }
        });
    }

    private void mouseDown(int x, int y, int button) {
        if (button != Input.Buttons.LEFT) {
            return;
        }
        Vector3 touchPos = new Vector3();
        touchPos.set(x, y, 0F);
        camera.unproject(touchPos);
        boolean hasPickedNode =
                level.nodes
                        .find(node -> touchPos.dst(node.x, node.y, 0F) < 10F)
                        .peek(pickedNode -> {
                            if (markedNodeId == 0 && markedEdge == null) {
                                markedNodeId = pickedNode.id;
                            } else if (markedEdge != null) {
                                Tuple2<Level, Integer> tuple = level.splitEdge(markedEdge);
                                Level levelWithEdge = tuple._1;
                                level = levelWithEdge.createEdge(tuple._2, pickedNode.id).getOrElse(level);
                                markedEdge = null;
                            } else if (markedNodeId == pickedNode.id) {
                                markedNodeId = 0;
                            } else {
                                level = level.createEdge(markedNodeId, pickedNode.id).getOrElse(level);
                                markedNodeId = 0;
                            }
                        }).isDefined();

        if (!hasPickedNode) {
            level.edges.find(
                    edge -> {
                        Vector2 middle = level.middle(edge);
                        return touchPos.dst(middle.x, middle.y, 0F) < 10F;
                    }).peek(pickedEdge -> {
                if (markedNodeId == 0 && markedEdge == null) {
                    markedEdge = pickedEdge;
                } else if (markedNodeId != 0) {
                    Tuple2<Level, Integer> tuple = level.splitEdge(pickedEdge);
                    Level levelWithEdge = tuple._1;
                    level = levelWithEdge.createEdge(tuple._2, markedNodeId).getOrElse(level);
                    markedNodeId = 0;
                } else if (markedEdge == pickedEdge) {
                    markedEdge = null;
                } else {
                    Tuple2<Level, Integer> tuple1 = level.splitEdge(pickedEdge);
                    Level levelWithEdge1 = tuple1._1;
                    Tuple2<Level, Integer> tuple2 = levelWithEdge1.splitEdge(markedEdge);
                    Level levelWithEdge2 = tuple2._1;
                    level = levelWithEdge2.createEdge(tuple1._2, tuple2._2).getOrElse(level);
                    markedEdge = null;
                }
            });
        }
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.87F, 0.85F, 0.85F, 1F);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();
        game.font.draw(game.batch,
                "Current distance: " + level.distanceEdges() + ", winning distance: " + level.winDistance,
                10F, 780F);
        game.batch.end();

        ShapeRenderer sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        sr.setProjectionMatrix(camera.combined);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        level.edges.forEach(edge -> {
            Node n1 = level.nodeById(edge.id1);
            Node n2 = level.nodeById(edge.id2);
            sr.setColor(Color.BLACK);
            sr.rectLine(n1.x, n1.y, n2.x, n2.y, 6F);
            Vector2 middle = level.middle(edge);
            sr.setColor(edge == markedEdge ? Color.RED : Color.PURPLE);
            sr.circle(middle.x, middle.y, 10F);
        });
        level.nodes.forEach(node -> {
            if (node.id == markedNodeId) sr.setColor(Color.RED);
            else if (node.leaf) sr.setColor(Color.BLUE);
            else sr.setColor(Color.GREEN);
            sr.circle(node.x, node.y, 10F);
        });
        sr.end();

        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }

        level = level.wiggle();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

}
