package com.paula.seniorcare_app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_auth.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

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
            if (name.editText?.text.toString().trim().isNotEmpty() && email.editText?.text.toString().trim().isNotEmpty() && password.editText?.text.toString().trim().isNotEmpty() && rol_menu.text.toString().trim().isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.editText?.text.toString(), password.editText?.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //Guardado de datos
                        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.putString("email", email.editText?.text.toString())
                        prefs.apply()

                        //Imagen
                        val filename = email.editText?.text.toString() + "_profile_image.jpg"
                        val filepath = st.child("profile_images").child(filename)
                        filepath.putFile(uri).continueWithTask {
                            if (!it.isSuccessful) {
                                it.exception
                            }
                            filepath.downloadUrl
                        }.addOnCompleteListener{
                            if (it.isSuccessful) {
                                val uploadedImageUri: String = it.result.toString()

                                //Crear un usuario en la base de datos
                                db.collection("users").document(email.editText?.text.toString()).set(
                                    hashMapOf(
                                        "image" to uploadedImageUri,
                                        "name" to name.editText?.text.toString(),
                                        "email" to email.editText?.text.toString(),
                                        "rol" to rol_menu.text.toString(),
                                        "provider" to "SeniorCare",
                                        //"relatives" to relativesList
                                    )
                                )
                            } else {
                                Log.d("LOG", "Error")
                            }
                        }
                        showHome()
                    } else {
                        showAlertSignUp()
                    }
                }
            } else {
                emptyEditText(name)
                emptyEditText(email)
                emptyEditText(password)

                if(rol_menu.text.toString().trim().isEmpty()){
                    dropdown_menu.error = getString(R.string.empty_field)
                } else { dropdown_menu.error = null }
            }
        }

        val roles = resources.getStringArray(R.array.rol_usuario)
        val adapter = ArrayAdapter(
            this,
            R.layout.lista_roles,
            roles
        )
        rol_menu.setAdapter(adapter)
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