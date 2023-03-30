package taehyung.BubbleLevelIndicator

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SingleBubble(context: Context?, attribs: AttributeSet?): View(context, attribs) {
    private var _context: Context? = context
    private var _attribs: AttributeSet? = attribs
    private var width: Int? = 0
    private var height: Int?  = 0

    var x: Float? = 0f
    var isFlat: Boolean = false

    private var config: Configuration = resources.configuration

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width  = w
        height = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if(!isFlat) {
            drawRect(canvas)
            drawBubble(canvas)
        }

        invalidate()
    }

    private fun drawRect(canvas: Canvas?) {
        val paint = Paint()
        paint.setStyle(Paint.Style.FILL)
        paint.setColor(Color.parseColor("#F5FFC9"))
        paint.strokeWidth = 2f

        val paintInner = Paint()
        paintInner.setStyle(Paint.Style.STROKE)
        paintInner.setColor(Color.parseColor("#DF2E38"))
        paintInner.strokeWidth = 2f

        canvas?.save()
        canvas?.drawRect(width!! * 0.5f - 200f, height!! * 0.5f - 50f, width!! * 0.5f + 200f ,height!! * 0.5f + 50f, paint)
        canvas?.drawCircle(canvas.width / 2.0f, canvas.height / 2.0f, 50f, paintInner)
//        canvas?.drawRect(width!! * 0.5f - 50f, height!! * 0.5f - 50f, width!! * 0.5f + 50f ,height!! * 0.5f + 50f, paintInner)
        canvas?.restore()
    }

    private fun drawBubble(canvas: Canvas?) {
        var paint: Paint = Paint()
        paint.setColor(Color.BLUE)

        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            canvas?.save()
            canvas?.translate(width!! * 0.5f,height!! * 0.5f)
            canvas?.drawCircle( x!!.coerceIn(-10f, 10f) * 15f, 0f,  50f, paint)
            canvas?.restore()
        } else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            canvas?.save()
            canvas?.translate(width!! * 0.5f,height!! * 0.5f)
            canvas?.drawCircle((x!!- 90).coerceIn(-10f, 10f) * 15f, 0f, 50f, paint)
            canvas?.restore()
        }
    }

    fun setValues(x: Float, isFlat: Boolean) {
        this.x = x
        this.isFlat = isFlat
    }
}