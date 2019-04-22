package skot.todo.android

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.android.R

class ToDoItemDetailFragment : DialogFragment() {

    private lateinit var toDoItem: ToDoItem
    private lateinit var listener: OnToDoItemCreatedListener

    fun setData(toDoItem: ToDoItem) {
        this.toDoItem = toDoItem
    }

    fun setOnToDoItemCreatedListener(listener: OnToDoItemCreatedListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val detailView: View = ToDoView(toDoItem).getDetailView(context!!)

        detailView.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dismiss()
        }

        val taskCompleteButton = detailView.findViewById<Button>(R.id.completeTaskButton)

        taskCompleteButton.text = when (toDoItem.isComplete()) {
            true -> resources.getString(R.string.taskUndoCompleteButtonLabel)
            false -> resources.getString(R.string.taskCompleteButtonLabel)
        }

        taskCompleteButton.setOnClickListener {
            toDoItem.toggleCompletion()
            listener.onNewItemCreated(toDoItem)
            dismiss()
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(detailView)

            builder.create()
        } ?: throw IllegalStateException("AW FUCK")
    }
}


