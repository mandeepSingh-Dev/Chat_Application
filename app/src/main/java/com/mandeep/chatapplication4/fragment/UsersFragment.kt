package com.mandeep.chatapplication4.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mandeep.chatapplication4.Constants
import com.mandeep.chatapplication4.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication4.Firebase.data_Classes.User
import com.mandeep.chatapplication4.Firebase.Adapters.UsersAdapter
import com.mandeep.chatapplication4.R
import com.mandeep.chatapplication4.SharedPreferenceManager
import com.mandeep.chatapplication4.databinding.FragmentUsersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UsersFragment : Fragment() {

    lateinit var binding:FragmentUsersBinding

    @Inject
    lateinit var firebaseAuth:FirebaseAuth

    val firebaseViewmodel:FirebaseViewmodel by viewModels()
/*by lazy {
        ViewModelProvider(this).get(FirebaseViewmodel::class.java)
    }*/

    lateinit var userAdapter : UsersAdapter

    @Inject
    lateinit var firestore: FirebaseFirestore
    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager
    @Inject
    lateinit var mainDispatcher:MainCoroutineDispatcher

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("finjdfd","onCreate")
        super.onCreate(savedInstanceState)
        Log.d("dfjd675485844bfd",firebaseViewmodel.toString())
        navController = findNavController()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentUsersBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        val userId = sharedPreferenceManager.getString(Constants.KEY_USER_ID)
        val userName = sharedPreferenceManager.getString(Constants.KEY_USERNAME)
        //binding.userIdTextView.text = "$userName  $userId"
        val username = sharedPreferenceManager.getString(Constants.KEY_USERNAME)
        binding.userIdTextView?.text = username.toString()

        lifecycleScope.launch {
            firebaseViewmodel.getReceiverInfo(userId!!).collect {
             //   binding.userIdTextView.text = it.username.toString()
            }


        }

        userAdapter = UsersAdapter(requireContext(),firebaseViewmodel)
        //changes from CoroutineScope to lifecycleScope
        lifecycleScope.launch {
            firebaseViewmodel.usersList.collect{ userList->
                Log.d("dfidnfd",userList.size.toString())
                userAdapter.setData(userList as ArrayList<User> /* = java.util.ArrayList<com.mandeep.chatapplication2.Firebase.data_Classes.User> */)
            }
        }
        setUpRecycleView()


        var number = 0
        binding.addUser.setOnClickListener {

            navController.navigate(UsersFragmentDirections.actionUsersFragmentToStoryFragment())


           // number++
           // firebaseViewmodel._num.value = number
        }
        //changes from CoroutineScope to lifecycleScope
        lifecycleScope.launch {
            firebaseViewmodel.num1.collect{
                binding.addUser.text = it.toString()
            }
         /*   val map = hashMapOf(  Constants.KEY_USERNAME to "username"+System.currentTimeMillis().toString(),
                Constants.KEY_EMAIL to "email",
                Constants.KEY_PASSWORD to "password",
                Constants.KEY_PHONE to "phone",
                Constants.KEY_IMAGE to "bitmapString",
                Constants.KEY_ID to System.currentTimeMillis().toString())

            firestore.collection("Users").add(map).addOnSuccessListener {

            }*/
        }


        binding.logoutbutton.setOnClickListener {
        if(firebaseAuth.currentUser!=null) {
            firebaseAuth.signOut()
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.loginFragment,true)
            navController.navigate(R.id.loginFragment,null,navOptions.build())
        }
            else{
                Toast.makeText(requireContext(),"current user is null",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setUpRecycleView(){
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.usersRecyclerView.layoutManager = linearLayoutManager
        binding.usersRecyclerView.adapter = userAdapter

        userAdapter.setOnCustomClickListener(object: UsersAdapter.CustomOnClickListener{
            override fun onCustomClick(position: Int,user: User) {
                val bundle = Bundle()
                bundle.putString(Constants.KEY_RECEIVER_ID,user.userId)
                bundle.putString(Constants.KEY_USERNAME,user.username)
                navController.navigate(R.id.chatsFragment,bundle)
            }
        })
    }
}
