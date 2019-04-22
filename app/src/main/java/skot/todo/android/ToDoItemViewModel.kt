package skot.todo.android


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToDoItemViewModel : ViewModel() {
    val urgencyValueMap = HashMap<ToDoItem.TaskUrgency, Int>()


    private val idToItemMap = HashMap<String, ToDoItem>()
    var updateRequired: MutableLiveData<Boolean> = MutableLiveData()

    init {

        idToItemMap.clear()
        updateRequired.postValue(false)

        // Urgency Values
        // Allow the HIGH urgency tasks to rise to the top
        urgencyValueMap[ToDoItem.TaskUrgency.HIGH] = 1
        urgencyValueMap[ToDoItem.TaskUrgency.MEDIUM] = 2
        urgencyValueMap[ToDoItem.TaskUrgency.LOW] = 3
    }

    fun getToDoItems(): List<ToDoItem> {
        return idToItemMap.values.map { it.copy() }.toList()
    }

    /**
     * Searches data for an item with a matching item id
     * Returns true if the new item is inserted or overwrites an existing value
     */
    fun addOrUpdateItems(items: List<ToDoItem>) {
        for (item in items) {
            val existingItem: ToDoItem? = idToItemMap[item.taskId]

            if (existingItem == null) {
                addItem(item)
                println(">>> adding: ${item.taskName}")
            } else if (item.lastModifiedMillis > existingItem.lastModifiedMillis) {
                addItem(item)
            }
        }
    }

    private fun addItem(newItem: ToDoItem) {
        synchronized(idToItemMap) {
            idToItemMap[newItem.taskId] = newItem
            updateRequired.postValue(true)
        }
    }


}