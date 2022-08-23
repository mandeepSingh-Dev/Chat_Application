package com.mandeep.chatapplication2.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mandeep.chatapplication2.Constants
import com.mandeep.chatapplication2.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication2.R
import com.mandeep.chatapplication2.SharedPreferenceManager
import com.mandeep.chatapplication2.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var sharedPreferenceManager:SharedPreferenceManager
    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    @Inject
    lateinit var firestore: FirebaseFirestore

    val firebaseViewmodel:FirebaseViewmodel by viewModels()
    lateinit var binding : FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(LayoutInflater.from(requireContext()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.LoginButton.setOnClickListener {
            val email = binding.edittextEmail.editText?.text.toString()
            val password = binding.edittextpassword.editText?.text.toString()
            Log.d("digfndf",email.toString())

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

                Log.d("fidnfd","Done")
                lifecycleScope.launch {
                    firebaseViewmodel.usersList.collect{userlistt->
                        userlistt.forEach { user->
                            Log.d("digfndf",user.email)
                            if (user.email.equals(email))
                            {
                               // sharedPreferenceManager.setB
                                sharedPreferenceManager.putBoolean(Constants.IS_SIGNED_IN,true)
                                sharedPreferenceManager.putString(Constants.KEY_USER_ID,user.userId)
                                sharedPreferenceManager.putString(Constants.KEY_USERNAME,user.username)
                                findNavController().navigate(R.id.usersFragment)
                            }
                        }
                    }

                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"Wrong Credentials \n or May be user not exist ",Toast.LENGTH_SHORT).show()
            }
        }
        binding.signupButton.setOnClickListener {
            findNavController().navigate(R.id.signupFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        if(sharedPreferenceManager.getBoolean(Constants.IS_SIGNED_IN))
        {
            findNavController().navigate(R.id.usersFragment)
        }
    }
}