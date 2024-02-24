package me.theclashfruit.rithle.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class ImageUtil(val context: Context) {
    val glide = Glide.get(context)

    val requestManager = glide.requestManagerRetriever.get(context)

    fun loadImage(avatarUrl: String?, renderProfileImage: (Drawable) -> Unit) {
        try {
            requestManager
                .load(avatarUrl)
                .centerCrop()
                .circleCrop()
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("Glide", "loadImage: failed")
                        return true
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("Glide", "loadImage: ready")
                        resource?.let { renderProfileImage(it) }
                        return true
                    }

                }).submit()
        } catch (e: Exception) {
            Log.e("GlideR", "loadImage: ${e.message}")
        }
    }
}