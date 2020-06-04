package com.vieja.bricklist

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_newproject.*
import java.net.HttpURLConnection
import java.net.URL


class ProjectCreationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newproject)

        //ustawienie własnego toolbara
        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon?.mutate()?.let {
            it.setTint(ContextCompat.getColor(this, R.color.colorSecondary))
            toolbar.navigationIcon = it
        }
        //przycisk powrotu do poprzedniego okna
        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                onBackPressed()
            }
        })

        val view = constraintLayout
        val context = this

        //przycisk ADD
        val addButton: Button = findViewById(R.id.addButton) as Button
        addButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val id = idInputEditText.text.toString()
                val name = nameInputEditText.text.toString()
                hideKeyboard(this@ProjectCreationActivity)
                if (id == "") {
                    Snackbar.make(view, "ID cannot be empty!", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                } else if (name == "") {
                    Snackbar.make(view, "Name cannot be empty!", Snackbar.LENGTH_LONG).setAction("Action", null).show()
                } else {
                    val downloadData = DownloadData(view, context, id.toInt(), name)
                    downloadData.execute("http://fcds.cs.put.poznan.pl/MyWeb/BL/" + id + ".xml")
                }
            }
        })
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private class DownloadData (val view: View, val context: Context, val id: Int, val name: String) : AsyncTask<String, Void, String>() {
            private val TAG = "DownloadData"
            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                if (result == "" || result == null) {
                    Snackbar.make(view, "Cannot find set with given ID!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
                else {//Log.wtf(TAG, "onPostExecute is $result")
                    val dbAccess: DBAccess? = DBAccess.getInstance(context)
                    dbAccess!!.open()
                    val projectID = dbAccess.addProject(id, name)
                    if (projectID == -1L) {
                        dbAccess.close()
                        Snackbar.make(view, "This set already exists in your project list!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    } else {
                        var notAdded = dbAccess.addComponents(projectID, result)
                        dbAccess.close()
                        if (notAdded.isNotEmpty()) {
                            var i=0
                            while(i<notAdded.size) {
                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("Androidly Alert")
                                builder.setMessage("Cannot add unknown parts, ItemID: "+notAdded.get(i)+", ColorID: "+notAdded.get(i+1))
                                if(i+2 == notAdded.size) {
                                    builder.setPositiveButton(android.R.string.yes) { dialog, which -> endActivity(context) }
                                } else {
                                    builder.setPositiveButton(android.R.string.yes) { dialog, which -> }
                                }
                                val dialog: AlertDialog = builder.create()
                                dialog.show()
                                i+=2
                            }
                        }

                    }
                }
            }

            private fun endActivity(context: Context) {
                val ac : Activity = context as Activity
                ac.finish()
            }

            override fun doInBackground(vararg url: String?): String {
                Log.wtf(TAG, "doInBackground: starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: Error Downloading")
                }
                return rssFeed
            }

            private fun downloadXML(urlPath: String?): String {
                val url = URL(urlPath)
                try {
                    val huc: HttpURLConnection = url.openConnection() as HttpURLConnection
                    val responseCode: Int = huc.getResponseCode()
                    if (responseCode.equals(HttpURLConnection.HTTP_NOT_FOUND)) return ""
                } catch (e: Exception) {
                    return ""
                }
                return url.readText()
            }
        }
    }
}