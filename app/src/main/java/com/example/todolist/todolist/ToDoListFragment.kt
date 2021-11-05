package com.example.todolist.todolist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.todo.Tasks

class ToDoListFragment : Fragment() {

    private lateinit var todoRecyclerView: RecyclerView

    private val toDoListViewModel by lazy { ViewModelProvider(this).get(ToDoListViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_to_do_list, container, false)
        todoRecyclerView = view.findViewById(R.id.todo_recycler_view)
        val linearLayoutManager = LinearLayoutManager(context)
        todoRecyclerView.layoutManager = linearLayoutManager
        todoRecyclerView.adapter = ToDoAdapter(toDoListViewModel.toDoList)
        return view
    }

    private inner class ToDoViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private var taskTextView: CheckBox = itemView.findViewById(R.id.todo_checkbox)
        private var dateTextView: TextView = itemView.findViewById(R.id.todo_date)

        fun bind(task: Tasks) {
            taskTextView.text = task.task
            dateTextView.text = task.date.toString()
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