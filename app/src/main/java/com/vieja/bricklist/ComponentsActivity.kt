package com.vieja.bricklist

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class ComponentsActivity : AppCompatActivity() {

    private var adapter: RecyclerView.Adapter<*>? = null
    private var componentsCardsList: List<Component> = ArrayList<Component>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_components)

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
    }

    override fun onStart() {
        super.onStart()
        val recyclerView = findViewById(R.id.componentsList) as RecyclerView
        recyclerView.setHasFixedSize(true)

        val dbAccess: DBAccess? = DBAccess.getInstance(this)
        dbAccess!!.open()
        componentsCardsList = dbAccess.getComponentsOfProject()

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ComponentsActivity)
            adapter = ComponentListAdapter(this@ComponentsActivity, componentsCardsList)
        }
    }


}