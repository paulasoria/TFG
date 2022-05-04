package com.paula.seniorcare_app

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.model.User
import kotlinx.android.synthetic.main.fragment_add_relative.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AddRelativeFragment : Fragment(), SearchView.OnQueryTextListener {
    private val db = FirebaseFirestore.getInstance()
    private var adapter: RelativesAdapter? = null
    private var relativesList = ArrayList<User>()
    private var searchList = ArrayList<User>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_add_relative, container, false)
        val relativesSearchView:SearchView = view.findViewById(R.id.relativesSearchView)
        relativesSearchView.setOnQueryTextListener(this)
        return view
    }

    private fun getAllUsers(){
        relativesList.clear()
        db.collection("users").get().addOnSuccessListener { documents ->
            //relativesList.addAll(documents.toObjects(User::class.java))
            for (document in documents) {
                val name : String = document.data.getValue("name").toString()
                val email : String = document.data.getValue("email").toString()
                val role : String = document.data.getValue("role").toString()
                val image : String = document.data.getValue("image").toString()
                val user = User(name, email, null, role, image, null)
                relativesList.add(user)
            }
        }
    }

    suspend fun getSearchUsersCoroutine(p0:String): QuerySnapshot? {
        return try {
            val data = db.collection("users").whereGreaterThanOrEqualTo("email",p0).whereEqualTo("email",p0).get().await()
            data
        } catch (e: Exception) {
            null
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        //Log.d(TAG,"Text Submit: "+p0)
        relativesSearchView.clearFocus()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (p0 != null) {
                    val documents = getSearchUsersCoroutine(p0)
                    documents?.iterator()?.forEach { document ->
                        val name : String = document.data.getValue("name").toString()
                        val email : String = document.data.getValue("email").toString()
                        val role : String = document.data.getValue("role").toString()
                        val image : String = document.data.getValue("image").toString()
                        val user = User(name, email, null, role, image, null)
                        searchList.add(user)
                    }
                }
            }
            showResults()
        }
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
                val name : String = document.data.getValue("name").toString()
                val email : String = document.data.getValue("email").toString()
                val role : String = document.data.getValue("role").toString()
                val image : String = document.data.getValue("image").toString()
                val user = User(name, email, null, role, image, null)
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
            relativesGridView.setOnItemClickListener{ _,_,position,_ ->
                val intent = Intent(context,RelativeProfileActivity::class.java)
                intent.putExtra("user", searchList[position])
                startActivity(intent)
            }
        }
    }
}