package com.paula.seniorcare_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.model.Petition
import kotlinx.android.synthetic.main.fragment_add_relative.*
import kotlinx.android.synthetic.main.fragment_petitions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PetitionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view:View = inflater.inflate(R.layout.fragment_petitions, container, false)

        getPendingPetitions()

        return view
    }

    private fun getPendingPetitions(){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val petitionsList = ArrayList<Petition>()
        petitionsList.clear()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (uid != null) {
                    val petitions = getPendingPetitionsFromDB(db, uid)
                    petitions?.iterator()?.forEach { petition ->
                        val id : String = petition.data.getValue("id").toString()
                        val sender : String = petition.data.getValue("sender").toString()
                        val receiver : String = petition.data.getValue("receiver").toString()
                        val state : String = petition.data.getValue("state").toString()
                        val p = Petition(id, sender, receiver, state)
                        if (state == "pending") {
                            petitionsList.add(p)
                        }
                    }
                }
            }
        }
        showResults(petitionsList)
    }

    private suspend fun getPendingPetitionsFromDB(db: FirebaseFirestore, uid: String): QuerySnapshot? {
        return try {
            val data = db.collection("users").document(uid).collection("petitions").get().await()
            data
        } catch (e: Exception) {
            null
        }
    }

    private fun showResults(petitionsList: ArrayList<Petition>){
        val adapter: RelativesAdapter?
        if(petitionsList.isEmpty()){
            noPetitionsTextView.visibility = View.VISIBLE
            noPetitionsTextView.text = getString(R.string.no_petitions)
            //QUITAR CONTENIDO DEL ADAPTER ???
            adapter = PetitionsAdapter(petitionsList, requireContext())     //CREAR ADAPTER
            petitionsListView.adapter = adapter
        } else {
            noPetitionsTextView.visibility = View.INVISIBLE
            adapter = PetitionsAdapter(petitionsList, requireContext())
            petitionsListView.adapter = adapter
            petitionsListView.setOnItemClickListener{ _,_,position,_ ->
                /*val intent = Intent(context,RelativeProfileActivity::class.java)
                intent.putExtra("user", petitionsList[position])
                startActivity(intent)*/
            }
        }
    }
}