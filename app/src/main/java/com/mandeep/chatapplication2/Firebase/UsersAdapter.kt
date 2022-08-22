package com.mandeep.chatapplication2.Firebase

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Adapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mandeep.chatapplication2.R

class UsersAdapter(val context:Context):RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {

   inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        val anim = AnimationUtils.loadAnimation(context,R.anim.alpha_animation)
        init {
            itemView.animation = anim
        }
        val textView= itemView.findViewById<TextView>(R.id.textViewUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item,parent,false))
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = "Username : ${arrlist[position].username} \n UserId : ${arrlist[position].userId} "
        holder.itemView.setOnClickListener {
            customOnClickListener.onCustomClick(position = position, user = arrlist[position])
        }
    }

    override fun getItemCount(): Int {
        return arrlist.size
    }

    var arrlist = ArrayList<User>()
    fun setData(arrayList: ArrayList<User>){
        arrlist = arrayList
        notifyDataSetChanged()
    }

    interface CustomOnClickListener{
        fun onCustomClick(position:Int,user:User)
    }
    lateinit var customOnClickListener: CustomOnClickListener
    fun setOnCustomClickListener(customOnClickListener: CustomOnClickListener){
        this.customOnClickListener = customOnClickListener
    }


}