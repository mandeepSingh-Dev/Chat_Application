package com.mandeep.chatapplication2.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.Layout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.mandeep.chatapplication2.Constants
import com.mandeep.chatapplication2.Firebase.Chat
import com.mandeep.chatapplication2.Firebase.ChatAdapter
import com.mandeep.chatapplication2.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication2.R
import com.mandeep.chatapplication2.SharedPreferenceManager
import com.mandeep.chatapplication2.Utils.MBitmap
import com.mandeep.chatapplication2.databinding.FragmentChatsBinding
import com.mandeep.chatapplication2.databinding.ImageDialogueBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class ChatsFragment : Fragment() {

    lateinit var binding : FragmentChatsBinding

    @Inject
    lateinit var firebaseFirestore :FirebaseFirestore
    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager
    @Inject
    lateinit var dispatcherMain :MainCoroutineDispatcher

    val firebaseViewmodel:FirebaseViewmodel by viewModels()


    lateinit var chatAdapter: ChatAdapter
     var bitmap: Bitmap?=null

     var receiverId:String?=null
     var senderId:String?=null
     var message:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("fdoifndf",firebaseViewmodel.toString())
        arguments?.let {
             receiverId = it.getString(Constants.KEY_RECEIVER_ID).toString()
             receiverId?.let {
                firebaseViewmodel._receiverId.value = it
             }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater)
        return binding.root
    }

    var nummm =0
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        lifecycleScope.launch {
            firebaseViewmodel.getReceiverInfo(receiverId!!).collect {
                binding.chatHeadertextView.text = it
            }
        }

        senderId = sharedPreferenceManager.getString(Constants.KEY_USER_ID)
        chatAdapter = ChatAdapter(context = requireContext(), receiverId = receiverId!!, senderId = senderId!!)


        binding.attachIcon.setOnClickListener {
            launchher.launch("image/*")
            nummm++
           // chatAdapter.myClick("Hello")
        }


        binding.sendButton.setOnClickListener {
Log.d("dfodmfd","dofd")

            message = binding.senderTextField.text.toString()
            binding.senderTextField.setText("")

            var displayName  = bitmap?.let { MBitmap.encodeBitmap(it) }
            Log.d("84ht48gh4e",displayName.toString())
         //   Toast.makeText(requireContext(),displayName.toString(),Toast.LENGTH_SHORT).show()
                    val map = hashMapOf(
                        Constants.KEY_RECEIVER_ID to receiverId,
                        Constants.KEY_SENDER_ID to senderId,
                        Constants.KEY_MESSAGE to message,
                        Constants.KEY_TIMESTAMP to System.currentTimeMillis().toString(),
                        Constants.KEY_IMAGE_NAME to displayName)

                    firebaseFirestore.collection("Chats").add(map).addOnSuccessListener {
                        bitmap = null
                        displayName = null
                        binding.imageView.visibility = View.GONE
                    }.addOnFailureListener {  bitmap = null
                                              displayName = null
                    binding.imageView.visibility = View.GONE }
        }

        lifecycleScope.launchWhenCreated {
            firebaseViewmodel.chatList.collect { chatList ->

                Log.d("ofmdfd",chatList.size.toString())
                val filteredChatList = ArrayList<Chat>()
                chatList.forEach { chat ->

                    if (receiverId.equals(chat.receiverId) && senderId.equals(chat.senderId)  ) {
                        filteredChatList.add(chat)
                    }
                    if(receiverId.equals(chat.senderId) && senderId.equals(chat.receiverId))
                    {
                        filteredChatList.add(chat)
                    }
                }

                Log.d("dfin454545dfd",filteredChatList.size.toString())
             //   receiverId?.let { receiverId ->
                  //  chatAdapter = ChatAdapter(context = requireContext(), receiverId = receiverId, senderId = senderId!!, filteredChatList)
              //  }
             //   Toast.makeText(requireContext(),filteredChatList.size.toString()+"chatScreen",Toast.LENGTH_SHORT).show()
                chatAdapter.setData(filteredChatList)
                binding.chatRecyclerVIew.scrollToPosition(chatAdapter.itemCount - 1);

                //  chatAdapter.notifyDataSetChanged()

                // chatAdapter.setData(it as ArrayList<Chat>)
                // chatAdapter.setData(filteredChatList)
            }
        }
        setUpRecycleView()

        CoroutineScope(dispatcherMain).launch {
            firebaseViewmodel.bitmapstateFlow.collect {
                it?.let {
                    Log.d("sfdbfd", it.toString())
                   // binding.imageView.setImageBitmap(it)
                    bitmap = it

                   // val displayName = bitmap?.let { bitmap -> MBitmap.saveToStorage(requireContext(), bitmap) }

                }
            }


        }


    }
    val launchher = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback{
        it?.let {
            val ip = context?.contentResolver?.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(ip)
            firebaseViewmodel.bitmappppmutablesStateFlow.value = bitmap
            showDialogue(bitmap = bitmap)
        }
    })

   fun setUpRecycleView(){
       val linearLayoutManager = LinearLayoutManager(requireContext())
       linearLayoutManager.stackFromEnd = true
       binding.chatRecyclerVIew.layoutManager = linearLayoutManager
       binding.chatRecyclerVIew.adapter = chatAdapter
   }


    fun showDialogue(bitmap: Bitmap){
        val imagediagueBinding =  ImageDialogueBinding.inflate(LayoutInflater.from(requireContext()))
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())
                              .setView(imagediagueBinding.root)
       val alertdialogue  = materialAlertDialogBuilder.show()

        imagediagueBinding.imageVIewDialogue.setImageBitmap(bitmap)
        imagediagueBinding.checkImageDialogue.setOnClickListener {
            binding.imageView.visibility = View.VISIBLE
            binding.imageView.setImageBitmap(bitmap)
            alertdialogue.cancel()
        }

    }



}