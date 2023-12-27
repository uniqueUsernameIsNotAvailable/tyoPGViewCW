package com.tyomased.pgportable.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tyomased.pgportable.ItemSwipeHelper
import androidx.recyclerview.widget.RecyclerView as RV
import com.tyomased.pgportable.R
import kotlinx.android.synthetic.main.dialog_create_table.view.*
import kotlinx.android.synthetic.main.dialog_create_table.view.columnsRecyclerView
import kotlinx.android.synthetic.main.item_create_table.view.*

data class TableColumn(val type: String, val value: String)

class CreateTableRVViewHolder(itemView: View) : RV.ViewHolder(itemView)

class CreateTableDiffCallback : DiffUtil.ItemCallback<TableColumn>() {
    override fun areItemsTheSame(
        oldItem: TableColumn, newItem: TableColumn
    ) = oldItem === newItem

    override fun areContentsTheSame(
        oldItem: TableColumn, newItem: TableColumn
    ) = oldItem == newItem
}

typealias LA = ListAdapter<TableColumn, CreateTableRVViewHolder>
typealias ColChangeCallback = (adapter: CreateTableRVAdapter, col: TableColumn, position: Int) -> Unit

class CreateTableRVAdapter(val onUpdateColumn: ColChangeCallback) :
    LA(CreateTableDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CreateTableRVViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_create_table, parent, false)
        return CreateTableRVViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CreateTableRVViewHolder, position: Int
    ) {
        val data = getItem(position)
        holder.itemView.run {
            typeInput.setText(data.type)
            columnNameInput.setText(data.value)

            typeInput.text
            typeInput.addTextChangedListener {
                val item = getItem(position)
                onUpdateColumn(
                    this@CreateTableRVAdapter,
                    item.copy(type = it.toString()),
                    position
                )
            }
            columnNameInput.addTextChangedListener {
                val item = getItem(position)
                onUpdateColumn(
                    this@CreateTableRVAdapter,
                    item.copy(value = it.toString()),
                    position
                )
            }
        }
    }
}

class CreateTableDialog(
    context: Context,
    onSubmitColumns: (tableName: String, columns: List<TableColumn>) -> Unit
) {
    private val dialog: AlertDialog
    private val columnList = mutableListOf(
        TableColumn("SERIAL", "id"), TableColumn("TEXT", "name")

    )
    private val adapter: CreateTableRVAdapter
    private var tableName = "new_table"

    init {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_create_table, null)

        adapter = CreateTableRVAdapter { adapter, col, pos ->
            columnList[pos] = col
        }
        adapter.submitList(columnList.toList())
        view.columnsRecyclerView.adapter = adapter

        val s = ItemSwipeHelper {
            columnList.removeAt(it.adapterPosition)
            adapter.submitList(columnList.toList())
        }

        ItemTouchHelper(s).attachToRecyclerView(view.columnsRecyclerView)
        val builder =
            MaterialAlertDialogBuilder(context).setView(view).setNeutralButton(
                context.getString(R.string.dialog_add_column), null
            )
                .setPositiveButton(context.getString(R.string.dialog_create)) { _, _ ->
                    onSubmitColumns(tableName, columnList)
                }.setTitle(context.getString(R.string.dialog_create_table))

        dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                columnList.add(TableColumn("", ""))
                adapter.submitList(columnList.toList())
            }
        }

        view.tableNameInput.addTextChangedListener {
            val name = it.toString()
            tableName = name
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                name.isNotEmpty()
        }
    }

    fun invoke() = dialog.show()
}