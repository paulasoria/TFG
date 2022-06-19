package com.paula.seniorcare_app

import android.content.ContentValues
import android.content.Intent
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
import com.google.gson.Gson
import com.paula.seniorcare_app.model.Alert
import com.paula.seniorcare_app.model.User
import com.paula.seniorcare_app.model.Videocall
import kotlinx.android.synthetic.main.activity_relative_profile.*
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
        roleTextView.text = user.role.toString()
        Glide.with(this).load(user.image.toString()).centerCrop().into(profileImageView)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
                if(relativeIsAdded(relativeUid)?.isEmpty == false) {
                    withContext(Dispatchers.Main) {
                        videocallButton.visibility = View.VISIBLE
                        deleteRelativeButton.visibility = View.VISIBLE
                        addRelativeButton.visibility = View.INVISIBLE
                    }
                } else {
                    videocallButton.visibility = View.INVISIBLE
                    deleteRelativeButton.visibility = View.INVISIBLE
                    addRelativeButton.visibility = View.VISIBLE
                    if (petitionIsPendingByReceiver(relativeUid)?.isEmpty == true && petitionIsPendingBySender(relativeUid)?.isEmpty == true) { //No peticion pendiente
                        withContext(Dispatchers.Main) {
                            addRelativeButton.isEnabled = true
                        }
                    } else {    //Peticion pendiente
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Solicitud de familiar pendiente", Toast.LENGTH_SHORT).show()
                            addRelativeButton.isEnabled = false
                        }
                    }
                }
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
            //Notificacion al receiver
            //Receiver envía notificación al sender
            // - Accepted: Abrir reunión jitsi
            // - Refussed: Guardar en el historial

            val intent = Intent(baseContext, VideocallActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("uid", user.uid.toString())     //PASAR DATOS DEL FAMILIAR
            intent.putExtra("name", user.name.toString())
            intent.putExtra("email", user.email.toString())
            intent.putExtra("image", user.image.toString())
            startActivity(intent)

            /*val id = UUID.randomUUID().toString()
            val sender = FirebaseAuth.getInstance().currentUser!!.uid
            val receiver = user.uid.toString()
            val calendar = Calendar.getInstance()
            val time = "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
            val date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)}/${calendar.get(Calendar.YEAR)}"
            val state = "waiting"
            val v = Videocall(id, sender, receiver, date, time, state)

            val gson: Gson? = null
            val json = gson?.toJson(v)*/

            //LLAMADA SERVICIO WEB
            /*val url = ""  //URL SERVIDOR

            val outputJson = Gson()
            val request = StringRequest(Request.Method.GET, url, {
                val jsonObject = JSONObject(it)
                //jsonObject.get()
            }, {
                Log.d(ContentValues.TAG, "ERROR")
            })
            Volley.newRequestQueue(this).add(request)*/
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

    private suspend fun relativeIsAdded(uid: String): QuerySnapshot? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            return db.collection("users").document(currentUid).collection("relatives").whereEqualTo("uid",uid).get().await()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun petitionIsPendingByReceiver(receiver: String): QuerySnapshot? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            return db.collection("users").document(currentUid).collection("petitions").whereEqualTo("receiver",receiver).get().await()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun petitionIsPendingBySender(sender: String): QuerySnapshot? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
            return db.collection("users").document(currentUid).collection("petitions").whereEqualTo("sender",sender).get().await()
        } catch (e: Exception) {
            null
        }
    }
}