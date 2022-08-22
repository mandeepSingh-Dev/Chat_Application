package com.mandeep.chatapplication2

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManager(context: Context) {

    lateinit var sharedPreferenceManager: SharedPreferences
    lateinit var editor:SharedPreferences.Editor
    init{
        sharedPreferenceManager  = context.getSharedPreferences(Constants.KEY_SHAREDPREFERENCE,Context.MODE_PRIVATE)
        editor = sharedPreferenceManager.edit()
    }

    fun putBoolean(key:String,boolean: Boolean)
    {
        editor.putBoolean(key,boolean)
        editor.apply()
    }
    fun getBoolean(key:String) = sharedPreferenceManager.getBoolean(key,false)

    fun putString(key:String,value:String){
        editor.putString(key,value)
        editor.apply()
    }
    fun getString(key:String) = sharedPreferenceManager.getString(key,"")

    fun apply(){
        editor.apply()
      }

    fun clear(){
        editor.clear()
    }

}