package com.example.todoandroid


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToDoItemViewModel : ViewModel() {

    val urgencyValueMap = HashMap<ToDoItem.TaskUrgency, Int>()
    private val idToItemMap = HashMap<String, ToDoItem>()

    val toDoItems = MutableLiveData<List<ToDoItem>>()

    init {
        toDoItems.value = listOf()
        idToItemMap.clear()

        // Urgency Values
        // Allow the HIGH urgency tasks to rise to the top
        urgencyValueMap[ToDoItem.TaskUrgency.HIGH] = 1
        urgencyValueMap[ToDoItem.TaskUrgency.MEDIUM] = 2
        urgencyValueMap[ToDoItem.TaskUrgency.LOW] = 3
    }

    /**
     * Receives a list (from server)
     */
    fun updateValues(newList: List<ToDoItem>) {

        var requiresUpdate = false
        for(item: ToDoItem in newList) {
            // careful! short-circuit! put method first
            requiresUpdate = addOrUpdateToDoItem(item) || requiresUpdate
        }

        if (requiresUpdate){
            postUpdate()
        }
    }


    /**
     * Searches data for an item with a matching item id
     * Returns true if the new item is inserted or overwrites an existing value
     */
    fun addOrUpdateToDoItem(newItem: ToDoItem): Boolean {

        val existingItem = idToItemMap[newItem.taskId]

        if (existingItem == null) {
            addItem(newItem)
            println(">>> adding: ${newItem.taskName}")
            return true
        } else if (newItem != existingItem) {
            if (newItem.completedMillis > existingItem.completedMillis) {
                addItem(newItem)
                return true
            }
        }

        return false
    }

    private fun addItem(newItem: ToDoItem) {
        idToItemMap[newItem.taskId] = newItem
    }

    fun postUpdate() {
        toDoItems.postValue(idToItemMap.values.toList())
    }

}