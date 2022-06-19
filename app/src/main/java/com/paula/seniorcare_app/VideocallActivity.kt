package com.paula.seniorcare_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_relative_profile.*
import kotlinx.android.synthetic.main.activity_show_alert.*
import kotlinx.android.synthetic.main.activity_videocall.*

class VideocallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videocall)

        val uid = intent.getStringExtra("uid").toString()
        val name = intent.getStringExtra("name").toString()
        val email = intent.getStringExtra("email").toString()
        val image = intent.getStringExtra("image").toString()

        nameVideocallTextView.text = name
        Glide.with(this).load(image).centerCrop().into(imageVideocallImageView)

        //Crear videollamada en firestore con estado "waiting"
        //Google Cloud Function para poner llamada como "waiting" en el otro usuario

        endCallButton.setOnClickListener{
            finish()
            //Modificar videollamada en firestore con estado "lost"
            //Google Cloud Function para poner llamada como "lost" en el otro usuario
        }
    }
}