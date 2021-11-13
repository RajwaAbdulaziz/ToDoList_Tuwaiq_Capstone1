package com.example.todolist.database

import androidx.room.Room
import android.content.Context
import androidx.lifecycle.LiveData
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "task_database"

class TaskRepo private constructor(context: Context) : TaskDao {


    private val database: TaskDatabase = Room.databaseBuilder(
        context.applicationContext,
        TaskDatabase::class.java,
        DATABASE_NAME,
    ).build()

    private val taskDao = database.taskDao()

    private val executor = Executors.newSingleThreadExecutor()

    override fun getAllTasks(): LiveData<List<Task>> = taskDao.getAllTasks()

    override fun getTask(id: UUID): LiveData<Task?> {
        return taskDao.getTask(id)
    }

    override fun updateTask(task: Task) {
        executor.execute {
            taskDao.updateTask(task)
        }
    }

    override fun addTask(task: Task) {
        executor.execute {
            taskDao.addTask(task)
        }
    }

    override fun deleteTask(id: UUID) {
        executor.execute{
            taskDao.deleteTask(id)
        }
    }

    override fun deleteAllTasks() {
        executor.execute{
            taskDao.deleteAllTasks()
        }
    }

    companion object {
        var INSTANCE: TaskRepo? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TaskRepo(context)
            }
        }

        fun get(): TaskRepo {
            return INSTANCE ?:
            throw IllegalStateException("TaskRepo must be initialized")
        }
    }
}
