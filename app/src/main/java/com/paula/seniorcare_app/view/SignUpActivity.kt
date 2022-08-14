package com.paula.seniorcare_app.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.SignUpContract
import com.paula.seniorcare_app.presenter.SignUpPresenter
import kotlinx.android.synthetic.main.activity_auth.signUpButton
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity(), SignUpContract.View {
    private val GALLERY_INTENT = 2
    private var uri: Uri = Uri.EMPTY
    private val signUpPresenter = SignUpPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val db = FirebaseFirestore.getInstance()
        val st = FirebaseStorage.getInstance().reference

        userImageView.setOnClickListener {
            selectImageFromGallery()
        }

        signUpButton.setOnClickListener {
            val name = nameTextInput.editText?.text.toString()
            val email = emailTextInput.editText?.text.toString()
            val password = passwordTextInput.editText?.text.toString()
            val roleMenu = roleMenuTextView.text.toString()
            if (name.trim().isNotEmpty() && email.trim().isNotEmpty() && password.trim().isNotEmpty() && roleMenu.trim().isNotEmpty()) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        if(signUpPresenter.signUp(email, password)) {
                            if(uri == Uri.EMPTY){
                                signUpPresenter.createUser(db, "https://firebasestorage.googleapis.com/v0/b/seniorcare-tfg.appspot.com/o/no_photo_user.jpg?alt=media&token=5c2a71ea-774b-450a-868c-4fce85e356c8", name, email, roleMenu)
                                if(roleMenu == "Administrador"){
                                    val homeIntent = Intent(baseContext, HomeActivity::class.java)
                                    startActivity(homeIntent)
                                } else {    //Familiar
                                    val tvIntent = Intent(baseContext, TvActivity::class.java)
                                    startActivity(tvIntent)
                                }
                            } else {
                                val segments = uri.path!!.split("/".toRegex()).toTypedArray()
                                val filename = segments[segments.size - 1]
                                val uploadedSuccessfully = signUpPresenter.uploadPhoto(st, uri, filename)
                                if (uploadedSuccessfully) {
                                    val url = signUpPresenter.getPhotoUrl(st, filename)
                                    url?.let {
                                        signUpPresenter.createUser(db, url, name, email, roleMenu)
                                        if(roleMenu == "Administrador"){
                                            val homeIntent = Intent(baseContext, HomeActivity::class.java)
                                            startActivity(homeIntent)
                                        } else {    //Familiar
                                            val tvIntent = Intent(baseContext, TvActivity::class.java)
                                            startActivity(tvIntent)
                                        }
                                    }
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                showAlertSignUp()
                            }
                        }
                    }
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

        val roles = resources.getStringArray(R.array.user_role)
        val adapter = ArrayAdapter(
            this,
            R.layout.lista_menu,
            roles
        )
        roleMenuTextView.setAdapter(adapter)
    }

    override fun showAlertSignUp(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(getString(R.string.signup_error))
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun emptyEditText(x: TextInputLayout) {
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
            Glide.with(this).load(uri.toString()).centerCrop().into(userImageView)
        }
    }
}