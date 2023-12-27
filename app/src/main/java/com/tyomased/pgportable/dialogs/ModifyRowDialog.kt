package com.tyomased.pgportable.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tyomased.pgportable.R

class ModifyRowDialog(
    val parent: ViewGroup,
    val header: List<String>,
    val row: Map<String, String>,
    val onSubmit: (r: Map<String, String>) -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater
            .inflate(R.layout.dialog_modify_row, parent, false)
        return AlertDialog.Builder(context)
            .setView(view)
            .setTitle(requireContext().getString(R.string.dialog_modify_row))
            .setPositiveButton(requireContext().getString(R.string.dialog_ok))
            { dialog, which ->
                TODO()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                TODO()
            }
            .create()
    }
}