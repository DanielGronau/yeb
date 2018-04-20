package org.yeb

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Screen
//import com.badlogic.gdx.audio.Music;
//import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils

class GameScreen(internal val game: YebGame) : Screen {

    private val dropImage = Texture(Gdx.files.internal("droplet.png"))
    private val bucketImage = Texture(Gdx.files.internal("bucket.png"))
    //Sound dropSound;
    //Music rainMusic;
    private val camera = OrthographicCamera().also {
         it.setToOrtho(false, 800F, 480F)
    }
    private val bucket = Rectangle((800 / 2 - 64 / 2).toFloat(),20F, 64F, 64F)
    private val raindrops = Array<Rectangle>()

    private var lastDropTime: Long = 0
    private var dropsGathered: Int = 0

    private fun spawnRaindrop() {
        val raindrop = Rectangle()
        raindrop.x = MathUtils.random(0, 800 - 64).toFloat()
        raindrop.y = 480F
        raindrop.width = 64F
        raindrop.height = 64F
        raindrops.add(raindrop)
        lastDropTime = TimeUtils.nanoTime()
    }

    override fun render(delta: Float) {
        // clear the screen with a dark blue color. The
        // arguments to glClearColor are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        Gdx.gl.glClearColor(0F, 0F, 0.2F, 1F)
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT)

        // tell the camera to update its matrices.
        camera.update()

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.projectionMatrix = camera.combined

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin()
        game.font.draw(game.batch, "Drops Collected: $dropsGathered", 10F, 470F)
        game.batch.draw(bucketImage, bucket.x, bucket.y)
        raindrops.forEach { raindrop -> game.batch.draw(dropImage, raindrop.x, raindrop.y) }
        game.batch.end()

        // process user input
        if (Gdx.input.isTouched) {
            val touchPos = Vector3()
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0F)
            camera.unproject(touchPos)
            bucket.x = touchPos.x - 64 / 2
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT))
            bucket.x -= 400 * Gdx.graphics.deltaTime
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            bucket.x += 400 * Gdx.graphics.deltaTime

        // make sure the bucket stays within the screen bounds
        if (bucket.x < 0)
            bucket.x = 0F
        if (bucket.x > 800 - 64)
            bucket.x = (800 - 64).toFloat()

        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnRaindrop()

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we play back
        // a sound effect as well.
        val iter = raindrops.iterator()
        while (iter.hasNext()) {
            val raindrop = iter.next()
            raindrop.y -= 200 * Gdx.graphics.deltaTime
            if (raindrop.y + 64 < 0)
                iter.remove()
            if (raindrop.overlaps(bucket)) {
                dropsGathered++
                //dropSound.play();
                iter.remove()
            }
        }

        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            game.screen = MainMenuScreen(game)
            dispose()
        }
    }

    override fun resize(width: Int, height: Int) {}

    override fun show() {
        // start the playback of the background music
        // when the screen is shown
        //rainMusic.play();
    }

    override fun hide() {}

    override fun pause() {}

    override fun resume() {}

    override fun dispose() {
        dropImage.dispose()
        bucketImage.dispose()
        // dropSound.dispose();
        // rainMusic.dispose();
    }

}
