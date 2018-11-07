package com.tobiasschuerg.progresscircle

/**
 * Created by toanpc on 26/02/2017.
 */

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private val arcBackgroundPain: Paint
    private val arcProgressPaint: Paint
    private val textPaint: Paint

    private val mTextBounds = Rect()
    private val bounds = RectF()

    private var centerX: Int = 0
    private var centerY: Int = 0
    private var mWidthArcBG: Int = 0
    private var mWidthAcrPrimary: Int = 0
    private val mTextSizeProgress: Int
    private val radius: Int

    private var progress: Int = 75
    private var progressPercentage: Float = 0.75f
    var progressMax: Int = 100

    init {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        val primaryColor: Int
        primaryColor = typedValue.data
        context.theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
        val accentColor = typedValue.data

        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, defStyleAttr, defStyleRes).apply {
            try {
                val progressWidth =
                    getDimensionPixelSize(R.styleable.CustomProgressBar_progressWidth, 10) // FIXME: default
                mTextSizeProgress = getDimensionPixelSize(R.styleable.CustomProgressBar_textSize, 10) // FIXME: default
                radius = getDimensionPixelSize(R.styleable.CustomProgressBar_radius, 40) // FIXME: default
                mWidthArcBG = progressWidth
                mWidthAcrPrimary = progressWidth


            } finally {
                recycle()
            }
        }

        arcBackgroundPain = Paint().apply {
            isDither = true
            style = Paint.Style.STROKE
            color = primaryColor
            strokeWidth = mWidthArcBG.toFloat()
            isAntiAlias = true
        }

        arcProgressPaint = Paint().apply {
            isDither = true
            style = Paint.Style.STROKE
            color = accentColor
            strokeWidth = mWidthAcrPrimary.toFloat()
            isAntiAlias = true
        }

        textPaint = Paint().apply {
            isAntiAlias = true
            textSize = mTextSizeProgress.toFloat()
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, android.R.color.black)
            strokeWidth = 2f
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

        val padding: Float = mWidthArcBG / 2f
        bounds.set(canvas.clipBounds)
        bounds.inset(padding, padding)

        canvas.drawArc(bounds, 270f, 360f, false, arcBackgroundPain)
        canvas.drawArc(bounds, 270f, -(360f * progressPercentage), false, arcProgressPaint)

        drawTextCentred(canvas)
    }

    private fun drawTextCentred(canvas: Canvas) {
        val text = getText(progress, progressMax, progressPercentage * 100)
        textPaint.getTextBounds(text, 0, text.length, mTextBounds)
        canvas.drawText(text, centerX - mTextBounds.exactCenterX(), centerY - mTextBounds.exactCenterY(), textPaint)
    }

    var getText: (progress: Int, max: Int, percentage: Float) -> String = { progess: Int, max: Int, percentage: Float ->
        percentage.toInt().toString() + "%"
    }

}