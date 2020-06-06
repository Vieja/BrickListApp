package com.vieja.bricklist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*


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
        componentsCardsList = dbAccess.getComponentsOfProject(intent.getIntExtra("ProjectID",0))

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ComponentsActivity)
            adapter = ComponentListAdapter(this@ComponentsActivity, componentsCardsList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        var active = checkIfActive()
        if (active == 0) menu!!.findItem(R.id.menuIsActive).setTitle("Activate project")
        else menu!!.findItem(R.id.menuIsActive).setTitle("Deactivate project")
        return true
    }

    private fun checkIfActive(): Int {
        val dbAccess: DBAccess? = DBAccess.getInstance(this)
        dbAccess!!.open()
        return dbAccess.checkIfActive(intent.getIntExtra("ProjectID",0))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.getItemId()) {
            R.id.menuIsActive -> {
                var previous = deactivateProject()
                if (previous == 0) item.title = "Deactivate project"
                else item.title = "Activate project"
                true
            }
            R.id.menuExport -> {
                exportToXML()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun exportToXML() {
        val dbAccess: DBAccess? = DBAccess.getInstance(this)
        dbAccess!!.open()
        var xmlek = dbAccess.getXML(intent.getIntExtra("ProjectID",0))
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com")
        intent.putExtra(Intent.EXTRA_SUBJECT, "[BrickLink] Missing parts")
        intent.putExtra(Intent.EXTRA_TEXT, xmlek)
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

    private fun deactivateProject(): Int {
        val dbAccess: DBAccess? = DBAccess.getInstance(this)
        dbAccess!!.open()
        return dbAccess.deactivateProject(intent.getIntExtra("ProjectID",0))
    }


}