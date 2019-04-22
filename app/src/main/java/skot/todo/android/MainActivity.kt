package skot.todo.android

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.text.format.DateFormat
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.android.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.json.Json
import kotlinx.serialization.list
import java.net.Socket
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), OnToDoItemCreatedListener {

    private var taskTypeFilter: ToDoItem.TaskType? = ToDoItem.TaskType.SHOPPING

    private lateinit var toDoItemViewModel: ToDoItemViewModel
    private var connectToServer = false

    private val updateObservable = Observable.create<String> { subscriber ->
        println(">>> Starting server comms...")
        while (true) {
            if (connectToServer) {
                try {
                    val socket = Socket("192.168.1.7", 12321)
                    subscriber.onNext(socket.getInputStream().readBytes().toString(Charset.defaultCharset()))
                    socket.close()
                } catch (e: java.lang.Exception) {
                    println(">>> Oopsie-daisy! $e")
                }
            }
            Thread.sleep(1000)
        }

    }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext {
            synchronized(toDoItemViewModel) {
                toDoItemViewModel.addOrUpdateItems(parseData(it))
            }
            updateLastUpdatedTime(System.currentTimeMillis())
        }
        .doOnDispose {
            println(">>> All done...")
        }
        .doOnError {
            println(">>> Uh-oh! ${it.message}")
        }


    // Observer on the ViewModel/LiveData
    // Updates the UI when changes to the data are detected
    private val toDoListObserver = Observer<Boolean> {

        val mainDisplay = findViewById<LinearLayout>(R.id.mainDisplay)
        mainDisplay.removeAllViews()
        toDoItemViewModel.getToDoItems()
            .filter {
                when (taskTypeFilter) {
                    null -> true
                    else -> {
                        it.taskType == taskTypeFilter
                    }
                }
            }
            .sortedWith(compareBy { toDoItemViewModel.urgencyValueMap[it.taskUrgency] })
            .forEach { toDoItem ->
                val toDoView = ToDoView(toDoItem)
                val listView = toDoView.getListView(this, mainDisplay)

                listView.setOnLongClickListener { view ->
                    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(100)
                    showItemDetailFragment(toDoItem)
                    true
                }

                listView.findViewById<View>(R.id.taskSecondaryLayout)
                    .setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                vibrator.vibrate(500)
                                true
                            }
                            else -> {
                                false
                            }
                        }
                    }

                mainDisplay.addView(listView)
            }
    }


    // TODO configuration page


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toDoItemViewModel = ViewModelProviders.of(this).get(ToDoItemViewModel::class.java)
        toDoItemViewModel.updateRequired.observe(this, toDoListObserver)
        updateObservable.subscribe()

        // Populate the task filter
        val taskTypeList = ToDoItem.TaskType.values().map { it.name }.toMutableList()
        taskTypeList.add("ALL")
        taskTypeList.sort()

        val taskTypeAdapter =
            ArrayAdapter<String>(this, R.layout.spinner_item_custom, taskTypeList)
        val filterTaskTypeSpinner = findViewById<Spinner>(R.id.filterByTaskType)
        filterTaskTypeSpinner.adapter = taskTypeAdapter
        filterTaskTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                taskTypeFilter =
                    try {
                        ToDoItem.TaskType.valueOf(parent?.selectedItem.toString())
                    } catch (e: Exception) {
                        null
                    }
                toDoItemViewModel.updateRequired.postValue(true)
            }

        }

    }

    override fun onResume() {
        super.onResume()
        connectToServer = true
    }

    override fun onStop() {
        super.onStop()
        connectToServer = false

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
                try {
                    val socket = Socket("192.168.1.7", 12322)
                    val outputStream = socket.getOutputStream()
                    val payloadContent = Json.stringify(ToDoItem.serializer().list, toDoItemViewModel.getToDoItems())
                    outputStream.write(payloadContent.toByteArray())
                    socket.close()
                } catch (e: Exception) {

                }

            }
        }
    }
}
