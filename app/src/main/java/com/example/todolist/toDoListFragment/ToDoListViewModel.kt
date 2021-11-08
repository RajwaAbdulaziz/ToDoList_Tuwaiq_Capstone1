package com.example.todolist.toDoListFragment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.todolist.database.TaskRepo
import com.example.todolist.database.Tasks
import java.util.*

class ToDoListViewModel: ViewModel() {

    private val taskRepo = TaskRepo.get()

    val liveDataCrimes = taskRepo.getAllTasks()

    fun addTask(task: Tasks) {
        taskRepo.addTask(task)
    }
}