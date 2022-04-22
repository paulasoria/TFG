package com.paula.seniorcare_app

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.model.User
import kotlinx.android.synthetic.main.fragment_add_relative.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddRelativeFragment : Fragment(), SearchView.OnQueryTextListener {
    private val db = FirebaseFirestore.getInstance()
    var adapter: RelativesAdapter? = null
    private var relativesList = ArrayList<User>()
    var searchList = ArrayList<User>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view:View = inflater.inflate(R.layout.fragment_add_relative, container, false)
        var relativesSearchView:SearchView = view.findViewById(R.id.relativesSearchView)
        var noResultsTextView:TextView = view.findViewById(R.id.noResultsTextView)
        var relativesGridView:GridView = view.findViewById(R.id.relativesGridView)

        relativesSearchView.setOnQueryTextListener(this)

        return view
    }

    private fun getAllUsers(){
        relativesList.clear()
        db.collection("users").get().addOnSuccessListener { documents ->
            //relativesList.addAll(documents.toObjects(User::class.java))
            for (document in documents) {
                var name : String = document.data.getValue("name").toString()
                var email : String = document.data.getValue("email").toString()
                var image : String = document.data.getValue("image").toString()
                var user = User(name, email, null, null, image, null)
                relativesList.add(user)
            }
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        Log.d(TAG,"Text Submit: "+p0)
        relativesSearchView.clearFocus()
        GlobalScope.launch(Dispatchers.Main) {  //SE ADELANTABA, CORRUTINAS, AHORA FUNCIONA A LA SEGUNDA ¿?
            withContext(Dispatchers.IO) {
                if (p0 != null) {
                    getSearchUsers(p0)
                }
            }
        }
        showResults()
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

    private fun getSearchUsers(p0:String){
        searchList.clear()
        db.collection("users").whereGreaterThanOrEqualTo("email",p0).whereEqualTo("email",p0).get().addOnSuccessListener { documents ->
            //searchList.addAll(documents.toObjects(User::class.java))
            for(document in documents){
                var name : String = document.data.getValue("name").toString()
                var email : String = document.data.getValue("email").toString()
                var image : String = document.data.getValue("image").toString()
                var user = User(name, email, null, null, image, null)
                searchList.add(user)
            }
        }
    }

    private fun showResults(){
        if(searchList.isEmpty()){
            noResultsTextView.visibility = View.VISIBLE
            noResultsTextView.text = getString(R.string.no_results)
        } else {
            noResultsTextView.visibility = View.INVISIBLE
            adapter = RelativesAdapter(searchList, requireContext())
            relativesGridView.adapter = adapter
            /*relativesGridView.setOnItemClickListener(){
                //Ver perfil de usuario
            }*/
        }
    }
}