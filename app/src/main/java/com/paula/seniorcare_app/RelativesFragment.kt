package com.paula.seniorcare_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RelativesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_relatives, container, false)
        val addRelativeButton:Button = view.findViewById(R.id.addRelativeButton)
        val petitionsButton:FloatingActionButton = view.findViewById(R.id.petitionsButton)

        addRelativeButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.wrapper,AddRelativeFragment())?.commit()
        }

        petitionsButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.wrapper,PetitionsFragment())?.commit()
        }

        return view
    }
}