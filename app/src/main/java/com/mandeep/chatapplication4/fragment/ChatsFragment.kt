package com.mandeep.chatapplication4.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.mandeep.chatapplication4.Connectivity.MyConnectivityManager
import com.mandeep.chatapplication4.Connectivity.State
import com.mandeep.chatapplication4.Constants
import com.mandeep.chatapplication4.Firebase.data_Classes.Chat
import com.mandeep.chatapplication4.Firebase.Adapters.ChatAdapter
import com.mandeep.chatapplication4.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication4.MainActivity
import com.mandeep.chatapplication4.R
import com.mandeep.chatapplication4.SharedPreferenceManager
import com.mandeep.chatapplication4.Utils.FCMHttpServerRequest
import com.mandeep.chatapplication4.Utils.MBitmap
import com.mandeep.chatapplication4.Utils.Util
import com.mandeep.chatapplication4.databinding.FragmentChatsBinding
import com.mandeep.chatapplication4.databinding.ImageDialogueBinding
import com.mandeep.chatapplication4.retrofit.ApiService
import com.mandeep.chatapplication4.retrofit.DataClass.Hit
import com.mandeep.chatapplication4.retrofit.WallpaperAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class ChatsFragment : Fragment() /*,BottomSheetDialogue3.WallpaperListener_fragment*/,WallpaperAdapter.WallpaperListener{

    private var username: String? = null
    private var receiverUser_cloud_token: String? = null
    lateinit var binding : FragmentChatsBinding

    @Inject
    lateinit var firebaseFirestore :FirebaseFirestore
    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager
    @Inject
    lateinit var dispatcherMain :MainCoroutineDispatcher

    var state:State = State.UnAvailable


    val firebaseViewmodel:FirebaseViewmodel by viewModels()

    lateinit var chatAdapter: ChatAdapter

     var bitmap: Bitmap?=null
     var receiverId:String?=null
     var senderId:String?=null
     var message:String = ""

    lateinit var bottomSheetDialogue: BottomSheetDialogue
    lateinit var bottomSheetDialogue2 : BottomSheetDialogue2

    var selectedListSize:Int=0


    lateinit var gettingFragmentInstanceListenerr: GettingFragmentInstanceListener
    val sample = "Sample"

    lateinit var myConnectivityManager:MyConnectivityManager
    var netWorkState:State?=null

    var mainActivity:MainActivity?=null

    var bottomSheetDialog3 :BottomSheetDialogue3?=null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity

        bottomSheetDialogue = BottomSheetDialogue()
        bottomSheetDialogue2 = BottomSheetDialogue2()

        gettingFragmentInstanceListenerr = context as GettingFragmentInstanceListener
        gettingFragmentInstanceListenerr.getFragmentInstance(this)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("fdoifndf",firebaseViewmodel.toString())
        arguments?.let {
             receiverId = it.getString(Constants.KEY_RECEIVER_ID).toString()
             receiverId?.let {
                firebaseViewmodel._receiverId.value = it
             }
        }
        bottomSheetDialogue = BottomSheetDialogue()

        val manager = MyConnectivityManager(requireContext())
        lifecycleScope.launch {
            manager.observe().collect{
                firebaseViewmodel._state.value = it
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater)
        return binding.root
    }

    var nummm =0
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("NotifyDataSetChanged", "SimpleDateFormat", "SetTextI18n", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        CoroutineScope(dispatcherMain).launch {
            updateConnectivity()
        }

        lifecycleScope.launch{
            firebaseViewmodel.state.collect{ statee ->
           statee?.let {
               state = it
           }
            }
        }


          lifecycleScope.launch {
            //for getting Receiver user info
            firebaseViewmodel.getReceiverInfo(receiverId!!).collect { userr ->
                /**assigning user cloud token to this variable  */
                receiverUser_cloud_token = userr.user_cloud_token
                Log.d("dgnfgfd",userr.user_cloud_token.toString())
                username = userr.username

                binding.chatHeadertextViewUserName.text = userr.username.toString()
                userr.is_Online?.let { is_Online ->
                    val online_Status = if (is_Online) {
                        binding.onlineImageCardview.visibility = View.VISIBLE
                        "online"
                    } else {
                        userr.userId?.let {
                            binding.onlineImageCardview.visibility = View.GONE
                            Util.convertTime(it.toLong())
                        }
                        // "offline"
                    }
                    binding.chatHeadertextViewOnlineStatus.setText("\n $online_Status")

                }
                userr.color?.let {
                    binding.chatRecyclerVIew.setBackgroundResource(it.toInt())
                    binding.cardViewHeader.setBackgroundResource(it.toInt())
                    binding.headerTextViewLinearlayout.setBackgroundResource(it.toInt())
                    binding.root.setBackgroundResource(it.toInt())
                }
                userr.wallpaperLink?.let {
                    binding.chatRecyclerVIew.setBackgroundResource(resources.getColor(R.color.Transparent))
                    binding.cardViewHeader.setBackgroundResource(resources.getColor(R.color.Transparent))
                    binding.headerTextViewLinearlayout.setBackgroundResource(resources.getColor(R.color.Transparent))
                    binding.root.setBackgroundResource(resources.getColor(R.color.Transparent))
                    Glide.with(requireContext()).load(it).into(binding.chatScreenWallpaperr)

                    Log.d("dfindgd", it)
                }
            }
        }

        /**for set wallpapper in layout */
        /*  lifecycleScope.launch {
            firebaseViewmodel.hit.collect{
                 if (it != null) {
                     binding.chatRecyclerVIew.setBackgroundResource(resources.getColor(R.color.Transparent))
                     binding.cardViewHeader.setBackgroundResource(resources.getColor(R.color.Transparent))
                     binding.headerTextViewLinearlayout.setBackgroundResource(resources.getColor(R.color.Transparent))
                     binding.root.setBackgroundResource(resources.getColor(R.color.Transparent))
                     Glide.with(requireContext()).load(it?.largeImageURL).into(binding.chatScreenWallpaperr)

                     Log.d("dfindgd", it.toString())

                     //binding.chatScreenWallpaperr.setImageBitmap()
                 }
             }
        }*/
        //for getting  network state
        /* CoroutineScope(Dispatchers.Main).launch{

                val manager = MyConnectivityManager(requireContext())
                manager.observe().collect{ state ->
                 Toast.makeText(requireContext(),state.toString(),Toast.LENGTH_SHORT).show()
                    netWorkState = state
                    firebaseViewmodel._state.value = state
                    when(state)
                    {
                        State.UnAvailable -> {
                            binding.internetIndicationTextView.alpha = 1f
                            binding.chatHeadertextViewOnlineStatus.visibility = View.GONE
                            Log.d("dguieinge",state.toString())
                        }
                        State.Lose -> {
                            binding.internetIndicationTextView.alpha = 1f
                            binding.chatHeadertextViewOnlineStatus.visibility = View.GONE
                              Log.d("dguieinge",state.toString())
                        }
                        else ->{
                            binding.internetIndicationTextView.alpha = 0f
                            binding.chatHeadertextViewOnlineStatus.visibility = View.VISIBLE
                        }
                    }
                }
            }*/

        CoroutineScope(dispatcherMain).launch {
            firebaseViewmodel.getReceiverInfo("mjxUpy56qQD1En7AUI8B").collect {
                 Log.d("fidngdf",it.username.toString()+it.is_Online.toString())
            }
        }


        senderId = sharedPreferenceManager.getString(Constants.KEY_USER_ID)
        chatAdapter = ChatAdapter(context = requireContext(), receiverId = receiverId!!, senderId = senderId!!,firebaseViewmodel)


        binding.attachIcon.setOnClickListener {
            launchher.launch("image/*")
            nummm++
           // chatAdapter.myClick("Hello")
        }

        FirebaseFirestore.setLoggingEnabled(true)

        val messagelist =ArrayList<HashMap<String,out Any>>()
        binding.sendButton.setOnClickListener {

            message = binding.senderTextField.text.toString()
            binding.senderTextField.setText("")

            var bitmapString  = bitmap?.let { MBitmap.encodeBitmap(it) }

                    val map =  when(state)
                    {
                     State.Available -> {
                         Log.d("dguiidfngdf","avoialoable ")
                         createMap(bitmapString.toString(),true) }
                     else ->  {  createMap(bitmapString.toString(),false)  }
                    }
                    val isSent :Boolean = map.get(Constants.KEY_IS_SENT) as Boolean
                    if(!isSent){
                        messagelist.add(map)
                    }

            map.entries.forEach {
                Log.d("dfingfg",it.key.toString()+"   "+it.value.toString())
            }

            firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).add(map).addOnSuccessListener { doc ->

                receiverUser_cloud_token?.let {
                    Log.d("gifngf",it.toString())
                    val senderName = sharedPreferenceManager.getString(Constants.KEY_USERNAME)
                    FCMHttpServerRequest.sendNotification(it,senderName.toString(),message.toString(),doc.id.toString())
                }
                        /** updating receiver user recent time for showing user on top in users list */
                Log.d("dfindgdff",doc.id.toString() +"gfifinfgkf")
                        firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).document(receiverId!!).update(Constants.KEY_RECENT_TIME,System.currentTimeMillis().toString())
                        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).document(doc.id.toString()).update(Constants.KEY_DOC_ID,doc.id.toString())
                     //   firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).document(it.id.toString()).update(Constants.KEY_IS_REACHED,true)

                        bitmap = null
                        bitmapString = null
                        binding.imageView.visibility = View.GONE
                    }
                .addOnFailureListener {
                            Log.d("dfdingfdg",it.message.toString())
                            bitmap = null
                            bitmapString = null
                            binding.imageView.visibility = View.GONE
                        }.addOnCompleteListener {
                            Log.d("idnfgd","idnfdf")
                }

        }

        CoroutineScope(Dispatchers.Main).launch {
            firebaseViewmodel.state.collect {
                when(it){
                 State.Available -> {
                     Log.d("dfinidgd","available")
                     if(!messagelist.isEmpty()) {
                         Log.d("dfinidgd",messagelist.size.toString())
                         messagelist.forEach { map ->
                             Log.d("f444obnff", map[Constants.KEY_TIMESTAMP].toString())
                             firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).whereEqualTo(Constants.KEY_TIMESTAMP,map[Constants.KEY_TIMESTAMP].toString()).addSnapshotListener { value, error ->
                                 value?.documents?.forEach {
                                     firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).document(it.id).update(Constants.KEY_IS_SENT,true).addOnSuccessListener {
                                         Log.d("dindvd","success")
                                     }
                                 }
                             }
                         }
                         messagelist.clear()
                     }
                 }
                 else -> {
                     Log.d("dfinidgd","lose")
                     messagelist.clear()
                 }
                }
            }
        }


        /**this is for getting chats list from viewmodel to show in chat screen */
        lifecycleScope.launch {
            firebaseViewmodel.chatList.collect { chatList ->

                Log.d("ofmdsizew43949834fd",chatList.size.toString())
                val filteredChatList = ArrayList<Chat>()
                chatList.forEach { chat ->

                    if (receiverId.equals(chat.receiverId) && senderId.equals(chat.senderId)  ) {
                        filteredChatList.add(chat)
                      //  firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).document(chat.doc_id.toString()).update(Constants.KEY_IS_SEEN,true)
                      //  firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).document()
                    }
                    if(receiverId.equals(chat.senderId) && senderId.equals(chat.receiverId))
                    {
                        filteredChatList.add(chat)
                    }

                    /** THIS IS FOR SET THE VALUE OF IS_SEEN TO TRUE WHEN USER OPEN THE CHAT SCREEN */
                    if(senderId.equals(chat.receiverId))
                    {
                        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).document(chat.doc_id.toString()).update(Constants.KEY_IS_SEEN,true).addOnSuccessListener {
                            firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).document(chat.doc_id.toString()).update(Constants.KEY_IS_SENT, false)
                            firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).document(chat.doc_id.toString()).update(Constants.KEY_IS_REACHED, false)
                        }
                    }
                }
                //   receiverId?.let { receiverId ->
                //  chatAdapter = ChatAdapter(context = requireContext(), receiverId = receiverId, senderId = senderId!!, filteredChatList)
                //  }
                //   Toast.makeText(requireContext(),filteredChatList.size.toString()+"chatScreen",Toast.LENGTH_SHORT).show()
                chatAdapter.setData(filteredChatList)
                binding.chatRecyclerVIew.scrollToPosition(chatAdapter.itemCount - 1)


                //  chatAdapter.notifyDataSetChanged()

                // chatAdapter.setData(it as ArrayList<Chat>)
                // chatAdapter.setData(filteredChatList)
            }
        }
        setUpRecycleView()

        lifecycleScope.launch {
            firebaseViewmodel.bitmapstateFlow.collect {
                it?.let {
                    Log.d("sfdbfd", it.toString())
                   // binding.imageView.setImageBitmap(it)
                    bitmap = it

                   // val displayName = bitmap?.let { bitmap -> MBitmap.saveToStorage(requireContext(), bitmap) }

                }
            }

        }

        /**this is for gettinng live size of selectedItems List of Recyclerview selectedItems thourgh viewmodel StateFlow */
        lifecycleScope.launch {
            firebaseViewmodel.selectedListSize.collect {
                Log.d("eufubdfef",it.toString())
                selectedListSize = it
                (activity as MainActivity).setSelectedListSize(it,chatAdapter)
                if(selectedListSize > 0)
                {
                    binding.deleteButton.visibility = View.VISIBLE
                }else{
                    binding.deleteButton.visibility = View.GONE
                }
            }
        }

        binding.deleteButton.setOnClickListener {
            lifecycleScope.launch {
                chatAdapter.deleteOperation()
            }
        }
        binding.ColorsDotsIconButton.setOnClickListener {

            CoroutineScope(Dispatchers.Main).launch {
                val moreTextView = bottomSheetDialogue2.view?.findViewById<TextView>(R.id.moreWallpapers)
                bottomSheetDialogue2.show(activity?.supportFragmentManager!!,"TAG")

                moreTextView?.setOnClickListener {
                    Toast.makeText(requireContext(),"Logstare",Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch{
                        apiService.fetchData(Constants.API_KEY).hits?.let{
                            bottomSheetDialog3 = BottomSheetDialogue3(it as ArrayList<Hit> ,this@ChatsFragment)
                            bottomSheetDialog3?.show(activity?.supportFragmentManager!!,"Tag")
                        }
                    }
                }


            }
        }
        bottomSheetDialogue2.setBottomSheet2Listener(object : BottomSheetDialogue2.Bottomsheet2listener{
            override fun colorClickBottomsheet(color: Int) {

                binding.chatRecyclerVIew.setBackgroundResource(color)
                binding.cardViewHeader.setBackgroundResource(color)
                binding.headerTextViewLinearlayout.setBackgroundResource(color)
                binding.root.setBackgroundResource(color)

                firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).document(receiverId!!).update(Constants.KEY_COLOR,color.toString()).addOnSuccessListener {}

            }
        })

    }
    @Inject
    lateinit var apiService:ApiService

    private val launchher = registerForActivityResult(ActivityResultContracts.GetContent(),
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

       chatAdapter.setCustomONClickListener(object: ChatAdapter.CustomOnClickListener{
           override fun customClick(chat: Chat) {
               activity?.supportFragmentManager?.let { fragmentManager ->

                   val bundle = Bundle()
                   bundle.putString(Constants.KEY_IMAGE,chat.imagePath)
                   bottomSheetDialogue.arguments =  bundle
                   bottomSheetDialogue.show(fragmentManager,"TAG")


               }
           }
       })
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

    fun convertTime(time:Long):String{
        val simpleDateformat = SimpleDateFormat("h:mm a")
        val format = simpleDateformat.format(Date(time))
        return format.toString()
    }

    interface GettingFragmentInstanceListener{
        fun getFragmentInstance(fragment: Fragment)
    }

    fun setBackPressed(isBackPressed: Boolean){
        Log.d("dfindf",isBackPressed.toString())
        chatAdapter.setOnBackPressed(isBackPressed)
    }

    fun createWallpaperDialogue(){
        val wallpaperBottomDialogue = BottomSheetDialog(requireContext())
        val wallpaperView = LayoutInflater.from(requireContext()).inflate(R.layout.wallpapers_dialgue,null)
        wallpaperBottomDialogue.setContentView(R.layout.wallpapers_dialgue)
        wallpaperBottomDialogue.show()
    }

    var hit:Hit?=null
    override fun wallpaperClickk(hitt: Hit) {
        Log.d("difindfk",hitt.largeImageURL.toString())
        hit = hit
        firebaseViewmodel._hit.value = hitt

        firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).document(receiverId!!).update(Constants.KEY_WALLPAPER, hitt.largeImageURL).addOnSuccessListener {

            bottomSheetDialog3?.dismiss()
            firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).document(receiverId!!).update(Constants.KEY_COLOR, null).addOnSuccessListener {}

        }


    }

   suspend fun updateConnectivity() {
        val connectivityManager = MyConnectivityManager(requireContext())
        connectivityManager.observe().collect{ state ->
          firebaseViewmodel._state.value = state
        }
    }

   /* override fun wallpaperClick(hit: Hit) {
         Log.d("o4ojg4eg",hit.id.toString())
    }*/

    fun createMap(bitmapString:String,network:Boolean) = hashMapOf(
            Constants.KEY_RECEIVER_ID to receiverId,
            Constants.KEY_SENDER_ID to senderId,
            Constants.KEY_MESSAGE to message,
            Constants.KEY_TIMESTAMP to System.currentTimeMillis().toString(),
            Constants.KEY_IMAGE_NAME to bitmapString,
            Constants.KEY_IS_SEEN to false,
            Constants.KEY_DOC_ID to "doc_id",
            Constants.KEY_IS_SENT to network,
            Constants.KEY_IS_REACHED to false
        )
    }

