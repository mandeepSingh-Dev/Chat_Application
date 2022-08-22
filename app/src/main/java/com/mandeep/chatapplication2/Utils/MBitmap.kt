package com.mandeep.chatapplication2.Utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
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

    fun decodeBitmap(bitmapString: String): Bitmap {
        try {
            val byteArray = Base64.decode(bitmapString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            return bitmap
        } catch (e: Exception) {
            return Bitmap.createBitmap(130, 130, Bitmap.Config.ARGB_8888)
        }
    }


    fun saveToStorage(context: Context,bitmap: Bitmap):Pair<String,Boolean> {


        val displayName = "msgImage${System.currentTimeMillis()}"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.RELATIVE_PATH,Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.ImageColumns.MIME_TYPE,"image/png")
            put(MediaStore.Images.ImageColumns.DISPLAY_NAME,displayName)
            put(MediaStore.Images.ImageColumns.BUCKET_ID,System.currentTimeMillis().toString())
        }
        val insertedUri = context.contentResolver.insert(uri,contentValues)
        var isStored = insertedUri?.let {
            val out = context.contentResolver.openOutputStream(it)
            val isStoredd = bitmap.compress(Bitmap.CompressFormat.JPEG,100,out)
            isStoredd
        }
        val pair = isStored?.let {
            Log.d("fkdnfd",displayName)
            Pair(displayName,isStored) }
                Log.d("fidnfdf",pair?.first.toString())
        return pair!!
    }

    fun getBitmapFromStorage(context: Context,displayName : String):Bitmap{
        var bitmap:Bitmap?=null
        val selction = MediaStore.Images.Media.RELATIVE_PATH + " LIKE: ?"
        val selctionArg = arrayOf("% $displayName %" )

      val cursor =   context.contentResolver.query(uri,null,selction,selctionArg,null,null)
        cursor?.let { cursor ->
            val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
            val relativePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.RELATIVE_PATH)
            val idColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)
            if(cursor.moveToFirst())
            {
                val displayName = cursor.getString(displayNameColumn)
                val relativePath = cursor.getString(relativePathColumn)
                val id = cursor.getLong(idColumn)

                val finalUri = Uri.withAppendedPath(uri,id.toString())
                val ipStream = context.contentResolver.openInputStream(finalUri)
                ipStream
                bitmap = BitmapFactory.decodeStream(ipStream)
            }
        }
        return bitmap!!
    }
}
}