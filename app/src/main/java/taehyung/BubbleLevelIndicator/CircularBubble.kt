package taehyung.BubbleLevelIndicator

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import kotlin.math.sqrt

class CircularBubble(context: Context?, attribs: AttributeSet?): View(context, attribs) {
    private var _context: Context?  = context
    private var width: Int? = 0
    private var height: Int?  = 0
    private var radius: Float = 0.0f

    private var config: Configuration = resources.configuration

    private var circleColor: Paint
    private var redStroke: Paint

    var x: Float? = 0f
    var y: Float? = 0f
    var isFlat: Boolean = false

    init {
        circleColor = Paint(Paint.ANTI_ALIAS_FLAG)
        redStroke = Paint(Paint.ANTI_ALIAS_FLAG)
        circleColor.setColor(Color.parseColor("#F5FFC9"))
        redStroke.setStyle(Paint.Style.STROKE)
        redStroke.setColor(Color.argb(255,255,0,0))
        redStroke.strokeWidth = 2f;
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width  = w
        height = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (isFlat) {
            drawCircle(canvas)
            drawBubble(canvas)
        }

        invalidate()
    }

    private fun drawCircle(canvas: Canvas?) {
        canvas?.save()

        if(config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            radius = width!! * 0.3f
            canvas?.translate(canvas.width / 2.0f, canvas.height / 2.0f)
        }
        else if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            radius= height!! * 0.3f
            canvas?.translate(canvas.width / 2.0f, canvas.height / 2.0f)
        }

        canvas?.drawCircle(0.0f, 0.0f, radius, circleColor)
        canvas?.drawCircle(0.0f, 0.0f, radius * 0.2f, redStroke)
        canvas?.restore()
    }

    private fun drawBubble(canvas: Canvas?) {
        var paint: Paint = Paint()
        paint.setColor(Color.BLUE)
        val angleX = x!!.coerceIn(-10f, 10f)
        val angleY = y!!.coerceIn(-10f,10f)

        val limitDegree = 10f
        val point = PointF(angleX,angleY)
        val unitPoint = point.toUnit(limitation = limitDegree * 2)
        val unitCircularPoint = unitPoint.rectToPolar()

        if(config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val innerRadius = width!! * 0.3f
            radius = width!! * 0.05f
            canvas?.save()
            canvas?.translate(canvas.width / 2.0f, canvas.height / 2.0f)
            canvas?.drawCircle(unitCircularPoint.x * (innerRadius - radius) , unitCircularPoint.y * (innerRadius - radius) , radius, paint)
            canvas?.restore()
        }
        else if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val innerRadius = height!! * 0.3f
            radius = height!! * 0.05f
            canvas?.save()
            canvas?.translate(canvas.width / 2.0f, canvas.height / 2.0f)
            canvas?.drawCircle(unitCircularPoint.y * (innerRadius - radius) , -unitCircularPoint.x * (innerRadius - radius) , radius, paint)
            canvas?.restore()
        }
    }

    fun setValues(x: Float, y: Float, isFlat: Boolean) {
        this.x = x
        this.y = y
        this.isFlat = isFlat
    }

    // convert rectangular coordinates into circular coordinates
    private fun PointF.rectToPolar(): PointF {
        return PointF(
            x * sqrt(1 - y * y / 2),
            y * sqrt(1 - x * x / 2)
        )
    }

    // convert point into unit scale
    private fun PointF.toUnit(limitation: Float): PointF {
        return PointF(
            (x / limitation) * 2,
            (y / limitation) * 2
        )
    }
}