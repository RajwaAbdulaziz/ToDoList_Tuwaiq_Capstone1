package com.example.todolist.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM Tasks")
    fun getAllTasks(): LiveData<List<Tasks>>

    @Query("SELECT * FROM Tasks WHERE id = (:id)")
    fun getTask(id: UUID): LiveData<Tasks?>

    @Update
    fun updateTask(task: Tasks)

    @Insert
    fun addTask(task: Tasks)

    @Query("DELETE FROM Tasks WHERE id = (:id)")
    fun deleteTask(id: UUID)
}