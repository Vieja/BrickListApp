package com.vieja.bricklist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectListAdapter(private val context: Context, private val projectsList: List<Project>) : RecyclerView.Adapter<ProjectListAdapter.MainHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.project_card, parent, false))

    override fun getItemCount() = projectsList.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(projectsList[position])

        holder.name.setOnClickListener { view ->
            val intent = Intent(context,ComponentsActivity::class.java)
            intent.putExtra("ProjectID",holder.id)
            context.startActivity(intent)
        }
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.projectName)
        var id : Int = 0

        fun bind(pr: Project) {
            name.text = pr.name
            id = pr.id
        }

    }

}