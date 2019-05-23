package com.tobiasschuerg.progresscircle

/**
 * Circular progress bar with centered text.
 *
 * Initially based on a blog post by NGUYEN VAN TOAN:
 * https://techblog.vn/how-to-make-circle-custom-progress-bar-in-android
 */

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ProgressCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val arcBackgroundPain: Paint = Paint().apply {
        isDither = true
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val arcProgressPaint: Paint = Paint().apply {
        isDither = true
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val textPaint: Paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, android.R.color.black)
        strokeWidth = 2f
    }

    private val textBounds = Rect()
    private val circleBounds = RectF()

    private var centerX: Int = 0
    private var centerY: Int = 0

    private var radius: Int = 40
    private var clockwise: Boolean = false

    private var progress: Int = 75
    private var progressPercentage: Float = 0.75f
    var progressMax: Int = 100

    init {
        val typedValue = TypedValue()

        context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        var colorInactive = typedValue.data

        context.theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
        var colorActive = typedValue.data

        context.theme.obtainStyledAttributes(attrs, R.styleable.ProgressCircleView, defStyleAttr, defStyleRes).apply {
            try {
                val thickness: Float = getDimensionPixelSize(R.styleable.ProgressCircleView_thickness, 10).toFloat()
                arcBackgroundPain.strokeWidth = thickness
                arcProgressPaint.strokeWidth = thickness

                val textSize = getDimensionPixelSize(R.styleable.ProgressCircleView_textSize, 10).toFloat()
                textPaint.textSize = textSize

                radius = getDimensionPixelSize(R.styleable.ProgressCircleView_radius, radius)

                clockwise = getBoolean(R.styleable.ProgressCircleView_clockwise, false)
                colorInactive = getColor(R.styleable.ProgressCircleView_colorInactive, colorInactive)
                arcBackgroundPain.color = colorInactive
                colorActive = getColor(R.styleable.ProgressCircleView_colorActive, colorActive)
                arcProgressPaint.color = colorActive
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidthHeight = View.MeasureSpec.getSize(radius)
        centerX = viewWidthHeight / 2
        centerY = viewWidthHeight / 2
        setMeasuredDimension(viewWidthHeight, viewWidthHeight)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        this.progressPercentage = progress.toFloat() / progressMax.toFloat()
        invalidate()
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding: Float = arcBackgroundPain.strokeWidth / 2f
        circleBounds.set(canvas.clipBounds)
        circleBounds.inset(padding, padding)

        canvas.drawArc(circleBounds, 270f, 360f, false, arcBackgroundPain)

        val arcDegrees = when (clockwise) {
            true -> (360f * progressPercentage)
            false -> -(360f * progressPercentage)
        }
        canvas.drawArc(circleBounds, 270f, arcDegrees, false, arcProgressPaint)

        drawTextCentred(canvas)
    }

    private fun drawTextCentred(canvas: Canvas) {
        val text = getText(progress, progressMax, progressPercentage * 100)
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(text, centerX - textBounds.exactCenterX(), centerY - textBounds.exactCenterY(), textPaint)
    }

    var getText: (progress: Int, max: Int, percentage: Float) -> String = { _: Int, _: Int, percentage: Float ->
        percentage.toInt().toString() + "%"
    }
}