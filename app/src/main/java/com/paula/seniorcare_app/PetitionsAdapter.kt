package com.paula.seniorcare_app

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.model.Petition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PetitionsAdapter(private var petitionsList: ArrayList<Petition>, var context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return petitionsList.size
    }

    override fun getItem(p0: Int): Any {
        return petitionsList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rootView: View = View.inflate(context, R.layout.petition_item, null)
        val db = FirebaseFirestore.getInstance()

        val image : ImageView = rootView.findViewById(R.id.relativePetitionImageView)
        val name : TextView = rootView.findViewById(R.id.namePetitionTextView)
        val email : TextView = rootView.findViewById(R.id.emailPetitionTextView)
        val petition : Petition = getItem(p0) as Petition

        Glide.with(context).load(petition.senderImage).into(image)
        name.text = petition.senderName
        email.text = petition.senderEmail

        return rootView
    }

    private suspend fun getSenderOfPetition(db: FirebaseFirestore, id: String): DocumentSnapshot? {
        return try {
            val sender = db.collection("users").document(id).get().await()
            sender
        } catch (e: Exception) {
            null
        }
    }
}