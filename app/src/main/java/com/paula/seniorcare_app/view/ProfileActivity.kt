package com.paula.seniorcare_app.view

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.ProfileContract
import com.paula.seniorcare_app.presenter.ProfilePresenter
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity(), ProfileContract.View {
    private val GALLERY_INTENT = 2
    private val profilePresenter = ProfilePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val db = FirebaseFirestore.getInstance()
        var downloadImage : String?
        var user: DocumentSnapshot?
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                user = profilePresenter.getUser(db, uid)
            }
            nameTextView.text = user?.get("name") as String?
            emailTextView.text = user?.get("email") as String?
            roleTextView.text = user?.get("role") as String?
            downloadImage = user?.get("image") as String?
            Glide.with(this@ProfileActivity).load(downloadImage.toString()).centerCrop().into(profileImageView)

        }

        editImageButton.setOnClickListener {    //SE ADELANTA (PORQUE NO GUARDA IMAGEN EN BBDD), MIRAR CORRUTINAS
            selectImageFromGallery()
        }

        editNameButton.setOnClickListener {
            showEditNameDialog()
        }

        logOutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
            val authIntent = Intent(this, AuthActivity::class.java)
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
        val db = FirebaseFirestore.getInstance()
        val st = FirebaseStorage.getInstance().reference

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            val uri = data!!.data!!
            profileImageView.setImageURI(uri)

            val segments = uri.path!!.split("/".toRegex()).toTypedArray()
            val filename = segments[segments.size - 1]

            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    val uploadedSuccessfully = profilePresenter.uploadPhoto(st, uri, filename)
                    if (uploadedSuccessfully) {
                        val url = profilePresenter.getPhotoUrl(st, filename)
                        url?.let {
                            profilePresenter.updatePhotoUrl(db, url)
                        }
                    }
                }
            }
        }
    }

    override fun showEditNameDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
        val newNameEditText = dialogLayout.findViewById<EditText>(R.id.et_editText)
        val db = FirebaseFirestore.getInstance()

        newNameEditText.hint = nameTextView.text.toString()

        builder.setTitle("Introduce tu nombre")
        builder.setPositiveButton("Guardar") { _, _ ->
            nameTextView.text = newNameEditText.text.toString()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    profilePresenter.editUserName(db, newNameEditText.text.toString())
                }
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.setView(dialogLayout)
        builder.show()
    }
}