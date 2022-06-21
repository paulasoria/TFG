package com.paula.seniorcare_app

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.paula.seniorcare_app.model.User

class CardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup) =
        Presenter.ViewHolder(ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
        })


    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val user = item as User
        val imageCardView = viewHolder.view as ImageCardView

        imageCardView.setMainImageDimensions(350,300)
        Glide.with(imageCardView).load(user.image).centerCrop().into(imageCardView.mainImageView)
        imageCardView.titleText = user.name
        imageCardView.contentText = user.email
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        TODO("Not yet implemented")
    }
}