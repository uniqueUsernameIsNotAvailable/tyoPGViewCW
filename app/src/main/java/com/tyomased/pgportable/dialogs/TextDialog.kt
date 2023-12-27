package com.tyomased.pgportable.dialogs

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tyomased.pgportable.R

enum class MessageType(title: String) {
    INFO("Info"),
    WARN("Warning"),
    ERROR("ERROR"),
}

class TextDialog(
    context: Context,
    type: MessageType,
    text: String
) {
    val builder = MaterialAlertDialogBuilder(context)
        .setTitle(type.name)
        .setMessage(text)
        .setPositiveButton(context.getString(R.string.dialog_ok), null)

    fun invoke() = builder.show()
}