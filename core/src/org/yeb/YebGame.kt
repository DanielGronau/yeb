package org.yeb

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class YebGame : Game() {
    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont

    override fun create() {
        batch = SpriteBatch()
        //Use LibGDX's default Arial font.
        font = BitmapFont()
        this.setScreen(MainMenuScreen(this))
    }

    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}
