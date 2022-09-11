package com.mandeep.chatapplication4.Firebase.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.mandeep.chatapplication4.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication4.Firebase.data_Classes.User
import com.mandeep.chatapplication4.R
import com.mandeep.chatapplication4.Utils.MBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsersAdapter(val context:Context,val firebaseViewmodel: FirebaseViewmodel):RecyclerView.Adapter<UsersAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
      // val binding = UserItemBinding.inflate(LayoutInflater.from(context))
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.user_item,parent,false))
       // return (MyViewHolder(binding))
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = arrlist[position]
       // holder.textView.text = "Username : ${user.username} \n\nUserId : ${user.userId}
        Log.d("dfidngd",user.username.toString()+"    "+user.doc_id.toString())
        holder.textView.text = " ${user.username} "
        Log.d("dgikndfgvd",user.doc_id.toString())

        CoroutineScope(Dispatchers.Main).launch {

            firebaseViewmodel.getReceiverInfo(user.doc_id?.trim().toString()).collect{ usser->


                usser.is_Online?.let { is_Online ->
                    Log.d("dfodmfd",is_Online.toString())
                    if(is_Online) {
                        holder.onlineIndicator.setBackgroundColor(android.graphics.Color.GREEN)
                    }else{
                        holder.onlineIndicator.setBackgroundColor(android.graphics.Color.LTGRAY)
                    }
                }
            }
        }



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

    inner class MyViewHolder(itemView: View/*binding : UserItemBinding*/):RecyclerView.ViewHolder(itemView/*binding.root*/)
    {
        val anim = AnimationUtils.loadAnimation(context,R.anim.alpha_animation)
        init {
            itemView.animation = anim
        }
        // val textView = binding.textViewUser
        val textView= itemView.findViewById<TextView>(R.id.textViewUser)
        val profileImage = itemView.findViewById<RoundedImageView>(R.id.UserProfilepic)
        val onlineIndicator = itemView.findViewById<View>(R.id.onlineIndicator)
    }



    var arrlist = ArrayList<User>()
    fun setData(arrayList: ArrayList<User>){
        arrlist = arrayList

        if(arrayList.size > 1) {
            notifyItemInserted(arrayList.size.minus(1))
        }else{
             notifyDataSetChanged()
        }
    }

    interface CustomOnClickListener{
        fun onCustomClick(position:Int,user: User)
    }
    lateinit var customOnClickListener: CustomOnClickListener
    fun setOnCustomClickListener(customOnClickListener: CustomOnClickListener){
        this.customOnClickListener = customOnClickListener
    }


}