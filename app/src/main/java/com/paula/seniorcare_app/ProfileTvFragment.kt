package com.paula.seniorcare_app

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_profile_tv.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileTvFragment : Fragment() {
    private val GALLERY_INTENT = 2
    private val TAG = "ProfileTvActivity"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
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
                user = getUserFromDB(db, uid)
            }
            nameTextView.text = user?.get("name") as String?
            emailTextView.text = user?.get("email") as String?
            roleTextView.text = user?.get("role") as String?
            providerTextView.text = user?.get("provider") as String
            downloadImage = user?.get("image") as String?
            Glide.with(this@ProfileTvFragment).load(downloadImage.toString()).centerCrop().into(profileImageView)

        }

        editImageButton.setOnClickListener {    //SE ADELANTA (PORQUE NO GUARDA IMAGEN EN BBDD), MIRAR CORRUTINAS
            selectImageFromGallery()
        }

        editNameButton.setOnClickListener {
            showEditNameDialog()
        }

        logOutButton.setOnClickListener{
            //Borrado de datos
            FirebaseAuth.getInstance().signOut()
            requireActivity().onBackPressed()
            val authIntent = Intent(context,AuthActivity::class.java)
            startActivity(authIntent)
        }

        return view
    }

    private suspend fun getUserFromDB(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            null
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
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        if(requestCode == GALLERY_INTENT && resultCode == AppCompatActivity.RESULT_OK){
            val uri = data!!.data!!
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
                    editUserNameInDatabase(db, uid, newNameEditText.text.toString())
                }
            }
        }
        builder.setNegativeButton("Cancelar",null)
        builder.setView(dialogLayout)
        builder.show()
    }

    private suspend fun editUserNameInDatabase(db: FirebaseFirestore, uid: String, name: String){
        try {
            db.collection("users").document(uid).update("name", name).await()
            true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "UPDATING USER NAME ERROR", e)
            false
        }
    }
}