package com.tyomased.pgportable.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.evrencoskun.tableview.adapter.AbstractTableAdapter
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder
import com.tyomased.pgportable.R
import com.tyomased.pgportable.viewmodels.TableViewModel
import kotlinx.android.synthetic.main.item_table_cell.view.*
import writeToTextFileScopedStorage

class TableViewAdapter(
    val context: Context,
    val viewModel: TableViewModel
) : AbstractTableAdapter<ColumnHeader, RowHeader, Cell>() {

    val updatedColor: Int
    val newColor: Int
    val deletedColor: Int

    lateinit var expHelp: List<List<Cell>>

    init {
        updatedColor =
            ContextCompat.getColor(context, R.color.table_cell_updated)
        newColor = ContextCompat.getColor(context, R.color.table_cell_new)
        deletedColor =
            ContextCompat.getColor(context, R.color.table_cell_deleted)
    }

    private fun rows2matrix(
        maps: List<Map<String, Cell>>
    ): List<List<Cell>> = maps
        .map { row ->
            viewModel.header.value!!.map { col -> Cell(row.get(col)?.data) }
        }

    fun updateTableContents() {
        setColumnHeaderItems(viewModel.header.value!!.map { ColumnHeader(it) })
        val prepared = viewModel.run {
            val prevRows = rows.value!!
                .mapIndexed { index, mutableMap ->
                    if (modifiedRows.value!!.containsKey(index)) {
                        val newMap = mutableMap
                            .toMutableMap()
                        newMap.putAll(modifiedRows.value!![index]!!)
                        newMap
                    } else
                        mutableMap
                }
            newRows.value!! + prevRows
        }
        setCellItems(rows2matrix(prepared))


        expHelp = rows2matrix(prepared)
        notifyDataSetChanged()
    }

    val getExpHelp: List<List<Cell>>
        get() = expHelp

    // On create methods
    override fun onCreateRowHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder = CellViewHolder.onCreate(parent, viewType)

    override fun onCreateCellViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder = CellViewHolder.onCreate(parent, viewType)

    override fun onCreateColumnHeaderViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder = CellViewHolder.onCreate(parent, viewType)

    override fun onCreateCornerView(parent: ViewGroup): View =
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_table_cell, parent, false)

    // On bind methods
    override fun onBindCellViewHolder(
        holder: AbstractViewHolder,
        cellItemModel: Cell?,
        columnPosition: Int,
        rowPosition: Int
    ) {
        val h = holder as CellViewHolder
        h.onBind(
            cellItemModel!!,
            rowPosition,
            columnPosition
        )

        viewModel.run {
            val newRowsSize = newRows.value?.size ?: 0
            val isUpdated = modifiedRows.value!!.containsKey(
                rowPosition - newRowsSize
            )
            val isNew = rowPosition <= newRows.value!!.size - 1
            val isDeleted =
                deletedRows.value!!.contains(rowPosition - newRowsSize)

            val cl = when {
                isUpdated -> updatedColor
                isNew -> newColor
                isDeleted -> deletedColor
                else -> null
            }
            cl?.let { h.itemView.cellView.setBackgroundColor(it) }
        }
    }

    override fun onBindColumnHeaderViewHolder(
        holder: AbstractViewHolder,
        columnHeaderItemModel: ColumnHeader?,
        columnPosition: Int
    ) {
        (holder as CellViewHolder).onBind(
            columnHeaderItemModel!!,
            -1,
            columnPosition
        )
    }

    override fun onBindRowHeaderViewHolder(
        holder: AbstractViewHolder,
        rowHeaderItemModel: RowHeader?,
        rowPosition: Int
    ) {
        (holder as CellViewHolder).onBind(
            rowHeaderItemModel!!,
            rowPosition,
            -1
        )
    }

    // Type getters
    override fun getCellItemViewType(position: Int): Int = 0

    override fun getColumnHeaderItemViewType(position: Int): Int = 0

    override fun getRowHeaderItemViewType(position: Int): Int = 0
}

open class Cell {
    val data: String

    constructor(v: String?) {
        data = v ?: "null"
    }

    constructor(old: Cell?) {
        data = old?.data ?: "null"
    }

    override fun toString(): String = when {
        data === "null" -> data
        else -> "'$data'"
    }
}




class ColumnHeader(data: String) : Cell(data)

class RowHeader(data: String) : Cell(data)

open class CellViewHolder(itemView: View) :
    AbstractViewHolder(itemView) {
    fun onBind(cell: Cell, row: Int, column: Int) {
        itemView.cellView.run { text = cell.data }
    }

    companion object {
        fun onCreate(
            parent: ViewGroup,
            viewType: Int? = null
        ): AbstractViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_table_cell, parent, false)
            return CellViewHolder(view)
        }
    }
}
