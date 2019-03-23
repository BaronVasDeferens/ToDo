package com.example.todoandroid

import kotlinx.serialization.*
import java.util.*

@Serializable
data class ToDoItem(
    val taskId: String = UUID.randomUUID().toString(),
    val taskName: String,
    val taskDetail: String,
    val taskUrgency: UrgencyLevel = UrgencyLevel.MEDIUM,
    val createdMillis: Long,
    val completedMillis: Long = 0L
    ) {

    enum class UrgencyLevel {
        LOW,
        MEDIUM,
        HIGH
    }
}