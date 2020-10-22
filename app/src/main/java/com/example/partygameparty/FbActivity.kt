package com.example.partygameparty

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.partygameparty.databinding.ActivityFbBinding
import kotlinx.android.synthetic.main.activity_fb.*
import kotlin.math.abs

/**
 * Fishbowl Activity - Shows words loaded from database
 * Launched from MainActivity via psActivity (data loader)
 * */

class FbActivity : AppCompatActivity(), SensorEventListener {

    private val fbVM: FbViewModel by lazy {
        ViewModelProviders.of(this).get(FbViewModel::class.java)
    }

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var prevTime = 0.0
    private var prevX = 0f
    private var prevY = 0f
    private var prevZ = 0f
    private var prevShake = 0.0
    private var detectShakes = true
    val timer = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            detectShakes = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fb)

        DataBindingUtil.setContentView<ActivityFbBinding>(
            this, R.layout.activity_fb
        ).apply {
            this.setLifecycleOwner(this@FbActivity)
            this.viewmodel = fbVM
        }

        FbData.mediaPlayer = MediaPlayer.create(this, R.raw.airhorn)
        FbData.mediaPlayer.setOnPreparedListener { Log.d("sensorstuff", "sound ready") }

        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            accelerometer = it
        }

        sensorManager.registerListener(this, accelerometer, 40000)
    }

    override fun onPause() {
        super.onPause()
        Log.d("partystuff", "PAUSED")
        FbData.isActive = false
        sensorManager.unregisterListener(this)
    }

    override fun onStop() {
        super.onStop()
        Log.d("partystuff", "on stopped")
        FbData.isActive = false
        sensorManager.unregisterListener(this)
    }

    private fun skipWord() {
        val curTime = System.currentTimeMillis()/1000.toDouble()//graphLastXValue += 0.1
        val dif = curTime - prevTime
        if (dif > 1.0) {
            fbVM.skipButton()
            prevTime = curTime
        }
    }


    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                if (FbData.timerRunning) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    var shake = abs(x + y + z - prevX - prevY - prevZ)
                    val alpha = 0.9

                    val filteredShake = alpha * prevShake + (1 - alpha) * shake
                    prevShake = filteredShake
                    //Log.d("partystuff", "val is $filteredShake")

                    if (filteredShake > FbData.shakeThresh) {
                        Log.d("partystuff", "shake detected with a value of $filteredShake")
                        skipWord()
                    }
                    prevZ = z
                    prevY = y
                    prevX = x
                }
            }
        }
    }
}
