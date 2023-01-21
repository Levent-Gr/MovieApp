package com.leventgorgu.inviousgchallenge.utils

import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.leventgorgu.inviousgchallenge.R

fun ImageView.loadImageWithGlide(imageUrl:String, circularProgressDrawable: CircularProgressDrawable){
    val options = RequestOptions()
        .placeholder(circularProgressDrawable)
        .error(R.mipmap.ic_launcher_round)

    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(imageUrl)
        .into(this)
}

fun placeHolderProgressBar(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        start()
    }
}

@BindingAdapter("android:loadImageUrl")
fun loadImage(imageView: ImageView, moviePosterUrl:String?) {
    moviePosterUrl?.let {
        imageView.loadImageWithGlide(moviePosterUrl,placeHolderProgressBar(imageView.context))
    }
}