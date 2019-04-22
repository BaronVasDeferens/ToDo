package skot.todo.android

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.android.R
import java.util.*
import java.util.concurrent.TimeUnit

private const val taskUrgencyHigh = R.color.red
private const val taskUrgencyMedium = R.color.gold
private const val taskUrgencyLow = R.color.blue_light

class ToDoView(private val toDoItem: ToDoItem) {

    fun getListView(context: Context, viewGroup: ViewGroup): View {

        val inflater = LayoutInflater.from(context)
        val myListView: View = inflater.inflate(R.layout.todo_view, viewGroup, false)

        // Create and adjust the list view
        val taskNameDisplay = myListView.findViewById<TextView>(R.id.taskNameDisplay)
        taskNameDisplay.text = toDoItem.taskName

        val taskDetailDisplay = myListView.findViewById<TextView>(R.id.taskDetailDisplay)
        taskDetailDisplay.text = toDoItem.taskDetail

        val timeView = myListView.findViewById<TextView>(R.id.taskCreatedLabel)
        val dateTime = DateFormat.format("yyyy-MM-dd hh:mm", Date(toDoItem.createdMillis)).toString()
        val composed = context.resources.getString(R.string.task_created_time_default) + " " + dateTime
        timeView.text =  composed

        val taskUrgencyIndicator = myListView.findViewById<LinearLayout>(R.id.taskUrgencyIndicator)
        taskUrgencyIndicator.background = getUrgencyColor(context, toDoItem.taskUrgency)

        val taskUrgencyIcon = myListView.findViewById<ImageView>(R.id.taskUrgencyIcon)
        val iconContent = when (toDoItem.taskType) {
            ToDoItem.TaskType.TASK -> R.drawable.icon_task
            ToDoItem.TaskType.SHOPPING -> R.drawable.icon_shoppingcart
        }

        taskUrgencyIcon.background = context.getDrawable(iconContent)

        if (toDoItem.completedMillis > 0) {
            myListView.findViewById<ImageView>(R.id.checkmark).visibility = View.VISIBLE
        }

        return myListView
    }

    fun getDetailView(context: Context): View {
        val inflater = LayoutInflater.from(context)
        val detailView: View = inflater.inflate(R.layout.todo_view_detail, null, false)

        val taskName = detailView.findViewById<TextView>(R.id.taskNameDisplay)
        taskName.text = toDoItem.taskName
        taskName.background = getUrgencyColor(context, toDoItem.taskUrgency)

        val dateTime = DateFormat.format("yyyy-MM-dd hh:mm", Date(toDoItem.createdMillis)).toString()
        detailView.findViewById<TextView>(R.id.taskCreatedValue).text = dateTime

        if (toDoItem.isComplete()) {
            // Complete: show how long the item  took to complete
            val millisDifference = toDoItem.completedMillis - toDoItem.createdMillis
            val daysDifference = TimeUnit.DAYS.convert(millisDifference, TimeUnit.MILLISECONDS)
            val daysDifferenceLabel = "$daysDifference DAYS"
            detailView.findViewById<TextView>(R.id.taskAgeValue).text = daysDifferenceLabel
            detailView.findViewById<TextView>(R.id.taskAgeLabel).text = context.resources.getString(R.string.completedIn)

        } else {
            // Not complete: show how long the task has been in progress
            val millisDifference = System.currentTimeMillis() - toDoItem.createdMillis
            val daysDifference = TimeUnit.DAYS.convert(millisDifference, TimeUnit.MILLISECONDS)
            val daysDifferenceLabel = "$daysDifference DAYS"
            detailView.findViewById<TextView>(R.id.taskAgeValue).text = daysDifferenceLabel

        }

        detailView.findViewById<TextView>(R.id.taskDetailDisplay).text = toDoItem.taskDetail

        return detailView
    }

    private fun getUrgencyColor(context: Context, taskUrgency: ToDoItem.TaskUrgency): Drawable {
        return when (toDoItem.taskUrgency) {
            ToDoItem.TaskUrgency.HIGH -> context.resources.getDrawable(taskUrgencyHigh, null)
            ToDoItem.TaskUrgency.MEDIUM -> context.resources.getDrawable(taskUrgencyMedium, null)
            ToDoItem.TaskUrgency.LOW -> context.resources.getDrawable(taskUrgencyLow, null)
        }
    }

}