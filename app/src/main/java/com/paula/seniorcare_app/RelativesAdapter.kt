package com.paula.seniorcare_app

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.paula.seniorcare_app.model.User
import kotlinx.android.synthetic.main.relative_item.view.*
import org.w3c.dom.Text

class RelativesAdapter(var relativesList: ArrayList<User>, var context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return relativesList.size
    }

    override fun getItem(p0: Int): Any {
        return relativesList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var rootView: View = View.inflate(context, R.layout.relative_item, null)

        var image : ImageView = rootView.findViewById(R.id.relativeItemImageView)
        var name : TextView = rootView.findViewById(R.id.relativeItemName)
        var email : TextView = rootView.findViewById(R.id.relativeItemEmail)
        var relative : User = getItem(p0) as User

        Glide.with(context).load(relative.image).into(image)
        name.text = relative.name
        email.text = relative.email

        return rootView
    }

}