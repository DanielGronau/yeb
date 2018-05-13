package org.yeb;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import org.yeb.menu.MenuScreen;

import java.util.LinkedList;
import java.util.List;

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
    public Texture titleBanner;
    public Texture levelSolvedBanner;
    private final List<Disposable> disposables = new LinkedList<>();

    private YebGame() {}

    public static YebGame instance() {
        return INSTANCE;
    }

    @Override
    public void create() {
        batch = register(new SpriteBatch());
        font = register(new BitmapFont()); //default Arial font
        jointClick = register(Gdx.audio.newSound(Gdx.files.internal("aTone.mp3")));
        buttonClick = register(Gdx.audio.newSound(Gdx.files.internal("button.mp3")));
        winSound = register(Gdx.audio.newSound(Gdx.files.internal("tada.mp3")));
        titleBanner = register(new Texture("yeb_title.png"));
        levelSolvedBanner = register(new Texture("level_solved.png"));
        menuMusic = register(Gdx.audio.newMusic(Gdx.files.internal("gameMenu.mp3")));
        menuMusic.setLooping(true);
        menuMusic.setVolume(0.5F);

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

    private <T extends Disposable> T register(T disposable) {
        disposables.add(disposable);
        return disposable;
    }

    @Override
    public void dispose() {
        disposables.forEach(Disposable::dispose);
    }
}
