package com.paula.seniorcare_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*


enum class ProviderType {
    BASIC,
    GOOGLE
}

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setup()
    }

    private fun setup(){
        title = "Perfil"

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val name = prefs.getString("name", null)
        val email = prefs.getString("email", null)
        val rol = prefs.getString("rol", null)
        nameTextView.text = name
        emailTextView.text = email
        rolTextView.text = rol

        for (user in FirebaseAuth.getInstance().currentUser!!.providerData) {
            if (user.providerId == "google.com") {
                providerTextView.text = "Google"
            } else {
                providerTextView.text = "Email"
            }
        }

        logOutButton.setOnClickListener{
            //Borrado de datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()

            val authIntent = Intent(this,AuthActivity::class.java)
            startActivity(authIntent)
        }
    }
}