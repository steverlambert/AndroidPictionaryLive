package com.example.partygameparty

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat


// Stroke width for the the paint.
private const val STROKE_WIDTH = 8f

/**
 * Custom view that allows a user to draw with their finger
 * Sends draw strokes to a firebase database
 */

class MyDrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Holds the path being drawn
    private var path = Path()
    private var h0 = 0
    private var w0 = 0

    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private lateinit var frame: Rect

    // Set up the paint configuration
    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    // only draw if finger has moves more than tolerance
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var currentX = 0f
    private var currentY = 0f

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    // Called on startup and whenever the drawview size changes
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        // store height and width to be sent to the database
        w0 = width
        h0 = height
        if (PictData.hosting) {
            PictData.setHostSize(width, height)
        }

        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)

        // create a rectangular frame around the picture
        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
        extraCanvas.drawRect(frame, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    /**
     * The following methods are called based on the touch event
     * The path object is changed based on the touch locations
     * Each time the path object is changed, the command and data is sent to the database
     */
    private fun touchStart() {
        path.reset()
        PictData.writeToDatabase("RESET", 0f, 0f, 0f, 0f)
        path.moveTo(motionTouchEventX, motionTouchEventY)
        PictData.writeToDatabase("MOVE", motionTouchEventX, motionTouchEventY, 0f, 0f)

        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove() {
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching point (x1,y1), and ending at (x2,y2)
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )

            PictData.writeToDatabase(
                "QUAD",
                motionTouchEventX,
                motionTouchEventY,
                currentX,
                currentY
            )

            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to save it.
            extraCanvas.drawPath(path, paint)
        }
        invalidate()
    }

    fun clearDrawing() {
        PictData.writeToDatabase("CLEAR", 0f, 0f, 0f, 0f)
        // call onsizechanged to clear
        onSizeChanged(w0, h0, w0, h0)
        path.reset()
        invalidate()
    }

    private fun touchUp() {
        // Reset the path so it doesn't get drawn again.
        path.reset()
        PictData.writeToDatabase("RESET", 0f, 0f, 0f, 0f)
    }
}
