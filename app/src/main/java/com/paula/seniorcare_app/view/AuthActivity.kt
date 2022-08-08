package com.paula.seniorcare_app.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.contract.AuthContract
import com.paula.seniorcare_app.interactor.AuthInteractor
import com.paula.seniorcare_app.presenter.AuthPresenter
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity(), AuthContract.View {

    private val GOOGLE_SIGN_IN = 1
    lateinit var authPresenter: AuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        authPresenter = AuthPresenter(this, AuthInteractor())

        if(FirebaseAuth.getInstance().currentUser != null){
            authLayout.visibility = View.INVISIBLE
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val user = authPresenter.getUser(uid)
                    if(user?.get("role") == "Administrador") {
                        val homeIntent = Intent(baseContext, HomeActivity::class.java)
                        startActivity(homeIntent)
                    } else {
                        val tvIntent = Intent(baseContext, TvActivity::class.java)
                        startActivity(tvIntent)
                    }
                }
            }
        }

        if(getRotation(applicationContext) == "horizontal"){
            signUpButton.isEnabled = false
            googleButton.isEnabled = false
        }

        logInButton.setOnClickListener {
            val logInIntent = Intent(this, LogInActivity::class.java)
            startActivity(logInIntent)
        }

        signUpButton.setOnClickListener {
            val signUpIntent = Intent(this, SignUpActivity::class.java)
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

    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    override fun getRotation(context: Context): String {
        return when ((context.getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.orientation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> "vertical"
            Surface.ROTATION_90 -> "horizontal"
            else -> "horizontal"
        }
    }

    override fun showAlertGoogle(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(getString(R.string.google_error))
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                                    authPresenter.createUserFromGoogle(account)
                                }
                                val homeIntent = Intent(baseContext, HomeActivity::class.java)
                                startActivity(homeIntent)
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
}