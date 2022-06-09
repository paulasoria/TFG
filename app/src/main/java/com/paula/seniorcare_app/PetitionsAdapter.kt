package com.paula.seniorcare_app

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.model.Petition
import kotlinx.coroutines.tasks.await


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

        val acceptButton : Button = rootView.findViewById(R.id.acceptButton)
        val rejectButton : Button = rootView.findViewById(R.id.rejectButton)

        val image : ImageView = rootView.findViewById(R.id.relativePetitionImageView)
        val name : TextView = rootView.findViewById(R.id.namePetitionTextView)
        val email : TextView = rootView.findViewById(R.id.emailPetitionTextView)
        val petition : Petition = getItem(p0) as Petition

        Glide.with(context).load(petition.senderImage).into(image)
        name.text = petition.senderName
        email.text = petition.senderEmail

        acceptButton.setOnClickListener {
            changePetitionState(db, petition, "accepted")
            //Google Function que actualice peticion del sender
            petitionsList.remove(petition)
            addNewRelative(db, petition)
            //Googlw Function que agregue al receiver como familiar
            notifyDataSetChanged()
        }

        rejectButton.setOnClickListener {
            changePetitionState(db, petition, "rejected")
            //Google Function que actualice peticion del sender
            petitionsList.remove(petition)
            notifyDataSetChanged()
        }

        return rootView
    }

    private fun changePetitionState(db: FirebaseFirestore, petition: Petition, state: String) {
        db.collection("users").document(petition.receiver.toString()).collection("petitions").document(petition.id.toString()).update("state", state)
    }

    private fun addNewRelative(db: FirebaseFirestore, petition: Petition){
        db.collection("users").document(petition.receiver.toString()).collection("relatives").document(petition.sender.toString()).set(
            hashMapOf(
                "uid" to petition.sender,
                "image" to petition.senderImage,
                "name" to petition.senderName,
                "email" to petition.senderEmail
            )
        )
    }
}