package com.example.todolist.toDoListFragment


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.database.TaskRepo
import com.example.todolist.database.Tasks

class ToDoListViewModel: ViewModel() {

    private val taskRepo = TaskRepo.get()

    val liveDataTasks = taskRepo.getAllTasks()

    val livePriority = taskRepo.getPriority()

    val liveUndone = taskRepo.getUndone()

    fun getTasksTagged(tag:String):LiveData<List<Tasks>>{
        return taskRepo.getTag(tag)
    }

    fun addTask(task: Tasks) {
        taskRepo.addTask(task)
    }

    fun deleteAllTasks() {
        taskRepo.deleteAllTasks()
    }
}