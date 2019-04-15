package com.example.todoandroid

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import java.util.*


class CreateNewItemFragment : DialogFragment() {

    private lateinit var itemCreatedListener: OnToDoItemCreatedListener
    private lateinit var myView: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        myView = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(R.layout.add_item_dialog)
            builder.create()
        } ?: throw IllegalStateException("AW FUCK")

        return myView
    }

    fun setOnItemCreatedListener(listener: OnToDoItemCreatedListener) {
        itemCreatedListener = listener
    }

    override fun onStart() {
        super.onStart()

        val taskTypeList = ToDoItem.TaskType.values().map { it.name }.toMutableList()
        taskTypeList.sort()
        val taskTypeAdapter =
            ArrayAdapter<String>(context!!, R.layout.spinner_item_custom, taskTypeList)
        myView.findViewById<Spinner>(R.id.selectTaskType).adapter = taskTypeAdapter

        val urgencyList = ToDoItem.TaskUrgency.values().map { it.name }.toMutableList()
        val urgencyAdapter = ArrayAdapter<String>(context!!, R.layout.spinner_item_custom, urgencyList)
        myView.findViewById<Spinner>(R.id.selectTaskUrgency).adapter = urgencyAdapter


        // Setup the "Done" button-- add a task and close the dialog
        myView.findViewById<Button>(R.id.addTaskAndDone).setOnClickListener {
            itemCreatedListener.onNewItemCreated(createToDoItem())
            dismiss()
        }

        // Setup the "Add Another" button-- add the item and keep going
        myView.findViewById<Button>(R.id.addTaskAndContinueButton).setOnClickListener {
            itemCreatedListener.onNewItemCreated(createToDoItem())
        }

        // Setup the "Cancel" button
        myView.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dismiss()
        }

    }

    private fun createToDoItem(): ToDoItem {
        return ToDoItem(
            UUID.randomUUID().toString(),
            myView.findViewById<EditText>(R.id.editItemName).text.toString(),
            myView.findViewById<EditText>(R.id.editItemDesc).text.toString(),
            ToDoItem.TaskType.valueOf(myView.findViewById<Spinner>(R.id.selectTaskType).selectedItem.toString()),
            ToDoItem.TaskUrgency.valueOf(myView.findViewById<Spinner>(R.id.selectTaskUrgency).selectedItem.toString()),
            System.currentTimeMillis()
        )
    }
}