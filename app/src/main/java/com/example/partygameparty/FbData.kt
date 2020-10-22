package com.example.partygameparty

import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.collection.ArraySet
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import kotlin.random.Random

/**
 * Fishbowl Data object
 * All logic and variables for fishbowl activity
 * (and the word display component of pictionary)
 */

object FbData {
    private var wordStack = ArraySet<String>()
    val timerTime = MutableLiveData<String>()
    var timerRunning = false
    val currentWord = MutableLiveData<String>()
    val scoreView = MutableLiveData<String>()
    var score = 0
    lateinit var mediaPlayer: MediaPlayer
    var shakeThresh = 4
    var isActive = true

    val timer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val seconds = (millisUntilFinished / 1000)
            timerTime.value = seconds.toString()
        }

        override fun onFinish() {
            timerEnd()
            currentWord.value = "PRESS CHECK WHEN READY"
        }
    }

    init {
        resetUI()
    }

    fun resetUI() {
        currentWord.value = "PRESS CHECK WHEN READY"
        scoreView.value = "000"
        score = 0
        cancelTimer()
        isActive = true
    }


    fun addWord(word: String) {
        wordStack.add(word)
    }

    fun popWord(): String {
        var word = "END OF LIST"
        if (!wordStack.isEmpty()) {
            val i = Random.nextInt(0, stackSize())
            word = wordStack.removeAt(i)
        }
        currentWord.value = word
        return word
    }

    fun skipWord() {
        if (timerRunning) {
            if (currentWord.value != "END OF LIST" && currentWord.value != "PRESS CHECK WHEN READY") {
                addWord(currentWord.value!!)
                currentWord.value = popWord()
            }
        }
    }

    fun buttonCorrect() {
        if (timerRunning && stackSize() > 0) {
            increaseScore()
        } else if (!timerRunning) {
            resetScore()
            start_timer()
        } else {
            cancelTimer()
        }
        popWord()
    }

    fun stackSize(): Int {
        return wordStack.size
    }

    fun clearStack() {
        wordStack.clear()
    }

    fun increaseScore() {
        score++
        scoreView.value = score.toString()
    }

    fun resetScore() {
        score = 0
        scoreView.value = score.toString()
    }

    fun loadWordsFromDatabase(data: DataSnapshot, code: String) {
        for (child in data.child(code).children) {
            for (word in child.children) {
                addWord(word.value.toString())
            }
        }
    }

    fun start_timer() {
        if (!timerRunning) {
            timerRunning = true
            timer.start()
        }
    }

    fun cancelTimer() {
        if (timerRunning) {
            timer.cancel()
            timerRunning = false
            timerTime.value = "---"
        }
    }

    fun timerEnd() {
        if (isActive) {
            mediaPlayer.start()
        }
        timerRunning = false
        currentWord.value = "TIME UP, CHECK WHEN READY"
    }
}