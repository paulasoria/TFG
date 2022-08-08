package com.paula.seniorcare_app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.paula.seniorcare_app.R
import kotlinx.android.synthetic.main.activity_tv.*

class TvActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv)

        makeCurrentFragment(RelativesFragment())
        left_navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.relativesTvFragment -> makeCurrentFragment(RelativesFragment())
                R.id.profileTvFragment -> makeCurrentFragment(ProfileTvFragment())
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.wrapper_tv, fragment)
        commit()
    }
}