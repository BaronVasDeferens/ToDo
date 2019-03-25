package com.example.todoandroid

import android.content.Context
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

private val taskUrgencyHigh = R.color.red
private val taskUrgencyMedium = R.color.gold
private val taskUrgencyLow = R.color.blue_light

class ToDoView: LinearLayout {

    private lateinit var toDoItem: ToDoItem
    private lateinit var myListView: View           // View as a item in a list
    private lateinit var myDetailView: View

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)

    constructor(context: Context, viewGroup: ViewGroup, toDoItem: ToDoItem): super(context) {
        this.toDoItem = toDoItem
        val inflater = LayoutInflater.from(context)
        myListView = inflater.inflate(R.layout.todo_view, viewGroup, false)

        // Create and adjust the list view
        val taskNameDisplay = myListView.findViewById<TextView>(R.id.taskNameDisplay)
        taskNameDisplay.text = toDoItem.taskName

        val taskDetailDisplay = myListView.findViewById<TextView>(R.id.taskDetailDisplay)
        taskDetailDisplay.text = toDoItem.taskDetail

        val timeView = myListView.findViewById<TextView>(R.id.taskCreatedDisplay)
        val dateTime = DateFormat.format("yyyy-MM-dd hh:mm", Date(toDoItem.createdMillis)).toString()
        val composed = resources.getString(R.string.task_created_time_default) + " " + dateTime
        timeView.text =  composed

        val taskUrgencyIndicator = myListView.findViewById<LinearLayout>(R.id.taskUrgencyIndicator)
        taskUrgencyIndicator.background = when (toDoItem.taskUrgency) {
            ToDoItem.TaskUrgency.HIGH -> resources.getDrawable(taskUrgencyHigh, null)
            ToDoItem.TaskUrgency.MEDIUM -> resources.getDrawable(taskUrgencyMedium, null)
            ToDoItem.TaskUrgency.LOW -> resources.getDrawable(taskUrgencyLow, null)
        }

        val taskUrgencyIcon = myListView.findViewById<ImageView>(R.id.taskUrgencyIcon)
        val iconContent = when (toDoItem.taskType) {
            ToDoItem.TaskType.TASK -> R.drawable.icon_task
            ToDoItem.TaskType.SHOPPING -> R.drawable.icon_shoppingcart
        }

        taskUrgencyIcon.background =  context.getDrawable(iconContent)


        // Create and adjust the detail view
        // TODO
    }

    fun getListView(): View {
        return myListView
    }

    fun getDetailView() {

    }
}