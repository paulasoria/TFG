package com.paula.seniorcare_app

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.paula.seniorcare_app.model.User

class RelativesAdapter(private var relativesList: ArrayList<User>, var context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return relativesList.size
    }

    override fun getItem(p0: Int): Any {
        return relativesList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rootView: View = View.inflate(context, R.layout.relative_item, null)

        val image : ImageView = rootView.findViewById(R.id.relativeItemImageView)
        val name : TextView = rootView.findViewById(R.id.relativeItemName)
        val email : TextView = rootView.findViewById(R.id.relativeItemEmail)
        val relative : User = getItem(p0) as User

        Glide.with(context).load(relative.image).centerCrop().into(image)
        name.text = relative.name
        email.text = relative.email

        return rootView
    }
}