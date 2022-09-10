package com.mandeep.chatapplication2.Firebase.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mandeep.chatapplication2.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication2.Firebase.data_Classes.Chat
import com.mandeep.chatapplication2.R
import com.mandeep.chatapplication2.Utils.MBitmap
import com.mandeep.chatapplication2.Utils.Util

/**senderId is also UserId*/
class ChatAdapter(val context: Context, val receiverId: String,val senderId:String,val firebaseViewmodel: FirebaseViewmodel) :RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    private var filteredChatList = ArrayList<Chat>()
    private val SEND_VIEWTYPE = 1
    private val RECEIVE_VIEWTYPE = 2
    lateinit var customOnClickListener: CustomOnClickListener

    val selectedColor = R.color.teal_700
    val unSelectedColor = Color.Transparent

    val selectedList = ArrayList<Chat>()
    var isSelected = false
    var isBackPressed:Boolean = false

    var viewtypee:Int?=null

    val drawabel = context.getDrawable(R.drawable.ic_sharp_check_24)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        Log.d("dsnfdf","attached")

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

    val view = if(viewType == SEND_VIEWTYPE) {
           viewtypee = SEND_VIEWTYPE
          LayoutInflater.from(context).inflate(R.layout.sender_item, parent, false)
      }else{
        viewtypee = RECEIVE_VIEWTYPE
        LayoutInflater.from(context).inflate(R.layout.reciever_item, parent, false)

      }
        return MyViewHolder(view)
    }


    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val chat = filteredChatList[position]

        chat.is_sent?.let { isSent->
            if(isSent)
            {
                holder.isSeenImageView?.let {
                    it.setImageDrawable(context.getDrawable(R.drawable.ic_sharp_check_24))
                    it.setColorFilter(androidx.appcompat.R.color.material_blue_grey_800)
                }
            }else{
                 holder.isSeenImageView?.let {
                  it.setImageDrawable(context.getDrawable(R.drawable.ic_clock))
                  it.setColorFilter(R.color.white)
              }
            }
        }

      /*  chat.is_reached?.let {
            Log.d("foidgnfdg",it.toString())
            if(it)
            {
                holder.doubleTickConstraintLayout?.visibility = View.VISIBLE
                holder.isSeenImageView?.visibility = View.GONE
                holder.tickFirstPart?.setColorFilter(android.R.color.darker_gray)
                holder.tickSecondPart?.setColorFilter(android.R.color.darker_gray)
            }
            else{
                holder.doubleTickConstraintLayout?.visibility = View.GONE
                holder.isSeenImageView?.visibility = View.VISIBLE
                *//* holder.tickFirstPart?.setColorFilter( R.color.teal_200)
                 holder.tickSecondPart?.setColorFilter( R.color.teal_200)*//*
            }
        }*/

        chat.is_seen?.let { isSeen ->
            if(isSeen)
            {
                /*holder.isSeenImageView?.let {
                    it.setImageDrawable(context.getDrawable(R.drawable.ic_sharp_check_24))
                    it.setColorFilter(androidx.appcompat.R.color.material_blue_grey_800)
                }*/
                holder.doubleTickConstraintLayout?.visibility = View.VISIBLE
                holder.isSeenImageView?.visibility = View.GONE
               // holder.tickFirstPart?.setColorFilter(R.color.teal_200)
                //holder.tickSecondPart?.setColorFilter(R.color.teal_200)
            }
            else{
           /*     holder.doubleTickConstraintLayout?.visibility = View.GONE
                holder.isSeenImageView?.visibility = View.VISIBLE
              */
                Log.d("dignfg","is_Seen false ")
                holder.doubleTickConstraintLayout?.visibility = View.GONE
                holder.isSeenImageView?.visibility = View.VISIBLE

            }
        }



        if(chat.imagePath!=null  )
                {
                    var bitmap = chat.imagePath.let { MBitmap.decodeBitmap(it) }
                    if (bitmap != null ) {
                        val lp = LinearLayout.LayoutParams(230, 230)
                        lp.setMargins(15,15,15,15)
                        holder.messageImageVIew.visibility = View.VISIBLE
                        holder.messageImageVIew.layoutParams = lp
                        holder.messageImageVIew.setImageBitmap(bitmap)

                    }else {

                        holder.messageImageVIew.visibility = View.GONE
                    }
                }

        holder.messagetext.text = " ${chat.message} \n ${Util.convertTime(chat.timeStamp.toLong())}"

        holder.itemView.setOnClickListener {
            isSelected = checkSelectedList()
            if(isSelected) {

                chat.isSelected?.let { isSelected ->
                    if (!chat.isSelected!!) {
                        chat.isSelected = true
                        holder.itemView.setBackgroundResource(selectedColor)
                        selectedList.add(chat)
                        Log.d("doigfndfd", selectedList.size.toString())
                    } else {
                        chat.isSelected = false
                        holder.itemView.setBackgroundResource(unSelectedColor.toArgb())
                        selectedList.remove(chat)
                    }
                }
            }else{
                customOnClickListener.customClick(chat = chat)
            }
            firebaseViewmodel._selectedListSize.value = selectedList.size
        }

        chat.isSelected?.let { isSelected ->
            if(isSelected)
            {
                holder.itemView.setBackgroundResource(selectedColor)
            }else{
                holder.itemView.setBackgroundColor(unSelectedColor.toArgb())
            }
        }

        holder.itemView.setOnLongClickListener {

            chat.isSelected?.let { isSelected ->
                if (!chat.isSelected!!) {
                    chat.isSelected = true
                    it.setBackgroundResource(selectedColor)
                    selectedList.add(chat)
                } else {
                    chat.isSelected = false
                    it.setBackgroundResource(unSelectedColor.toArgb())
                    selectedList.remove(chat)
                }
            }
            Log.d("doigfndfd", selectedList.size.toString())
            firebaseViewmodel._selectedListSize.value = selectedList.size

            return@setOnLongClickListener true
        }
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
        val isSeenImageView :ImageView? = itemView.findViewById(R.id.isSent)
        val doubleTickConstraintLayout : ConstraintLayout? = itemView.findViewById(R.id.doubleTickconstrainlayout)
       val tickFirstPart:ImageView? = itemView.findViewById(R.id.tickFirstpart)
       val tickSecondPart:ImageView? = itemView.findViewById(R.id.tickSecondpart)

   }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(chatArraylist:ArrayList<Chat>){
        filteredChatList = chatArraylist
        Log.d("fdkfndf",filteredChatList.size.toString())

     //   Toast.makeText(context,filteredChatList.size.toString(),Toast.LENGTH_SHORT).show()

        notifyDataSetChanged()
       // notifyItemInserted(filteredChatList.size.minus(1))
    }

    fun checkSelectedList():Boolean{
        return selectedList.isNotEmpty()

    }

    interface  CustomOnClickListener{
        fun customClick(chat : Chat)
    }
    fun setCustomONClickListener(customOnClickListener: CustomOnClickListener){
       this.customOnClickListener = customOnClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setOnBackPressed(isBackPressed:Boolean){
        Log.d("sfidnfdf",isBackPressed.toString())
        this.isBackPressed = isBackPressed
        if(isBackPressed)
        {
            Log.d("ifndfd",selectedList.size.toString())
            selectedList.forEach { chat->
                chat.isSelected = false
            }
            selectedList.removeAll(selectedList)
            firebaseViewmodel._selectedListSize.value = 0
            notifyDataSetChanged()
        Log.d("ifndfd",selectedList.size.toString())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    suspend fun deleteOperation(){

        selectedList.forEach {

            filteredChatList.remove(it)
            firebaseViewmodel.deleteChats(it)
        }
        selectedList.clear()
        firebaseViewmodel._selectedListSize.value = 0
        notifyDataSetChanged()
    }
}

