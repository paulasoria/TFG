package com.paula.seniorcare_app

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.paula.seniorcare_app.model.Petition

class PetitionsAdapter(private var petitionsList: ArrayList<Petition>, var context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return petitionsList.size
    }

    override fun getItem(p0: Int): Any {
        return petitionsList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rootView: View = View.inflate(context, R.layout.petition_item, null)

        /*val image : ImageView = rootView.findViewById(R.id.relativeItemImageView)
        val name : TextView = rootView.findViewById(R.id.relativeItemName)
        val email : TextView = rootView.findViewById(R.id.relativeItemEmail)
        val petition : Petition = getItem(p0) as Petition

        Glide.with(context).load(petition.image).into(image)
        name.text = petition.name
        email.text = petition.email*/

        return rootView
    }

}