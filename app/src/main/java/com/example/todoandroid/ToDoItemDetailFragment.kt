package com.example.todoandroid

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders

class ToDoItemDetailFragment : DialogFragment() {

    private lateinit var toDoItem: ToDoItem

    fun setData(toDoItem: ToDoItem) {
        this.toDoItem = toDoItem
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val detailView: View = ToDoView(toDoItem).getDetailView(context!!)

        detailView.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dismiss()
        }

        detailView.findViewById<Button>(R.id.completeTaskButton).setOnClickListener {
            toDoItem.markCompleted()
//            toDoItemViewModel.addOrUpdateToDoItem(toDoItem)
            dismiss()
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(detailView)

            builder.create()
        } ?: throw IllegalStateException("AW FUCK")
    }


}