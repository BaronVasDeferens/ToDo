package com.example.todoandroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.util.Log
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {


    // TODO add data class
    // TODO add serialization from kotlinx
    // TODO configuration page

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

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
                val parsedData = parseData(it)
                updateView(parsedData)
            }
            .doOnError { }
            .subscribe()

    }

    private fun parseData(data: String): ToDoItem {
        Log.d("derp", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        println(data)
        return Json.parse(ToDoItem.serializer(), data)
    }

    private fun updateView(data: ToDoItem) {

        val taskNameDisplay = findViewById<TextView>(R.id.taskNameDisplay)
        taskNameDisplay.text = data.taskName
        
        val timeView = findViewById<TextView>(R.id.dataDisplay)
        val dateTime = DateFormat.format("yyyy MM dd hh:mm:ss", Date(data.createdMillis)).toString()
        timeView.text = dateTime
    }

}
