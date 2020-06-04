package com.vieja.bricklist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var adapter: RecyclerView.Adapter<*>? = null
    private var projectCardsList: List<Project> = ArrayList<Project>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        var collapsingToolbar = findViewById(R.id.collapsingToolbar) as CollapsingToolbarLayout
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorAccent));

        addProjectButton.setOnClickListener { view ->
            startActivity(Intent(this,ProjectCreationActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val recyclerView = findViewById(R.id.projectsList) as RecyclerView
        recyclerView.setHasFixedSize(true)

        val dbAccess: DBAccess? = DBAccess.getInstance(this)
        dbAccess!!.open()
        projectCardsList = dbAccess.getActiveProjects()

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ProjectListAdapter(this@MainActivity, projectCardsList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }
}
