package com.vieja.bricklist

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class ProjectListAdapter (private val context: Context, private val projectsList: List<String>) : RecyclerView.Adapter<ProjectListAdapter.MainHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.project_card, parent, false))

    override fun getItemCount() = projectsList.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(projectsList[position])

        holder.firstName.setOnClickListener { view ->
            context.startActivity(Intent(context,SettingsActivity::class.java))
        }
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val firstName = itemView.findViewById<TextView>(R.id.projectName)

        fun bind(name: String) {
            firstName.text = name
        }

    }

}