package com.mandeep.chatapplication4.Firebase.data_Classes

data class User(val userId:String?,
                val username:String?,
                val phone:String?,
                val email:String?,
                val password:String?,
                val imageString:String?,
                val is_Online:Boolean?,
                val recentTime:String?,
                val color:String?,
                val doc_id:String?,
                val wallpaperLink:String?,
                val user_cloud_token:String?
                )
{
    constructor(userId: String,username: String,phone: String,email: String,password: String):this(userId,username,phone,email,password,null,false,null,null,null,null,null)
    constructor(userId: String,username: String,phone: String,email: String,password: String,imageString: String,is_Online: Boolean?,doc_id: String?):this(userId,username,phone,email,password,imageString,is_Online,null,null,doc_id,null,null)
   // constructor(userId: String,username: String,phone: String,email: String,password: String,imageString: String,is_Online: Boolean?,doc_id: String?):this(userId,username,phone,email,password,imageString,is_Online,null,null,doc_id)
   constructor(userId: String,username: String,phone: String,email: String,password: String,imageString: String,is_Online: Boolean?,doc_id: String?,wallpaperLink: String?):this(userId,username,phone,email,password,imageString,is_Online,null,null,doc_id,wallpaperLink,null)
    constructor(userId: String,username: String,phone: String,email: String,password: String,imageString: String,is_Online: Boolean?,recentTime: String?,color: String?,doc_id: String?,wallpaperLink: String?):this(userId,username,phone,email,password,imageString,is_Online,recentTime,color,doc_id,wallpaperLink,null)

}

