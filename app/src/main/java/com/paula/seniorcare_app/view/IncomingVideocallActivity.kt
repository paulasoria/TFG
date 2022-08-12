package com.paula.seniorcare_app.view

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.IncomingVideocallContract
import com.paula.seniorcare_app.presenter.IncomingVideocallPresenter
import kotlinx.android.synthetic.main.activity_incoming_videocall.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IncomingVideocallActivity : AppCompatActivity(), IncomingVideocallContract.View {
    private val incomingVideocallPresenter = IncomingVideocallPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_videocall)

        val db = FirebaseFirestore.getInstance()

        nameVideocallTextView.text = intent.getStringExtra("senderName").toString()
        emailVideocallTextView.text = intent.getStringExtra("senderEmail").toString()
        val senderImage = intent.getStringExtra("senderImage").toString()
        Glide.with(this).load(senderImage).centerCrop().into(imageVideocallImageView)
        val senderUid = intent.getStringExtra("senderUid").toString()
        val callId = intent.getStringExtra("callId").toString()
        val receiverJwt = intent.getStringExtra("receiverJwt").toString()

        rejectCallButton.setOnClickListener{
            rejectVideocall(db, callId, senderUid)
        }

        acceptCallButton.setOnClickListener {
            acceptVideocall(db, callId, senderUid, receiverJwt)
        }
    }

    override fun rejectVideocall(db: FirebaseFirestore, callId: String, senderUid: String){
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                incomingVideocallPresenter.changeStateCall(db, callId, "rejected")
                val sender = incomingVideocallPresenter.getUser(db, senderUid)
                val senderToken = sender?.get("token") as String
                incomingVideocallPresenter.rejectCallHttp(senderToken, callId)
            }
            finish()
        }
    }

    override fun acceptVideocall(db: FirebaseFirestore, callId: String, senderUid: String, receiverJwt: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                incomingVideocallPresenter.changeStateCall(db, callId, "accepted")
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