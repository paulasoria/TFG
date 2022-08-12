package com.paula.seniorcare_app.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.adapter.RelativesAdapter
import com.paula.seniorcare_app.contract.AddRelativeContract
import com.paula.seniorcare_app.interactor.AddRelativeInteractor
import com.paula.seniorcare_app.dataclass.User
import com.paula.seniorcare_app.presenter.AddRelativePresenter
import kotlinx.android.synthetic.main.fragment_add_relative.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddRelativeFragment : Fragment(), AddRelativeContract.View, SearchView.OnQueryTextListener {

    lateinit var addRelativePresenter: AddRelativePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addRelativePresenter = AddRelativePresenter(this, AddRelativeInteractor())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_add_relative, container, false)
        val relativesSearchView:SearchView = view.findViewById(R.id.relativesSearchView)
        val petitionsButton:FloatingActionButton = view.findViewById(R.id.petitionsButton)

        relativesSearchView.setOnQueryTextListener(this)

        petitionsButton.setOnClickListener {
            showPetitions()
        }

        return view
    }

    override fun showPetitions(){
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val user = addRelativePresenter.getUser(db, uid)
                if(user?.get("role") == "Administrador") {
                    activity?.supportFragmentManager?.beginTransaction()?.replace(
                        R.id.wrapper,
                        PetitionsFragment()
                    )?.commit()
                } else {    //Familiar
                    activity?.supportFragmentManager?.beginTransaction()?.replace(
                        R.id.wrapper_tv,
                        PetitionsFragment()
                    )?.commit()
                }
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        val db = FirebaseFirestore.getInstance()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        val searchList = ArrayList<User>()
        searchList.clear()
        relativesSearchView.clearFocus()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (query != null) {
                    val documents = addRelativePresenter.getSearchUsers(db, query)
                    documents?.iterator()?.forEach { document ->
                        val uid : String = document.data.getValue("uid").toString()
                        val token : String = document.data.getValue("token").toString()
                        val name : String = document.data.getValue("name").toString()
                        val email : String = document.data.getValue("email").toString()
                        val role : String = document.data.getValue("role").toString()
                        val image : String = document.data.getValue("image").toString()
                        val user = User(uid, token, image ,name, email, role)
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

    override fun showResults(searchList: ArrayList<User>) {
        val adapter: RelativesAdapter?
        if(searchList.isEmpty()){
            noResultsTextView.visibility = View.VISIBLE
            noResultsTextView.text = getString(R.string.no_results)
            adapter = RelativesAdapter(searchList, requireContext())
            relativesGridView.adapter = adapter
        } else {
            noResultsTextView.visibility = View.INVISIBLE
            adapter = RelativesAdapter(searchList, requireContext())
            relativesGridView.adapter = adapter
            relativesGridView.setOnItemClickListener{ _,_,position,_ ->
                val intent = Intent(context, RelativeProfileActivity::class.java)
                intent.putExtra("user", searchList[position])
                startActivity(intent)
            }
        }
    }
}