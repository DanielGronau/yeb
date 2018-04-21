package org.yeb.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.yeb.YebGame;

public class DesktopLauncher {
    public static void main(String ... args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 1000;
        config.height = 800;
        new LwjglApplication(new YebGame(), config);
    }
}
