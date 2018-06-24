package org.yeb.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.yeb.SoundBank;
import org.yeb.YebGame;
import org.yeb.game.GameScreen;
import org.yeb.model.Level;
import org.yeb.util.Droplet;
import org.yeb.util.UiHelper;

import java.util.HashSet;
import java.util.Set;

import static org.yeb.menu.Levels.WON_LEVELS;

public class MenuScreen extends ScreenAdapter {

    private final Stage stage = new Stage();
    private final ShapeRenderer sr = new ShapeRenderer();
    private final OrthographicCamera camera = new OrthographicCamera();
    private Set<Droplet> droplets = new HashSet<>();

    public MenuScreen() {
        camera.setToOrtho(false, 1000F, 800F);
        camera.update();
        YebGame game = YebGame.instance();
        Skin skin = UiHelper.makeSkin(game.font, Color.BLUE);
        Skin wonSkin = UiHelper.makeSkin(game.font, new Color(0x6495EDFF));
        for (int index = 0; index < Levels.LEVELS.size(); index++) {
            Level level = Levels.LEVELS.get(index);
            Skin levelSkin = WON_LEVELS.contains(index) ? wonSkin : skin;
            stage.addActor(UiHelper.makeButton(levelSkin, "Level " + (index + 1), 100, 500 - 50 * index,
                    () -> {
                        game.setScreen(new GameScreen(level));
                        dispose();
                    }));
        }

        skin = UiHelper.makeSkin(game.font, Color.PURPLE);
        stage.addActor(UiHelper.makeButton(skin, "Toggle SFX", 100, 50, SoundBank::toggleSfx));
        stage.addActor(UiHelper.makeButton(skin, "Toggle Rain", 250, 50, SoundBank::toggleMusic));

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {

        YebGame game = YebGame.instance();
        Gdx.gl.glClearColor(game.background.r, game.background.g, game.background.b, game.background.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        droplets = Droplet.generateAndRemove(delta, droplets);
        Droplet.render(camera, sr, droplets, delta);

        game.batch.begin();
        game.batch.draw(YebGame.instance().titleBanner, 100, 650);
        game.batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
        super.dispose();
    }
}