package com.icure.kses.modoo.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.icure.kses.modoo.R

class CustomTextOutlineView : AppCompatTextView {

    var stroke: Boolean = false
    var strokeWidth: Float = 0.0f
    var strockColor: Int = 0

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attr: AttributeSet?, defStyle: Int): super(context, attr, defStyle){
        initView(context, attr)
    }

    private fun initView(context: Context, attrs: AttributeSet?){
        var ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextOutlineView)
        stroke = ta.getBoolean(R.styleable.CustomTextOutlineView_textStroke, false)
        strokeWidth = ta.getFloat(R.styleable.CustomTextOutlineView_textStrokeWidth, 0.0f)
        strockColor = ta.getInt(R.styleable.CustomTextOutlineView_textStrokeColor, 0xFFFFFF)
    }

    override fun onDraw(canvas: Canvas?) {
        if(stroke){
            var states = textColors
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = strokeWidth
            setTextColor(strockColor)
            super.onDraw(canvas)
            paint.style = Paint.Style.FILL
            setTextColor(states)
        }
        super.onDraw(canvas)
    }
}