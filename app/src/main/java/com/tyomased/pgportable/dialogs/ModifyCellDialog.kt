package com.tyomased.pgportable.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tyomased.pgportable.R
import com.tyomased.pgportable.fragments.Cell
import kotlinx.android.synthetic.main.dialog_modify_cell.view.*

class ModifyCellDialog(
    context: Context,
    oldCell: Cell,
    modifyCallback: (newCell: Cell) -> Unit,
    deleteCallback: () -> Unit
) {
    private val builder: MaterialAlertDialogBuilder

    init {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.dialog_modify_cell, null)
        val input = view.cellDataInput
        input.setText(oldCell.data)

        builder = MaterialAlertDialogBuilder(context)
            .setView(view)
            .setTitle("Edit data")
            .setPositiveButton("Ok") { dialog, which ->
                modifyCallback(Cell(input.text.toString()))
            }
            .setNegativeButton("Delete row") { dialog, which ->
                deleteCallback()
            }
    }

    fun invoke() = builder.show()
}
