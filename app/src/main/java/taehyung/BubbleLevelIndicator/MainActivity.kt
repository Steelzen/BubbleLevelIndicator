package taehyung.BubbleLevelIndicator

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.util.LinkedList
import java.util.Queue
import kotlin.math.atan2

class MainActivity : AppCompatActivity() {
    lateinit var singleBubble: SingleBubble
    lateinit var circularBubble: CircularBubble

    var textPortrait: TextView? = null
    var textLandscape: TextView? = null

    lateinit var _sm: SensorManager
    lateinit var config: Configuration

    var xSingleValues: Queue<Float> = LinkedList<Float>()
    var xCircularValues: Queue<Float> = LinkedList<Float>()
    var yCircularValues: Queue<Float> = LinkedList<Float>()

    var xSingleMax: Float? = null
    var xSingleMin: Float? = null
    var xCircularMax: Float? = null
    var xCircularMin: Float? = null
    var yCircularMax: Float? = null
    var yCircularMin: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawable(ColorDrawable(Color.parseColor("#C9EEFF")))

        config = resources.configuration
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT)
            setContentView(R.layout.activity_main)
        else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
            setContentView(R.layout.activity_main_landscape)

//        // Set the title
//        setTitle("BLI")
//
//        // Set the background color of the title bar
//        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.teal_200)))

        _sm = getSystemService(SENSOR_SERVICE) as SensorManager

        singleBubble = findViewById<SingleBubble>(R.id.singlebubble)
        circularBubble = findViewById<CircularBubble>(R.id.circularbubble)
        textPortrait = findViewById<TextView>(R.id.text_portrait)
        textLandscape = findViewById<TextView>(R.id.text_landscape)

        setAccelerometerListener()
    }

    // private function that set the accelerometer listener
    private fun setAccelerometerListener() {
        if (_sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            _sm.registerListener(object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                }

                override fun onSensorChanged(event: SensorEvent?) {
                    val singleX = angleModifier(event!!.values[0], event!!.values[1])

                    val circularX = angleModifier(event!!.values[0], event!!.values[2])
                    val circularY = angleModifier(event!!.values[1], event!!.values[2])

                    val isFlat = isFlat(circularX, circularY)

                    // maintain 500 orientation values on queue for each list
                    // set up all max and min values of sensor orientation according to display mode
                    if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
                        addValuesIntoQueues(
                            "%.1f".format(singleX).toFloat(),
                            "%.1f".format(circularX).toFloat(),
                            "%.1f".format(circularY).toFloat()
                        )
                        xSingleMax = xSingleValues.max()
                        xSingleMin = xSingleValues.min()
                        xCircularMax = xCircularValues.max()
                        xCircularMin = xCircularValues.min()
                        yCircularMax = yCircularValues.max()
                        yCircularMin = yCircularValues.min()
                    }
                    else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        addValuesIntoQueues(
                            "%.1f".format(singleX).toFloat(),
                            "%.1f".format(circularX).toFloat(),
                            -"%.1f".format(circularY).toFloat()
                        )
                        xSingleMax = xSingleValues.max() - 90
                        xSingleMin = xSingleValues.min() - 90
                        xCircularMax = yCircularValues.max()
                        xCircularMin = yCircularValues.min()
                        yCircularMax = xCircularValues.max()
                        yCircularMin = xCircularValues.min()
                    }

                    if(isFlat){
                        textPortrait?.text = (buildString {
                            append("Max X: ")
                            append(xCircularMax)
                            append(", Y: ")
                            append(yCircularMax)
                            append("\n")
                            append("Min X: ")
                            append(xCircularMin)
                            append(", Y: ")
                            append(yCircularMin)
                            append("\n")
                            append("Current Angle X: ")
                            append("%.1f".format(circularX).toFloat())
                            append(", Y: ")
                            append("%.1f".format(circularY).toFloat())
                        })
                        textLandscape?.text = (buildString {
                            append("Max X: ")
                            append(xCircularMax)
                            append(", Y: ")
                            append(yCircularMax)
                            append("\n")
                            append("Min X: ")
                            append(xCircularMin)
                            append(", Y: ")
                            append(yCircularMin)
                            append("\n")
                            append("Current Angle X: ")
                            append(-"%.1f".format(circularY).toFloat())
                            append(", Y: ")
                            append("%.1f".format(circularX).toFloat())
                        })
                    } else{
                        textPortrait?.text = (buildString {
                            append("Max: ")
                            append(xSingleMax)
                            append("\n")
                            append("Min: ")
                            append(xSingleMin)
                            append("\n")
                            append("Current Angle: ")
                            append("%.1f".format(singleX).toFloat())
                        })
                        textLandscape?.text =(buildString {
                            append("Max: ")
                            append("%.1f".format(xSingleMax))
                            append("\n")
                            append("Min: ")
                            append("%.1f".format(xSingleMin))
                            append("\n")
                            append("Current Angle: ")
                            append("%.1f".format((singleX).toFloat() - 90))
                        })
                    }

                    singleBubble?.setValues(singleX.toFloat(), isFlat)
                    circularBubble?.setValues(circularX.toFloat(), -circularY.toFloat(), isFlat)

                }
            }, _sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // calculate the orientation of device based on three values of accelerometers
    private fun angleModifier(value1: Float, value2: Float): Double {
        return atan2(value1.toDouble(), value2.toDouble()) / (Math.PI / 180)
    }

    // evaluate whether the device is in flat or not
    fun isFlat(angleX: Double, angleY: Double):Boolean{
        return (angleX.toInt() in -20..20 && angleY.toInt() in -20..20)
    }

    // maintain 500 rotation values for each angles
    private fun addValuesIntoQueues(x: Float, y: Float, z: Float) {
        if (xSingleValues.size >= 500) {
            xSingleValues.remove()
            xSingleValues.add(x)
        } else {
            xSingleValues.add(x)
        }

        if (xCircularValues.size >= 500) {
            xCircularValues.remove()
            xCircularValues.add(y)
        } else {
            xCircularValues.add(y)
        }

        if (yCircularValues.size >= 500) {
            yCircularValues.remove()
            yCircularValues.add(z)
        } else {
            yCircularValues.add(z)
        }
    }
}