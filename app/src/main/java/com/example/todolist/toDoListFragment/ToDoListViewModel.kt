package com.example.todolist.toDoListFragment


import androidx.lifecycle.ViewModel
import com.example.todolist.database.TaskRepo
import com.example.todolist.database.Task

class ToDoListViewModel: ViewModel() {

    private val taskRepo = TaskRepo.get()

    val liveDataTasks = taskRepo.getAllTasks()

    fun addTask(task: Task) {
        taskRepo.addTask(task)
    }

    fun deleteAllTasks() {
        taskRepo.deleteAllTasks()
    }
}