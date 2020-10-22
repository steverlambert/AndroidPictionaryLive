package com.example.partygameparty

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_db.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Database Activity - Sends user input to the database
 * Launched from MainActivity
 */

class dbActivity : AppCompatActivity() {

    lateinit var builder: AlertDialog.Builder
    lateinit var dialog: AlertDialog

    private val dbVM: dbViewModel by lazy {
        ViewModelProviders.of(this).get(dbViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_db)
        dbManager.loadDatabaseReference()
        builder = AlertDialog.Builder(this)
        dialog = builder.create()

        setUI()
    }

    fun setUI() {
        if (dbVM.nameConfirmed) {
            editText_name.text.clear()
            editText_name.visibility = View.GONE
            textView_name.text = dbVM.name
            textView_name.visibility = View.VISIBLE
        }

        if (dbVM.codeConfirmed) {
            editText_code.text.clear()
            editText_code.visibility = View.GONE
            textView_code.text = dbVM.code
            textView_code.visibility = View.VISIBLE
        }

        if (dbVM.wordList.size > 0) {
            for (word in dbVM.wordList) {
                val text_view: TextView = TextView(this)
                text_view.setTypeface(Typeface.create("baloo", Typeface.NORMAL), Typeface.NORMAL)
                text_view.textSize = 16f
                text_view.text = word
                scrollLayout.addView(text_view)
            }
        }
    }

    fun writeToDatabase(view: View) {

        dbManager.database.child("WORD BANKS").child(dbVM.code).child(dbVM.name).setValue(dbVM.wordList)
            .addOnSuccessListener {
                successfulPush()
            }
            .addOnFailureListener {
                // Write failed
                // ...
            }
    }

    fun successfulPush() {
        builder.setTitle("Submitted to ${dbVM.code} as ${dbVM.name}")
        builder.setMessage("Add More Words?")
        //builder.setCancelable(true)
        builder.setPositiveButton(
            "ADD MORE",
            DialogInterface.OnClickListener { dialog, id ->
            })
        builder.setNegativeButton(
            "RETURN TO MAIN MENU",
            DialogInterface.OnClickListener {dialog, id ->
                backToMain()
            })
        dialog = builder.create()
        dialog.show()

    }

    fun backButton(view: View) {
        backToMain()
    }

    fun backToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun addWordtoList(view: View) {
        val word = editText_word.text.toString().toUpperCase()
        editText_word.text.clear()

        dbVM.wordList.add(word)
        val text_view: TextView = TextView(this)
        text_view.setTypeface(Typeface.create("baloo", Typeface.NORMAL), Typeface.NORMAL)
        text_view.textSize = 16f
        text_view.text = word

        scrollLayout.addView(text_view)
        dbVM.index++
    }

    fun removeWordfromList(view: View) {
        dbVM.wordList.removeAt(dbVM.index-1)
        scrollLayout.removeViewAt(dbVM.index-1)
        dbVM.index--
    }

    fun confirmName(view: View) {
        if (!dbVM.nameConfirmed) {
            dbVM.name = editText_name.text.toString().toUpperCase()
            editText_name.text.clear()
            editText_name.visibility = View.GONE
            textView_name.text = dbVM.name
            textView_name.visibility = View.VISIBLE
            dbVM.nameConfirmed = true
        }
    }

    fun confirmCode(view: View) {
        if (!dbVM.codeConfirmed) {
            dbVM.code = editText_code.text.toString().toUpperCase()
            editText_code.text.clear()
            editText_code.visibility = View.GONE
            textView_code.text = dbVM.code
            textView_code.visibility = View.VISIBLE
            dbVM.codeConfirmed = true
        }
    }
}