package com.example.todolist.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Tasks(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var taskTitle: String = "",
    var dueDate: Date? = null,
    var currentDate: Date? = Date(),
    var isDone: Boolean = false,
    var description: String = "",
    var priority: Int = 0,
    var tag: String = ""
)
