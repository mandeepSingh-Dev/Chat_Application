package com.mandeep.chatapplication2.Firebase

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.mandeep.chatapplication2.R
import com.mandeep.chatapplication2.Utils.MBitmap
class UsersAdapter(val context:Context):RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {



   inner class MyViewHolder(itemView: View/*binding : UserItemBinding*/):RecyclerView.ViewHolder(itemView/*binding.root*/)
    {
        val anim = AnimationUtils.loadAnimation(context,R.anim.alpha_animation)
        init {
            itemView.animation = anim
        }
       // val textView = binding.textViewUser
        val textView= itemView.findViewById<TextView>(R.id.textViewUser)
        val profileImage = itemView.findViewById<RoundedImageView>(R.id.UserProfilepic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
      // val binding = UserItemBinding.inflate(LayoutInflater.from(context))
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item,parent,false))
       // return (MyViewHolder(binding))
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = arrlist[position]
       // holder.textView.text = "Username : ${user.username} \n\nUserId : ${user.userId} "
        holder.textView.text = " ${user.username} "

        user.imageString?.let {
          val bitmap = MBitmap.decodeBitmap(user.imageString.toString())
            Log.d("dfindfd",bitmap.toString()+"dvdoifnd")
            holder.profileImage.setImageBitmap(bitmap)
        }

        holder.itemView.setOnClickListener {
            customOnClickListener.onCustomClick(position = position, user = user)
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