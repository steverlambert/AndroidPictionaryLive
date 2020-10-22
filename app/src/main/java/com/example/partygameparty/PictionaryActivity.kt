package com.example.partygameparty

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.partygameparty.databinding.ActivityPictionaryBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_pictionary.*
import kotlin.math.abs


/**
 * Pictionary Activity - Holds the DrawView and MimicView
 * Launched from PictionarySplashActivity
 */
class PictionaryActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var database: DatabaseReference
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private var prevTime = 0.0
    private var prevX = 0f
    private var prevY = 0f
    private var prevZ = 0f
    private var prevShake = 0.0

    private val fbVM: FbViewModel by lazy {
        ViewModelProviders.of(this).get(FbViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pictionary)

        DataBindingUtil.setContentView<ActivityPictionaryBinding>(
            this, R.layout.activity_pictionary
        ).apply {
            this.setLifecycleOwner(this@PictionaryActivity)
            this.viewmodel = fbVM
        }

        val mdv = findViewById<MyDrawView>(R.id.myDrawView1)
        val mv = findViewById<MimicView>(R.id.mimicView)

        if (PictData.hosting) {
            mv.visibility = View.INVISIBLE
            mdv.visibility = View.VISIBLE
        } else {
            mdv.visibility = View.INVISIBLE
            mv.visibility = View.VISIBLE
            buttonpictskip.visibility = View.GONE
            textviewcurword.visibility = View.GONE
            buttoncorrect.visibility = View.GONE
            midlinearlayout.visibility = View.GONE
        }

        PictData.setFriend(mv)
        database = Firebase.database.reference

        database.child("PICTIONARY").child(PictData.team).addValueEventListener(picListener)

        FbData.mediaPlayer = MediaPlayer.create(this, R.raw.airhorn)
        FbData.mediaPlayer.setOnPreparedListener{ Log.d("sensorstuff", "sound ready")}

        this.sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            accelerometer = it
        }

        sensorManager.registerListener(this, accelerometer, 40000)
    }

    override fun onStop() {
        super.onStop()
        Log.d("partystuff", "on stopped")
        database.child("PICTIONARY").child(PictData.team).removeEventListener(picListener)
        sensorManager.unregisterListener(this)
    }

    override fun onPause() {
        super.onPause()
        Log.d("partystuff", "on paused")
        myDrawView1.clearDrawing()
        database.child("PICTIONARY").child(PictData.team).removeEventListener(picListener)
        FbData.isActive = false
        sensorManager.unregisterListener(this)
    }

    val picListener = object : ValueEventListener {
        override fun onDataChange(allData: DataSnapshot) {
            PictData.loadPathFromDatabase(allData)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("partystuff", "loadPost:onCancelled", databaseError.toException())
        }
    }

    private fun skipWord() {
        val curTime = System.currentTimeMillis()/1000.toDouble()//graphLastXValue += 0.1
        val dif = curTime - prevTime
        if (dif > 1.0) {
            fbVM.skipButton()
            prevTime = curTime
        }
    }

    fun buttonClear(view: View) {
        myDrawView1.clearDrawing()
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