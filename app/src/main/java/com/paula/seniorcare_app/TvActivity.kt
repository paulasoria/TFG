package com.paula.seniorcare_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_tv.*

class TvActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv)

        //USAR RELATIVESFRAGMENT EN HORIZONTAL O HACER NUEVO FRAGMENT???
        //val relativesTvFragment = RelativesTvFragment()
        val relativesTvFragment = RelativesFragment()
        //val profileTvFragment = profileTvFragment()


        makeCurrentFragment(relativesTvFragment)
        left_navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.relativesTvFragment -> makeCurrentFragment(relativesTvFragment)
                //R.id.profileTvFragment -> makeCurrentFragment(profileTvFragment)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
        replace(R.id.wrapper_tv, fragment)
        commit()
    }
}