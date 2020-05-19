package com.vieja.bricklist

import android.content.Context
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.util.*


class MainActivity : AppCompatActivity() {

    private var adapter: RecyclerView.Adapter<*>? = null
    private var projectCardsList: List<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        var collapsingToolbar = findViewById(R.id.collapsingToolbar) as CollapsingToolbarLayout
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorAccent));

        val recyclerView = findViewById(R.id.projectsList) as RecyclerView
        recyclerView.setHasFixedSize(true)

        projectCardsList = listOf(
            "test1",
            "test2",
            "test3"
        )

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
