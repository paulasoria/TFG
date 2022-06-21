package com.paula.seniorcare_app

import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.paula.seniorcare_app.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class TvFragment : BrowseSupportFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        badgeDrawable = resources.getDrawable(R.drawable.texto_logo_transparent)
        title = "SeniorCare"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        brandColor = resources.getColor(R.color.dark_blue)
        searchAffordanceColor = resources.getColor(R.color.grayish_blue)

        loadRows()

        setOnItemViewClickedListener { _, item, _, _ ->
            val user = item as User
            //val intent = Intent(activity, DetailsFragment::class.java)
            //intent.putExtra("image", user.image)
            //startActivity(intent)
        }
    }

    private fun loadRows(){
        val db = FirebaseFirestore.getInstance()
        val relatives = HeaderItem(0, "Familiares")
        val cosas = HeaderItem(1, "Cosas")

        val adapter = ArrayObjectAdapter(CardPresenter())
        //Obtener familiares y añadirlos al adapter
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val documents = getRelatives(db)
                documents?.iterator()?.forEach { document ->
                    val uid: String = document.data.getValue("uid").toString()
                    val name: String = document.data.getValue("name").toString()
                    val email: String = document.data.getValue("email").toString()
                    val image: String = document.data.getValue("image").toString()
                    val role: String = document.data.getValue("role").toString()
                    val user = User(uid, null, image, name, email, role)
                    adapter.add(user)
                }
            }
            val windowAdapter = ArrayObjectAdapter(ListRowPresenter())
            windowAdapter.add(ListRow(relatives, adapter))
            setAdapter(windowAdapter)
        }
    }

    private suspend fun getRelatives(db: FirebaseFirestore): QuerySnapshot? {
        return try {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid
            val data = db.collection("users").document(currentUid.toString()).collection("relatives").get().await()
            data
        } catch (e: Exception) {
            null
        }
    }
}