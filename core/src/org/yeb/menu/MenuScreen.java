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

    private final static String[] helpText = {
        "Yeb is a small puzzle game with the goal to create the shortest \"net\" between given points.",
        "To add a connection, you simply click on both points you want to connect.",
        "The first point will be marked with an orange circle, and can be unselected as well.",
        "However, it is not always possible to connect points, e.g. if this would create a cycle.",
        "Once a new connection is made, the net tries to shorten itself, like rubber bands would do.",
        "You win a level when the net is shorter than a given distance (shown in the top left).",
        "In higher levels, there are gray obstacles, which you have to avoid to win, too.",
        "You may go back to the menu, reset a level or undo the last move with the buttons at the bottom.",
        "Further, you can remove connections by right-clicking at their purple mid-points.",
        "",
        "Yeb is a free game and Open Source software (https://github.com/DanielGronau/yeb). Have fun!"
    };

    private final Stage stage = new Stage();
    private final ShapeRenderer sr = new ShapeRenderer();
    private final OrthographicCamera camera = new OrthographicCamera();
    private Set<Droplet> droplets = new HashSet<>();
    private boolean showHelp = false;

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

        skin = UiHelper.makeSkin(game.font, Color.DARK_GRAY);
        stage.addActor(UiHelper.makeButton(skin, "Help", 400, 50, () -> showHelp = !showHelp));

        skin = UiHelper.makeSkin(game.font, new Color(0xB22222FF));
        stage.addActor(UiHelper.makeButton(skin, "Exit", 850, 50, Gdx.app::exit));

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
        showHelp();
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

    private void showHelp() {
        if (showHelp) {
            YebGame game = YebGame.instance();
            game.batch.setColor(.2f, .2f, .2f, .5f);
            game.batch.draw(YebGame.instance().whitePixel, 250, 150, 700, 380);
            game.batch.setColor(Color.WHITE);
            int y = 500;
            for (String s : helpText) {
                game.font.draw(game.batch, s, 280, y);
                y -= 30;
            }
        }
    }
}