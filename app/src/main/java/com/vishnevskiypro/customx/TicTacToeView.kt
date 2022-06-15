package com.vishnevskiypro.customx

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

class TicTacToeView(
    context: Context,
    attributesSet: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
): View(context, attributesSet, defStyleAttr, defStyleRes) {

    var ticTacToeField: TicTacToeField? = null
    set(value){
        field?.listeners?.remove(listener)
        field = value
        value?.listeners?.add(listener)
        updateNewSizes()
        requestLayout()
        invalidate()
    }

    private var player1color by Delegates.notNull<Int>()
    private var player2color by Delegates.notNull<Int>()
    private var gridColor by Delegates.notNull<Int>()

    private val fieldRect = RectF(0F, 0F, 0F, 0F)
    private var cellSize: Float = 0F
    private var cellPadding: Float = 0F

    private lateinit var player1Paint: Paint
    private lateinit var player2Paint: Paint
    private lateinit var gridPaint: Paint

    constructor(context: Context, attributesSet: AttributeSet?, defStyleAttr: Int) : this(context, attributesSet, defStyleAttr, R.style.DefaultTicTacToeFieldStyle)
    constructor(context: Context, attributesSet: AttributeSet?) : this(context, attributesSet, R.attr.ticTacToeFieldStyle)
    constructor(context: Context) : this(context, null)

    init {
        if (attributesSet != null){
            initAttributes(attributesSet, defStyleAttr, defStyleRes)
        } else {
            initDefaultColors()
        }
        initPaints()
        if(isInEditMode){
            ticTacToeField = TicTacToeField(8,6)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ticTacToeField?.listeners?.add(listener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        ticTacToeField?.listeners?.remove(listener)
    }

    private fun initPaints(){
        player1Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        player1Paint.color = player1color
        player1Paint.style = Paint.Style.STROKE
        player1Paint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics)

        player2Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        player2Paint.color = player2color
        player2Paint.style = Paint.Style.STROKE
        player2Paint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, resources.displayMetrics)

        gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        gridPaint.color = gridColor
        gridPaint.style = Paint.Style.STROKE
        gridPaint.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
    }


    private fun initAttributes(attributesSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int){
        val typedArray = context.obtainStyledAttributes(attributesSet, R.styleable.TicTacToeView, defStyleAttr, defStyleRes)
        player1color = typedArray.getColor(R.styleable.TicTacToeView_player1color, PLAYER1_DEFAULT_COLOR)
        player2color = typedArray.getColor(R.styleable.TicTacToeView_player2color, PLAYER2_DEFAULT_COLOR)
        gridColor = typedArray.getColor(R.styleable.TicTacToeView_gridColor, GRID_COLOR)

        typedArray.recycle()
    }

    private fun initDefaultColors(){
        player1color = PLAYER1_DEFAULT_COLOR
        player2color = PLAYER2_DEFAULT_COLOR
        gridColor = GRID_COLOR
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateNewSizes()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minimumWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minimumHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredCellSizePixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            DESIRED_CELL_SIZE, resources.displayMetrics).toInt()

        val rows = ticTacToeField?.rows ?: 0
        val columns = ticTacToeField?.columns ?: 0

        val desiredWidth = max(minimumWidth, columns * desiredCellSizePixels + paddingLeft + paddingRight)
        val desiredHeight = max(minimumHeight, rows + desiredCellSizePixels + paddingTop + paddingBottom)

        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (ticTacToeField == null) return
        if (cellSize == 0F) return
        if (fieldRect.width() <= 0) return
        if (fieldRect.height() <= 0) return

        drawGrid(canvas)
        drawCells(canvas)
    }

    private fun drawGrid(canvas: Canvas){
        val field = this.ticTacToeField ?: return
        val xStart = fieldRect.left
        val xEnd = fieldRect.right

        for (i in 0..field.rows){
            val y = fieldRect.top + cellSize * i
            canvas.drawLine(xStart, y, xEnd, y, gridPaint)
        }

        val yStart = fieldRect.top
        val yEnd = fieldRect.bottom

        for (i in 0..field.columns){
            val x = fieldRect.left + cellSize
            canvas.drawLine(x, yStart, x, yEnd, gridPaint)
        }

    }

    private fun drawCells(canvas: Canvas){

    }

    private fun updateNewSizes(){
        val field = this.ticTacToeField ?: return

        val safeWidth = width-paddingLeft-paddingRight
        val safeHeight = height-paddingTop-paddingBottom

        val cellWidth = safeWidth / field.columns.toFloat()
        val cellHeight = safeHeight / field.rows.toFloat()

        cellSize = min(cellWidth, cellHeight)
        cellPadding = cellSize * 0.2F

        val fieldWidth = cellSize * field.columns
        val fieldHeight = cellSize * field.rows

        fieldRect.left = paddingLeft + (safeWidth - fieldWidth) / 2
        fieldRect.top = paddingTop + (safeHeight - fieldHeight) / 2
        fieldRect.right = fieldRect.left + fieldWidth
        fieldRect.bottom = fieldRect.top + fieldHeight
    }

    private val listener: OnFieldChangedListener = {

    }

    companion object {
        const val PLAYER1_DEFAULT_COLOR = Color.GREEN
        const val PLAYER2_DEFAULT_COLOR = Color.RED
        const val GRID_COLOR = Color.GRAY

        const val DESIRED_CELL_SIZE = 50f
    }

}