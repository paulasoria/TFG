package com.paula.seniorcare_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_incoming_videocall.*

class IncomingVideocallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_videocall)

        nameVideocallTextView.text = intent.getStringExtra("senderName").toString()
        emailVideocallTextView.text = intent.getStringExtra("senderEmail").toString()
        val senderImage = intent.getStringExtra("senderImage").toString()
        Glide.with(this).load(senderImage).centerCrop().into(imageVideocallImageView)

        rejectCallButton.setOnClickListener{
            finish()
            //Modificar videollamada en firestore con estado "lost" ????????????
            //Google Cloud Function para poner llamada como "lost" en el otro usuario ?????????????????
        }

        acceptCallButton.setOnClickListener {
            //Aceptar llamada
        }
    }
}