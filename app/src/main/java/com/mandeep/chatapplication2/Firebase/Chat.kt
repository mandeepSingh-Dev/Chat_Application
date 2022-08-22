package com.mandeep.chatapplication2.Firebase

data class Chat(/*val id :Int,*/val senderId:String,val receiverId:String,val message:String,val timeStamp:String,val imagePath: String?)
{
    constructor(senderId:String, receiverId:String, message:String, timeStamp:String):this(senderId,receiverId,message,timeStamp,null)
}