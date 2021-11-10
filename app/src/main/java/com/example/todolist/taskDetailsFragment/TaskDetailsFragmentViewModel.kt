package com.example.todolist.taskDetailsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.todolist.database.TaskRepo
import com.example.todolist.database.Tasks
import java.util.*

class TaskDetailsFragmentViewModel: ViewModel() {

    private val taskRepo = TaskRepo.get()
    private val taskIdLiveData = MutableLiveData<UUID>()

    var taskLiveData: LiveData<Tasks?> =
        Transformations.switchMap(taskIdLiveData){
            taskRepo.getTask(it)
        }

    fun loadTask(taskId: UUID) {
        taskIdLiveData.value = taskId
    }

    fun safeUpdates(task: Tasks) {
        taskRepo.updateTask(task)
    }

    fun delete(id: UUID) {
        taskRepo.deleteTask(id)
    }
}