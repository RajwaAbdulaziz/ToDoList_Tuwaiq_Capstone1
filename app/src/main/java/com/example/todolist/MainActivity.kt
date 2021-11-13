package com.example.todolist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.todolist.toDoListFragment.CHANNEL_ID
import com.example.todolist.toDoListFragment.CHANNEL_NAME
import com.example.todolist.toDoListFragment.ToDoListFragment

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment == null) {
            val fragment = ToDoListFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container_view, fragment).commit()
        }
    }


}