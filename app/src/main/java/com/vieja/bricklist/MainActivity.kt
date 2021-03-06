package com.vieja.bricklist

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var adapter: RecyclerView.Adapter<*>? = null
    private var projectCardsList: List<Project> = ArrayList<Project>()
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
        val toolbar: Toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        var collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorAccent))

        addProjectButton.setOnClickListener { view ->
            startActivity(Intent(this, ProjectCreationActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val recyclerView = findViewById<RecyclerView>(R.id.projectsList)
        recyclerView.setHasFixedSize(true)

        val dbAccess: DBAccess? = DBAccess.getInstance(this)
        dbAccess!!.open()
        val showArchived = prefs!!.getBoolean("archive", false)
        if (showArchived)
            projectCardsList = dbAccess.getAllProjects()
        else projectCardsList = dbAccess.getActiveProjects()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ProjectListAdapter(this@MainActivity, projectCardsList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        this.startActivity(Intent(this, SettingsActivity::class.java))
        return true
    }
}
