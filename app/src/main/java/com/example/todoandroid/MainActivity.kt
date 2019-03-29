package com.example.todoandroid

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.net.Socket
import java.nio.charset.Charset
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var toDoItemViewModel: ToDoItemViewModel

    // TODO configuration page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toDoItemViewModel = ViewModelProviders.of(this).get(ToDoItemViewModel::class.java)

        // Observer on the ViewModel/LiveData
        // Updates the UI when
        val toDoListObserver = Observer<List<ToDoItem>> {

            val mainDisplay = findViewById<LinearLayout>(R.id.mainDisplay)
            mainDisplay.removeAllViews()

            toDoItemViewModel.toDoItems.value
                ?.sortedWith( compareBy { toDoItemViewModel.urgencyValueMap[it.taskUrgency] })
                ?.forEach { data ->
                val taskView = ToDoView(this, mainDisplay, data).getListView()

                taskView.findViewById<LinearLayout>(R.id.taskPrimaryLayout)
                    .setOnLongClickListener {
                        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.vibrate(200)
                        true
                    }

                taskView.findViewById<LinearLayout>(R.id.taskSecondaryLayout)
                    .setOnLongClickListener {
                        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.vibrate(500)
                        true
                    }

                mainDisplay.addView(taskView)
            }
        }

        // Watch the list of items and update the views as they cahnge
        toDoItemViewModel.toDoItems.observe(this, toDoListObserver)
    }

    override fun onStart() {
        super.onStart()

        // RxJava
        // Fires a regularly repeating request to the server for data
        // and pushes data to the ViewModel for this activity
        Observable.create<String> { subscriber ->
            Executors.newSingleThreadScheduledExecutor()!!.scheduleAtFixedRate({
                val socket = Socket("192.168.1.7", 12321)
                val inputReader = socket.getInputStream()
                subscriber.onNext(inputReader.readBytes().toString(Charset.defaultCharset()))
                socket.close()
            }, 0, 1, TimeUnit.SECONDS)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                toDoItemViewModel.toDoItems.postValue(parseData(it))
            }
            .doOnError {

            }
            .subscribe()

    }

    private fun parseData(data: String): List<ToDoItem> {
        return Json.parse(ToDoItem.serializer().list, data)
    }
}
