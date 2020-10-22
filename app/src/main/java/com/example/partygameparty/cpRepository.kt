package com.example.partygameparty

import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.activity_cp.*

/**
 * Catch Phrase Data
 * All logic and variables for catch phrase
 */

object cpRepository {
    var customWords = ArrayList<String>()

    private val _currentWord = MutableLiveData<String>()
    private var oneInt = 0
    private var twoInt = 0
    private var seconds = 0L
    private var winThreshold = 7
    private var timerRunning = false
    private lateinit var context: Context
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayer2: MediaPlayer

    // VISIBILITIES //
    var skipVis = MutableLiveData<Boolean>()
    var wordVis = MutableLiveData<Boolean>()
    var scoreOneVis = MutableLiveData<Boolean>()
    var scoreTwoVis = MutableLiveData<Boolean>()
    var timerButtonVis = MutableLiveData<Boolean>()

    val teamOneScore = MutableLiveData<String>()
    val teamTwoScore = MutableLiveData<String>()
    val timerTime = MutableLiveData<String>()


    private var word_index = 0
    val currentWord: LiveData<String>
        get() = _currentWord

    init {
        _currentWord.value = "START TIMER TO START"
        teamOneScore.value = oneInt.toString()
        teamTwoScore.value = twoInt.toString()
        timerTime.value = ""
        timerButtonVis.value = true
        skipVis.value = false
        wordVis.value = true
        scoreOneVis.value = true
        scoreTwoVis.value = true
    }

    fun nextWord() {
        if (customWords.size > 0) {
            if (timerRunning) {
                word_index = (word_index + 1) % customWords.size
                _currentWord.value = customWords[word_index]
            }
        }
    }

    fun increaseOneScore() {
        oneInt++
        teamOneScore.value = oneInt.toString()
        if (oneInt == winThreshold) {
            endGame("ONE")
        }
    }

    fun increaseTwoScore() {
        twoInt++
        teamTwoScore.value = twoInt.toString()
        if (twoInt == winThreshold) {
            endGame("TWO")
        }
    }

    fun setContext(con: Context) {
        context = con
        mediaPlayer = MediaPlayer.create(context, R.raw.airhorn)
        mediaPlayer2 = MediaPlayer.create(context, R.raw.trumpet)
        mediaPlayer.setOnPreparedListener { Log.d("sensorstuff", "sound ready") }
    }

    private fun endGame(winner: String) {
        mediaPlayer2.start()
        Toast.makeText(context, "TEAM $winner WINS!", Toast.LENGTH_LONG).show()
        oneInt = 0
        twoInt = 0
        teamOneScore.value = oneInt.toString()
        teamTwoScore.value = twoInt.toString()
    }

    internal fun timerEnd() {
        timerRunning = false
        mediaPlayer.start()
        timerTime.value = "TIME'S UP!"
        timerButtonVis.value = true
        scoreOneVis.value = true
        scoreTwoVis.value = true
        skipVis.value = false

    }

    fun loadWordsFromDatabase(data: DataSnapshot, code: String) {
        for (child in data.child(code).children) {
            for (word in child.children) {
                customWords.add(word.value.toString())
            }
        }
    }

    fun start_timer() {

        if (!timerRunning) {
            timerRunning = true
            timerButtonVis.value = false
            scoreOneVis.value = false
            scoreTwoVis.value = false
            skipVis.value = true
            nextWord()

            val timer = object : CountDownTimer(20000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    seconds = (millisUntilFinished / 1000)
                    timerTime.value = seconds.toString()
                }

                override fun onFinish() {
                    timerEnd()
                }
            }
            timer.start()
        }
    }

}