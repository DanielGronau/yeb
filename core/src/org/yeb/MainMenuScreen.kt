package org.yeb

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera

class MainMenuScreen(internal val game: YebGame) : Screen {
    private val camera = OrthographicCamera().also {
        it.setToOrtho(false, 800f, 480f)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()
        game.batch.projectionMatrix = camera.combined

        game.batch.begin()
        game.font.draw(game.batch, "Welcome to Yeb!!! ", 100f, 150f)
        game.font.draw(game.batch, "Tap anywhere to begin!", 100f, 100f)
        game.batch.end()

        if (Gdx.input.isTouched) {
            val node1 = Node(1, 100F,100F, true)
            val node2 = Node(2, 900F,100F, true)
            val node3 = Node(3, 900F,700F, true)
            val node4 = Node(4, 100F,700F, true)

            game.screen = GameScreen(game, Level(setOf(node1, node2, node3, node4), setOf(), 1840F))
            dispose()
        }
    }

    override fun resize(width: Int, height: Int) {}

    override fun show() {}

    override fun hide() {}

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {}
}