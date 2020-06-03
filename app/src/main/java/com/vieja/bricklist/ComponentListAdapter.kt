package com.vieja.bricklist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ComponentListAdapter(private val context: Context, private val componentsList: List<Component>) : RecyclerView.Adapter<ComponentListAdapter.MainHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.component_card, parent, false))

    override fun getItemCount() = componentsList.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(componentsList[position])

        holder.name.setOnClickListener { view ->
            context.startActivity(Intent(context,SettingsActivity::class.java))
        }
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.partName)
        val color = itemView.findViewById<TextView>(R.id.partColor)
        val count = itemView.findViewById<TextView>(R.id.partCount)
        var id : Int = 0

        fun bind(pr: Component) {
            name.text = pr.name
            color.text = pr.color
            count.text = pr.count
            id = pr.id
        }

    }

}