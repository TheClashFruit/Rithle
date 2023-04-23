package me.theclashfruit.rithle.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.ColorSpace
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.classes.RithleSingleton
import me.theclashfruit.rithle.fragments.ProjectViewFragment
import me.theclashfruit.rithle.models.ModrinthSearchHitsModel

class ModListAdapter(
    private val modList: ArrayList<ModrinthSearchHitsModel>,
    private val appContext: Context,
    private val fragmentManager: FragmentManager,
) : RecyclerView.Adapter<ModListAdapter.StreamHolder?>() {
    class StreamHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(itemView: View) {
            Log.d("RecyclerView", "CLICK!")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StreamHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val view: View = layoutInflater.inflate(R.layout.list_item, parent, false)

        return StreamHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StreamHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.textView).text  = modList[position].title
        holder.itemView.findViewById<TextView>(R.id.textView2).text = "by ${modList[position].author}"
        holder.itemView.findViewById<TextView>(R.id.textView3).text = modList[position].description

        if(modList[position].color != null) {
            val finalColor: Int =
                if(Integer.toHexString(modList[position].color!!).length < 6)
                    Color.parseColor("#" + Integer.toHexString(modList[position].color!!) + "0")
                else
                    Color.parseColor("#" + Integer.toHexString(modList[position].color!!))

            holder.itemView.findViewById<LinearLayout>(R.id.featuredGalleryBg)
                .setBackgroundColor(finalColor)

            holder.itemView.findViewById<ImageView>(R.id.featuredGalleryIcon)
                .setBackgroundColor(finalColor)
        }

        if(modList[position].featured_gallery != null) {
            RithleSingleton.getInstance(appContext).imageLoader.get(
                modList[position].featured_gallery.toString(),
                object : ImageLoader.ImageListener {
                    override fun onResponse(
                        response: ImageLoader.ImageContainer?,
                        isImmediate: Boolean
                    ) {
                        if (response != null) {
                            holder.itemView.findViewById<ImageView>(R.id.featuredGalleryIcon)
                                .setImageBitmap(response.bitmap)
                        }
                    }

                    override fun onErrorResponse(error: VolleyError?) {
                        Log.e("imageLoader", error!!.stackTraceToString())
                    }
                })
        }

        if(modList[position].icon_url!!.isNotEmpty()) {
            RithleSingleton.getInstance(appContext).imageLoader.get(
                modList[position].icon_url.toString(),
                object : ImageLoader.ImageListener {
                    override fun onResponse(
                        response: ImageLoader.ImageContainer?,
                        isImmediate: Boolean
                    ) {
                        if (response != null) {
                            holder.itemView.findViewById<ImageView>(R.id.imageView)
                                .setImageBitmap(response.bitmap)

                            holder.itemView.findViewById<ImageView>(R.id.imageView)
                                .scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                    }

                    override fun onErrorResponse(error: VolleyError?) {
                        Log.e("imageLoader", error!!.stackTraceToString())
                    }
                })
        }

        holder.itemView.rootView.setOnClickListener {
            val mainFragmentTransaction = fragmentManager.beginTransaction()
            val projectViewFragment = ProjectViewFragment.newInstance(modList[position].project_id.toString())

            mainFragmentTransaction
                .addToBackStack("projectViewFragment")
                .add(R.id.parentFragmentContainer, projectViewFragment)
                .commit()


        }
    }

    override fun getItemCount() = modList.size
}