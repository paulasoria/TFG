package com.paula.seniorcare_app

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        setup()
        session()    //Comprobar si existe una sesión activa
    }

    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    private fun session(){
        val db = FirebaseFirestore.getInstance()
        if(FirebaseAuth.getInstance().currentUser != null){
            authLayout.visibility = View.INVISIBLE
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val user = getUser(db, uid)
                    if(user?.get("role") == "Administrador"){
                        showHome()
                    } else {    //Familiar
                        showTv()
                    }
                }
            }
        }
    }

    private suspend fun getUser(db: FirebaseFirestore, uid: String): DocumentSnapshot? {
        return try {
            val user = db.collection("users").document(uid).get().await()
            user
        } catch (e: Exception) {
            null
        }
    }

    fun getRotation(context: Context): String {
        return when ((context.getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.orientation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> "vertical"
            Surface.ROTATION_90 -> "horizontal"
            else -> "horizontal"
        }
    }

    private fun setup(){

        if(getRotation(applicationContext) == "horizontal"){
            signUpButton.isEnabled = false
            googleButton.isEnabled = false
        }

        logInButton.setOnClickListener {
            val logInIntent = Intent(this,LogInActivity::class.java)
            startActivity(logInIntent)
        }

        signUpButton.setOnClickListener {
            val signUpIntent = Intent(this,SignUpActivity::class.java)
            startActivity(signUpIntent)
        }

        googleButton.setOnClickListener {
            //val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("39173987489-uqh5d2n78ur9mitch5gla7gho3jv6cff.apps.googleusercontent.com").requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun showAlertGoogle(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(getString(R.string.google_error))
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(){
        val homeIntent = Intent(this,HomeActivity::class.java)
        startActivity(homeIntent)
    }

    private fun showTv(){
        val intent = Intent(this,TvActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val db = FirebaseFirestore.getInstance()
        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    createUserFromGoogle(db, account)
                                }
                                showHome()
                            }
                        } else {
                            showAlertGoogle()
                        }
                    }
                }
            } catch (e: ApiException){
                showAlertGoogle()
            }
        }
    }

    private suspend fun createUserFromGoogle(db: FirebaseFirestore, account: GoogleSignInAccount): Boolean {
        return try {
            val token = FirebaseInstanceId.getInstance().instanceId.await().token
            db.collection("users").document(account.id.toString()).set(
                hashMapOf(
                    "uid" to account.id.toString(),
                    "token" to token,
                    "image" to R.drawable.no_photo_user,
                    "name" to account.displayName,
                    "email" to account.email,
                    "role" to "Administrador"
                )
            ).await()
            true
        }  catch (e: Exception) {
            Log.e(ContentValues.TAG, "CREATING USER FROM GOOGLE ERROR", e)
            false
        }
    }
}