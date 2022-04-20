package com.paula.seniorcare_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fragmentCalls = FragmentCalls()
        val fragmentRelatives = FragmentRelatives()
        val fragmentAlerts = FragmentAlerts()

        makeCurrentFragment(fragmentCalls)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.fragmentCalls -> makeCurrentFragment(fragmentCalls)
                R.id.fragmentRelatives -> makeCurrentFragment(fragmentRelatives)
                R.id.fragmentAlerts -> makeCurrentFragment(fragmentAlerts)
            }
            true
        }

        setup()
    }

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.wrapper, fragment)
        commit()
    }

    private fun setup(){
        profileButton.setOnClickListener(){
            val profileIntent = Intent(this,ProfileActivity::class.java)
            startActivity(profileIntent)
        }
    }
}