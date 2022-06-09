package com.paula.seniorcare_app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.model.User
import kotlinx.android.synthetic.main.fragment_add_relative.*
import kotlinx.android.synthetic.main.fragment_add_relative.noResultsTextView
import kotlinx.android.synthetic.main.fragment_relatives.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

        val db = FirebaseFirestore.getInstance()
        val addedRelativesList = ArrayList<User>()
        addedRelativesList.clear()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val documents = getAddedRelativesList(db)
                documents?.iterator()?.forEach { document ->
                    val uid: String = document.data.getValue("uid").toString()
                    val name: String = document.data.getValue("name").toString()
                    val email: String = document.data.getValue("email").toString()
                    val image: String = document.data.getValue("image").toString()
                    val user = User(uid, name, email, null, null, image)
                    addedRelativesList.add(user)
                }
            }
            showResults(addedRelativesList)
        }


        return view
    }

    private suspend fun getAddedRelativesList(db: FirebaseFirestore) : QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            val data = db.collection("users").document(currentUid.toString()).collection("relatives").get().await()
            data
        } catch (e: Exception) {
            null
        }
    }

    private fun showResults(addedRelativesList: ArrayList<User>){
        val adapter: RelativesAdapter?
        if(addedRelativesList.isEmpty()){
            noResultsTextView.visibility = View.VISIBLE
            noResultsTextView.text = getString(R.string.no_results)
            //QUITAR CONTENIDO DEL ADAPTER ???
            adapter = RelativesAdapter(addedRelativesList, requireContext())
            addedRelativesGridView.adapter = adapter
        } else {
            noResultsTextView.visibility = View.INVISIBLE
            adapter = RelativesAdapter(addedRelativesList, requireContext())
            addedRelativesGridView.adapter = adapter
            addedRelativesGridView.setOnItemClickListener{ _,_,position,_ ->
                val intent = Intent(context,RelativeProfileActivity::class.java)
                intent.putExtra("user", addedRelativesList[position])
                startActivity(intent)
            }
        }
    }
}