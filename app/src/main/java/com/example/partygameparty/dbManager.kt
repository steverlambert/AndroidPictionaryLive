package com.example.partygameparty

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * Database Manager
 * Holds database reference - for convenience only
 */

object dbManager {

    lateinit var database: DatabaseReference

    fun loadDatabaseReference() {
        database = Firebase.database.reference
    }

}