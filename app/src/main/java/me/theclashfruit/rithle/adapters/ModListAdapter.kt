package me.theclashfruit.rithle.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.models.ModrinthSearchHitsModel

class ModListAdapter(private val modList: ArrayList<ModrinthSearchHitsModel>) : RecyclerView.Adapter<ModListAdapter.StreamHolder?>() {

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

    override fun onBindViewHolder(holder: StreamHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.textView).text  = modList[position].title
        holder.itemView.findViewById<TextView>(R.id.textView2).text = modList[position].description
    }

    override fun getItemCount() = modList.size
}