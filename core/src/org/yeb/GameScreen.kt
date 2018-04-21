package org.yeb

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3


class GameScreen(private val game: YebGame, private var level: Level) : Screen {

    private var markedNodeId = 0

    private val camera = OrthographicCamera().also {
        it.setToOrtho(false, 1000F, 800F)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.87F, 0.85F, 0.85F, 1F)
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)

        // tell the camera to update its matrices.
        camera.update()

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.projectionMatrix = camera.combined

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin()
        game.font.draw(game.batch, "Current distance: ${level.distanceEdges()}, winning distance: ${level.winDistance}", 10F, 780F)
        game.batch.end()

        val sr = ShapeRenderer()
        sr.setAutoShapeType(true)
        sr.projectionMatrix = camera.combined
        sr.color = Color.BLACK

        sr.begin(ShapeRenderer.ShapeType.Filled)
        level.edges.forEach { edge ->
            val n1 = level.nodeById(edge.id1)
            val n2 = level.nodeById(edge.id2)
            sr.rectLine(n1.x, n1.y, n2.x, n2.y, 6F)
        }
        level.nodes.forEach { node ->
            sr.color = if (node.id == markedNodeId) Color.RED else
                if (node.leaf) Color.BLUE else Color.GREEN
            sr.circle(node.x, node.y, 10F)
        }
        sr.end()

        // process user input
        if (Gdx.input.isTouched) {
            val touchPos = Vector3()
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0F)
            camera.unproject(touchPos)
            level.nodes.forEach {node ->
                if (touchPos.dst(node.x, node.y, 0F) < 10F) {
                    when (markedNodeId) {
                        0 -> markedNodeId = node.id
                        node.id -> markedNodeId = 0
                        else -> {
                            level = level.createEdge(node.id, markedNodeId)
                            markedNodeId = 0
                        }
                    }
                }
            }
        }

        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            game.screen = MainMenuScreen(game)
            dispose()
        }

        level = level.wiggle()
    }

    override fun resize(width: Int, height: Int) {}

    override fun show() {}

    override fun hide() {}

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {}

}
