package com.example.todoandroid

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ToDoItem(
    val taskId: String = UUID.randomUUID().toString(),
    val taskName: String,
    val taskDetail: String,
    val taskType: TaskType = TaskType.TASK,
    val taskUrgency: TaskUrgency = TaskUrgency.MEDIUM,
    val createdMillis: Long,
    var completedMillis: Long = 0L,
    var lastModifiedMillis: Long = 0L
) {

    enum class TaskType {
        TASK,
        SHOPPING;
    }

    enum class TaskUrgency {
        LOW,
        MEDIUM,
        HIGH
    }

    fun toggleCompletion() {
        completedMillis = if (completedMillis == 0L) {
            System.currentTimeMillis()
        } else {
            0L
        }
        lastModifiedMillis = System.currentTimeMillis()
        println(">>> $taskName: Complete? ${isComplete()}")
    }

    fun isComplete(): Boolean {
        return completedMillis > 0
    }


}