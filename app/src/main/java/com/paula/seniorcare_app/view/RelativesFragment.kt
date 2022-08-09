package com.paula.seniorcare_app.view

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
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.adapter.RelativesAdapter
import com.paula.seniorcare_app.contract.RelativesContract
import com.paula.seniorcare_app.interactor.RelativesInteractor
import com.paula.seniorcare_app.dataclass.User
import com.paula.seniorcare_app.presenter.RelativesPresenter
import kotlinx.android.synthetic.main.fragment_add_relative.noResultsTextView
import kotlinx.android.synthetic.main.fragment_relatives.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RelativesFragment : Fragment(), RelativesContract.View {

    lateinit var relativesPresenter: RelativesPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        relativesPresenter = RelativesPresenter(this, RelativesInteractor())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_relatives, container, false)
        val addRelativeButton:Button = view.findViewById(R.id.addRelativeButton)
        val petitionsButton:FloatingActionButton = view.findViewById(R.id.petitionsButton)

        addRelativeButton.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val db = FirebaseFirestore.getInstance()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val user = relativesPresenter.getUser(db, uid)
                    if(user?.get("role") == "Administrador"){
                        activity?.supportFragmentManager?.beginTransaction()?.replace(
                            R.id.wrapper,
                            AddRelativeFragment()
                        )?.commit()
                    } else {    //Familiar
                        activity?.supportFragmentManager?.beginTransaction()?.replace(
                            R.id.wrapper_tv,
                            AddRelativeFragment()
                        )?.commit()
                    }
                }
            }
        }

        petitionsButton.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val db = FirebaseFirestore.getInstance()
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val user = relativesPresenter.getUser(db, uid)
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

        val db = FirebaseFirestore.getInstance()
        val addedRelativesList = ArrayList<User>()
        addedRelativesList.clear()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val documents = relativesPresenter.getAddedRelativesList(db)
                documents?.iterator()?.forEach { document ->
                    val uid: String = document.data.getValue("uid").toString()
                    val name: String = document.data.getValue("name").toString()
                    val email: String = document.data.getValue("email").toString()
                    val image: String = document.data.getValue("image").toString()
                    val role: String = document.data.getValue("role").toString()
                    val user = User(uid, null, image, name, email, role)
                    addedRelativesList.add(user)
                }
            }
            showResults(addedRelativesList)
        }
        return view
    }

    override fun showResults(addedRelativesList: ArrayList<User>) {
        val adapter: RelativesAdapter?
        if(addedRelativesList.isEmpty()){
            noResultsTextView.visibility = View.VISIBLE
            noResultsTextView.text = getString(R.string.no_relatives)
            adapter = RelativesAdapter(addedRelativesList, requireContext())
            addedRelativesGridView.adapter = adapter
        } else {
            noResultsTextView.visibility = View.INVISIBLE
            adapter = RelativesAdapter(addedRelativesList, requireContext())
            addedRelativesGridView.adapter = adapter
            /*addedRelativesGridView.setOnKeyListener { _,_,_ ->
                addedRelativesGridView.setSelection(addedRelativesGridView.selectedItemPosition + 1)
                true
            }*/
            addedRelativesGridView.setOnItemClickListener{ _,_,position,_ ->
                val intent = Intent(context, RelativeProfileActivity::class.java)
                intent.putExtra("user", addedRelativesList[position])
                startActivity(intent)
            }
        }
    }
}