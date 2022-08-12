package com.paula.seniorcare_app.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.adapter.PetitionsAdapter
import com.paula.seniorcare_app.contract.PetitionsContract
import com.paula.seniorcare_app.dataclass.Petition
import com.paula.seniorcare_app.presenter.PetitionsPresenter
import kotlinx.android.synthetic.main.fragment_petitions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PetitionsFragment : Fragment(), PetitionsContract.View {
    private val petitionsPresenter = PetitionsPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view:View = inflater.inflate(R.layout.fragment_petitions, container, false)
        getPendingPetitions()
        return view
    }

    override fun getPendingPetitions(){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val petitionsList = ArrayList<Petition>()
        petitionsList.clear()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (uid != null) {
                    val petitions = petitionsPresenter.getPendingPetitions(db, uid)
                    petitions?.iterator()?.forEach { petition ->
                        val state : String = petition.data.getValue("state").toString()
                        val sender : String = petition.data.getValue("sender").toString()
                        if (state == "pending" && sender != uid) {
                            val id : String = petition.data.getValue("id").toString()
                            val receiver : String = petition.data.getValue("receiver").toString()
                            val senderUser = petitionsPresenter.getSenderOfPetition(db, sender)
                            val senderName : String = senderUser?.data?.getValue("name").toString()
                            val senderEmail : String = senderUser?.data?.getValue("email").toString()
                            val senderImage : String = senderUser?.data?.getValue("image").toString()
                            val senderRole : String = senderUser?.data?.getValue("role").toString()
                            val p = Petition(id, sender, senderName, senderEmail, senderImage, senderRole, receiver, state)
                            petitionsList.add(p)
                        }
                    }
                }
            }
            showResults(petitionsList)
        }
    }

    override fun showResults(petitionsList: ArrayList<Petition>) {
        val adapter: PetitionsAdapter?
        if(petitionsList.isEmpty()){
            noPetitionsTextView.visibility = View.VISIBLE
            noPetitionsTextView.text = getString(R.string.no_petitions)
            adapter = PetitionsAdapter(petitionsList, requireContext())
            petitionsListView.adapter = adapter
        } else {
            noPetitionsTextView.visibility = View.INVISIBLE
            adapter = PetitionsAdapter(petitionsList, requireContext())
            petitionsListView.adapter = adapter
        }
    }
}