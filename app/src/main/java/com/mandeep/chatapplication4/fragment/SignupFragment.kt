package com.mandeep.chatapplication4.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mandeep.chatapplication4.Constants
import com.mandeep.chatapplication4.Constants.IS_SIGNED_IN
import com.mandeep.chatapplication4.Constants.KEY_COLOR
import com.mandeep.chatapplication4.Constants.KEY_DOC_ID
import com.mandeep.chatapplication4.Constants.KEY_EMAIL
import com.mandeep.chatapplication4.Constants.KEY_ID
import com.mandeep.chatapplication4.Constants.KEY_IMAGE
import com.mandeep.chatapplication4.Constants.KEY_IS_ONLINE
import com.mandeep.chatapplication4.Constants.KEY_PASSWORD
import com.mandeep.chatapplication4.Constants.KEY_PHONE
import com.mandeep.chatapplication4.Constants.KEY_RECENT_TIME
import com.mandeep.chatapplication4.Constants.KEY_USERNAME
import com.mandeep.chatapplication4.Constants.KEY_USER_ID
import com.mandeep.chatapplication4.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication4.R
import com.mandeep.chatapplication4.SharedPreferenceManager
import com.mandeep.chatapplication4.Utils.MBitmap
import com.mandeep.chatapplication4.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@AndroidEntryPoint
class SignupFragment : Fragment() {

    @Inject
    lateinit var auth:FirebaseAuth
    @Inject
    lateinit var firestore:FirebaseFirestore
    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore
    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    val firebaseViewmodel :FirebaseViewmodel by viewModels()

    lateinit var binding : FragmentSignupBinding
    var dispatcher = Dispatchers.Main

    var username = " "
    var phone = " "
    var email = " "
    var password = " "
    var bitmap:Bitmap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("dfigndf",sharedPreferenceManager.getString(KEY_USER_ID).toString())

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.profileImageView.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.SignUpButton.setOnClickListener {
            username =  binding.userNameInput.editText?.text.toString()
            email = binding.emailInput.editText?.text.toString()
            password =  binding.passwordInput.editText?.text.toString()
            phone = binding.phoneInput.editText?.text.toString()
            val bitmapString = bitmap?.let { MBitmap.encodeBitmap(it) }

            Log.d("fidnfdf",bitmapString.toString())
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                Toast.makeText(context,"Sign Up Successfully",Toast.LENGTH_LONG).show()
                sharedPreferenceManager.putBoolean(IS_SIGNED_IN,true)


                val map = hashMapOf(  KEY_USERNAME to username,
                    KEY_EMAIL to email,
                    KEY_PASSWORD to password,
                    KEY_PHONE to phone,
                    KEY_IMAGE to bitmapString,
                    KEY_ID to System.currentTimeMillis().toString(),
                    KEY_IS_ONLINE to true,
                    KEY_RECENT_TIME to System.currentTimeMillis().toString(),
                    KEY_COLOR to null,
                    KEY_DOC_ID to null
                )

                val collection = firestore.collection(Constants.KEY_COLLECTION_USER)
                collection.add(map).addOnSuccessListener {

                    sharedPreferenceManager.putString(KEY_USER_ID,it.id)
                    sharedPreferenceManager.putString(KEY_USERNAME,username)

                    firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).document(it.id.toString()).update(Constants.KEY_DOC_ID,it.id.toString())


                  //  findNavController().popBackStack()
                    findNavController().navigate(R.id.usersFragment)

                }.addOnFailureListener { Log.d("vgfudvd",it.message.toString()) }
                Log.d("dfgindfvd",collection.id+" ")
            }.addOnFailureListener { Log.d("fdsdifnsdf",it.message.toString()) }
        }

    }


    val launcher = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            it?.let {
               val ipStream = context?.contentResolver?.openInputStream(it)
                 bitmap = BitmapFactory.decodeStream(ipStream)
                binding.profileImageView.setImageBitmap(bitmap)
            }
        })

}