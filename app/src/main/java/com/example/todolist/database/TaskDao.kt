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

    @Query("DELETE FROM Tasks")
    fun deleteAllTasks()

    @Query("SELECT * FROM TASKS ORDER BY priority")
    fun getPriority(): LiveData<List<Tasks>>

    @Query("SELECT * FROM TASKS WHERE tag = (:tag)")
    fun getTag(tag: String): LiveData<List<Tasks>>

    @Query("SELECT * FROM TASKS WHERE isDone = 0")
    fun getUndone() : LiveData<List<Tasks>>
}
