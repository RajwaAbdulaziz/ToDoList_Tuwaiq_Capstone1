package com.example.todolist.toDoListFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.database.Tasks
import com.example.todolist.taskDetailsFragment.TaskDetailsFragment
import com.example.todolist.taskDetailsFragment.TaskDetailsFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

const val TASK_ID_KEY = "uuid"
var ADD_TASK = ""
var UPDATE_TASK = ""
var updateIsClicked = false
class ToDoListFragment : Fragment() {

    private lateinit var todoRecyclerView: RecyclerView
    private lateinit var addTaskButton: ImageButton
    private lateinit var undoneButton: AppCompatButton

    private val toDoListViewModel by lazy { ViewModelProvider(this).get(ToDoListViewModel::class.java) }

    private val fragmentTaskDetailsViewModel by lazy {
        ViewModelProvider(this).get(
            TaskDetailsFragmentViewModel::class.java
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_to_do_list, container, false)
        todoRecyclerView = view.findViewById(R.id.todo_recycler_view)
        val linearLayoutManager = LinearLayoutManager(context)
        todoRecyclerView.layoutManager = linearLayoutManager
        addTaskButton = view.findViewById(R.id.add_task_button)

        addTaskButton.setOnClickListener{
            ADD_TASK = "ADD"
            //Log.d("Rajwa", task.id.toString())
            val fragment = TaskDetailsFragment()
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toDoListViewModel.liveDataCrimes.observe(viewLifecycleOwner, Observer {
            updateUI(it)
         }
        )
    }

    private fun updateUI(tasks: List<Tasks>) {

        val toDoAdapter = ToDoAdapter(tasks)
        todoRecyclerView.adapter = toDoAdapter
    }

    private inner class ToDoViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var task: Tasks
        private var taskCheckBox: CheckBox = itemView.findViewById(R.id.todo_checkbox)
        private var dateTextView: TextView = itemView.findViewById(R.id.todo_date)
        private var deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        init {
            itemView.setOnClickListener(this)
            deleteButton.setOnClickListener(this)
            taskCheckBox.setOnClickListener(this)
        }

        fun bind(task: Tasks) {
            this.task = task
            taskCheckBox.text = task.taskTitle
            dateTextView.text = task.dueDate.toString()
            taskCheckBox.isChecked = task.isDone

            if (Date().after(task.dueDate)) {
                taskCheckBox.setTextColor((resources.getColor(R.color.design_default_color_error)))
            }
        }

        fun bind2(task: Tasks) {
            this.task = task
            taskCheckBox.text = task.taskTitle
            dateTextView.text = task.dueDate.toString()
            taskCheckBox.isChecked = task.isDone

            if (Date().after(task.dueDate)) {
                taskCheckBox.setTextColor((resources.getColor(R.color.design_default_color_error)))
            }
        }

        override fun onClick(p0: View?) {

            when (p0) {
                itemView -> {
                    updateIsClicked = true
                    UPDATE_TASK = "UPDATE"
                    val bottomSheetFragment: BottomSheetDialogFragment = TaskDetailsFragment()
                    fragmentTaskDetailsViewModel.safeUpdates(task)
                    val args = Bundle()
                    args.putSerializable(TASK_ID_KEY, task.id)
                    bottomSheetFragment.arguments = args
                    bottomSheetFragment.show(fragmentManager!!, bottomSheetFragment.tag)
                }
                deleteButton -> {
                    fragmentTaskDetailsViewModel.delete(task.id)
                }
                taskCheckBox -> {
                    task.isDone = true
                }
            }
        }
    }

    private inner class ToDoAdapter(var task: List<Tasks>): RecyclerView.Adapter<ToDoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
            val view = when(viewType) {
                0 -> layoutInflater.inflate(R.layout.to_do_list_item, parent, false)
                else -> layoutInflater.inflate(R.layout.to_do_list_item2, parent, false)
            }
            return ToDoViewHolder(view)
        }

        override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
            val task = task[position]
            if (holder.itemViewType == 0) {
                holder.bind(task)
            } else {
                holder.bind2(task)
            }
//            val swipeGesture = object : SwipeGesture() {
//
//                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//
//                    when(direction) {
//                        ItemTouchHelper.LEFT -> fragmentTaskDetailsViewModel.delete(task.id)
//                    }
//                }
//            }
//
//            val itemTouchHelper = ItemTouchHelper(swipeGesture)
//            itemTouchHelper.attachToRecyclerView(todoRecyclerView)
        }

        override fun getItemCount(): Int = task.size

        override fun getItemViewType(position: Int): Int {
            return if (task[position].isDone) {
                0
            } else {
                1
            }
        }
    }
}