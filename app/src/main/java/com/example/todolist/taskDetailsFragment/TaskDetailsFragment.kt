package com.example.todolist.taskDetailsFragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.DatePickerDialogFragment
import com.example.todolist.R
import com.example.todolist.database.Tasks
import com.example.todolist.toDoListFragment.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

const val DATE_PICKER = "date"
const val TASK_DUE_DATE = "DUE DATE"

class TaskDetailsFragment: BottomSheetDialogFragment(), DatePickerDialogFragment.DatePickerCallback{

        private lateinit var task: Tasks
        private lateinit var taskTitleEditText: EditText
        private lateinit var taskDescriptionEditText: EditText
        private lateinit var taskPriority: RadioGroup
        private lateinit var dueDateButton: Button
        private lateinit var saveTaskDetails: ImageButton
        private lateinit var highPriority : RadioButton
        private lateinit var mediumPriority : RadioButton
        private lateinit var lowPriority : RadioButton

        var dueDate = Date()

        private val fragmentTaskDetailsViewModel by lazy {
            ViewModelProvider(this).get(
                TaskDetailsFragmentViewModel::class.java
            )
        }

    private val toDoListViewModel by lazy { ViewModelProvider(this).get(ToDoListViewModel::class.java) }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_task_details, container, false)
            taskTitleEditText = view.findViewById(R.id.taskTitleEditText)
            taskDescriptionEditText = view.findViewById(R.id.taskDescriptionEditText)
            dueDateButton = view.findViewById(R.id.dueDateButton)
            saveTaskDetails = view.findViewById(R.id.save_task_button)
            taskPriority = view.findViewById(R.id.radioGroup)
            highPriority = view.findViewById(R.id.high_button)
            mediumPriority = view.findViewById(R.id.medium_button)
            lowPriority = view.findViewById(R.id.low_button)


            dueDateButton.apply {
                text = dueDate.toString()
            }
            return view
        }

        override fun onStart() {
            super.onStart()

            dueDateButton.setOnClickListener {

                val args = Bundle()
                args.putSerializable(TASK_DUE_DATE, task.dueDate)

                val datePicker = DatePickerDialogFragment()
                datePicker.arguments = args

                datePicker.setTargetFragment(this, 0)
                datePicker.show(this.parentFragmentManager, DATE_PICKER)
            }
            if (updateIsClicked) {
                saveTaskDetails.visibility = View.GONE
            }
            updateIsClicked = false

            saveTaskDetails.setOnClickListener {
                ADD_TASK = ""
                task.taskTitle = taskTitleEditText.text.toString()
                task.description = taskDescriptionEditText.text.toString()
                toDoListViewModel.addTask(task)
                val fragment = ToDoListFragment()
                activity?.let {
                    it.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }

                val textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        task.taskTitle = p0.toString()
                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }
                }
                taskTitleEditText.addTextChangedListener(textWatcher)

            val textWatcher2 = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    task.description = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            }
            taskDescriptionEditText.addTextChangedListener(textWatcher2)

            val radioButtonChecked = taskPriority.setOnCheckedChangeListener { radioGroup, i ->
                val view = radioGroup.rootView.findViewById<RadioButton>(i)
                when(view){
                    highPriority -> {
                        task.priority = highPriority.text.toString()
                    }
                    mediumPriority -> {
                        task.priority = mediumPriority.text.toString()
                    } lowPriority -> {
                       task.priority = lowPriority.text.toString()
                    }
                }
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            fragmentTaskDetailsViewModel.taskLiveData.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer {
                    it?.let {
                        task = it
                        taskTitleEditText.setText(it.taskTitle)
                        taskDescriptionEditText.setText(it.description)
                        dueDateButton.text = it.dueDate.toString()
                        when(task.priority){
                            "High" -> taskPriority.check(R.id.high_button)
                            "Medium" -> taskPriority.check(R.id.medium_button)
                            "Low" -> taskPriority.check(R.id.low_button)
                        }
                    }
                }
            )
        }

        override fun onDateSelected(date: Date) {
            task.dueDate = date
            dueDateButton.text = date.toString()
        }

        override fun onStop() {
            super.onStop()
            UPDATE_TASK = ""
            fragmentTaskDetailsViewModel.safeUpdates(task)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ADD_TASK == "ADD") {
            task = Tasks()
        }

        if (UPDATE_TASK == "UPDATE") {
            val taskId = arguments?.getSerializable(TASK_ID_KEY) as UUID
            fragmentTaskDetailsViewModel.loadTask(taskId)
        }
    }
}

