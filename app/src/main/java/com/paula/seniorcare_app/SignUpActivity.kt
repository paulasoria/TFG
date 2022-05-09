package com.paula.seniorcare_app

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_auth.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private val GALLERY_INTENT = 2
    private val db = FirebaseFirestore.getInstance()
    private val st = FirebaseStorage.getInstance().reference
    private var uri: Uri = Uri.EMPTY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setup()
    }

    private fun setup(){
        title = "Registro"

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
                    //Crear un usuario en la base de datos
                    db.collection("users").document(uid).set(
                        hashMapOf(
                            "uid" to uid,
                            "image" to null,
                            "name" to name,
                            "email" to email,
                            "role" to roleMenu,
                            "provider" to "SeniorCare",
                            //"relatives" to relativesList
                        )
                    ).addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

                    //Imagen
                    val segments = uri.path!!.split("/".toRegex()).toTypedArray()
                    val filename = segments[segments.size - 1]
                    val filepath = st.child(uid).child(filename)
                    filepath.putFile(uri).continueWithTask {
                        filepath.downloadUrl
                    }.addOnSuccessListener {
                        db.collection("users").document(uid).update("image", it.toString())
                            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!") }
                            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
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