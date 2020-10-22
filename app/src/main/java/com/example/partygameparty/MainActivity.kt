package com.example.partygameparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders


/**
 * Main Menu - Launches other activites
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startCP(view: View) {
        val intent = Intent(this, psActivity::class.java)
        intent.putExtra("KEY", "catch")

        startActivity(intent)
    }

    fun startFB(view: View) {
        val intent = Intent(this, psActivity::class.java)
        intent.putExtra("KEY", "fish")

        startActivity(intent)
    }

    fun startDB(view: View) {
        val intent = Intent(this, dbActivity::class.java)
        startActivity(intent)
    }

    fun startPic(view: View) {
        val intent = Intent(this, psActivity::class.java)
        intent.putExtra("KEY", "pict")

        startActivity(intent)
    }
}