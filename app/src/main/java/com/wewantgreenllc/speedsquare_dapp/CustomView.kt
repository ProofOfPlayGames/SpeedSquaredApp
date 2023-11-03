package com.alvinalexander.rectangledemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

class CustomView(context: Context?) : View(context) {
    private val rectangle: Rect
    private val paint: Paint

    init {
        val x = 50
        val y = 50
        val sideLength = 200

        // create a rectangle that we'll draw later
        rectangle = Rect(x, y, sideLength, sideLength)

        // create the Paint and set its color
        paint = Paint()
        paint.color = Color.GRAY
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.drawRect(rectangle, paint)
    }
}