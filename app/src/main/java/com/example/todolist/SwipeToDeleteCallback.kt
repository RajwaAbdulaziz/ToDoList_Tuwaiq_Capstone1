package com.example.todolist

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeToDeleteCallback: ItemTouchHelper.Callback() {

    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var colorDrawableBackground2: ColorDrawable
    private lateinit var deleteIcon: Drawable

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

        //deleteIcon = getDrawable(R.drawable.ic_baseline_delete_24)!!

        val itemView = viewHolder.itemView
        //val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

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

//        if (dX > 0)
//            c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
//        else
//            c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

            //c.restore()

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        }
    }

