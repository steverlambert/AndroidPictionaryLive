package com.example.partygameparty

import android.graphics.Path
import android.util.Log
import com.google.firebase.database.DataSnapshot

/**
 * Pictionary Data object
 * Stores data and logic used by the DrawView and MimicView
 */

object PictData {

    private lateinit var mimicView: MimicView

    // width and height of MimicView
    var viewWidth = 0
    var viewHeight = 0
    // width and height of DrawView
    var hostHeight = 0
    var hostWidth = 0
    // scaling ratios
    var hRatio = 0f
    var wRatio = 0f

    private var cX = 0f
    private var cY = 0f
    private var mX = 0f
    private var mY = 0f
    private var action = "RESET"

    var team = "TEAM1"
    var hosting = true

    var sharedPath = Path()

    // set the mimicview to draw the received commands on
    fun setFriend(mimic: MimicView) {
        mimicView = mimic
    }

    fun setHostSize(width: Int, height: Int) {
        val seg = pathSeg("SIZE", 0f, 0f, 0f, 0f, width, height)
        dbManager.database.child("PICTIONARY").child(team).setValue(seg)
    }

    fun writeToDatabase(command: String, motX: Float, motY: Float, curX: Float, curY: Float) {
        if (hosting) {
            val seg = pathSeg(command, curX, curY, motX, motY, 0, 0)
            dbManager.database.child("PICTIONARY").child(team).setValue(seg)
        }
    }

    fun loadPathFromDatabase(data: DataSnapshot) {

        // get the command from the database
        action = data.child("command").value.toString()

        when (action) {
            "CLEAR" -> {
                mimicView.clearDrawing()
            }
            "SIZE" -> {
                hostHeight = data.child("height").value.toString().toInt()
                hostWidth = data.child("width").value.toString().toInt()
                hRatio = viewHeight.toFloat() / hostHeight.toFloat()
                wRatio = (viewWidth.toFloat() / hostWidth.toFloat())
            }
            "RESET" -> {
                Log.d("partystuff", "RESET")
                sharedPath.reset()
            }
            "MOVE" -> {
                mX = data.child("motX").value.toString().toFloat() * wRatio
                mY = data.child("motY").value.toString().toFloat() * hRatio

                sharedPath.moveTo(mX, mY)
            }
            "QUAD" -> {
                mX = data.child("motX").value.toString().toFloat() * wRatio
                mY = data.child("motY").value.toString().toFloat() * hRatio
                cX = data.child("curX").value.toString().toFloat() * wRatio
                cY = data.child("curY").value.toString().toFloat() * hRatio

                sharedPath.quadTo(cX, cY, (mX + cX) / 2, (mY + cY) / 2)
                mimicView.drawStuff()
            }
        }
    }

    // segment of the path object to send to the database
    data class pathSeg(
        var command: String = "",
        var curX: Float = 0f,
        var curY: Float = 0f,
        var motX: Float = 0f,
        var motY: Float = 0f,
        var width: Int = 0,
        var height: Int = 0
    )

}