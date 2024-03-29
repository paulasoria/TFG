package com.paula.seniorcare_app.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.paula.seniorcare_app.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        makeCurrentFragment(HistoryFragment())

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.historyFragment -> makeCurrentFragment(HistoryFragment())
                R.id.relativesFragment -> makeCurrentFragment(RelativesFragment())
                R.id.alertsFragment -> makeCurrentFragment(AlertsFragment())
            }
            true
        }

        profileButton.setOnClickListener {
            val profileIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileIntent)
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.wrapper, fragment)
        commit()
    }
}