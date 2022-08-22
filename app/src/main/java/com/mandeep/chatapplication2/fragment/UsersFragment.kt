package com.mandeep.chatapplication2.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.mandeep.chatapplication2.Constants
import com.mandeep.chatapplication2.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication2.Firebase.User
import com.mandeep.chatapplication2.Firebase.UsersAdapter
import com.mandeep.chatapplication2.R
import com.mandeep.chatapplication2.SharedPreferenceManager
import com.mandeep.chatapplication2.databinding.FragmentUsersBinding
import com.mandeep.chatapplication2.databinding.UserItemBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UsersFragment : Fragment() {

    lateinit var binding:FragmentUsersBinding

    val firebaseViewmodel by lazy {
        ViewModelProvider(this).get(FirebaseViewmodel::class.java)
    }

    lateinit var adapter : UsersAdapter

    @Inject
    lateinit var firestore: FirebaseFirestore
    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager
    @Inject
    lateinit var mainDispatcher:MainCoroutineDispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("dfjd675485844bfd",firebaseViewmodel.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentUsersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = sharedPreferenceManager.getString(Constants.KEY_USER_ID)
        binding.userIdTextView.text = userId


        adapter = UsersAdapter(requireContext())
        CoroutineScope(mainDispatcher).launch {
            firebaseViewmodel.usersList.collect{
                Log.d("dfidnfd",it.size.toString())
                val linearLayoutManager = LinearLayoutManager(requireContext())
                binding.usersRecyclerView.layoutManager = linearLayoutManager
                binding.usersRecyclerView.adapter = adapter
                adapter.setData(it as ArrayList<User>)

                adapter.setOnCustomClickListener(object:UsersAdapter.CustomOnClickListener{
                    override fun onCustomClick(position: Int,user:User) {
                        val bundle = Bundle()
                        bundle.putString(Constants.KEY_RECEIVER_ID,user.userId)
                        bundle.putString(Constants.KEY_USERNAME,user.username)
                        findNavController().navigate(R.id.chatsFragment,bundle)
                    }
                })
            }
        }

        var number = 0
        binding.addUser.setOnClickListener {

            number++
            firebaseViewmodel._num.value = number
        }
             CoroutineScope(Dispatchers.Main).launch {
            firebaseViewmodel.state.collect{
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

    }

}
