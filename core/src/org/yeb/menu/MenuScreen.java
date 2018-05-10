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
    private final Texture title = new Texture("yeb_title.png");

    public MenuScreen() {
        Skin skin = UiHelper.makeSkin(YebGame.instance().font, Color.BLUE);
        for (int index = 0; index < Levels.LEVELS.size(); index++) {
            Level level = Levels.LEVELS.get(index);
            stage.addActor(UiHelper.makeButton(skin, "Level " + (index + 1), 100, 500 - 50 * index,
                    () -> {
                        YebGame.instance().setScreen(new GameScreen(level));
                        YebGame.instance().menuMusic.stop();
                        dispose();
                    }));
        }
        Gdx.input.setInputProcessor(stage);
        YebGame.instance().menuMusic.play();
    }

    @Override
    public void render(float delta) {
        YebGame game = YebGame.instance();
        Gdx.gl.glClearColor(game.background.r, game.background.g, game.background.b, game.background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(title, 100, 650);
        game.batch.end();

        stage.act();
        stage.draw();
    }

}