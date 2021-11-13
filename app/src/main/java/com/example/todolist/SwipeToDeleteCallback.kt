package com.example.todolist


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeToDeleteCallback: ItemTouchHelper.Callback() {

    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var colorDrawableBackground2: ColorDrawable

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        colorDrawableBackground = ColorDrawable(Color.parseColor("#FFF46E64"))
        colorDrawableBackground2 = ColorDrawable(Color.parseColor("#FF80C4CD"))

        val itemView = viewHolder.itemView

        if (dX > 0) {
            colorDrawableBackground.setBounds(
                itemView.left,
                itemView.top,
                dX.toInt(),
                itemView.bottom
            )
        } else {
            colorDrawableBackground2.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        }

            colorDrawableBackground.draw(c)
            colorDrawableBackground2.draw(c)

            c.save()

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        }
    }

