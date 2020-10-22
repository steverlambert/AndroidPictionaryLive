package com.example.partygameparty

import android.util.Log
import androidx.lifecycle.ViewModel

/**
 * Database Activity ViewModel
 * Stores UI data for the database activity
 */

class dbViewModel: ViewModel() {

    override fun onCleared() {
        super.onCleared()
        Log.d("tag", "ViewModel instance about to be destroyed")
    }

    var code = "DEFAULTCODE"
    var name = "defaultname"
    var wordList = ArrayList<String>()
    var index = 0
    var codeConfirmed = false
    var nameConfirmed = false

}