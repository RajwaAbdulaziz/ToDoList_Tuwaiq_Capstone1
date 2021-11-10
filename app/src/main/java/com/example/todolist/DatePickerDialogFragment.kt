package com.example.todolist

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.todolist.taskDetailsFragment.TASK_DUE_DATE
import java.util.*

class DatePickerDialogFragment: DialogFragment() {

    interface DatePickerCallback {

        fun onDateSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val date = arguments?.getSerializable(TASK_DUE_DATE) as? Date

            val calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dateListener = DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
                val resultDate = GregorianCalendar(i, i2, i3).time
                targetFragment.let {
                    (it as DatePickerCallback).onDateSelected(resultDate)
                }
            }
            return DatePickerDialog(
                requireContext(),
                dateListener,
                year,
                month,
                day
            )
    }
}