package com.saltwort.cotizadorgasto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saltwort.cotizadorgasto.database.EntityRecord

class Adapter(private val list : List<EntityRecord>) : RecyclerView.Adapter<Adapter.MyViewHolder>() {

    // Clase ViewHolder para manejar los elementos del RecyclerView
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textBalance: TextView = itemView.findViewById(R.id.textBalance)
        val textSign : TextView = itemView.findViewById(R.id.textSign)
        val textDate : TextView = itemView.findViewById(R.id.textDate)
        val textDetail : TextView = itemView.findViewById(R.id.textDetail)
    }

    // Crear nuevos elementos de vista (invocado por el LayoutManager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_balance, parent, false)
        return MyViewHolder(view)
    }

    // Reemplazar el contenido de una vista (invocado por el LayoutManager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        holder.textBalance.text = model.balance.toString()
        holder.textSign.text = model.sign
        holder.textDate.text = model.date
        holder.textDetail.text = model.detail
    }

    // Retornar el tamaño del dataset (invocado por el LayoutManager)
    override fun getItemCount(): Int {
        return list.size
    }
}