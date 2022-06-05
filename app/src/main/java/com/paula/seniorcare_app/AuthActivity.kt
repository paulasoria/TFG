package com.paula.seniorcare_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_auth.*

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
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        if(email != null){
            authLayout.visibility = View.INVISIBLE
            showHome()
        }
    }

    private fun setup(){
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
                            val relativesNull = ArrayList<String>()
                            val petitionsNull = ArrayList<String>()
                            db.collection("users").document(account.id.toString()).set(
                                hashMapOf(
                                    "uid" to account.id.toString(),
                                    "image" to R.drawable.no_photo_user,
                                    "name" to account.displayName,
                                    "email" to account.email,
                                    "role" to "No se sabe",   //REVISAR
                                    "provider" to "Google",
                                    "relatives" to relativesNull,
                                    "petitions" to petitionsNull
                                )
                            )
                            showHome()
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
}