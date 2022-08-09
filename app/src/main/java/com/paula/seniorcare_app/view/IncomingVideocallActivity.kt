package com.paula.seniorcare_app.view

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.IncomingVideocallContract
import com.paula.seniorcare_app.interactor.IncomingVideocallInteractor
import com.paula.seniorcare_app.presenter.IncomingVideocallPresenter
import kotlinx.android.synthetic.main.activity_incoming_videocall.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class IncomingVideocallActivity : AppCompatActivity(), IncomingVideocallContract.View {

    lateinit var incomingVideocallPresenter: IncomingVideocallPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_videocall)
        incomingVideocallPresenter = IncomingVideocallPresenter(this, IncomingVideocallInteractor())

        val db = FirebaseFirestore.getInstance()

        nameVideocallTextView.text = intent.getStringExtra("senderName").toString()
        emailVideocallTextView.text = intent.getStringExtra("senderEmail").toString()
        val senderImage = intent.getStringExtra("senderImage").toString()
        Glide.with(this).load(senderImage).centerCrop().into(imageVideocallImageView)
        val senderUid = intent.getStringExtra("senderUid").toString()
        val callId = intent.getStringExtra("callId").toString()
        val receiverJwt = intent.getStringExtra("receiverJwt").toString()

        rejectCallButton.setOnClickListener{
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    incomingVideocallPresenter.changeStateCall(db, callId, "rejected")
                    //HTTP FUNCTION
                    val sender = incomingVideocallPresenter.getUser(db, senderUid)
                    val senderToken = sender?.get("token") as String
                    incomingVideocallPresenter.rejectCallHttp(senderToken, callId)
                }
                finish()
            }
        }

        acceptCallButton.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    incomingVideocallPresenter.changeStateCall(db, callId, "accepted")
                    //HTTP FUNCTION
                    val sender = incomingVideocallPresenter.getUser(db, senderUid)
                    val senderToken = sender?.get("token") as String    //Dice que es null ???
                    incomingVideocallPresenter.acceptCallHttp(senderToken, callId)
                }
                val intent = Intent(baseContext, VideocallActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val sp: SharedPreferences = getSharedPreferences("JWT_FILE", MODE_PRIVATE)
                val edit: SharedPreferences.Editor = sp.edit()
                edit.putString("JWT",receiverJwt)
                edit.apply()
                intent.putExtra("callId",callId)
                startActivity(intent)
            }
        }
    }
}