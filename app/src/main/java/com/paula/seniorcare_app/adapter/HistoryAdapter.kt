package com.paula.seniorcare_app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.paula.seniorcare_app.R
import com.paula.seniorcare_app.dataclass.History

class HistoryAdapter(private var historyList: ArrayList<History>, var context: Context) : BaseAdapter() {
    override fun getCount(): Int {
        return historyList.size
    }

    override fun getItem(p0: Int): Any {
        return historyList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val rootView: View = View.inflate(context, R.layout.history_item, null)

        val name : TextView = rootView.findViewById(R.id.nameHistoryTextView)
        val time : TextView = rootView.findViewById(R.id.timeHistoryTextView)
        val info : TextView = rootView.findViewById(R.id.infoHistoryTextView)
        val date : TextView = rootView.findViewById(R.id.dateHistoryTextView)
        val history : History = getItem(p0) as History

        name.text = history.receiver
        time.text = history.time
        info.text = history.tag
        date.text = history.date

        return rootView
    }
}