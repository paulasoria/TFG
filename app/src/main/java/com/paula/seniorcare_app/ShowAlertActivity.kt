package com.paula.seniorcare_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_show_alert.*

class ShowAlertActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_alert)

        msgShowAlert.text = intent.getStringExtra("msg").toString()
        timeShowAlert.text = intent.getStringExtra("time").toString()
        dateShowAlert.text = intent.getStringExtra("date").toString()

        stopShowAlertButton.setOnClickListener {
            finish()
        }
    }
}