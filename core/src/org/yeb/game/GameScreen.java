package org.yeb.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.yeb.YebGame;
import org.yeb.menu.MenuScreen;
import org.yeb.model.Edge;
import org.yeb.model.Level;
import org.yeb.model.Node;
import org.yeb.util.Optionals;
import org.yeb.util.Pair;
import org.yeb.util.UiHelper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.Stack;

import static org.yeb.util.UiHelper.makeButton;

public class GameScreen extends ScreenAdapter {

    private static final float JOINT_RADIUS = 15F;
    private static final float SHADOW = 10F;
    private static final Color SHADOW_COLOR = new Color(0.7F,0.7F,0.7F, 0.2F);
    private static final double HALF_TONE = Math.pow(2, 1.0 / 12);
    private static final int[] SCALE = {0, 2, 5, 7, 11, 12}; //c,d,f,g,h,C
    private static final Random RANDOM = new Random();

    private final OrthographicCamera camera = new OrthographicCamera();
    private final Stage stage = new Stage();
    private final ShapeRenderer sr = new ShapeRenderer();

    private Level level;
    private Stack<Level> history = new Stack<>();
    private Optional<Joint> marked = Optional.empty();
    private boolean win = false;

    public GameScreen(Level level) {
        this.level = level;
        camera.setToOrtho(false, 1000F, 800F);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gameInput());
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);

        Skin skin = UiHelper.makeSkin(YebGame.instance().font, Color.GRAY);
        stage.addActor(makeButton(skin, "Resign", 50, 20, this::toMenuScreen));
        stage.addActor(makeButton(skin, "Reset", 200, 20, this::reset));
        stage.addActor(makeButton(skin, "Undo", 350, 20, this::undo));
    }

    private boolean mouseDown(int x, int y, int button) {
        Vector3 touchPos = new Vector3(x, y, 0F);
        camera.unproject(touchPos);
        return Optionals.or(pickedNodeJoint(touchPos), () -> pickedEdgeJoint(touchPos))
                       .map(GameScreen::makePing)
                       .map(pickedJoint ->
                                    marked = marked.map(markedJoint -> {
                                        tryToCreateEdge(pickedJoint, markedJoint);
                                        return Optional.<Joint>empty();
                                    }).orElseGet(() -> Optional.of(pickedJoint)))
                       .isPresent();
    }

    private void tryToCreateEdge(Joint picked, Joint marked) {
        Pair<Level, Integer> tuple1 = picked.withNode(level);
        Level levelWithEdge1 = tuple1._1;
        Pair<Level, Integer> tuple2 = marked.withNode(levelWithEdge1);
        Level levelWithEdge2 = tuple2._1;
        levelWithEdge2.createEdge(tuple1._2, tuple2._2).ifPresent(newLevel -> {
            history.push(level);
            level = newLevel;
        });
    }

    private Optional<Joint> pickedNodeJoint(Vector3 touchPos) {
        return level.nodes.values().stream()
                       .filter(node -> touchPos.dst(node.pos.x, node.pos.y, 0F) < JOINT_RADIUS)
                       .findFirst()
                       .map(node -> Joint.ofNodeId(node.id));
    }

    private Optional<Joint> pickedEdgeJoint(Vector3 touchPos) {
        return level.edges.stream()
                       .filter(edge -> {
                           Vector2 middle = level.middle(edge);
                           return touchPos.dst(middle.x, middle.y, 0F) < JOINT_RADIUS;
                       })
                       .findFirst()
                       .map(Joint::ofEdge);
    }

    @Override
    public void render(float delta) {
        YebGame game = YebGame.instance();
        level = level.wiggle();
        if (win != level.hasWon()) {
            YebGame.instance().winSound(0.3F);
            win = true;
        }

        Color background = game.background;
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.setColor(Color.DARK_GRAY);
        game.font.draw(game.batch, distanceFeedback(), 10F, 780F);
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
        sr.setColor(SHADOW_COLOR);
        level.obstacles.forEach(obstacle -> obstacle.renderShadow(sr, SHADOW, SHADOW));
        level.edges.forEach(edge -> {
            Node n1 = level.nodeById(edge.id1);
            Node n2 = level.nodeById(edge.id2);
            sr.rectLine(n1.pos.x + SHADOW, n1.pos.y -SHADOW, n2.pos.x + SHADOW, n2.pos.y - SHADOW, 6F);
            Vector2 middle = level.middle(edge);
            sr.circle(middle.x + SHADOW, middle.y - SHADOW, JOINT_RADIUS);
        });
        level.nodes.values().forEach(node -> {
            sr.circle(node.pos.x + SHADOW, node.pos.y - SHADOW, JOINT_RADIUS);
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
        sr.end();
    }

    private Color nodeJointColor(Node node) {
        if (marked.filter(m -> node.id == m.asNode(level).id).isPresent()) {
            return Color.RED;
        } else if (node.leaf) {
            return Color.BLUE;
        } else {
            return Color.GREEN;
        }
    }

    private Color edgeJointColor(Edge edge) {
        return marked.flatMap(m -> m.asEdge().filter(e -> e == edge)).isPresent()
                       ? Color.RED
                       : Color.PURPLE;
    }

    private String distanceFeedback() {
        //No String.format in GWT :(
        BigDecimal bd = new BigDecimal(Float.toString(level.totalEdgeLength()))
                                .setScale(2, BigDecimal.ROUND_HALF_UP);
        return "Winning distance: " + level.winLength + ", current distance: " + bd.doubleValue();
    }

    private void toMenuScreen() {
        YebGame.instance().setScreen(new MenuScreen());
        dispose();
    }

    private void reset() {
        if (!history.isEmpty()) {
            GameScreen.this.level = history.firstElement();
            history = new Stack<>();
        }
    }

    private void undo() {
        if (!history.isEmpty()) {
            GameScreen.this.level = history.pop();
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

    private static Joint makePing(Joint joint) {
        float pan = (RANDOM.nextFloat() % 2) - 1;
        float pitch = (float) Math.pow(HALF_TONE, SCALE[RANDOM.nextInt(6)]);
        YebGame.instance().jointClick(1F, pitch, pan);
        return joint;
    }

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
        super.dispose();
    }
}
