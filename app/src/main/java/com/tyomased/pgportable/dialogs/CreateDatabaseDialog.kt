package com.tyomased.pgportable.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tyomased.pgportable.R
import kotlinx.android.synthetic.main.dialog_create_database.view.*

class CreateDatabaseDialog(
    context: Context,
    onCreateCallback: (dbName: String) -> Unit
) {
    val builder: MaterialAlertDialogBuilder

    init {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_create_database, null)
        builder = MaterialAlertDialogBuilder(context)
            .setView(view)
            .setTitle(context.getString(R.string.dialog_create_database))
            .setPositiveButton(context.getString(R.string.dialog_create))
            { dialog, which ->
                onCreateCallback(view.dbNameInput.text.toString())
            }
            .setNegativeButton(
                context.getString(R.string.dialog_cancel),
                null
            )
    }

    fun invoke() = builder.show()
}
