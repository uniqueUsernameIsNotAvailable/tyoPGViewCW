package com.tyomased.pgportable

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper.*

class ItemSwipeHelper(
    val onSwipeDelete: (viewHoler: RecyclerView.ViewHolder) -> Unit
) : ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int
    ) {
        onSwipeDelete(viewHolder)
    }
}
