package org.yeb.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import org.yeb.YebGame;
import org.yeb.menu.MainMenuScreen;
import org.yeb.model.Level;
import org.yeb.model.Node;
import org.yeb.util.Skins;


public class GameScreen extends ScreenAdapter {

    private Option<NodeLike> marked = Option.none();
    private final YebGame game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private Level level;

    private Stage stage = new Stage();

    public GameScreen(YebGame game, Level level) {
        this.game = game;
        this.level = level;
        camera.setToOrtho(false, 1000F, 800F);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return mouseDown(screenX, screenY, button);
            }
        });
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);

        Skin skin = Skins.makeSkin(game.font, Color.GRAY);
        TextButton button = new TextButton("Button1", skin);
        button.setPosition(20, 20);
        button.addListener(new InputListener() {

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Press a Button - UP");
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("Press a Button - DOWN");
                return true;
            }
        });
        stage.addActor(button);
    }

    private boolean mouseDown(int x, int y, int button) {
        if (button != Input.Buttons.LEFT) {
            return false;
        }
        Vector3 touchPos = new Vector3(x, y, 0F);
        camera.unproject(touchPos);
        return level.nodes
                       .find(node -> touchPos.dst(node.x, node.y, 0F) < 10F)
                       .map(node -> NodeLike.ofNodeId(node.id))
                       .orElse(() -> level.edges.find(
                               edge -> {
                                   Vector2 middle = level.middle(edge);
                                   return touchPos.dst(middle.x, middle.y, 0F) < 10F;
                               }).map(NodeLike::ofEdge))
                       .map(picked -> {
                           if (marked.isEmpty()) {
                               marked = Option.of(picked);
                           } else {
                               Tuple2<Level, Integer> tuple1 = picked.withNode(level);
                               Level levelWithEdge1 = tuple1._1;
                               Tuple2<Level, Integer> tuple2 = marked.get().withNode(levelWithEdge1);
                               Level levelWithEdge2 = tuple2._1;
                               level = levelWithEdge2.createEdge(tuple1._2, tuple2._2).getOrElse(level);
                               marked = Option.none();
                           }
                           return true;
                       })
                       .getOrElse(false);
    }

    @Override
    public void render(float delta) {
        level = level.wiggle();

        Color background = game.background;
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.setColor(Color.DARK_GRAY);
        game.font.draw(game.batch,
                "Winning distance: " + level.winDistance + ", current distance: " + level.distanceEdges(),
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
            sr.setColor(marked.flatMap(m -> m.asEdge().map(e -> e == edge)).getOrElse(false)
                                ? Color.RED
                                : Color.PURPLE);
            sr.circle(middle.x, middle.y, 10F);
        });
        level.nodes.forEach(node -> {
            if (marked.map(m -> node.id == m.asNode(level).id).getOrElse(false)) sr.setColor(Color.RED);
            else if (node.leaf) sr.setColor(Color.BLUE);
            else sr.setColor(Color.GREEN);
            sr.circle(node.x, node.y, 10F);
        });
        sr.end();

        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }

        stage.act();
        stage.draw();
    }

}