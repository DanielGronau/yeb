package org.yeb.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.yeb.YebGame;
import org.yeb.game.GameScreen;
import org.yeb.model.Level;
import org.yeb.util.UiHelper;

public class MenuScreen extends ScreenAdapter {

    private final Stage stage = new Stage();
    private final Color background;
    private final Texture title = new Texture("yeb_title.png");
    private final YebGame game;

    public MenuScreen(YebGame game) {
        this.game = game;
        background = game.background;
        Skin skin = UiHelper.makeSkin(game.font, Color.BLUE);
        for (int index = 0; index < Levels.LEVELS.size(); index++) {
            Level level = Levels.LEVELS.get(index);
            stage.addActor(UiHelper.makeButton(skin, "Level " + (index + 1), 100, 500 - 50 * index,
                    () -> {
                        game.setScreen(new GameScreen(game, level));
                        dispose();
                    }));
        }
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(background.r, background.g, background.b, background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(title, 100, 650);
        game.batch.end();

        stage.act();
        stage.draw();
    }

}