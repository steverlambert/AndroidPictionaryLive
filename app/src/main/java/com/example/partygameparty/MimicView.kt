package com.example.partygameparty

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat

// Stroke width for the the paint
private const val STROKE_WIDTH = 8f

/**
 * Custom view that mirrors what is drawn on a custom DrawView
 */
class MimicView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap
    private lateinit var frame: Rect

    // Set the paint configuration
    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        Log.d("partystuff", "into sizechanged, $width, $height")

        // Store width and height for scaling
        PictData.viewWidth = width
        PictData.viewHeight = height

        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)

        // Create a frame around the picture
        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
        extraCanvas.drawRect(frame, paint)
    }

    // Where the drawing actually happens
    // sharedPath are the strokes drawn on the DrawView
    fun drawStuff() {
        extraCanvas.drawPath(PictData.sharedPath, paint)
        extraCanvas.drawBitmap(extraBitmap, 0f, 0f, null)
        invalidate()
    }

    fun clearDrawing() {
        onSizeChanged(
            PictData.viewWidth,
            PictData.viewHeight,
            PictData.viewWidth,
            PictData.viewHeight
        )
        invalidate()
    }
}
