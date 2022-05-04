package com.paula.seniorcare_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.paula.seniorcare_app.model.User
import kotlinx.android.synthetic.main.activity_relative_profile.*

class RelativeProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_relative_profile)

        val user = intent.getSerializableExtra("user") as User
        nameTextView.text = user.name.toString()
        emailTextView.text = user.email.toString()
        //rol
        Glide.with(this).load(user.image.toString()).into(profileImageView)
    }
}