package me.theclashfruit.rithle.classes

import androidx.recyclerview.widget.DiffUtil
import me.theclashfruit.rithle.models.ModrinthSearchHitsModel

class ListDiffCallback constructor(private val oldList: ArrayList<ModrinthSearchHitsModel>?, private val newList: ArrayList<ModrinthSearchHitsModel>?) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList!!.size
    }

    override fun getNewListSize(): Int {
        return newList!!.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList!![oldItemPosition].project_id == newList!![newItemPosition].project_id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList!![oldItemPosition].title.equals(newList!![newItemPosition].title)
    }
}