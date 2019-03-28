package com.example.todoandroid


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToDoItemViewModel: ViewModel() {

    val toDoItems = MutableLiveData<List<ToDoItem>>()

    init {
        toDoItems.value = listOf()
    }

}