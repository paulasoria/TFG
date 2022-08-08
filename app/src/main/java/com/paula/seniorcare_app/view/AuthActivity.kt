package com.paula.seniorcare_app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    override fun showAlertGoogle(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(getString(R.string.google_error))
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun showChooseRoleDialog(account: GoogleSignInAccount){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.choose_role_layout, null)
        val familiarRadioButton = dialogLayout.findViewById<RadioButton>(R.id.familiarRadioButton)
        val adminRadioButton = dialogLayout.findViewById<RadioButton>(R.id.adminRadioButton)

        builder.setTitle("Escoge tu rol de usuario")
        builder.setPositiveButton("Aceptar") { _, _ ->
            if(familiarRadioButton.isChecked) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        authPresenter.createUserFromGoogle(account, "Familiar")
                        val tvIntent = Intent(baseContext, TvActivity::class.java)
                        startActivity(tvIntent)
                    }
                }
            } else if(adminRadioButton.isChecked) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        authPresenter.createUserFromGoogle(account, "Administrador")
                        val homeIntent = Intent(baseContext, HomeActivity::class.java)
                        startActivity(homeIntent)
                    }
                }
            }

        }
        builder.setNegativeButton("Cancelar", null)
        builder.setView(dialogLayout)
        builder.show()
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
                            showChooseRoleDialog(account)
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