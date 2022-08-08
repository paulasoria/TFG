package com.paula.seniorcare_app.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.ProfileTvContract
import com.paula.seniorcare_app.interactor.ProfileTvInteractor
import com.paula.seniorcare_app.presenter.ProfileTvPresenter
import kotlinx.android.synthetic.main.fragment_profile_tv.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileTvFragment : Fragment(), ProfileTvContract.View {

    private val GALLERY_INTENT = 2
    lateinit var profileTvPresenter: ProfileTvPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileTvPresenter = ProfileTvPresenter(this, ProfileTvInteractor())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_profile_tv, container, false)
        val editImageButton: TextView = view.findViewById(R.id.editImageButton)
        val editNameButton: ImageButton = view.findViewById(R.id.editNameButton)
        val logOutButton: Button = view.findViewById(R.id.logOutButton)

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        var downloadImage : String?
        var user: DocumentSnapshot?

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                user = profileTvPresenter.getUserFromDB(db, uid)
            }
            nameTextView.text = user?.get("name") as String?
            emailTextView.text = user?.get("email") as String?
            roleTextView.text = user?.get("role") as String?
            downloadImage = user?.get("image") as String?
            Glide.with(this@ProfileTvFragment).load(downloadImage.toString()).centerCrop().into(profileImageView)
        }

        editImageButton.setOnClickListener {
            selectImageFromGallery()
        }

        editNameButton.setOnClickListener {
            showEditNameDialog()
        }

        logOutButton.setOnClickListener{
            deleteUserData()
            val authIntent = Intent(context, AuthActivity::class.java)
            startActivity(authIntent)
        }
        return view
    }

    override fun selectImageFromGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_INTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val db = FirebaseFirestore.getInstance()
        val st = FirebaseStorage.getInstance().reference
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        if(requestCode == GALLERY_INTENT && resultCode == AppCompatActivity.RESULT_OK){
            val uri = data!!.data!!
            profileImageView.setImageURI(uri)
            val segments = uri.path!!.split("/".toRegex()).toTypedArray()
            val filename = segments[segments.size - 1]

            lifecycleScope.launch {
                withContext(Dispatchers.IO){
                    val uploadedSuccessfully = profileTvPresenter.uploadPhotoToFireStorage(st, uri, filename, uid)
                    if (uploadedSuccessfully) {
                        val url = profileTvPresenter.getURLofPhotoInFireStorage(st, filename, uid)
                        url?.let {
                            profileTvPresenter.updatePhotoURLForUser(db, uid, url)
                        }
                    }
                }
            }
        }
    }

    override fun deleteUserData() {
        FirebaseAuth.getInstance().signOut()
        requireActivity().onBackPressed()
    }

    override fun showEditNameDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
        val newNameEditText = dialogLayout.findViewById<EditText>(R.id.et_editText)
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        newNameEditText.hint = nameTextView.text.toString()

        builder.setTitle("Introduce tu nombre")
        builder.setPositiveButton("Guardar") { _,_ ->
            nameTextView.text = newNameEditText.text.toString()

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    profileTvPresenter.editUserNameInDatabase(db, uid, newNameEditText.text.toString())
                }
            }
        }
        builder.setNegativeButton("Cancelar",null)
        builder.setView(dialogLayout)
        builder.show()
    }
}