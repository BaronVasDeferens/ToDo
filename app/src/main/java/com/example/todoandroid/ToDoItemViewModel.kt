package com.example.todoandroid


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToDoItemViewModel: ViewModel() {

    val urgencyValueMap = HashMap<ToDoItem.TaskUrgency, Int>()

    val toDoItems = MutableLiveData<List<ToDoItem>>()

    init {
        toDoItems.value = listOf()

        // Urgency Values
        // Allow the HIGH urgency tasks to rise to the top
        urgencyValueMap[ToDoItem.TaskUrgency.HIGH] = 1
        urgencyValueMap[ToDoItem.TaskUrgency.MEDIUM] = 2
        urgencyValueMap[ToDoItem.TaskUrgency.LOW] = 3
    }

    fun updateValues(newList: List<ToDoItem>) {
        toDoItems.postValue(newList)
    }

}