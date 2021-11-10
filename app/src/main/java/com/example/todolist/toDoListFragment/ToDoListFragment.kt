package com.example.todolist.toDoListFragment

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.SwipeToDeleteCallback
import com.example.todolist.database.Tasks
import com.example.todolist.taskDetailsFragment.TaskDetailsFragment
import com.example.todolist.taskDetailsFragment.TaskDetailsFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import android.content.Intent
import android.content.pm.ResolveInfo

import android.content.pm.PackageManager
import android.net.Uri


const val TASK_ID_KEY = "uuid"
var ADD_TASK = ""
var UPDATE_TASK = ""
var updateIsClicked = false
class ToDoListFragment : Fragment() {

    private lateinit var todoRecyclerView: RecyclerView
    private lateinit var addTaskButton: ImageButton
    private lateinit var deleteAllTasks: ImageButton
    private lateinit var priorityFilterButton: AppCompatButton
    private lateinit var undoneFilterButton: AppCompatButton
    private lateinit var homeTagButton: ImageButton
    private lateinit var shoppingTagButton: ImageButton
    private lateinit var workTagButton: ImageButton
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var hintButton: ImageButton

    private val fragmentToDoListViewModel by lazy { ViewModelProvider(this).get(ToDoListViewModel::class.java) }

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
        //addTaskButton = view.findViewById(R.id.add_task_button)
        deleteAllTasks = view.findViewById(R.id.delete_all_button)
        priorityFilterButton = view.findViewById(R.id.priority_button)
        undoneFilterButton = view.findViewById(R.id.undone_button)
        homeTagButton = view.findViewById(R.id.home_tag_button)
        shoppingTagButton = view.findViewById(R.id.shopping_tag_button)
        workTagButton = view.findViewById(R.id.work_tag_button)
        fabAdd = view.findViewById(R.id.fab_add)
        hintButton = view.findViewById(R.id.hint_button)

        fabAdd.setOnClickListener {
            ADD_TASK = "ADD"
            val fragment = TaskDetailsFragment()
            activity?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
//        addTaskButton.setOnClickListener{
//            ADD_TASK = "ADD"
//            val fragment = TaskDetailsFragment()
//            activity?.let {
//                it.supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container_view, fragment)
//                    .addToBackStack(null)
//                    .commit()
//            }
//        }

        deleteAllTasks.setOnClickListener{
            fragmentToDoListViewModel.deleteAllTasks()
        }

        return view
    }

    var newTasks = listOf<Tasks>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hintButton.tooltipText = "Slide left to delete, right to edit"

        priorityFilterButton.setOnClickListener {
            fragmentToDoListViewModel.livePriority.observe(viewLifecycleOwner, Observer {
                updateUI(it)
            })
        }

        homeTagButton.setOnClickListener{
            fragmentToDoListViewModel.getTasksTagged("Home").observe(viewLifecycleOwner, Observer {
                updateUI(it)
            })
        }

        shoppingTagButton.setOnClickListener{
            fragmentToDoListViewModel.getTasksTagged("Shopping").observe(viewLifecycleOwner, Observer {
                updateUI(it)
            })
        }

        workTagButton.setOnClickListener{
            fragmentToDoListViewModel.getTasksTagged("Work").observe(viewLifecycleOwner, Observer {
                updateUI(it)
            })
        }

        fragmentToDoListViewModel.liveDataTasks.observe(viewLifecycleOwner, Observer {
            updateUI(it)
            newTasks = it
         }
        )

        undoneFilterButton.setOnClickListener {
            fragmentToDoListViewModel.liveUndone.observe(viewLifecycleOwner, Observer {
                updateUI(it)
                newTasks = it
            }
            )
        }

        val swipeToDeleteCallback = object: SwipeToDeleteCallback() {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = newTasks[viewHolder.adapterPosition]
                    when(direction) {
                        RIGHT -> fragmentTaskDetailsViewModel.delete(task.id)
                        LEFT -> {
                            updateIsClicked = true
                            UPDATE_TASK = "UPDATE"
                            val bottomSheetFragment: BottomSheetDialogFragment = TaskDetailsFragment()
                            fragmentTaskDetailsViewModel.safeUpdates(task)
                            val args = Bundle()
                            args.putSerializable(TASK_ID_KEY, task.id)
                            bottomSheetFragment.arguments = args
                            //bottomSheetFragment.setStyle(STYLE_NORMAL, R.style.MyDialog)
                            //bottomSheetFragment.setStyle(STYLE_NO_TITLE, R.style.MyDialog)
                            bottomSheetFragment.show(fragmentManager!!, bottomSheetFragment.tag)
                        }
                    }
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
            itemTouchHelper.attachToRecyclerView(todoRecyclerView)
    }

    private fun updateUI(tasks: List<Tasks>) {
        val toDoAdapter = ToDoAdapter(tasks)
        todoRecyclerView.adapter = toDoAdapter
    }

    private inner class ToDoViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var task: Tasks
        private var taskCheckBox: CheckBox = itemView.findViewById(R.id.todo_checkbox)
        private var dateTextView: TextView = itemView.findViewById(R.id.todo_date)
        private val tweetButton: ImageView = itemView.findViewById(R.id.tweet_button)

        init {
            itemView.setOnClickListener(this)
            taskCheckBox.setOnClickListener(this)
            tweetButton.setOnClickListener(this)
        }

        fun bind(task: Tasks) {
            this.task = task
            taskCheckBox.text = task.taskTitle
            taskCheckBox.isChecked = task.isDone

            if (task.isDone) {
                tweetButton.visibility = View.VISIBLE
            } else {
                tweetButton.visibility = View.GONE
            }

            if (task.dueDate != null) {
                var spf = SimpleDateFormat("E LLL dd hh:mm:ss z yyyy")
                val parsedDueDate = spf.parse(task.dueDate.toString())
                spf = SimpleDateFormat("dd MMM yyyy")
                val formattedDueDate = spf.format(parsedDueDate)
                dateTextView.text = formattedDueDate.toString()
            } else {
                dateTextView.text = "No due date"
            }

            if (task.dueDate != null) {

                var spf = SimpleDateFormat("E LLL dd hh:mm:ss z yyyy")
                val parsedCurrentDate = spf.parse(task.currentDate.toString())
                val parsedDueDate = spf.parse(task.dueDate.toString())
                spf = SimpleDateFormat("dd MMM yyyy")
                val formattedCurrentDate = spf.format(parsedCurrentDate)
                val formattedDueDate = spf.format(parsedDueDate)

            if (formattedCurrentDate > formattedDueDate) {
                taskCheckBox.setTextColor((resources.getColor(R.color.design_default_color_error)))
                }
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
                taskCheckBox -> {
                    task.isDone = !task.isDone
                    fragmentTaskDetailsViewModel.safeUpdates(task)
                }
                tweetButton -> {
                    val url = "https://twitter.com/intent/tweet?source=webclient&text=My+Task+Is+Done!"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                }
//                priorityFilterButton -> {
//                    val new: List<Tasks> = toDoListViewModel.getPriority(task.priority)
//                    updateUI2(new)
//                }
            }
//            taskCheckBox.setOnCheckedChangeListener { _, b ->
//                if (b) {
//                    taskCheckBox.isChecked = false
//                    task.isDone = false
//                    fragmentTaskDetailsViewModel.safeUpdates(task)
//                } else {
//                    task.isDone = false
//                    fragmentTaskDetailsViewModel.safeUpdates(task)
//                }


        }
    }

    private inner class ToDoAdapter(var task: List<Tasks>): RecyclerView.Adapter<ToDoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
            val view = layoutInflater.inflate(R.layout.to_do_list_item, parent, false)
            return ToDoViewHolder(view)
        }

        override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
            val task = task[position]
                holder.bind(task)
        }

        override fun getItemCount(): Int = task.size
    }
}