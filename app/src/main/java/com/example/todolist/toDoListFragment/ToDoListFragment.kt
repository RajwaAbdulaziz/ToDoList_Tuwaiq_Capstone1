package com.example.todolist.toDoListFragment

import android.annotation.SuppressLint
import android.app.*
import android.os.Bundle
import android.view.*
import android.widget.*
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
import com.example.todolist.database.Task
import com.example.todolist.taskDetailsFragment.TaskDetailsFragment
import com.example.todolist.taskDetailsFragment.TaskDetailsFragmentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import android.net.Uri
import com.example.todolist.MainActivity
import android.widget.ArrayAdapter
import android.content.*
import android.content.Context.NOTIFICATION_SERVICE
import androidx.appcompat.app.AlertDialog
import android.graphics.Color
import android.graphics.Paint
import android.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

const val TASK_ID_KEY = "uuid"
var ADD_TASK = ""
var UPDATE_TASK = ""
var updateIsClicked = false
var homeClicked = false
var shoppingClicked = false
var workClicked = false
var wantTweet = true
val CHANNEL_ID = "CHANNEL ID"
val CHANNEL_NAME = "CHANNEL_NAME"
val NOTIFICATION_ID = 0
val listNoti = mutableListOf<UUID>()

class ToDoListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var todoRecyclerView: RecyclerView
    private lateinit var deleteAllTasks: ImageButton
    private lateinit var homeTagButton: ImageButton
    private lateinit var shoppingTagButton: ImageButton
    private lateinit var workTagButton: ImageButton
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var hintButton: ImageButton
    private lateinit var spinner: Spinner
    private lateinit var startingText: TextView


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
        deleteAllTasks = view.findViewById(R.id.delete_all_button)
        homeTagButton = view.findViewById(R.id.home_tag_button)
        shoppingTagButton = view.findViewById(R.id.shopping_tag_button)
        workTagButton = view.findViewById(R.id.work_tag_button)
        fabAdd = view.findViewById(R.id.fab_add)
        hintButton = view.findViewById(R.id.hint_button)
        spinner = view.findViewById(R.id.spinner)
        startingText = view.findViewById(R.id.startingText)


        val sortList = arrayOf("Sort", "Priority", "Undone")

        spinner.onItemSelectedListener = this

        val aa = context?.let {
            ArrayAdapter(it, android.R.layout.simple_spinner_item, sortList) }
        aa?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = aa

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

        deleteAllTasks.setOnClickListener {
            val builder = context?.let { it1 -> AlertDialog.Builder(it1) }
            builder?.let {

                it.setMessage("Are you sure you want to Delete All Tasks?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        fragmentToDoListViewModel.deleteAllTasks()
                        startingText.visibility = View.VISIBLE
                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.dismiss()
                    }
                val alert = builder.create()
                alert.show()
            }
        }

        return view
    }

    var newTasks = listOf<Task>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        hintButton.tooltipText = "Slide left to delete, right to edit"

        homeTagButton.setOnClickListener {
            if (!homeClicked) {
                homeClicked = true
                shoppingTagButton.visibility = View.INVISIBLE
                workTagButton.visibility = View.INVISIBLE
                fragmentToDoListViewModel.liveDataTasks
                    .observe(viewLifecycleOwner, Observer { list ->
                        if (list.isNotEmpty()) {
                            val a = list.filter { it.tag == "Home" }
                             updateUI(a as MutableList<Task>)
                        }
                    })
            } else if(homeClicked){
                homeClicked = false
                fragmentToDoListViewModel.liveDataTasks.observe(viewLifecycleOwner, Observer {
                    updateUI(it as MutableList<Task>)
                }
                )
                shoppingTagButton.visibility = View.VISIBLE
                workTagButton.visibility = View.VISIBLE
            }
        }

        shoppingTagButton.setOnClickListener {
            if (!shoppingClicked) {
                shoppingClicked = true
                homeTagButton.visibility = View.INVISIBLE
                workTagButton.visibility = View.INVISIBLE
                fragmentToDoListViewModel.liveDataTasks
                    .observe(viewLifecycleOwner, Observer { list ->
                        if (list.isNotEmpty()) {
                            val a = list.filter { it.tag == "Shopping" }
                            updateUI(a as MutableList<Task>)
                        }
                    })
            } else if (shoppingClicked) {
                shoppingClicked = false
                fragmentToDoListViewModel.liveDataTasks.observe(viewLifecycleOwner, Observer {
                    updateUI(it as MutableList<Task>)
                }
                )
                homeTagButton.visibility = View.VISIBLE
                workTagButton.visibility = View.VISIBLE
            }
        }

        workTagButton.setOnClickListener {
            if (!workClicked) {
                workClicked = true
                shoppingTagButton.visibility = View.INVISIBLE
                homeTagButton.visibility = View.INVISIBLE
                fragmentToDoListViewModel.liveDataTasks
                    .observe(viewLifecycleOwner, Observer { list ->
                        if (list.isNotEmpty()) {
                            val a = list.filter { it.tag == "Work" }
                            updateUI(a as MutableList<Task>)
                        }
                    })
            } else if (workClicked) {
                workClicked = false
                fragmentToDoListViewModel.liveDataTasks.observe(viewLifecycleOwner, Observer {
                    updateUI(it as MutableList<Task>)
                }
                )
                shoppingTagButton.visibility = View.VISIBLE
                homeTagButton.visibility = View.VISIBLE
            }
        }

        fragmentToDoListViewModel.liveDataTasks
            .observe(viewLifecycleOwner, Observer {
                newTasks = it
            })

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
                            bottomSheetFragment.show(fragmentManager!!, bottomSheetFragment.tag)
                        }
                    }
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
            itemTouchHelper.attachToRecyclerView(todoRecyclerView)
    }

    private fun updateUI(tasks: MutableList<Task>) {
        val toDoAdapter = ToDoAdapter(tasks)
        todoRecyclerView.adapter = toDoAdapter
    }

    private inner class ToDoViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var task: Task
        private var taskCheckBox: CheckBox = itemView.findViewById(R.id.todo_checkbox)
        private var dateTextView: TextView = itemView.findViewById(R.id.todo_date)
        private val tweetButton: ImageView = itemView.findViewById(R.id.tweet_button)

        init {
            itemView.setOnClickListener(this)
            taskCheckBox.setOnClickListener(this)
            tweetButton.setOnClickListener(this)
        }

        fun bind(task: Task) {
            this.task = task

            taskCheckBox.text = task.taskTitle
            taskCheckBox.isChecked = task.isDone

            val wmbPreference = PreferenceManager.getDefaultSharedPreferences(context)
            val isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true)

            if (isFirstRun) {

                val builder = context?.let { it1 -> AlertDialog.Builder(it1) }
                builder?.setMessage("Do you want to cancel tweet button?")
                    ?.setCancelable(false)
                    ?.setPositiveButton("Yes") { dialog, id ->
                        wantTweet = false
                    }
                    ?.setNegativeButton("No") { dialog, id ->
                        wantTweet = true
                        dialog.dismiss()
                    }
                val alert = builder?.create()
                alert?.show()
                val editor: SharedPreferences.Editor = wmbPreference.edit()
                editor.putBoolean("FIRSTRUN", false)
                editor.apply()

            }

            if (task.isDone) {
                taskCheckBox.paintFlags = taskCheckBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                if (wantTweet) {
                    tweetButton.visibility = View.VISIBLE
                } else {
                    tweetButton.visibility = View.GONE
                }
            } else {
                tweetButton.visibility = View.GONE
            }

            if (task.dueDate != null) {
                val formattedDueDate = formatDateSingle(task)
                dateTextView.text = formattedDueDate
            } else {
                dateTextView.text = "No due date"
            }

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            val manager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }

        val notification = context?.let { NotificationCompat.Builder(it, CHANNEL_ID) }
            ?.setContentTitle("Your (${task.taskTitle}) task is overdue")
            ?.setContentText("Click here to update or delete it")
            ?.setSmallIcon(R.drawable.ic_baseline_checklist_24)
            ?.setPriority(NotificationCompat.PRIORITY_HIGH)
            ?.setContentIntent(pendingIntent)
            ?.build()

        val notificationManager = context?.let { NotificationManagerCompat.from(it) }

            if (task.dueDate != null) {

                val (formattedCurrentDate, formattedDueDate) = formatDatePair(task)

                if (formattedCurrentDate > formattedDueDate) {
                taskCheckBox.setTextColor((resources.getColor(R.color.design_default_color_error)))
                    if (!listNoti.contains(task.id)) {
                        listNoti.add(task.id)
                        if (notification != null) {
                        notificationManager?.notify(NOTIFICATION_ID, notification)
                        }
                    }
                }
            }
        }

        override fun onClick(p0: View?) {

            when (p0) {
                itemView -> {
                    if (task.isDone) {
                        Toast.makeText(context, "You can't update a finished task", Toast.LENGTH_SHORT).show()
                        itemView.isClickable = false
                    } else {
                        updateIsClicked = true
                        UPDATE_TASK = "UPDATE"
                        val bottomSheetFragment: BottomSheetDialogFragment = TaskDetailsFragment()
                        fragmentTaskDetailsViewModel.safeUpdates(task)
                        val args = Bundle()
                        args.putSerializable(TASK_ID_KEY, task.id)
                        bottomSheetFragment.arguments = args
                        bottomSheetFragment.show(fragmentManager!!, bottomSheetFragment.tag)
                    }
                }
                taskCheckBox -> {
                    task.isDone = !task.isDone
                    fragmentTaskDetailsViewModel.safeUpdates(task)
                }
                tweetButton -> {
                    val url = "https://twitter.com/intent/tweet?source=webclient&text=My+Task+(${task.taskTitle})+Is+Done!"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDatePair(task: Task): Pair<String, String> {
        var spf = SimpleDateFormat("E LLL dd hh:mm:ss z yyyy")
        val parsedCurrentDate = spf.parse(task.currentDate.toString())
        val parsedDueDate = spf.parse(task.dueDate.toString())
        spf = SimpleDateFormat("dd MMM yyyy")
        val formattedCurrentDate = spf.format(parsedCurrentDate)
        val formattedDueDate = spf.format(parsedDueDate)
        return Pair(formattedCurrentDate, formattedDueDate)
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDateSingle(task: Task): String{
        var spf = SimpleDateFormat("E LLL dd hh:mm:ss z yyyy")
        val parsedDueDate = spf.parse(task.dueDate.toString())
        spf = SimpleDateFormat("dd MMM yyyy")
         return spf.format(parsedDueDate)
    }

    private inner class ToDoAdapter(var task: MutableList<Task>): RecyclerView.Adapter<ToDoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
            val view = layoutInflater.inflate(R.layout.to_do_list_item, parent, false)
            if (task.size > 0) {
                startingText.visibility = View.GONE
            }
                return ToDoViewHolder(view)
        }

        override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
            val taskAt = task[position]
            if (taskAt.isDone) {
                task.remove(taskAt)
                task.add(task.size, taskAt)
                val newTask = task[position]
                holder.bind(newTask)
            } else {
                holder.bind(taskAt)
            }
        }

        override fun getItemCount(): Int = task.size
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        when(p2) {
            1 -> {
                fragmentToDoListViewModel.liveDataTasks.observe(viewLifecycleOwner, Observer { list ->
                    if (list.isNotEmpty()) {
                        updateUI(list.sortedBy { it.priority } as MutableList<Task>)
                    }
                })
            }
            2 -> {
                fragmentToDoListViewModel.liveDataTasks.observe(viewLifecycleOwner, Observer { list ->
                    if (list.isNotEmpty()) {
                        updateUI(list.filterNot { it.isDone } as MutableList<Task>)
                    }
                })
            }
            0 -> {
                fragmentToDoListViewModel.liveDataTasks.observe(viewLifecycleOwner, Observer {
                        updateUI(it as MutableList<Task>)
                    })
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}