package com.example.partygameparty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TableRow
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_ps.*


/**
 * Data Loader Activity - Reads from the database and stores words locally
 * Launched from MainActivity
 * Launches activity requested on the main menu
 */

class psActivity : AppCompatActivity() {

    var packList = ArrayList<String?>()
    lateinit var dataSnapshot: DataSnapshot
    var buttonMap = HashMap<Int, String?>()
    var nextActivity = "catch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ps)

        nextActivity = intent.getStringExtra("KEY")!!

        dbManager.loadDatabaseReference()
        dbManager.database.child("WORD BANKS").addListenerForSingleValueEvent(dbListener)
    }

    override fun onStop() {
        super.onStop()
        dbManager.database.child("WORD BANKS").removeEventListener(dbListener)
    }

    override fun onPause() {
        super.onPause()
        dbManager.database.child("WORD BANKS").removeEventListener(dbListener)
    }

    private fun buildTable() {
        Log.d("partystuff", "into build table")

        lateinit var row: TableRow

        for ((i, word) in packList.withIndex()) {
            val button = Button(this)
            button.text = word
            button.id = i
            button.setOnClickListener(View.OnClickListener {
                startGame(word!!)
            })
            buttonMap[i] = word
            if (i % 3 == 0) {

                row = TableRow(this)
                table_buttons.addView(row)
            }
            row.addView(button)
        }
    }

    fun startGame(word: String) {
        Log.d("partystuff", "start cP with $word")

        when (nextActivity) {
            "catch" -> {
                cpRepository.customWords.clear()
                cpRepository.loadWordsFromDatabase(dataSnapshot, word)
                val intent = Intent(this, cpActivity::class.java)
                startActivity(intent)
            }
            "pict" -> {
                FbData.clearStack()
                FbData.loadWordsFromDatabase(dataSnapshot, word)
                FbData.resetUI()
                val intent = Intent(this, PictionarySplashActivity::class.java)
                startActivity(intent)
            }
            "fish" -> {
                FbData.clearStack()
                FbData.resetUI()
                FbData.loadWordsFromDatabase(dataSnapshot, word)
                val intent = Intent(this, FbActivity::class.java)
                startActivity(intent)
            }
        }
    }

    val dbListener = object : ValueEventListener {
        override fun onDataChange(allData: DataSnapshot) {
            dataSnapshot = allData
            for (wordPackage in allData.children) {
                val name = wordPackage.key
                packList.add(name)
                Log.d("partystuff", "$name")
            }
            buildTable()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.d("partystuff", "loadPost:onCancelled", databaseError.toException())
        }
    }

}
