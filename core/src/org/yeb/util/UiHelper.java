package org.yeb.util;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import org.yeb.SoundBank;
import org.yeb.YebGame;

public class UiHelper {

    public static Skin makeSkin(BitmapFont font, Color baseColor) {
        Skin skin = new Skin();
        skin.add("default", font);

        //Create a texture
        Pixmap pixmap = new Pixmap(70, 30, Pixmap.Format.RGB888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("background", new Texture(pixmap));

        //Create a button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("background", new Color(baseColor));
        textButtonStyle.down = skin.newDrawable("background", new Color(baseColor).mul(0.8F));
        textButtonStyle.font = skin.getFont("default");

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.background = skin.newDrawable("background", new Color(baseColor));
        textFieldStyle.focusedBackground = textFieldStyle.background;
        textFieldStyle.font = skin.getFont("default");
        textFieldStyle.fontColor = skin.getFont("default").getColor();

        skin.add("default", textButtonStyle);
        skin.add("default", textFieldStyle);
        return skin;
    }

    public static TextButton makeButton(Skin skin, String caption, int x, int y, Runnable action) {
        TextButton button = new TextButton(caption, skin);
        button.setPosition(x, y);
        button.setWidth(100F);
        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    SoundBank.buttonClick();
                    action.run();
                }
                return true;
            }
        });
        return button;
    }

}
