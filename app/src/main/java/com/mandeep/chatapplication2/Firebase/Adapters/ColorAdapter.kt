package com.mandeep.chatapplication2.Firebase.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mandeep.chatapplication2.R

class ColorAdapter(val context: Context,val list:List<Int>):RecyclerView.Adapter<ColorAdapter.MyViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
      val view = LayoutInflater.from( context).inflate(R.layout.color_item,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

      //  holder.colorView.setCardBackgroundColor(list[position])
        holder.colorView.setBackgroundResource(list[position])

        holder.itemView.setOnClickListener {
            customClickListenr.colorClick(list[position])
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val colorCardView = itemView.findViewById<CardView>(R.id.colorCardView)
        val colorView = itemView.findViewById<View>(R.id.colorView)
    }

    interface CustomClickListenr{
        fun colorClick(color:Int)
    }

    lateinit var customClickListenr: CustomClickListenr
    fun setCustomClcikCListern(customClickListenrr: CustomClickListenr){
        customClickListenr=  customClickListenrr
    }

}