package com.mandeep.chatapplication4.Utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object  Util {

    @SuppressLint("SimpleDateFormat")
    fun convertTime(time:Long):String{
        val simpleDateformat = SimpleDateFormat("h:mm a")
        val format = simpleDateformat.format(Date(time))
        return format.toString()
    }
}