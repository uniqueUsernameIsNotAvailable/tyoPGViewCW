package com.tyomased.pgportable.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView as RV
import com.tyomased.pgportable.R
import kotlinx.android.synthetic.main.dbview_item.view.*

class DBListViewHolder(itemView: View) : RV.ViewHolder(itemView) {
    fun bind(item: String, onDBItemClick: (dbName: String) -> Unit) {
        itemView.dbnameText.text = item
        itemView.setOnClickListener { onDBItemClick(item) }
    }

    companion object {
        fun from(parent: ViewGroup, viewType: Int): DBListViewHolder {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.dbview_item, parent, false)
            return DBListViewHolder(view)
        }
    }
}

class DBItemDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem
}

class DBListAdapter(val onDBItemClick: (dbName: String) -> Unit) :
    ListAdapter<String, DBListViewHolder>(DBItemDiffCallback()) {

    override fun onBindViewHolder(holder: DBListViewHolder, position: Int) =
        holder.bind(getItem(position), onDBItemClick)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DBListViewHolder.from(parent, viewType)
}
