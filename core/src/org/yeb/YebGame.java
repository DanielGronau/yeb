package org.yeb;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.yeb.menu.MenuScreen;

public class YebGame extends Game {

    private static YebGame INSTANCE = new YebGame();

    public SpriteBatch batch;
    public BitmapFont font;
    public Color background = new Color(0.87F, 0.85F, 0.85F, 1F);
    public boolean sfx = true;
    public boolean music = true;
    private Music menuMusic;
    private Sound jointClick;
    private Sound buttonClick;
    private Sound winSound;

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
        menuMusic.setVolume(0.5F);
        jointClick = Gdx.audio.newSound(Gdx.files.internal("aTone.mp3"));
        buttonClick = Gdx.audio.newSound(Gdx.files.internal("button.mp3"));
        winSound = Gdx.audio.newSound(Gdx.files.internal("tada.mp3"));
        this.setScreen(new MenuScreen());
    }

    public void playMenuMusic() {
        if (music) {
            menuMusic.play();
        }
    }

    public void stopMenuMusic() {
        if (menuMusic.isPlaying()) {
            menuMusic.stop();
        }
    }

    public void buttonClick() {
        if (sfx) {
            buttonClick.play();
        }
    }

    public void jointClick(float volume, float pitch, float pan) {
        if (sfx) {
            jointClick.play(volume, pitch, pan);
        }
    }

    public void winSound(float volume) {
        if (sfx) {
            winSound.play(volume);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        menuMusic.dispose();
        buttonClick.dispose();
        jointClick.dispose();
        winSound.dispose();
    }
}
