package com.icure.kses.modoo.notification

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.icure.kses.modoo.R

/**
 * Created by priyankam on 4/13/2016.
 */
class BadgeDrawable(context: Context) : Drawable() {
    private val mTextSize: Float
    private val mBadgePaint: Paint
    private val mTextPaint: Paint
    private val mTxtRect = Rect()
    private var mCount = ""
    private var mWillDraw = false
    override fun draw(canvas: Canvas) {
        if (!mWillDraw) {
            return
        }
        val bounds = bounds
        val width = bounds.right - bounds.left.toFloat()
        val height = bounds.bottom - bounds.top.toFloat()

        // Position the badge in the top-right quadrant of the icon.
        val radius = (Math.min(width, height) / 2 - 1) / 1.6.toFloat()
        val centerX = width - radius - 1
        val centerY = radius + 1

        // Draw badge circle.
        canvas.drawCircle(centerX, centerY, radius, mBadgePaint)

        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length, mTxtRect)
        val textHeight = mTxtRect.bottom - mTxtRect.top.toFloat()
        val textY = centerY + textHeight / 2f
        canvas.drawText(mCount, centerX, textY, mTextPaint)
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    fun setCount(count: Int) {
        mCount = Integer.toString(count)

        // Only draw a badge if there are notifications.
        mWillDraw = count > 0
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        // do nothing
    }

    @SuppressLint("WrongConstant")
    override fun getOpacity(): Int {
        // TODO Auto-generated method stub
        return 0
    }

    override fun setColorFilter(cf: ColorFilter?) {
        // TODO Auto-generated method stub
    }

    init {
        // mTextSize = 10dp;
        mTextSize = context.resources.getDimension(R.dimen.text_size_small)
        mBadgePaint = Paint()
        mBadgePaint.color = ContextCompat.getColor(context, R.color.colorAccent)
        mBadgePaint.isAntiAlias = true
        mBadgePaint.style = Paint.Style.FILL
        mTextPaint = Paint()
        mTextPaint.color = Color.WHITE
        mTextPaint.typeface = Typeface.DEFAULT_BOLD
        mTextPaint.textSize = mTextSize
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
    }
}