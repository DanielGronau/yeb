package org.yeb

import com.badlogic.gdx.math.Vector2

data class Node(val id: Int, val x: Float, val y: Float, val leaf: Boolean) {
   fun toVector() = Vector2(x,y)
}