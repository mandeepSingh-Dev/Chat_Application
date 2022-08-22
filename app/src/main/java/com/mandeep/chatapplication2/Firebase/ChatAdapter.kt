package com.mandeep.chatapplication2.Firebase

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mandeep.chatapplication2.R

class ChatAdapter(val context: Context, val receiverId: String,val senderId:String) :RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    private var filteredChatList = ArrayList<Chat>()
    val SEND_VIEWTYPE = 1
    val RECEIVE_VIEWTYPE = 2
/**senderId is also UserId*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val view = if(viewType == SEND_VIEWTYPE) {
        LayoutInflater.from(context).inflate(R.layout.sender_item, parent, false)
    }else{
        LayoutInflater.from(context).inflate(R.layout.reciever_item, parent, false)
    }
        return MyViewHolder(view)
    }

    var positionn = 0
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        positionn = position
        val chat = filteredChatList[position]
       // if(receiverId.equals(chat.receiverId) && senderId.equals(chat.senderId))
       // {
            holder.messagetext.text = "Message :  ${chat.message}  \nSender Id : ${chat.senderId}  \nReceiver Id: ${chat.receiverId} \n ${chat.timeStamp}"
       // }

    }

    override fun getItemCount(): Int {
       return filteredChatList.size
    }

    override fun getItemViewType(position: Int): Int {
        val chat = filteredChatList[position]
        return if(senderId.equals(chat.senderId)) SEND_VIEWTYPE else RECEIVE_VIEWTYPE
    }

   inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
       /* val anim = AnimationUtils.loadAnimation(context,R.anim.alpha_animation)
        init {
            itemView.animation = anim
        }*/
        val messagetext = itemView.findViewById<TextView>(R.id.messageTextView)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(chatArraylist:ArrayList<Chat>){
        filteredChatList = chatArraylist
        Log.d("fdkfndf",filteredChatList.size.toString())
        Toast.makeText(context,filteredChatList.size.toString(),Toast.LENGTH_SHORT).show()
        notifyDataSetChanged()
        notifyItemInserted(filteredChatList.size.minus(1))
    }
}