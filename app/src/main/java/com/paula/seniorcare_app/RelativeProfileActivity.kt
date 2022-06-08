package com.paula.seniorcare_app

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.paula.seniorcare_app.model.Petition
import com.paula.seniorcare_app.model.User
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_relative_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class RelativeProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relative_profile)

        setup()
    }

    private fun setup() {
        val user = intent.getSerializableExtra("user") as User
        val relativeUid = user.uid.toString()
        nameTextView.text = user.name.toString()
        emailTextView.text = user.email.toString()
        //role???
        Glide.with(this).load(user.image.toString()).into(profileImageView)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if(relativeIsAdded(relativeUid)){   //or petitionIsAccepted ???
                    //SI EL FAMILIAR ESTÁ AÑADIDO:
                    videocallButton.visibility = View.VISIBLE
                    deleteRelativeButton.visibility = View.VISIBLE
                    addRelativeButton.visibility = View.INVISIBLE
                } else if(petitionIsPending(relativeUid)?.isEmpty == false) { //Peticion pendiente
                    withContext(Dispatchers.Main) {
                        addRelativeButton.isEnabled = false             //Desactivar boton de añadir familiar
                    }
                }
                /*else {
                    //SI EL FAMILIAR NO ESTÁ AÑADIDO:
                    videocallButton.isEnabled = false
                    deleteRelativeButton.isEnabled = false
                    addRelativeButton.isEnabled = true
                }*/
            }
        }

        addRelativeButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            var functions: FirebaseFunctions
            functions = Firebase.functions
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    createPetitionInDatabase(db, functions, currentUid, relativeUid)   //sender, receiver
                    //Enviar notificación solicitud de familiar

                    //ESTO AL ACEPTAR: updateRelativesList(db, currentUid, relativeUid)
                }
            }
            Toast.makeText(this, "Solicitud de familiar enviada", Toast.LENGTH_SHORT).show()
            addRelativeButton.isEnabled = false
        }
    }

    private suspend fun createPetitionInDatabase(db: FirebaseFirestore, functions: FirebaseFunctions, sender: String, receiver: String): Boolean {
        return try {
            val id = UUID.randomUUID().toString()
            db.collection("users").document(sender).collection("petitions").document(id).set(hashMapOf(
                "id" to id,
                "sender" to sender,
                "receiver" to receiver,
                "state" to "pending"    //pending, accepted or rejected
            )).await()
            //AQUÍ SE DISPARA UNA GOOGLE CLOUD FUNCTION PARA CREAR LA PETICIÓN EN EL RECEIVER
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "CREATING PETITION IN DATABASE ERROR", e)
            false
        }
    }

    private suspend fun relativeIsAdded(/*db: FirebaseFirestore, currentUid: String, */uid: String): Boolean {
        return try {
            var found = false
            val db = FirebaseFirestore.getInstance()
            /*val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            val userData = db.collection("users").document(currentUid).get().await()
            val relativesList = userData.data?.get("relatives") as ArrayList<String>
            relativesList.iterator().forEach { relative ->
                if(relative == uid){
                    found = true
                }
            }*/
            found
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun petitionIsPending(uid: String): QuerySnapshot? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            val petition = db.collectionGroup("petitions").whereEqualTo("sender",currentUid).whereEqualTo("receiver",uid).whereEqualTo("state","pending").get().await()
            petition
        } catch (e: Exception) {
            null
        }
    }

    //CUANDO SE ACEPTE UNA SOLICITUD O SE RECIBA UNA SOLICITUD ACEPTADA
    private suspend fun updateRelativesList(db: FirebaseFirestore, currentUid: String, newRelative: String): Boolean {
        return try {
            val userData = db.collection("users").document(currentUid).get().await()
            /*val relativesList = userData.data?.get("relatives") as ArrayList<String>
            relativesList.add(newRelative)
            db.collection("users").document(currentUid).update("relatives", relativesList).await()*/
            true
        } catch (e: Exception) {
            false
        }
    }
}