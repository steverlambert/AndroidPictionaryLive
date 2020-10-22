package com.example.partygameparty

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.partygameparty.databinding.ActivityCpBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_cp.*

class cpActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_cp)

        cpRepository.setContext(this)
        val pVM = ViewModelProviders.of(this)
            .get(PartyViewModel::class.java)

        DataBindingUtil.setContentView<ActivityCpBinding>(
            this, R.layout.activity_cp
        ).apply {
            this.setLifecycleOwner(this@cpActivity)
            this.viewmodel = pVM
        }


    }

    override fun onStop() {
        super.onStop()
        Log.d("partystuff", "CP onstop")
    }

    override fun onPause() {
        super.onPause()
        Log.d("partystuff", "CP on pause")

    }
}
