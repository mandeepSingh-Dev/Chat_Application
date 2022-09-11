package com.mandeep.chatapplication4.Firebase

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mandeep.chatapplication4.Connectivity.State
import com.mandeep.chatapplication4.Firebase.data_Classes.Chat
import com.mandeep.chatapplication4.Firebase.data_Classes.Story
import com.mandeep.chatapplication4.Firebase.data_Classes.User
import com.mandeep.chatapplication4.R
import com.mandeep.chatapplication4.SharedPreferenceManager
import com.mandeep.chatapplication4.retrofit.DataClass.Hit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import com.mandeep.chatapplication4.Constants

@HiltViewModel
class FirebaseViewmodel @Inject constructor(private val firebaseFirestore: FirebaseFirestore):ViewModel() {

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    lateinit var usersList : Flow<List<User>>
    lateinit var chatList : Flow<List<Chat>>
    lateinit var storyList : Flow<List<Story>>
     lateinit var bitmap :Bitmap
     lateinit var imageBitmap : Flow<Bitmap>



     var _num = MutableStateFlow<Int>(1)
     val num1 : StateFlow<Int>
     get() = _num
     var num :Flow<Int> = num1

    var bitmappppmutablesStateFlow = MutableStateFlow<Bitmap?>(null)
    val bitmapstateFlow : StateFlow<Bitmap?>
    get() = bitmappppmutablesStateFlow

    val _receiverId = MutableStateFlow("")
    val receiverId : StateFlow<String>
    get() = _receiverId

    var receiverInfo = MutableStateFlow<String?>(null)
    val receiverInfoState:StateFlow<String?>
    get() = receiverInfo


    val _isSaved = MutableStateFlow(false)
    val isSaved : StateFlow<Boolean>
    get() = _isSaved

    val _downloadText = MutableStateFlow("Download Image")
    val downloadText : StateFlow<String>
    get() = _downloadText

    val _selectedListSize = MutableStateFlow(0)
    val selectedListSize : StateFlow<Int>
    get() = _selectedListSize

    val _bottomColor = MutableStateFlow(R.color.white)
    val bottomColor : StateFlow<Int>
    get() = bottomColor

    val _state = MutableStateFlow<State?>(null)
    val state : StateFlow<State?>
    get() = _state

    val _hit = MutableStateFlow<Hit?>(null)
    val hit : StateFlow<Hit?>
    get() = _hit

    val _count = MutableStateFlow<Int?>(null)
    val count : StateFlow<Int?>
    get() = _count

    val _update = MutableStateFlow<Boolean?>(true)
    val update : StateFlow<Boolean?>
        get() = _update

    init {

        usersList = callbackFlow {

            val snapShotListener = firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).orderBy(Constants.KEY_RECENT_TIME,Query.Direction.DESCENDING ).addSnapshotListener { value, error ->
                val arrlist = ArrayList<User>()
                arrlist.clear()
                value?.documents?.forEach {
                    if(!sharedPreferenceManager.getString(Constants.KEY_USER_ID).equals(it.id)) {
                        val username = it.getString(Constants.KEY_USERNAME).toString()
                        val phone = it.getString(Constants.KEY_PHONE).toString()
                        val userId = it.id.toString()
                        val email = it.getString(Constants.KEY_EMAIL).toString()
                        val password = it.getString(Constants.KEY_PASSWORD).toString()
                        val imageString = it.getString(Constants.KEY_IMAGE)
                        val is_Online = it.getBoolean(Constants.KEY_IS_ONLINE)
                        val doc_id = it.getString(Constants.KEY_DOC_ID)
                       /* val recentTime = it.getString(Constants.KEY_RECENT_TIME)
                        val color = it.getString(Constants.KEY_COLOR)*/

                        val user = User(username = username, phone = phone, userId = userId, email = email, password = password, imageString = imageString.toString(), is_Online = is_Online, doc_id = doc_id)
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

          val snapShotListener =    firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).orderBy(Constants.KEY_TIMESTAMP).addSnapshotListener { value, error ->
              val chatarraylist = ArrayList<Chat>()
              chatarraylist.clear()

                value?.documents?.forEach {
                    val senderId = it.getString(Constants.KEY_SENDER_ID).toString()
                    val receiverId = it.getString(Constants.KEY_RECEIVER_ID).toString()
                    val message  = it.getString(Constants.KEY_MESSAGE).toString()
                    val timeStamp = it.getString(Constants.KEY_TIMESTAMP).toString()
                    val imagePath = it.getString(Constants.KEY_IMAGE).toString()
                    val isSeen = it.getBoolean(Constants.KEY_IS_SEEN)
                    val doc_id = it.getString(Constants.KEY_DOC_ID).toString()
                    val is_sent = it.getBoolean(Constants.KEY_IS_SENT)
                    val is_reached = it.getBoolean(Constants.KEY_IS_REACHED)

                    chatarraylist.add(Chat(senderId = senderId, receiverId = receiverId, message = message,timeStamp = timeStamp,imagePath = imagePath, isSelected =  null, is_seen = isSeen, doc_id = doc_id, is_sent = is_sent,is_reached = is_reached))
                }
                trySend(chatarraylist)
            }
            awaitClose {
             snapShotListener.remove()
            }
        }
        storyList = callbackFlow {
            val snapShotListener = firebaseFirestore.collection(Constants.KEY_COLLECTION_STORY).addSnapshotListener { value, error ->
                val storyArrayList = ArrayList<Story>()
                storyArrayList.clear()

                value?.forEach {
                    val story_doc_id =  it.getString(Constants.KEY_STORY_DOC_ID)
                    val story_image =  it.getString(Constants.KEY_STORY_IMAGE)
                    val story_story_timing =  it.getString(Constants.KEY_STORY_TIMING)
                    val story_user_id =  it.getString(Constants.KEY_STORY_USER_ID)
                    val story_user_name =  it.getString(Constants.KEY_STORY_USERNAME)

                    storyArrayList.add(Story(story_user_id,story_user_name,story_image,story_story_timing,story_doc_id))
                }
                trySend(storyArrayList)
            }

            awaitClose{
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

     suspend fun getReceiverInfo(receiverId:String):Flow<User>{
       return  channelFlow {
            val job = firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).document(receiverId).addSnapshotListener { value, error ->
                val username = value?.getString(Constants.KEY_USERNAME)
                val email = value?.getString(Constants.KEY_EMAIL)
                val password = value?.getString(Constants.KEY_PASSWORD)
                val phone = value?.getString(Constants.KEY_PHONE)
                val image = value?.getString(Constants.KEY_IMAGE)
                val id = value?.getString(Constants.KEY_ID)
                val is_Online = value?.getBoolean(Constants.KEY_IS_ONLINE)
                val recentTime = value?.getString(Constants.KEY_RECENT_TIME)
                val color = value?.getString(Constants.KEY_COLOR)
                val doc_id = value?.getString(Constants.KEY_DOC_ID)
                val wallpaperLink = value?.getString(Constants.KEY_WALLPAPER)
                val cloud_Token = value?.getString(Constants.KEY_USER_CLOUD_TOKEN)

                Log.d("sdifindd",doc_id.toString())


                val user =  User(id.toString(),username.toString(),phone.toString(),email.toString(),password.toString(),image.toString(),is_Online,recentTime,color, doc_id,wallpaperLink,cloud_Token)

                //        viewModelScope.launch {
                username?.let { trySend(user) }
                //      }
            }
           awaitClose{
               job.remove()
           }
        }
    }

   suspend fun deleteChats(chat: Chat){

        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).get().addOnSuccessListener { qSnapshot->

                qSnapshot.documents.forEach { docId->
                    if(docId.getString(Constants.KEY_TIMESTAMP).toString().equals(chat.timeStamp))
                    {
                        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).document(docId.id).delete().addOnSuccessListener {
                            Log.d("dkndfdf",docId.getString(Constants.KEY_MESSAGE).toString())
                        }.addOnFailureListener {
                            Log.d("dgindgfd","failure")
                        }
                    }
                }


        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("dfidnvd","onCleared")
    }



}