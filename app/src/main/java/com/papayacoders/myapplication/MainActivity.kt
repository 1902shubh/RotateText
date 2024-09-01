package com.papayacoders.myapplication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import android.widget.TextView
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.rotatingTextView)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI)
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val adjustedRotationMatrix = FloatArray(9)
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayRotation = windowManager.defaultDisplay.rotation

            when (displayRotation) {
                Surface.ROTATION_0 -> SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    adjustedRotationMatrix
                )
                Surface.ROTATION_90 -> SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_Y,
                    SensorManager.AXIS_MINUS_X,
                    adjustedRotationMatrix
                )
                Surface.ROTATION_180 -> SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_MINUS_X,
                    SensorManager.AXIS_MINUS_Z,
                    adjustedRotationMatrix
                )
                Surface.ROTATION_270 -> SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_MINUS_Y,
                    SensorManager.AXIS_X,
                    adjustedRotationMatrix
                )
            }

            val orientation = FloatArray(3)
            SensorManager.getOrientation(adjustedRotationMatrix, orientation)

            val pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
            
            val roll = Math.toDegrees(orientation[2].toDouble()).toFloat()
            
            Log.d("SHUBH", "onSensorChanged:pitch $pitch")
            Log.d("SHUBH", "onSensorChanged:roll $roll")
            // Rotate the TextView based on the device's orientation
            
            if ((roll in -120.0..-60.0) ){
                textView.rotation = 90f
            }else if ((roll in -180.0..-160.0) || (roll in 160.0..180.0)){
                textView.rotation = 180f
            }else if ((roll in 60.0..120.0) ){
                textView.rotation = 270f
            }else if ((roll in -30.0..-0.0) || (roll in 0.0..30.0)){
                textView.rotation = 0f
            }
            
            

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // You can handle accuracy changes here if necessary
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the sensor listener when the activity is destroyed
        sensorManager.unregisterListener(this)
    }
}