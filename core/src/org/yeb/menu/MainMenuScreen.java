package org.yeb.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import org.yeb.YebGame;
import org.yeb.game.GameScreen;
import org.yeb.model.Level;
import org.yeb.util.Skins;

public class MainMenuScreen extends ScreenAdapter {

    private final Stage stage = new Stage();
    private final Color background;


    public MainMenuScreen(YebGame game) {
        background = game.background;
        Skin skin = Skins.makeSkin(game.font, Color.BLUE);
        Levels.LEVELS.zipWithIndex().forEach(tuple -> {
            Level level = tuple._1;
            int index = tuple._2;

            TextButton button = new TextButton("Level " + (index + 1), skin);
            button.setPosition(100, 700 - 50 * index);
            button.addListener(new InputListener() {

                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    game.setScreen(new GameScreen(game, level));
                    dispose();
                    return true;
                }
            });
            stage.addActor(button);
        });
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

}