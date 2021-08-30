package com.bartex.quizday.ui.adapters

import android.app.Activity
import android.net.Uri
import android.widget.ImageView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou

class SvgImageLoader(private val activity: Activity):IImageLoader<ImageView> {

    override fun loadInto(url: String, container: ImageView) {
        GlideToVectorYou
                .init()
                .with(activity)
                .load(Uri.parse(url), container)
    }

}