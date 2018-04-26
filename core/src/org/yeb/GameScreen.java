package org.yeb;

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
import org.yeb.model.Level;
import org.yeb.model.Node;


class GameScreen extends ScreenAdapter {

    private Option<NodeLike> marked = Option.none();
    private final YebGame game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private Level level;
    Skin skin;

    private Stage stage = new Stage();

    GameScreen(YebGame game, Level level) {
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

        makeSkin();
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

    private Skin makeSkin() {
        skin = new Skin();
        skin.add("default", game.font);

        //Create a texture
        Pixmap pixmap = new Pixmap(70, 30, Pixmap.Format.RGB888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("background", new Texture(pixmap));

        //Create a button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("background", Color.GRAY);
        textButtonStyle.down = skin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.over = skin.newDrawable("background", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);
        return skin;
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
        game.font.setColor(Color.DARK_GRAY);
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
