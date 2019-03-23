package com.example.todoandroid

import kotlinx.serialization.*


@Serializable
data class ToDoItem(val taskName: String, val taskDetail: String, val createdMillis: Long)