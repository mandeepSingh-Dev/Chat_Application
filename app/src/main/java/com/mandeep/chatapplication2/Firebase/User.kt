package com.mandeep.chatapplication2.Firebase

import java.net.PasswordAuthentication

data class User(val userId:String,val username:String,val phone:String,val email:String,val password:String,val imageString:String?)
{
    constructor(userId: String,username: String,phone: String,email: String,password: String):this(userId,username,phone,email,password,null)
}
