package org.yeb.client;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import org.yeb.YebGame;

public class HtmlLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(1000, 800);
        return cfg;
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new YebGame();
    }
}