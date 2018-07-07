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

public class YebGame extends Game implements CollectingDisposable {

    private static YebGame INSTANCE = new YebGame();

    public SpriteBatch batch;
    public BitmapFont font;
    public Color background = new Color(0.87F, 0.85F, 0.85F, 1F);
    public Texture titleBanner;
    public Texture whitePixel;
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
        titleBanner = register(new Texture("yeb_title.png"));
        whitePixel = register(new Texture("whitePixel.png"));
        levelSolvedBanner = register(new Texture("level_solved.png"));
        register(SoundBank.init());
        this.setScreen(new MenuScreen());
        SoundBank.playBackgroundNoise();
    }

    @Override
    public void dispose() {
        super.dispose();
        CollectingDisposable.super.dispose();
    }

    @Override
    public List<Disposable> disposables() {
        return disposables;
    }
}
