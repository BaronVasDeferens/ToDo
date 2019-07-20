package skot.todo.android


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.android.R


/**
 * A simple [Fragment] subclass.
 *
 */
class SettingsFragment : DialogFragment() {

    // TODO : Change this to a Preference screen: https://developer.android.com/guide/topics/ui/settings.html

    private lateinit var myView: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        myView = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(R.layout.fragment_settings)
            builder.create()
        } ?: throw IllegalStateException("AW FUCK")

        return myView
    }


    override fun onStart() {
        super.onStart()

        val prefs = activity?.getPreferences(Context.MODE_PRIVATE)

        if (prefs != null) {
            val ipAddress = prefs.getString(getString(R.string.ipAddress), "192.168.1.7")
            val port = prefs.getInt(getString(R.string.port), 12321)
            val interval = prefs.getInt(getString(R.string.interval), 5)

            myView.findViewById<EditText>(R.id.editIpAddress).setText(ipAddress)
            myView.findViewById<EditText>(R.id.editInterval).setText(interval.toString())
        }

        // Setup the "Done" button-- capture the values and store them in Shared Prefs
        myView.findViewById<Button>(R.id.doneButton).setOnClickListener {

            val ipAddress = myView.findViewById<EditText>(R.id.editIpAddress).text.toString()
            val interval = myView.findViewById<EditText>(R.id.editInterval).text.toString().toInt()

            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
            sharedPref!!.edit()
                .putString(getString(R.string.ipAddress), ipAddress)
                .putInt(getString(R.string.interval), interval)
                .apply()

            dismiss()
        }

        // Setup the "Cancel" button
        myView.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dismiss()
        }
    }

}
