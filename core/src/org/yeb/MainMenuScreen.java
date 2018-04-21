package org.yeb;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import io.vavr.collection.HashSet;
import org.yeb.model.Node;
import org.yeb.model.Level;

class MainMenuScreen implements Screen {

    private final YebGame game;
    private final OrthographicCamera camera = new OrthographicCamera();

    MainMenuScreen(YebGame game) {
        this.game = game;
        camera.setToOrtho(false, 1000f, 800f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0F, 0F, 0.2F, 1F);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Yeb!!! ", 100F, 150F);
        game.font.draw(game.batch, "Tap anywhere to begin!", 100F, 100F);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            Node node1 = new Node(1, 100F, 100F, true);
            Node node2 = new Node(2, 900F, 100F, true);
            Node node3 = new Node(3, 900F, 700F, true);
            Node node4 = new Node(4, 100F, 700F, true);

            game.setScreen(new GameScreen(game,
                    new Level(HashSet.of(node1, node2, node3, node4), HashSet.empty(), 1840F)));
            dispose();
        }
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