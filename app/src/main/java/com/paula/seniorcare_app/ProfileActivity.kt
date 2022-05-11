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
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email
    private val db = FirebaseFirestore.getInstance()
    private val st = FirebaseStorage.getInstance().reference
    private var uri: Uri = Uri.EMPTY
    private var downloadImage : String? = null
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val TAG = "ProfileActivity"

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
            roleTextView.text = it.get("role") as String?
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

            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    val uploadedSuccessfully = uploadPhotoToFireStorage(st, uri, filename, uid)
                    if (uploadedSuccessfully) {
                        val url = getURLofPhotoInFireStorage(st, filename, uid)
                        url?.let {
                            updatePhotoURLForUser(db, uid, url)
                        }
                    } else {
                        //TODO: Show the error to the user... something goes wrong...
                        // give feedback to the user if necessary.
                    }
                }
            }
        }
    }

    private suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String, uid: String): Boolean {
        return try {
            val filepath = st.child(uid).child(filename)
            filepath.putFile(uri).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "UPLOADING PHOTO ERROR", e)
            return false
        }
    }

    private suspend fun getURLofPhotoInFireStorage(st: StorageReference, filename: String, uid: String): String? {
        return try {
            val filepath = st.child(uid).child(filename)
            val result = filepath.downloadUrl.await()
            val url = result.toString()
            url
        } catch (e: Exception) {
            Log.e(TAG, "GETTING PHOTO URL ERROR", e)
            return null
        }
    }

    private suspend fun updatePhotoURLForUser(db: FirebaseFirestore, uid: String, url: String): Boolean? {
        return try {
            db.collection("users").document(uid).update("image", url).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "UPDATING PHOTO URL ERROR", e)
            return false
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