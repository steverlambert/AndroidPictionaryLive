package com.example.partygameparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_pictionary_splash.*

/**
 * Pictionary Splash Activity - Gathers host and team data
 * Launched from MainActivity via psActivity (data loader)
 * Launches PictionaryActivity
 */

class PictionarySplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pictionary_splash)
    }

    fun startPic(view: View) {
        val host = switch1.isChecked
        val team = toggleButton.isChecked

        PictData.hosting = host
        if (team) {
            PictData.team = "TEAM2"
        } else {
            PictData.team = "TEAM1"
        }

        FbData.resetUI()
        val intent = Intent(this, PictionaryActivity::class.java)
        startActivity(intent)
    }
}
