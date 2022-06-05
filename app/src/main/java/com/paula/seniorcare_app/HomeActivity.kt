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

        val historyFragment = HistoryFragment()
        val relativesFragment = RelativesFragment()
        val alertsFragment = AlertsFragment()

        makeCurrentFragment(relativesFragment)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.historyFragment -> makeCurrentFragment(historyFragment)
                R.id.relativesFragment -> makeCurrentFragment(relativesFragment)
                R.id.alertsFragment -> makeCurrentFragment(alertsFragment)
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
        profileButton.setOnClickListener {
            val profileIntent = Intent(this,ProfileActivity::class.java)
            startActivity(profileIntent)
        }
    }
}