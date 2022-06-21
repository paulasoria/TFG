package com.paula.seniorcare_app

import android.os.Bundle
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*

class DetailsFragment : DetailsSupportFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*val image = activity?.intent?.getStringExtra("image").toString()

        val selector = ClassPresenterSelector()
        selector.addClassPresenter(DetailsOverviewRow::class.java, FullWidthDetailsOverviewRowPresenter(DetailsPresenter()))

        val madapter = ArrayObjectAdapter(selector)
        val detailsOverview = DetailsOverviewRow("Media Item Details")
        // Add images and action buttons to the details view

        //Add some Actions
        /*val actionAdap = SparseArrayObjectAdapter()
        actionAdap[ACTION_PLAY.toInt()] = Action(1, "Buy $9.99")
        actionAdap[ACTION_REWIND.toInt()] = Action(2, "Rent $2.99")
        detailsOverview.actionsAdapter = actionAdap
        madapter.add(detailsOverview)
        adapter = madapter*/*/
    }
}