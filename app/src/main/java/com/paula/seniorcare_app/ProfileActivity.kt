package com.paula.seniorcare_app

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
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
    private var downloadImage: String? = null
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setup()
    }

    private fun setup() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)


        db.collection("users").document(uid).get().addOnSuccessListener {
            downloadImage = it.get("image") as String?
            Glide.with(this).load(downloadImage.toString()).into(profileImageView)
            nameTextView.text = it.get("name") as String?
            emailTextView.text = it.get("email") as String?
            roleTextView.text = it.get("role") as String?
            providerTextView.text = it.get("provider") as String?
        }

        editImageButton.setOnClickListener {    //SE ADELANTA (PORQUE NO GUARDA IMAGEN EN BBDD), MIRAR CORRUTINAS
            selectImageFromGallery()
        }

        editNameButton.setOnClickListener {
            showEditNameDialog()
        }

        logOutButton.setOnClickListener {
            //Borrado de datos
            val prefs =
                getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
            val authIntent = Intent(this, AuthActivity::class.java)
            startActivity(authIntent)
        }
    }

    private fun selectImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_INTENT)
    }


    /**
     * Uploads the selected [uri] to an [storageReference] with [userId] and [filename] as childs.
     */
    suspend fun uploadPhotoToFireStorage(
        storageReference: StorageReference,
        uri: Uri,
        filename: String,
        userId: String
    ): Boolean {
        return try {
            val filepath = storageReference.child(userId).child(filename)
            filepath.putFile(uri).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "UPLOADING PHOTO ERROR", e)
            return false
        }
    }

    /**
     * Obtains the selected url of passed [filename] of [userId] in [storageReference].
     */
    suspend fun getURLofPhotoInFireStorage(
        storageReference: StorageReference,
        filename: String,
        userId: String
    ): String? {
        return try {
            val filepath = storageReference.child(userId).child(filename)
            val result = filepath.downloadUrl.await()
            val url = result.toString()
            url
        } catch (e: Exception) {
            Log.e(TAG, "GETTING PHOTO URL ERROR", e)
            return null
        }
    }

    /**
     * Updates the [url] of [userId] in [fbFirestore].
     */
    suspend fun updatePhotoURLForUser(
        fbFirestore: FirebaseFirestore,
        userId: String,
        url: String,
    ): Boolean? {
        return try {
            fbFirestore.collection("users").document(userId).update("image", url).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "UPDATING PHOTO URL ERROR", e)
            return false
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            uri = data!!.data!!
            profileImageView.setImageURI(uri)

            // GET LOCAL URI AND NECESARY DATA
            val segments = uri.path!!.split("/".toRegex()).toTypedArray()
            val filename = segments[segments.size - 1]
            val storageReference = FirebaseStorage.getInstance().reference
            val userId = FirebaseAuth.getInstance().currentUser!!.uid


            /**
             * FOR THIS TASK, NOW WE PERFORM 3 **ASYNC OPERATIONS**
             *
             * 1. Upload the photo to Firebase Storage (this could be successful or not)
             * 2. Get the URL of the uploaded photo (only if the previous step was successful) (this also could be successful or not)
             * 3. Upload the URL of the uploaded photo to Firestore (only if the previous step was successful) (this also could be successful or not)
             *
             */


            // **ASYNC OPERATIONS** <3 COROUTINES  (avoid callback hell!)

            // In android, when you rotate the screen, the activity is destroyed, so everything starts again...
            // For the sake of simplicity, we guess that the user can't rotate the screen and this would executed without interruptions...
            // Check this: https://developer.android.com/topic/libraries/architecture/coroutines

            // We will use the lifecycleScope.launch

            lifecycleScope.launch {

                // By default lifecycleScope executes in the main thread...
                Log.d(TAG, "COROUTINE STARTS:${Thread.currentThread()}")

                // we need go to other thread in order to perform I/O operations
                withContext(Dispatchers.IO) {
                    Log.d(TAG, "COROUTINE IO OPERATIONS:${Thread.currentThread()}")

                    // (1)
                    val uploadedSuccessfully =
                        uploadPhotoToFireStorage(storageReference, uri, filename, userId)
                    if (uploadedSuccessfully) {
                        // (2)
                        val url = getURLofPhotoInFireStorage(storageReference, filename, userId)
                        url?.let {
                            //Work with non-null user
                            // (3)
                            updatePhotoURLForUser(db, userId, url)
                        }

                    } else {
                        //TODO: Show the error to the user... something goes wrong...
                        // give feedback to the user if necessary.
                    }
                }

                // If we want to interact with the UI we need to execute code in the main thread
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "COROUTINE UI OPERATIONS:${Thread.currentThread()}")
                    Toast.makeText(applicationContext, "SUBIDO!", Toast.LENGTH_SHORT).show()
                }

                // Which thread here?
                Log.d(TAG, "Thread outside above context:${Thread.currentThread()}")

            }

            // Implementing the async task in this way allow us write sequential code and avoid
            // the asynchronous callbacks. For me this is more readable and less error prone

        }
    }

    private fun showEditNameDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
        val newNameEditText = dialogLayout.findViewById<EditText>(R.id.et_editText)
        newNameEditText.hint = nameTextView.text.toString()

        builder.setTitle("Introduce tu nombre")
        builder.setPositiveButton("Guardar") { _, _ ->
            nameTextView.text = newNameEditText.text.toString()
            db.collection("users").document(userEmail.toString())
                .update("name", newNameEditText.text.toString())
                .addOnSuccessListener {
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot successfully updated!"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w(
                        ContentValues.TAG,
                        "Error updating document",
                        e
                    )
                }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.setView(dialogLayout)
        builder.show()
    }
}