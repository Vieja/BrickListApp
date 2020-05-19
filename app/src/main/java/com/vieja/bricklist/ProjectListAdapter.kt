package com.vieja.bricklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectListAdapter (var items: List<String>) : RecyclerView.Adapter<ProjectListAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.project_card, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val firstName = itemView.findViewById<TextView>(R.id.projectName)

        fun bind(name: String) {
            firstName.text = name
            itemView.setOnClickListener {
               // if (adapterPosition != RecyclerView.NO_POSITION)
            }
        }
    }

//    interface Callback {
//        fun onItemClicked(name: String)
//    }

}