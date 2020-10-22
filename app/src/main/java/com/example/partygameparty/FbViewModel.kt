package com.example.partygameparty

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

/**
 * Fishbowl ViewModel
 * Handles data binding for fishbowl activity
 * (and the word display component of pictionary)
 */

class FbViewModel : ViewModel() {

    val teamOneScore: LiveData<String>
        get() = FbData.scoreView

    val timerTime: LiveData<String>
        get() = FbData.timerTime

    val currentWord: LiveData<String>
        get() = FbData.currentWord

    fun correctButton() {
        FbData.buttonCorrect()
    }

    fun skipButton() {
        FbData.skipWord()
    }
}