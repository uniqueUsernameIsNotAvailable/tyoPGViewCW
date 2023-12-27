package com.tyomased.pgportable.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tyomased.pgportable.R
import com.tyomased.pgportable.viewmodels.DBTablesViewModel
import kotlinx.android.synthetic.main.dbview_item.view.*

class TableItemViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView)

class TableItemDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
        oldItem == newItem
}

class DBTablesAdapter(
    val viewModel: DBTablesViewModel,
    val onTableItemClick: (tableName: String) -> Unit
) : ListAdapter<String, TableItemViewHolder>(TableItemDiffCallback()) {

    override fun onBindViewHolder(holder: TableItemViewHolder, position: Int) {
        holder.itemView.run {
            setOnClickListener {
                onTableItemClick(getItem(position))
            }
            dbnameText.text = getItem(position)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TableItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dbview_item, parent, false)
        return TableItemViewHolder(view)
    }

}
