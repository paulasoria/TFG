package com.paula.seniorcare_app

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private val GALLERY_INTENT = 2
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email
    private val db = FirebaseFirestore.getInstance()
    private val st = FirebaseStorage.getInstance().reference
    private var uri: Uri = Uri.EMPTY
    private var downloadImage : String? = null
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setup()
    }

    private fun setup(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)


        db.collection("users").document(uid).get().addOnSuccessListener {
            downloadImage = it.get("image") as String?
            Glide.with(this).load(downloadImage.toString()).into(profileImageView)
            nameTextView.text = it.get("name") as String?
            emailTextView.text = it.get("email") as String?
            rolTextView.text = it.get("rol") as String?
            providerTextView.text = it.get("provider") as String
        }

        editImageButton.setOnClickListener {    //SE ADELANTA (PORQUE NO GUARDA IMAGEN EN BBDD), MIRAR CORRUTINAS
            selectImageFromGallery()
        }

        editNameButton.setOnClickListener {
            showEditNameDialog()
        }

        logOutButton.setOnClickListener{
            //Borrado de datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
            val authIntent = Intent(this,AuthActivity::class.java)
            startActivity(authIntent)
        }
    }

    private fun selectImageFromGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_INTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            uri = data!!.data!!
            profileImageView.setImageURI(uri)

            val segments = uri.path!!.split("/".toRegex()).toTypedArray()
            val filename = segments[segments.size - 1]
            val filepath = st.child(uid).child(filename)
            filepath.putFile(uri).continueWithTask {
                filepath.downloadUrl
            }.addOnSuccessListener {
                downloadImage = it.toString()
                db.collection("users").document(uid).update("image", it.toString())
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
            }
        }
    }

    private fun showEditNameDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
        val newNameEditText = dialogLayout.findViewById<EditText>(R.id.et_editText)
        newNameEditText.hint = nameTextView.text.toString()

        builder.setTitle("Introduce tu nombre")
        builder.setPositiveButton("Guardar") { _,_ ->
            nameTextView.text = newNameEditText.text.toString()
            db.collection("users").document(userEmail.toString()).update("name", newNameEditText.text.toString())
                .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!") }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
        }
        builder.setNegativeButton("Cancelar",null)
        builder.setView(dialogLayout)
        builder.show()
    }
}