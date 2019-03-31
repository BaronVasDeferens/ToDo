package com.example.todoandroid

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment

class ToDoItemDetailFragment: DialogFragment() {

    private lateinit var toDoItem: ToDoItem
    private lateinit var detailView: View

    fun setData(toDoItem: ToDoItem, detailView: View) {
        this.toDoItem = toDoItem
        this.detailView = detailView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(detailView)
                .setPositiveButton("Yep", DialogInterface.OnClickListener { dialog, id ->
                    // FIRE ZE MISSILES!
                })
                .setNegativeButton("Nope", DialogInterface.OnClickListener { dialog, id ->
                    // FIRE ZE MISSILES!
                })
            builder.create()
        } ?: throw IllegalStateException("AW FUCK")
    }
}