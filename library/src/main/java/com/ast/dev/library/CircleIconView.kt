package com.ast.dev.library

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * Custom circular view with customizable properties
 * Created by Axel Trajano on 23/10/2018.
 */
class CircleIconView(context: Context?, attrs: AttributeSet? = null) : View(context, attrs) {

    companion object {
        private const val DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT
        private const val DEFAULT_BORDER_COLOR = Color.BLACK
        private const val DEFAULT_BORDER_RATIO = 0.05f
        private const val DEFAULT_ICON_COLOR = -1
        private const val DEFAULT_CLIPPING = true
        const val DEFAULT_ICON_RATIO = 0.53f
        const val DEFAULT_ICON_OFFSET = 0.0f
    }

    var backgroundFillColor = DEFAULT_BACKGROUND_COLOR
    var borderColor = DEFAULT_BORDER_COLOR
    var borderRatio = DEFAULT_BORDER_RATIO
    var bitmap : Bitmap? = null
    var iconRatio = DEFAULT_ICON_RATIO
    var iconOffsetV = DEFAULT_ICON_OFFSET
    var iconOffsetH = DEFAULT_ICON_OFFSET
    var iconColor = DEFAULT_ICON_COLOR
    var clipToBorder = DEFAULT_CLIPPING

    private lateinit var iconFilter : PorterDuffColorFilter
    private var drawableId : Int = 0
    private var size = 0
    private val backgroundPaint = Paint()
    private val paint = Paint()
    private val path = Path()

    init {
        backgroundPaint.isAntiAlias = true
        paint.isAntiAlias = true
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        // Obtain a typed array of attributes
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CircleIconView,
            0, 0)

        try {
            // Extract custom attributes into member variables
            backgroundFillColor = typedArray.getColor(R.styleable.CircleIconView_backgroundFillColor, DEFAULT_BACKGROUND_COLOR)
            borderRatio = typedArray.getFloat(R.styleable.CircleIconView_borderRatio, DEFAULT_BORDER_RATIO)
            borderColor = typedArray.getColor(R.styleable.CircleIconView_borderColor, DEFAULT_BORDER_COLOR)
            clipToBorder = typedArray.getBoolean(R.styleable.CircleIconView_clipToBorder, DEFAULT_CLIPPING)
            drawableId = typedArray.getResourceId(R.styleable.CircleIconView_iconDrawable, drawableId)
            iconColor = typedArray.getColor(R.styleable.CircleIconView_iconColor, DEFAULT_ICON_COLOR)
            bitmap = BitmapFactory.decodeResource(resources, drawableId)
            iconRatio = typedArray.getFloat(R.styleable.CircleIconView_iconRatio, DEFAULT_ICON_RATIO)
            iconOffsetH = typedArray.getFloat(R.styleable.CircleIconView_iconOffsetH, DEFAULT_ICON_OFFSET)
            iconOffsetV = typedArray.getFloat(R.styleable.CircleIconView_iconOffsetV, DEFAULT_ICON_OFFSET)
        } finally {
            // TypedArray objects are shared and must be recycled.
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawBounds(canvas)
        drawIcon(canvas)
        drawBorder(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        size = Math.min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    private fun drawBounds(canvas: Canvas?) {
        if (!clipToBorder) {
            canvas?.drawColor(backgroundFillColor)
            return
        }

        val radius = size / 2f
        backgroundPaint.color = backgroundFillColor
        path.addCircle(radius, radius, radius, Path.Direction.CW)
        canvas?.drawPath(path, backgroundPaint)
        canvas?.clipPath(path)
    }

    private fun drawBorder(canvas: Canvas?) {
        val borderWidth = size * borderRatio
        if (borderWidth <= 0) return

        val radius = size / 2f
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth
        canvas?.drawCircle(radius, radius, radius - borderWidth / 2f, paint)
    }

    private fun drawIcon(canvas: Canvas?) {
        if (bitmap == null) return

        val radius = size / 2f
        val iconSize : Int = (size * iconRatio).toInt()

        bitmap?.let {
            bitmap = Bitmap.createScaledBitmap(it, iconSize, iconSize, false)
        }

        iconFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
        paint.colorFilter = iconFilter
        val offset = bitmap?.width?.div(2.0f)
        if (offset != null) {
            val offsetH = (radius - offset) + (offset * iconOffsetH)
            val offsetV = (radius - offset) + (offset * iconOffsetV)
            canvas?.drawBitmap(bitmap!!, offsetH, offsetV, if (iconColor == -1) null else paint)
        }
        paint.colorFilter = null
    }
}