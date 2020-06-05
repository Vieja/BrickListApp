package com.vieja.bricklist

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ComponentListAdapter(private val context: Context, private val componentsList: List<Component>) : RecyclerView.Adapter<ComponentListAdapter.MainHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = MainHolder(LayoutInflater.from(parent.context).inflate(R.layout.component_card, parent, false))

    override fun getItemCount() = componentsList.size

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.bind(componentsList[position])

        holder.plus.setOnClickListener { view ->
            //context.startActivity(Intent(context,SettingsActivity::class.java))
            val dbAccess: DBAccess? = DBAccess.getInstance(context)
            dbAccess!!.open()
            val result = dbAccess.updateQuantity(true, holder.id)
            holder.count.text = result
        }

        holder.minus.setOnClickListener { view ->
            val dbAccess: DBAccess? = DBAccess.getInstance(context)
            dbAccess!!.open()
            val result = dbAccess.updateQuantity(false, holder.id)
            holder.count.text = result
        }
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.partName)
        val color = itemView.findViewById<TextView>(R.id.partColor)
        val count = itemView.findViewById<TextView>(R.id.partCount)
        var image = itemView.findViewById<ImageView>(R.id.partImage)
        var plus = itemView.findViewById<TextView>(R.id.addItem)
        var minus = itemView.findViewById<TextView>(R.id.subItem)
        var id : Int = 0

        fun bind(pr: Component) {
            name.text = pr.name
            color.text = pr.color
            count.text = pr.count
            if (pr.byteArray != null) {
                image.setImageBitmap(BitmapFactory.decodeByteArray(pr.byteArray, 0,pr.byteArray.size))
            }
            id = pr.id
        }

    }

}