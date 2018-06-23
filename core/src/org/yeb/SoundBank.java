package org.yeb;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public final class SoundBank implements CollectingDisposable {

    private static final SoundBank INSTANCE = new SoundBank();
    private static final double HALF_TONE = Math.pow(2, 1.0 / 12);
    private static final int[] SCALE = {0, 2, 5, 7, 11, 12}; //c,d,f,g,h,C
    private static final Random RANDOM = new Random();

    public boolean sfx = true;
    public boolean music = true;
    private Music backgroundNoise;
    private Sound jointClick;
    private Sound buttonClick;
    private Sound winSound;
    private Sound invalidClick;
    private final List<Disposable> disposables = new LinkedList<>();

    private SoundBank() {}

    static SoundBank init() {
        INSTANCE.initialize();
        return INSTANCE;
    }

    private void initialize() {
        jointClick = register(Gdx.audio.newSound(Gdx.files.internal("aTone.mp3")));
        buttonClick = register(Gdx.audio.newSound(Gdx.files.internal("button.mp3")));
        winSound = register(Gdx.audio.newSound(Gdx.files.internal("tada.mp3")));
        invalidClick = register(Gdx.audio.newSound(Gdx.files.internal("invalid.mp3")));

        backgroundNoise = register(Gdx.audio.newMusic(Gdx.files.internal("rain.ogg")));
        backgroundNoise.setLooping(true);
        backgroundNoise.setVolume(0.3F);
    }

    static void playBackgroundNoise() {
        if (INSTANCE.music) {
            INSTANCE.backgroundNoise.play();
        }
    }

    private static void stopMenuMusic() {
        if (INSTANCE.backgroundNoise.isPlaying()) {
            INSTANCE.backgroundNoise.stop();
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
        INSTANCE.click(INSTANCE.invalidClick,1);
    }

    public static void winSound() {
        INSTANCE.click(INSTANCE.winSound,0.5F);
    }

    public static void buttonClick() {
        INSTANCE.click(INSTANCE.buttonClick,1);
    }

    private void click(Sound sound, float volume) {
        if (sfx) {
            sound.play(volume);
        }
    }

    public static void toggleSfx() {
        INSTANCE.sfx = ! INSTANCE.sfx;
    }

    public static void toggleMusic() {
        INSTANCE.music = ! INSTANCE.music;
        if (INSTANCE.music) {
            playBackgroundNoise();
        } else {
            stopMenuMusic();
        }
    }

    @Override
    public List<Disposable> disposables() {
        return disposables;
    }
}
