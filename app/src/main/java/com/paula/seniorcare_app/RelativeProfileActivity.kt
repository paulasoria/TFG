package com.paula.seniorcare_app

import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.paula.seniorcare_app.model.User
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_relative_profile.*
import kotlinx.android.synthetic.main.activity_relative_profile.emailTextView
import kotlinx.android.synthetic.main.activity_relative_profile.nameTextView
import kotlinx.android.synthetic.main.activity_relative_profile.profileImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*


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
                val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
                if(petitionIsPending(relativeUid,currentUid)?.isEmpty == false || petitionIsPending(currentUid,relativeUid)?.isEmpty == false) { //Peticion pendiente
                    withContext(Dispatchers.Main) {
                        addRelativeButton.isEnabled = false             //Desactivar boton de añadir familiar
                    }
                } else if(relativeIsAdded(relativeUid)) {
                    withContext(Dispatchers.Main) {
                        videocallButton.visibility = View.VISIBLE
                        deleteRelativeButton.visibility = View.VISIBLE
                        addRelativeButton.visibility = View.INVISIBLE
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
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    createPetitionInDatabase(db, currentUid, relativeUid)   //sender, receiver
                    //Enviar notificación solicitud de familiar
                }
            }
            Toast.makeText(this, "Solicitud de familiar enviada", Toast.LENGTH_SHORT).show()
            addRelativeButton.isEnabled = false
        }

        deleteRelativeButton.setOnClickListener {
            showDeleteRelativeDialog(relativeUid)
        }

        videocallButton.setOnClickListener {
            //Susto
        }
    }

    private fun showDeleteRelativeDialog(relativeUid: String){
        val db = FirebaseFirestore.getInstance()
        val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.delete_relative))
        builder.setPositiveButton("Aceptar") { _,_ ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    deleteRelativeFromDB(db, currentUid, relativeUid)
                }
            }
        }
        builder.setNegativeButton("Cancelar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private suspend fun deleteRelativeFromDB(db: FirebaseFirestore, currentUid: String, uid: String): Boolean {
        return try {
            db.collection("users").document(currentUid).collection("relatives").document(uid).delete().await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "DELETING RELATIVE ERROR", e)
            false
        }
    }

    private suspend fun createPetitionInDatabase(db: FirebaseFirestore, sender: String, receiver: String): Boolean {
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

    private suspend fun relativeIsAdded(uid: String): Boolean {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            db.collection("users").document(currentUid).collection("relatives").whereEqualTo("uid",uid).get().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun petitionIsPending(sender: String, receiver: String): QuerySnapshot? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            val petition = db.collection("users").document(sender).collection("petitions").whereEqualTo("receiver",receiver).get().await()
            petition
        } catch (e: Exception) {
            null
        }
    }
}