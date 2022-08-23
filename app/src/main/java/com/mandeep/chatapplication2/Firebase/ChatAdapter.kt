package com.mandeep.chatapplication2.Firebase

import android.R.attr.left
import android.R.attr.right
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mandeep.chatapplication2.R
import com.mandeep.chatapplication2.Utils.MBitmap

/**senderId is also UserId*/
class ChatAdapter(val context: Context, val receiverId: String,val senderId:String) :RecyclerView.Adapter<ChatAdapter.MyViewHolder>(),clickYoyO {

    private var filteredChatList = ArrayList<Chat>()
    private val SEND_VIEWTYPE = 1
    private val RECEIVE_VIEWTYPE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

    val view = if(viewType == SEND_VIEWTYPE) {
          LayoutInflater.from(context).inflate(R.layout.sender_item, parent, false)
      }else{
          LayoutInflater.from(context).inflate(R.layout.reciever_item, parent, false)
      }
        return MyViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val chat = filteredChatList[position]
       // holder.setItem(chat)
     //  holder.messagetext.text = "Message :  ${chat.message}  \nSender Id : ${chat.senderId}  \nReceiver Id: ${chat.receiverId} \n ${chat.timeStamp}"

                if(chat.imagePath?.isNotBlank()!!)
                {
                    Log.d("dfindfd",chat.imagePath.toString())
                    val bitmap = chat.imagePath?.let { MBitmap.decodeBitmap(it) }
                    if (bitmap != null   ) {
                        val lp = LinearLayout.LayoutParams(230, 230)
                        lp.setMargins(15,15,15,15)
                        holder.messageImageVIew.layoutParams = lp
                        holder.messageImageVIew.setImageBitmap(bitmap)
                    }
                }
        holder.messagetext.text = " ${filteredChatList[position].message} "

    }

    override fun getItemCount(): Int {
       return filteredChatList.size
    }

    override fun getItemViewType(position: Int): Int {
        val chat = filteredChatList[position]
        return if(senderId.equals(chat.senderId)) SEND_VIEWTYPE else RECEIVE_VIEWTYPE
    }

   inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messagetext: TextView = itemView.findViewById(R.id.messageTextView)
        val messageImageVIew:ImageView  = itemView.findViewById(R.id.messageImageView)

       @SuppressLint("SetTextI18n")
       fun setItem(chat:Chat){
           val chat = filteredChatList[position]
           //  holder.messagetext.text = "Message :  ${chat.message}  \nSender Id : ${chat.senderId}  \nReceiver Id: ${chat.receiverId} \n ${chat.timeStamp}"

           val bitmap = chat.imagePath?.let { MBitmap.decodeBitmap(it) }
           if (bitmap != null && chat.imagePath.isNotEmpty()) {
               val lp = LinearLayout.LayoutParams(230, 230)
               lp.setMargins(15,15,15,15)
               messageImageVIew.layoutParams = lp

               messageImageVIew.setImageBitmap(bitmap)
           }

           messagetext.text = " ${chat.message} "
       }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(chatArraylist:ArrayList<Chat>){
        filteredChatList = chatArraylist
        Log.d("fdkfndf",filteredChatList.size.toString())
     //   Toast.makeText(context,filteredChatList.size.toString(),Toast.LENGTH_SHORT).show()
       // notifyDataSetChanged()
        notifyItemInserted(filteredChatList.size.minus(1))
    }

    override fun myClick(string: String) {
         Log.d("dfodnfd",string)
    }
}

interface  clickYoyO{
    fun myClick(string:String)
}