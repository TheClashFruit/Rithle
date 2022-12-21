package me.theclashfruit.rithle.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.models.ModrinthSearchHitsModel

class DownloadsAdapter(private val downloadsList: ArrayList<ModrinthSearchHitsModel>, private val appContext: Context, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<DownloadsAdapter.StreamHolder?>() {

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

        val view: View = layoutInflater.inflate(R.layout.download_item, parent, false)

        return StreamHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StreamHolder, position: Int) {

    }

    override fun getItemCount() = downloadsList.size
}