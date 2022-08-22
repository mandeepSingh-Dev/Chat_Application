package com.mandeep.chatapplication2.Firebase

import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.contentValuesOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mandeep.chatapplication2.Constants
import com.mandeep.chatapplication2.SharedPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@HiltViewModel
class FirebaseViewmodel @Inject constructor(private val firebaseFirestore: FirebaseFirestore) : ViewModel() {

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    lateinit var usersList : Flow<List<User>>
    lateinit var chatList : Flow<List<Chat>>
    lateinit var bitmap :Bitmap
     lateinit var imageBitmap : Flow<Bitmap>


     var _num = MutableStateFlow<Int>(1)
     val state : StateFlow<Int>
     get() = _num
     var num :Flow<Int> = state

    var bitmappppmutablesStateFlow = MutableStateFlow<Bitmap?>(null)
    val bitmapstateFlow : StateFlow<Bitmap?>
    get() = bitmappppmutablesStateFlow

    val _receiverId = MutableStateFlow("")
    val receiverId : StateFlow<String>
    get() = _receiverId

    init {

        usersList = callbackFlow {

            val snapShotListener = firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).orderBy("id").addSnapshotListener { value, error ->
                val arrlist = ArrayList<User>()
                arrlist.clear()
                value?.documents?.forEach {
                    if(!sharedPreferenceManager.getString(Constants.KEY_USER_ID).equals(it.id)) {
                        val username = it.getString(Constants.KEY_USERNAME).toString()
                        val phone = it.getString(Constants.KEY_PHONE).toString()
                        val userId = it.id.toString()
                        val email = it.getString(Constants.KEY_EMAIL).toString()
                        val password = it.getString(Constants.KEY_PASSWORD).toString()
                        val user = User(username = username, phone = phone, userId = userId, email = email, password = password)
                        arrlist.add(user)
                    }
                }
                trySend(arrlist)
            }
            awaitClose {
                snapShotListener.remove()
            }
        }
        chatList = callbackFlow {

          val snapShotListener =    firebaseFirestore.collection("Chats").orderBy(Constants.KEY_TIMESTAMP).addSnapshotListener { value, error ->
              val chatarraylist = ArrayList<Chat>()
              chatarraylist.clear()

                value?.documents?.forEach {
                    val senderId = it.getString(Constants.KEY_SENDER_ID).toString()
                    val receiverId = it.getString(Constants.KEY_RECEIVER_ID).toString()
                    val message  = it.getString(Constants.KEY_MESSAGE).toString()
                    val timeStamp = it.getString(Constants.KEY_TIMESTAMP).toString()

                    chatarraylist.add(Chat(senderId = senderId, receiverId = receiverId, message = message,timeStamp = timeStamp))
                }
                trySend(chatarraylist)
            }
            awaitClose {
             snapShotListener.remove()
            }
        }


    }

    fun getData()=callbackFlow<List<User>> {
        val snapShotListener = firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).addSnapshotListener { value, error ->
            val arrlist = ArrayList<User>()
            arrlist.clear()

            value?.documents?.forEach {

                if(!sharedPreferenceManager.getString(Constants.KEY_USER_ID).equals(it.id)){
                    Log.d("dfindf",it.id)
                    Log.d("dfondfd",sharedPreferenceManager.getString(Constants.KEY_USER_ID).toString())
                    val username = it.getString(Constants.KEY_USERNAME).toString()
                    val phone = it.getString(Constants.KEY_PHONE).toString()
                    val userId = it.getString(Constants.KEY_USER_ID).toString()
                    val email = it.getString(Constants.KEY_EMAIL).toString()
                    val password = it.getString(Constants.KEY_PASSWORD).toString()
                    val user = User(username = username, phone = phone, userId = userId, email = email, password = password)
                    arrlist.add(user)
                }

            }
            trySend(arrlist)
        }
        awaitClose {
            snapShotListener.remove()
        }
    }

     fun setBitmap1(bitmap1:Bitmap){
        Log.d("dfodmfd",bitmap.toString())
        this.bitmap = bitmap1
         imageBitmap = flow {
             emit(bitmap1)
         }
        /*imageBitmap = flow{
            emit(bitmap)
        }
        imageBitmapstateFlow = flow{
            emit(bitmap)
        }.stateIn(viewModelScope)*/
    }

    fun setNum(num1 :Int){
        num = flow {
            emit(num1)
        }
    }




}