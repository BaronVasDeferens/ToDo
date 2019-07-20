package skot.todo.android


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.ConcurrentHashMap

class ToDoItemViewModel : ViewModel() {
    val urgencyValueMap = HashMap<ToDoItem.TaskUrgency, Int>()

    private val expirationMillis = 60 * 1000

    private val idToItemMap = ConcurrentHashMap<String, ToDoItem>()
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
        synchronized(idToItemMap) {
            return idToItemMap.values.map { it.copy() }.toList()
        }
    }

    /**
     * Searches data for an item with a matching item id
     */
    fun addOrUpdateItems(items: List<ToDoItem>) {

        synchronized(idToItemMap) {
            // Clear away expired items
            // Only works thanks to ConcurrentHashMap-- should I not do this?
            idToItemMap.values.forEach {
                if (isExpired(it)) voidItem(it)
            }

            for (item in items) {
                if (!isExpired(item)) {
                    val existingItem: ToDoItem? = idToItemMap[item.taskId]
                    if (existingItem == null) {
                        println(">>> ADDING NEW ITEM: ${item.taskName}")
                        addItem(item)
                    } else if (item.lastModifiedMillis > existingItem.lastModifiedMillis) {   // and is not expired (complted + expTime)
                        println(">>> UPDATING: ${item.taskName}")
                        addItem(item)
                    }
                }
            }
        }
    }

    private fun addItem(newItem: ToDoItem) {
        synchronized(idToItemMap) {
            idToItemMap[newItem.taskId] = newItem
            updateRequired.postValue(true)
        }
    }

    private fun voidItem(itemToVoid: ToDoItem) {
        synchronized(idToItemMap) {
            idToItemMap.remove(itemToVoid.taskId)
            updateRequired.postValue(true)
        }
    }

    private fun isExpired(item: ToDoItem): Boolean {
        return (item.completedMillis > 0L && item.completedMillis + expirationMillis < System.currentTimeMillis())
    }

}