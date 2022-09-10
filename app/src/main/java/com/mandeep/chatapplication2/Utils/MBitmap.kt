package com.mandeep.chatapplication2.Utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.lang.Exception

class  MBitmap() {

companion object {

    val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    fun encodeBitmap(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream)
        val byteArray = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        return byteArray.toString()
    }

    fun decodeBitmap(bitmapString: String): Bitmap? {
        try {
            val byteArray = Base64.decode(bitmapString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            Log.d("dfdfefv",bitmap.toString())
            return bitmap
        } catch (e: Exception) {
            Log.d("fidnf3r3fd",e.message.toString())
            return null
        }
    }


    fun saveToStorage(context: Context, bitmap: Bitmap):Boolean {

        val displayName = "msgImage${System.currentTimeMillis()}"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.RELATIVE_PATH,Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.ImageColumns.MIME_TYPE,"image/png")
            put(MediaStore.Images.ImageColumns.DISPLAY_NAME,displayName)
            put(MediaStore.Images.ImageColumns.BUCKET_ID,System.currentTimeMillis().toString())
        }
        val insertedUri = context.contentResolver.insert(uri,contentValues)
        val isStored = insertedUri?.let {
            val out = context.contentResolver.openOutputStream(it)
            val isStoredd = bitmap.compress(Bitmap.CompressFormat.JPEG,100,out)
            isStoredd
        }
        return isStored!!
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getBitmapFromStorage(context: Context, displayName : String):Bitmap{
        var bitmap:Bitmap?=null
        val selction = MediaStore.Images.Media.RELATIVE_PATH + " LIKE: ?"
        val selctionArg = arrayOf("% $displayName %" )

      val cursor =   context.contentResolver.query(uri,null,selction,selctionArg,null,null)
        cursor?.let { cursorr ->
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
            val relativePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.RELATIVE_PATH)
            val idColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)
            if(cursor.moveToFirst())
            {
                val displayName = cursor.getString(displayNameColumn)
                val relativePath = cursor.getString(relativePathColumn)
                val id = cursor.getLong(idColumn)

                val finalUri = Uri.withAppendedPath(uri,id.toString())
                Log.d("doggfndgd",finalUri.toString())
                val ipStream = context.contentResolver.openInputStream(finalUri)
                bitmap = BitmapFactory.decodeStream(ipStream)
            }
        }
        cursor?.close()
        return bitmap!!
    }
}
}