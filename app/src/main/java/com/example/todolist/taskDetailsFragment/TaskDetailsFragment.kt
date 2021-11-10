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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.DatePickerDialogFragment
import com.example.todolist.R
import com.example.todolist.database.Tasks
import com.example.todolist.toDoListFragment.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

const val DATE_PICKER = "date"
const val TASK_DUE_DATE = "DUE DATE"

class TaskDetailsFragment: BottomSheetDialogFragment(), DatePickerDialogFragment.DatePickerCallback{

        private lateinit var task: Tasks
        private lateinit var taskTitleEditText: EditText
        private lateinit var taskDescriptionEditText: EditText
        private lateinit var dueDateButton: Button
        private lateinit var saveTaskDetails: ImageButton
        private lateinit var taskPriority: RadioGroup
        private lateinit var highPriority : RadioButton
        private lateinit var mediumPriority : RadioButton
        private lateinit var lowPriority : RadioButton
        private lateinit var taskTag: RadioGroup
        private lateinit var homeTag : RadioButton
        private lateinit var shoppingTag : RadioButton
        private lateinit var workTag : RadioButton

        //var dueDate = Date()

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
            taskPriority = view.findViewById(R.id.priorityRadioGroup)
            highPriority = view.findViewById(R.id.high_button)
            mediumPriority = view.findViewById(R.id.medium_button)
            lowPriority = view.findViewById(R.id.low_button)
            taskTag = view.findViewById(R.id.tagsRadioGroup)
            homeTag = view.findViewById(R.id.home_tag)
            shoppingTag = view.findViewById(R.id.shopping_tag)
            workTag = view.findViewById(R.id.work_tag)



//            dueDateButton.apply {
//                text = task.dueDate.toString()
//            }

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
                if (task.taskTitle != "") {
                    toDoListViewModel.addTask(task)

                    val fragment = ToDoListFragment()
                    activity?.let {
                        it.supportFragmentManager.beginTransaction()
                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .replace(R.id.fragment_container_view, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                } else {
                    Toast.makeText(context, "You must set a task title", Toast.LENGTH_SHORT).show()
                }
            }

                val taskTitleTextWatcher = object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        task.taskTitle = p0.toString()
                    }

                    override fun afterTextChanged(p0: Editable?) {

                    }
                }
                taskTitleEditText.addTextChangedListener(taskTitleTextWatcher)

            val descTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    task.description = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            }

            taskDescriptionEditText.addTextChangedListener(descTextWatcher)


           taskPriority.setOnCheckedChangeListener { _, i ->

                when (taskPriority.checkedRadioButtonId) {
                    R.id.high_button -> {
                        task.priority = 1
                    }
                    R.id.medium_button -> {
                        task.priority = 2
                    }
                    R.id.low_button -> {
                        task.priority = 3
                    }
                }
            }

            taskTag.setOnCheckedChangeListener { _, i ->
                when(taskTag.checkedRadioButtonId){
                    R.id.home_tag -> {
                        task.tag = "Home"
                    }
                    R.id.shopping_tag -> {
                        task.tag = "Shopping"
                    }
                    R.id.work_tag -> {
                    task.tag = "Work"
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

                        if (it.dueDate != null) {
                            var spf = SimpleDateFormat("E LLL dd hh:mm:ss z yyyy")
                            val parsedDueDate = spf.parse(it.dueDate.toString())
                            spf = SimpleDateFormat("dd MMM yyyy")
                            val formattedDueDate = spf.format(parsedDueDate)
                            dueDateButton.text = formattedDueDate.toString()
                        }
                        when(task.priority){
                            1 -> taskPriority.check(R.id.high_button)
                            2 -> taskPriority.check(R.id.medium_button)
                            3 -> taskPriority.check(R.id.low_button)
                        }
                        when(task.tag){
                            "Home" -> taskTag.check(R.id.home_tag)
                            "Shopping" -> taskTag.check(R.id.shopping_tag)
                            "Work" -> taskTag.check(R.id.work_tag)
                        }
                    }
                }
            )
        }

        override fun onDateSelected(date: Date) {
            task.dueDate = date
            var spf = SimpleDateFormat("E LLL dd hh:mm:ss z yyyy")
            val parsedDueDate = spf.parse(date.toString())
            spf = SimpleDateFormat("dd MMM yyyy")
            val formattedDueDate = spf.format(parsedDueDate)
            dueDateButton.text = formattedDueDate.toString()
        }

        override fun onStop() {
            super.onStop()
            UPDATE_TASK = ""
            fragmentTaskDetailsViewModel.safeUpdates(task)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (ADD_TASK == "ADD") {
//            task = Tasks()
//        }

        if (UPDATE_TASK == "UPDATE") {
            val taskId = arguments?.getSerializable(TASK_ID_KEY) as UUID
            fragmentTaskDetailsViewModel.loadTask(taskId)
        } else {
            task = Tasks()
        }


    }
}

