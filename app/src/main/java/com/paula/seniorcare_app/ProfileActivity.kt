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
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private val GALLERY_INTENT = 2
    private var userEmail = FirebaseAuth.getInstance().currentUser?.email
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
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    downloadImage = uploadImage(filepath, uri).toString()
                }
                //Modificar un usuario en la base de datos
                db.collection("users").document(userEmail.toString()).update("image", downloadImage)
                    .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
            }
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

    suspend fun uploadImage(filepath: StorageReference, uri: Uri): Task<Uri> {
        filepath.putFile(uri).await()
        return filepath.downloadUrl
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
        }
        builder.setNegativeButton("Cancelar",null)
        builder.setView(dialogLayout)
        builder.show()
    }
}