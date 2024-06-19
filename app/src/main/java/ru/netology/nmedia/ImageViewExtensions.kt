package ru.netology.nmedia

import android.widget.ImageView
import com.bumptech.glide.Glide
import java.net.URL


fun ImageView.load(url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_loading_100dp)
        .error(R.drawable.ic_error_100dp)
        .timeout(10_000)
        .circleCrop()
        .into(this)
}