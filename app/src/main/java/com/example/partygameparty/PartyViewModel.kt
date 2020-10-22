package com.example.partygameparty

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Catch Phrase ViewModel
 * Handles data binding for catch phrase activity
 */

class PartyViewModel : ViewModel() {

    val currentWord: LiveData<String>
        get() = cpRepository.currentWord

    val teamOneScore: LiveData<String>
        get() = cpRepository.teamOneScore

    val teamTwoScore: LiveData<String>
        get() = cpRepository.teamTwoScore

    val timerTime: LiveData<String>
        get() = cpRepository.timerTime

    val timerbuttonVis: LiveData<Boolean>
        get() = cpRepository.timerButtonVis

    val skipbuttonVis: LiveData<Boolean>
        get() = cpRepository.skipVis

    val scoreOneVis: LiveData<Boolean>
        get() = cpRepository.scoreOneVis

    val scoreTwoVis: LiveData<Boolean>
        get() = cpRepository.scoreTwoVis

    val wordVis: LiveData<Boolean>
        get() = cpRepository.wordVis

    fun skipWordButton() {
        cpRepository.nextWord()
    }

    fun increaseTeamOneScore() {
        cpRepository.increaseOneScore()
    }

    fun increaseTeamTwoScore() {
        cpRepository.increaseTwoScore()
    }

    fun beginTimer() {
        cpRepository.start_timer()
    }


    override fun onCleared() {
        super.onCleared()
        Log.d("tag", "ViewModel instance about to be destroyed")
    }
}
