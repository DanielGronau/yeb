package org.yeb;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.yeb.menu.MenuScreen;

public class YebGame extends Game {

    private static YebGame INSTANCE = new YebGame();

    public SpriteBatch batch;
    public BitmapFont font;
    public Color background = new Color(0.87F, 0.85F, 0.85F, 1F);
    public Music menuMusic;

    private YebGame() {}

    public static YebGame instance() {
        return INSTANCE;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        //Use LibGDX's default Arial font.
        font = new BitmapFont();
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("gameMenu.mp3"));
        menuMusic.setLooping(true);
        this.setScreen(new MenuScreen());
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
