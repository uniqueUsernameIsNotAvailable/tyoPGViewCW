package com.tyomased.pgportable.dialogs

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tyomased.pgportable.R

class ConfirmAction(
    context: Context,
    text: String,
    okCallback: () -> Unit,
    cancelCallback: () -> Unit
) {
    val dialogBuilder = MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.dialog_confirm_action))
        .setMessage(text)
        .setPositiveButton(context.getString(R.string.dialog_yes))
        { _, _ -> okCallback() }
        .setNegativeButton(context.getString(R.string.dialog_cancel))
        { _, _ -> cancelCallback() }
        .setOnCancelListener { cancelCallback() }

    fun invoke() = dialogBuilder.show()
}