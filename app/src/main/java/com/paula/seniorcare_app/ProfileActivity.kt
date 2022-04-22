package com.paula.seniorcare_app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.net.URL
import java.nio.file.Path

class ProfileActivity : AppCompatActivity() {

    private val GALLERY_INTENT = 2
    var userEmail = FirebaseAuth.getInstance().currentUser?.email
    private val db = FirebaseFirestore.getInstance()
    private val st = FirebaseStorage.getInstance().reference
    private var uri: Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setup()
    }

    private fun setup(){
        title = "Perfil"

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        var downloadImage : String?

        db.collection("users").document(FirebaseAuth.getInstance().currentUser?.email.toString()).get().addOnSuccessListener {
            downloadImage = it.get("image") as String?
            Glide.with(this).load(downloadImage.toString()).into(profileImageView)
            nameTextView.text = it.get("name") as String?
            emailTextView.text = it.get("email") as String?
            rolTextView.text = it.get("rol") as String?
            providerTextView.text = it.get("provider") as String
        }

        editImageButton.setOnClickListener {    //SE ADELANTA CREO (PORQUE NO GUARDA IMAGEN EN BBDD), MIRAR CORRUTINAS
            selectImageFromGallery()
            //modificar bbdd
            val filename = userEmail.toString() + "_profile_image.jpg"
            val filepath = st.child("profile_images").child(filename)
            //filepath.delete()
            filepath.putFile(uri).continueWithTask {
                if (!it.isSuccessful) {
                    it.exception
                }
                filepath.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    val uploadedImageUri: String = it.result.toString()

                    //Modificar un usuario en la base de datos
                    db.collection("users").document(userEmail.toString()).update("image", uploadedImageUri)
                } else {
                    Log.d("LOG", "Error")
                }
            }
        }

        editNameButton.setOnClickListener {
            //Mostrar dialogo
            //db.collection("users").document(userEmail.toString()).update("name", userName)
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
        }
    }
}