package com.example.todolist.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM Task WHERE id = (:id)")
    fun getTask(id: UUID): LiveData<Task?>

    @Update
    fun updateTask(task: Task)

    @Insert
    fun addTask(task: Task)

    @Query("DELETE FROM Task WHERE id = (:id)")
    fun deleteTask(id: UUID)

    @Query("DELETE FROM Task")
    fun deleteAllTasks()
}
