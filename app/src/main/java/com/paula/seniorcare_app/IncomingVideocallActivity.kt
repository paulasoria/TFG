package com.paula.seniorcare_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_incoming_videocall.*

class IncomingVideocallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_videocall)

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