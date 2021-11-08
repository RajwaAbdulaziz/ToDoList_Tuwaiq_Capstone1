package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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