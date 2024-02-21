package me.theclashfruit.rithle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDivider
import me.theclashfruit.rithle.R
import me.theclashfruit.rithle.models.ItemWithIcon

class ModalAdapter(private val dataSet: Array<ItemWithIcon>) : RecyclerView.Adapter<ModalAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView
        val textView: TextView

        init {
            imageView = view.findViewById(R.id.imageView)
            textView = view.findViewById(R.id.textView)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.modal_icon_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.imageView.setImageResource(dataSet[position].icon)
        viewHolder.textView.text = dataSet[position].title
    }

    override fun getItemCount() = dataSet.size
}