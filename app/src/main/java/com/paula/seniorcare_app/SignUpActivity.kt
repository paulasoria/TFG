package com.paula.seniorcare_app

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.paula.seniorcare_app.model.User
import kotlinx.android.synthetic.main.activity_auth.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {

    private val GALLERY_INTENT = 2
    private var uri: Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setup()
    }

    private fun setup(){
        val db = FirebaseFirestore.getInstance()
        val st = FirebaseStorage.getInstance().reference

        userImageView.setOnClickListener {
            selectImageFromGallery()
        }

        signUpButton.setOnClickListener {
            val name = nameTextInput.editText?.text.toString();
            val email = emailTextInput.editText?.text.toString();
            val password = passwordTextInput.editText?.text.toString();
            val roleMenu = roleMenuTextView.text.toString();
            if (name.trim().isNotEmpty() && email.trim().isNotEmpty() && password.trim().isNotEmpty() && roleMenu.trim().isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                    //Guardado de datos
                    val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                    prefs.putString("email", email)
                    prefs.apply()

                    val uid = FirebaseAuth.getInstance().currentUser!!.uid

                    //Imagen
                    val segments = uri.path!!.split("/".toRegex()).toTypedArray()
                    val filename = segments[segments.size - 1]

                    lifecycleScope.launch {
                        withContext(Dispatchers.IO){
                            val uploadedSuccessfully = uploadPhotoToFireStorage(st, uri, filename, uid)
                            if (uploadedSuccessfully) {
                                val url = getURLofPhotoInFireStorage(st, filename, uid)
                                url?.let {
                                    createUserInDatabase(db, uid, url, name, email, roleMenu)
                                }
                            } else {
                                //TODO: Show the error to the user... something goes wrong...
                                // give feedback to the user if necessary.
                            }
                        }
                    }

                    showHome()
                }.addOnFailureListener {
                    showAlertSignUp()
                }

            } else {
                emptyEditText(nameTextInput)
                emptyEditText(emailTextInput)
                emptyEditText(passwordTextInput)
                if(roleMenuTextView.text.toString().trim().isEmpty()){
                    dropdownMenu.error = getString(R.string.empty_field)
                } else { dropdownMenu.error = null }
            }
        }

        val roles = resources.getStringArray(R.array.rol_usuario)
        val adapter = ArrayAdapter(
            this,
            R.layout.lista_roles,
            roles
        )
        roleMenuTextView.setAdapter(adapter)
    }

    private suspend fun createUserInDatabase(db: FirebaseFirestore, uid: String, url: String, name: String, email: String, roleMenu: String): Boolean {
        return try {
            val relativesNull = ArrayList<String>()
            val petitionsNull = ArrayList<DocumentReference>()
            db.collection("users").document(uid).set(
                hashMapOf(
                    "uid" to uid,
                    "image" to url,
                    "name" to name,
                    "email" to email,
                    "role" to roleMenu,
                    "provider" to "SeniorCare",
                    "relatives" to relativesNull,
                    "petitions" to petitionsNull
                )
            ).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "CREATING USER IN DATABASE ERROR", e)
            false
        }
    }

    private suspend fun uploadPhotoToFireStorage(st: StorageReference, uri: Uri, filename: String, uid: String): Boolean {
        return try {
            val filepath = st.child(uid).child(filename)
            filepath.putFile(uri).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "UPLOADING PHOTO ERROR", e)
            false
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
            null
        }
    }

    private suspend fun updatePhotoURLForUser(db: FirebaseFirestore, uid: String, url: String): Boolean? {
        return try {
            db.collection("users").document(uid).update("image", url).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "UPDATING PHOTO URL ERROR", e)
            false
        }
    }

    private fun showAlertSignUp(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(getString(R.string.signup_error))
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(){
        val homeIntent = Intent(this,HomeActivity::class.java)
        startActivity(homeIntent)
    }

    private fun emptyEditText(x: TextInputLayout) {
        if(x.editText?.text.toString().trim().isEmpty()){
            x.error = getString(R.string.empty_field)
        } else { x.error = null }
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
            userImageView.setImageURI(uri)
        }
    }
}