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
    private var markedEdge: Edge? = null

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

        sr.begin(ShapeRenderer.ShapeType.Filled)
        level.edges.forEach { edge ->
            val n1 = level.nodeById(edge.id1)
            val n2 = level.nodeById(edge.id2)
            sr.color = Color.BLACK
            sr.rectLine(n1.x, n1.y, n2.x, n2.y, 6F)
            val middle = level.middle(edge)
            sr.color = if (edge == markedEdge) Color.RED else Color.PURPLE
            sr.circle(middle.x, middle.y, 10F)
        }
        level.nodes.forEach { node ->
            sr.color = when {
                node.id == markedNodeId -> Color.RED
                node.leaf -> Color.BLUE
                else -> Color.GREEN
            }
            sr.circle(node.x, node.y, 10F)
        }
        sr.end()

        // process user input
        if (Gdx.input.isTouched) {
            val touchPos = Vector3()
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0F)
            camera.unproject(touchPos)
            val pickedNode = level.nodes.firstOrNull { node -> touchPos.dst(node.x, node.y, 0F) < 10F }
            if (pickedNode != null) {
                if (markedNodeId == 0 && markedEdge == null) {
                    markedNodeId = pickedNode.id
                } else if (markedEdge != null) {
                    val (levelWithEdge, id) = level.splitEdge(markedEdge!!)
                    level = levelWithEdge.createEdge(id, pickedNode.id) ?: level
                    markedEdge = null
                } else if (markedNodeId == pickedNode.id) {
                    markedNodeId = 0
                } else {
                    level = level.createEdge(markedNodeId, pickedNode.id) ?: level
                    markedNodeId = 0
                }
            } else {
                val pickedEdge = level.edges.firstOrNull { edge ->
                    val middle = level.middle(edge)
                    touchPos.dst(middle.x, middle.y, 0F) < 10F
                }
                if (pickedEdge != null) {
                    if (markedNodeId == 0 && markedEdge == null) {
                        markedEdge = pickedEdge
                    } else if (markedNodeId != 0) {
                        val (levelWithEdge, id) = level.splitEdge(pickedEdge)
                        level = levelWithEdge.createEdge(id, markedNodeId) ?: level
                        markedNodeId = 0
                    } else if (markedEdge == pickedEdge) {
                        markedEdge = null
                    } else {
                        val (levelWithEdge1, id1) = level.splitEdge(pickedEdge)
                        val (levelWithEdge2, id2) = levelWithEdge1.splitEdge(markedEdge!!)
                        level = levelWithEdge2.createEdge(id1, id2) ?: level
                        markedEdge = null
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
