package me.theclashfruit.rithle.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.classes.RithleSingleton
import me.theclashfruit.rithle.models.ModrinthSearchHitsModel

class ModListAdapter(private val modList: ArrayList<ModrinthSearchHitsModel>, private val appContext: Context) : RecyclerView.Adapter<ModListAdapter.StreamHolder?>() {

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

        val view: View = layoutInflater.inflate(R.layout.mod_item, parent, false)

        return StreamHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StreamHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.textView).text  = modList[position].title
        holder.itemView.findViewById<TextView>(R.id.textView2).text = "by ${modList[position].author}"
        holder.itemView.findViewById<TextView>(R.id.textView3).text = modList[position].description

        Log.d("iconUrl", modList[position].icon_url.toString())

        RithleSingleton.getInstance(appContext).imageLoader.get(modList[position].icon_url.toString(), object : ImageLoader.ImageListener {
            override fun onResponse(response: ImageLoader.ImageContainer?, isImmediate: Boolean) {
                if (response != null) {
                    holder.itemView.findViewById<ImageView>(R.id.imageView).setImageBitmap(response.bitmap)
                }
            }

            override fun onErrorResponse(error: VolleyError?) {
                Log.d("imageLoader", "wtf are you doing, you either don't have internet or the url is fucking wrong, btw the error is: ${error.toString()}")
            }
        })
    }

    override fun getItemCount() = modList.size
}