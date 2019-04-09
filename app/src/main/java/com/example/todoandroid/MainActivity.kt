package com.example.todoandroid

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.text.format.DateFormat
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), OnToDoItemCreatedListener {

    private lateinit var toDoItemViewModel: ToDoItemViewModel

    private lateinit var dispo: Disposable

    private val updateObservable = Observable.create<String> { subscriber ->
        Executors.newSingleThreadScheduledExecutor()!!.scheduleAtFixedRate({
            val socket = Socket("192.168.1.7", 12321)
            val inputReader = socket.getInputStream()
            subscriber.onNext(inputReader.readBytes().toString(Charset.defaultCharset()))
            socket.close()
        }, 0, 1, TimeUnit.SECONDS)
    }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext {
            synchronized(toDoItemViewModel) {
                toDoItemViewModel.addOrUpdateItems(parseData(it))
            }
            updateLastUpdatedTime(System.currentTimeMillis())
        }
        .doOnDispose {
            println(">>> All done...") }
        .doOnError {

        }

    // Observer on the ViewModel/LiveData
    // Updates the UI when changes to the data are detected
    private val toDoListObserver = Observer<Boolean> {

        val mainDisplay = findViewById<LinearLayout>(R.id.mainDisplay)
        mainDisplay.removeAllViews()
        toDoItemViewModel.getToDoItems()
            ?.sortedWith(compareBy { toDoItemViewModel.urgencyValueMap[it.taskUrgency] })
            ?.forEach { toDoItem ->
                val toDoView = ToDoView(toDoItem)
                val listView = toDoView.getListView(this, mainDisplay)

                listView.setOnClickListener {
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(200)
                    showItemDetailFragment(toDoItem)
                    true
                }

                listView.findViewById<LinearLayout>(R.id.taskSecondaryLayout)
                    .setOnClickListener {
                        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.vibrate(500)
                        true
                    }

                mainDisplay.addView(listView)
            }
    }


    // TODO configuration page



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toDoItemViewModel = ViewModelProviders.of(this).get(ToDoItemViewModel::class.java)

    }

    override fun onResume() {
        super.onResume()
        println(">>> onResume...")
        // Watch the list of items and update the views as they change
        toDoItemViewModel.updateRequired.observe(this, toDoListObserver)
        dispo = updateObservable.subscribe()
    }

    override fun onStop() {
        println(">>> onStop...")
        super.onStop()
        dispo.dispose()     // FIXME: this doesn't actually stop the Observable (or rather, the thread inside)

    }

    private fun parseData(data: String): List<ToDoItem> {
        return Json.parse(ToDoItem.serializer().list, data)
    }

    private fun updateLastUpdatedTime(millis: Long) {
        val dateTime = DateFormat.format("yyyy-MM-dd hh:mm:ss", Date(millis)).toString()
        val completedString = resources.getString(R.string.lastSync) + " " + dateTime
        findViewById<TextView>(R.id.lastUpdated).text = completedString
    }

    private fun showItemDetailFragment(toDoItem: ToDoItem) {
        val detailFragment = ToDoItemDetailFragment()
        detailFragment.setData(toDoItem)
        detailFragment.setOnToDoItemCreatedListener(this)
        detailFragment.show(supportFragmentManager, "derp")
    }

    fun addItem(view: View) {
        val createNewItemFragment = CreateNewItemFragment()
        createNewItemFragment.setOnItemCreatedListener(this)
        createNewItemFragment.show(supportFragmentManager, "DERP")
    }

    override fun onNewItemCreated(newItem: ToDoItem) {
        toDoItemViewModel.addOrUpdateItems(listOf(newItem))
        sendDataToServer()
    }

    fun sendDataToServer() {
        Executors.newSingleThreadExecutor()!!.execute {
            synchronized(toDoItemViewModel) {
                val socket = Socket("192.168.1.7", 12322)
                val outputStream = socket.getOutputStream()
                val payloadContent = Json.stringify(ToDoItem.serializer().list, toDoItemViewModel.getToDoItems())
                outputStream.write(payloadContent.toByteArray())
                socket.close()
            }
        }
    }
}
