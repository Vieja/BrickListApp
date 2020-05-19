package com.vieja.bricklist

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

class ProjectCreationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newproject)

        val toolbar: Toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)
        toolbar.navigationIcon?.mutate()?.let {
            it.setTint(ContextCompat.getColor(this, R.color.colorSecondary))
            toolbar.navigationIcon = it
        }

        toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                onBackPressed()
            }
        })
        val context = this
        val checkButton: Button = findViewById(R.id.checkButton) as Button
        checkButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val dbAccess: DBAccess? = DBAccess.getInstance(context)
                dbAccess!!.open()
                val quotes: List<String> = dbAccess.quotes
                Log.v("hi",quotes[0]+" "+quotes[1]+" "+quotes[2]+" "+quotes[quotes.size-1]+" ")
                dbAccess.close()
            }
        })
    }


}