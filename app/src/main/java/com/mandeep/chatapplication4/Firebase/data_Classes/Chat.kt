package com.mandeep.chatapplication4.Firebase.data_Classes

data class Chat(/*val id :Int,*/val senderId:String, val receiverId:String, val message:String, val timeStamp:String, val imagePath: String?=null,
                                var isSelected:Boolean?,val is_seen:Boolean?,val doc_id : String?,val is_sent:Boolean?,val is_reached:Boolean?)
{
    constructor(senderId:String, receiverId:String, message:String, timeStamp:String):this(senderId,receiverId,message,timeStamp,null,false,null,null,null,null)
    constructor(senderId:String, receiverId:String, message:String, timeStamp:String,imagePath: String?):this(senderId,receiverId,message,timeStamp,imagePath,false,null,null,null,null)

}