package com.example.todolist.todolist

import androidx.lifecycle.ViewModel
import com.example.todolist.todo.Tasks
import java.util.*

class ToDoListViewModel: ViewModel() {

    val toDoList = mutableListOf<Tasks>()

    init {
        for (i in 0..10) {
            val todo = Tasks()
            todo.task = "Task No. $i"
            todo.date = Date()
            toDoList.add(todo)
        }
    }
}