package org.yeb;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

import java.util.Random;

public final class SoundBank implements Disposable {

    private static final SoundBank INSTANCE = new SoundBank();

    private static final double HALF_TONE = Math.pow(2, 1.0 / 12);
    private static final int[] SCALE = {0, 2, 5, 7, 11, 12}; //c,d,f,g,h,C
    private static final Random RANDOM = new Random();


    public boolean sfx = true;
    public boolean music = true;
    private Music menuMusic;
    private Sound jointClick;
    private Sound buttonClick;
    private Sound winSound;
    private Sound invalidClick;

    private SoundBank() {}

    static SoundBank init() {
        INSTANCE.jointClick = Gdx.audio.newSound(Gdx.files.internal("aTone.mp3"));
        INSTANCE.buttonClick = Gdx.audio.newSound(Gdx.files.internal("button.mp3"));
        INSTANCE.winSound = Gdx.audio.newSound(Gdx.files.internal("tada.mp3"));
        INSTANCE.invalidClick = Gdx.audio.newSound(Gdx.files.internal("invalid.mp3"));

        INSTANCE.menuMusic = Gdx.audio.newMusic(Gdx.files.internal("gameMenu.mp3"));
        INSTANCE.menuMusic.setLooping(true);
        INSTANCE.menuMusic.setVolume(0.5F);
        return INSTANCE;
    }

    public static void playMenuMusic() {
        if (INSTANCE.music) {
            INSTANCE.menuMusic.play();
        }
    }

    public static void stopMenuMusic() {
        if (INSTANCE.menuMusic.isPlaying()) {
            INSTANCE.menuMusic.stop();
        }
    }

    public static void buttonClick() {
        if (INSTANCE.sfx) {
            INSTANCE.buttonClick.play();
        }
    }

    public static void jointClick() {
        if (INSTANCE.sfx) {
            float pan = (RANDOM.nextFloat() % 2) - 1;
            float pitch = (float) Math.pow(HALF_TONE, SCALE[RANDOM.nextInt(6)]);
            INSTANCE.jointClick.play(1F, pitch, pan);
        }
    }

    public static void invalidClick() {
        if (INSTANCE.sfx) {
            INSTANCE.invalidClick.play();
        }
    }

    public static void winSound() {
        if (INSTANCE.sfx) {
            INSTANCE.winSound.play(0.3F);
        }
    }

    public static void toggleSfx() {
        INSTANCE.sfx = ! INSTANCE.sfx;
    }

    public static void toggleMusic() {
        INSTANCE.music = ! INSTANCE.music;
        if (INSTANCE.music) {
            playMenuMusic();
        } else {
            stopMenuMusic();
        }
    }

    @Override
    public void dispose() {
        menuMusic.dispose();
        jointClick.dispose();
        buttonClick.dispose();
        winSound.dispose();
        invalidClick.dispose();
    }
}
