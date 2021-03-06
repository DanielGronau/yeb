package org.yeb.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.yeb.SoundBank;
import org.yeb.YebGame;
import org.yeb.menu.Levels;
import org.yeb.menu.MenuScreen;
import org.yeb.model.Edge;
import org.yeb.model.Level;
import org.yeb.model.Node;
import org.yeb.util.Droplet;
import org.yeb.util.Optionals;
import org.yeb.util.Pair;
import org.yeb.util.UiHelper;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import static org.yeb.util.UiHelper.makeButton;

public class GameScreen extends ScreenAdapter {

    private static final float JOINT_RADIUS = 15F;
    private static final float SHADOW = 10F;
    private static final Color SHADOW_COLOR = new Color(0.7F, 0.7F, 0.7F, 0.2F);

    private final OrthographicCamera camera = new OrthographicCamera();
    private final Stage stage = new Stage();
    private final ShapeRenderer sr = new ShapeRenderer();

    private Level level;
    private Stack<Level> history = new Stack<>();
    private Optional<Joint> marked = Optional.empty();
    private boolean win = false;
    private float animationTime = 0F;
    private Set<Droplet> droplets = new HashSet<>();

    public GameScreen(Level level) {
        this.level = level;
        camera.setToOrtho(false, 1000F, 800F);
        camera.update();

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gameInput());
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);

        Skin skin = UiHelper.makeSkin(YebGame.instance().font, Color.GRAY);
        stage.addActor(makeButton(skin, "Menu", 50, 20, this::toMenuScreen));
        stage.addActor(makeButton(skin, "Reset", 200, 20, this::reset));
        stage.addActor(makeButton(skin, "Undo", 350, 20, this::undo));
    }

    private boolean mouseDown(int x, int y, int button) {
        Vector3 touchPos = new Vector3(x, y, 0F);
        camera.unproject(touchPos);
        switch (button) {
            case Input.Buttons.LEFT:
                return Optionals.or(
                    pickedNodeJoint(touchPos),
                    () -> pickedEdgeJoint(touchPos))
                                .map(pickedJoint -> marked = marked.map(
                                    markedJoint -> jointClickedWithMarked(pickedJoint, markedJoint)
                                ).orElseGet(() -> jointClickedWithoutMarked(pickedJoint)))
                                .isPresent();
            case Input.Buttons.RIGHT:
                return pickedEdgeJoint(touchPos).map(this::removeEdge).orElse(false);
            default:
                return false;
        }
    }

    private boolean removeEdge(Joint joint) {
           return joint.asEdge()
                       .map(edge -> {
                           SoundBank.jointClick();
                           marked = Optional.empty();
                           history.push(level);
                           level = level.removeEdge(edge);
                           return true;
                       })
                       .orElse(false);
    }

    private Optional<Joint> jointClickedWithMarked(Joint pickedJoint, Joint markedJoint) {
        if (pickedJoint.equals(markedJoint) || tryToCreateEdge(pickedJoint, markedJoint)) {
            SoundBank.jointClick();
            return Optional.empty();
        } else {
            SoundBank.invalidClick();
            return marked;
        }
    }

    private Optional<Joint> jointClickedWithoutMarked(Joint pickedJoint) {
        if (level.allNodesConnected()) {
            SoundBank.invalidClick();
            return Optional.empty();
        } else {
            SoundBank.jointClick();
            animationTime = 0;
            return Optional.of(pickedJoint);
        }
    }

    private boolean tryToCreateEdge(Joint picked, Joint marked) {
        Pair<Level, Integer> tuple1 = picked.withNode(level);
        Level levelWithEdge1 = tuple1._1;
        Pair<Level, Integer> tuple2 = marked.withNode(levelWithEdge1);
        Level levelWithEdge2 = tuple2._1;
        return levelWithEdge2
                   .createEdge(tuple1._2, tuple2._2)
                   .map(newLevel -> {
                       history.push(level);
                       level = newLevel;
                       return true;
                   })
                   .orElse(false);
    }

    private Optional<Joint> pickedNodeJoint(Vector3 touchPos) {
        return level.nodes
                   .values().stream()
                   .filter(node -> touchPos.dst(node.pos.x, node.pos.y, 0F) < JOINT_RADIUS)
                   .findFirst()
                   .map(node -> Joint.ofNodeId(node.id));
    }

    private Optional<Joint> pickedEdgeJoint(Vector3 touchPos) {
        return level.edges
                   .stream()
                   .filter(edge -> {
                       Vector2 middle = level.middle(edge);
                       return touchPos.dst(middle.x, middle.y, 0F) < JOINT_RADIUS;
                   })
                   .findFirst()
                   .map(Joint::ofEdge);
    }

    @Override
    public void render(float delta) {
        animationTime = (animationTime + delta) % 1;

        YebGame game = YebGame.instance();
        level = level.wiggle();
        if (win != level.hasWon()) {
            SoundBank.winSound();
            win = true;
        }

        Color background = game.background;
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        droplets = Droplet.generateAndRemove(delta, droplets);
        Droplet.render(camera, sr, droplets, delta);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.setColor(Color.DARK_GRAY);
        game.font.draw(game.batch, distanceFeedback(), 10F, 780F);
        game.font.setColor(Color.WHITE);
        game.batch.end();

        renderLevel();

        if (win) {
            game.batch.begin();
            game.batch.draw(game.levelSolvedBanner,
                500 - game.levelSolvedBanner.getWidth() / 2,
                400 - game.levelSolvedBanner.getHeight() / 2);
            game.batch.end();
        }

        stage.act();
        stage.draw();
    }

    private void renderLevel() {
        sr.setAutoShapeType(true);
        sr.setProjectionMatrix(camera.combined);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glLineWidth(4);
        sr.setColor(SHADOW_COLOR);
        level.obstacles.forEach(obstacle -> obstacle.renderShadow(sr, SHADOW, SHADOW));
        level.edges.forEach(edge -> {
            Node n1 = level.nodeById(edge.id1);
            Node n2 = level.nodeById(edge.id2);
            sr.rectLine(n1.pos.x + SHADOW, n1.pos.y - SHADOW, n2.pos.x + SHADOW, n2.pos.y - SHADOW, 6F);
            Vector2 middle = level.middle(edge);
            sr.circle(middle.x + SHADOW, middle.y - SHADOW, JOINT_RADIUS);
        });
        level.nodes.values().forEach(node -> {
            sr.circle(node.pos.x + SHADOW, node.pos.y - SHADOW, JOINT_RADIUS);
        });
        marked.ifPresent(joint -> {
            sr.set(ShapeRenderer.ShapeType.Line);
            Vector2 center = joint.asNode(level).pos;
            sr.circle(center.x + SHADOW, center.y - SHADOW, JOINT_RADIUS + 3 + 10 * animationTime, 25);
            sr.set(ShapeRenderer.ShapeType.Filled);
        });

        sr.setColor(Color.DARK_GRAY);
        level.obstacles.forEach(obstacle -> obstacle.render(sr));
        level.edges.forEach(edge -> {
            Node n1 = level.nodeById(edge.id1);
            Node n2 = level.nodeById(edge.id2);
            sr.setColor(level.edgeIntersectsObstacle(edge) ? Color.ORANGE : Color.BLACK);
            sr.rectLine(n1.pos, n2.pos, 6F);
            Vector2 middle = level.middle(edge);
            sr.setColor(edgeJointColor(edge));
            sr.circle(middle.x, middle.y, JOINT_RADIUS);
        });
        level.nodes.values().forEach(node -> {
            sr.setColor(nodeJointColor(node));
            sr.circle(node.pos.x, node.pos.y, JOINT_RADIUS);
        });
        marked.ifPresent(joint -> {
            Gdx.gl.glLineWidth(3);
            sr.set(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.SALMON);
            Vector2 center = joint.asNode(level).pos;
            sr.circle(center.x, center.y, JOINT_RADIUS + 3 + 10 * animationTime, 25);
        });
        sr.end();
    }

    private Color nodeJointColor(Node node) {
        return node.leaf ? Color.BLUE : Color.GREEN;
    }

    private Color edgeJointColor(Edge edge) {
        return Color.PURPLE;
    }

    private String distanceFeedback() {
        //No String.format in GWT :(
        BigDecimal bd = new BigDecimal(Float.toString(level.totalEdgeLength()))
                            .setScale(2, BigDecimal.ROUND_HALF_UP);
        return "Winning distance: " + level.winLength + ", current distance: " + bd.doubleValue();
    }

    private void toMenuScreen() {
        if (level.hasWon()) {
            Levels.announceWin(history.firstElement());
        }
        YebGame.instance().setScreen(new MenuScreen());
        dispose();
    }

    private void reset() {
        if (!history.isEmpty()) {
            GameScreen.this.level = history.firstElement();
            history = new Stack<>();
            marked = Optional.empty();
        }
    }

    private void undo() {
        if (!history.isEmpty()) {
            GameScreen.this.level = history.pop();
            marked = Optional.empty();
        }
    }

    private InputAdapter gameInput() {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (win) {
                    toMenuScreen();
                    return true;
                }
                return mouseDown(screenX, screenY, button);
            }

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Keys.ESCAPE || (win && keycode == Keys.ENTER)) {
                    toMenuScreen();
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
        super.dispose();
    }
}
