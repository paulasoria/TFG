package com.paula.seniorcare_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.model.User
import kotlinx.android.synthetic.main.fragment_add_relative.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddRelativeFragment : Fragment(), SearchView.OnQueryTextListener {
    //private var relativesList = ArrayList<User>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_add_relative, container, false)
        val relativesSearchView:SearchView = view.findViewById(R.id.relativesSearchView)
        relativesSearchView.setOnQueryTextListener(this)
        return view
    }

    /*private fun getAllUsers(){
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
    }*/

    private suspend fun getSearchUsers(db: FirebaseFirestore, query:String): QuerySnapshot? {
        return try {
            //val data = db.collection("users").whereGreaterThanOrEqualTo("email",query).whereEqualTo("email",query).get().await()
            val data = db.collection("users").orderBy("email").startAt(query).get().await()
            data
        } catch (e: Exception) {
            null
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        //Log.d(TAG,"Text Submit: "+p0)
        val db = FirebaseFirestore.getInstance()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val searchList = ArrayList<User>()
        searchList.clear()
        relativesSearchView.clearFocus()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (query != null) {
                    val documents = getSearchUsers(db, query)
                    documents?.iterator()?.forEach { document ->
                        val uid : String = document.data.getValue("uid").toString()
                        val name : String = document.data.getValue("name").toString()
                        val email : String = document.data.getValue("email").toString()
                        val role : String = document.data.getValue("role").toString()
                        val image : String = document.data.getValue("image").toString()
                        val relatives : ArrayList<String> = document.data.getValue("relatives") as ArrayList<String>
                        val user = User(uid, name, email, null, role, image, relatives)
                        if (email != currentUserEmail) {
                            searchList.add(user)
                        }
                    }
                }
            }
            showResults(searchList)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        return true
    }

    private fun showResults(searchList: ArrayList<User>){
        val adapter: RelativesAdapter?
        if(searchList.isEmpty()){
            noResultsTextView.visibility = View.VISIBLE
            noResultsTextView.text = getString(R.string.no_results)
            //QUITAR CONTENIDO DEL ADAPTER ???
            adapter = RelativesAdapter(searchList, requireContext())
            relativesGridView.adapter = adapter
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