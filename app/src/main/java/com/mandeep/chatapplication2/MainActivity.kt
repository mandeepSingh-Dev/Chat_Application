package com.mandeep.chatapplication2

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import com.mandeep.chatapplication2.Connectivity.MyConnectivityManager
import com.mandeep.chatapplication2.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication2.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var job:Job

    val firebaseViewmodel by viewModels<FirebaseViewmodel>()
    lateinit var binding:ActivityMainBinding
    private val findNavController:NavController by  lazy { Navigation.findNavController(this,R.id.nav_host_fragment) }

    @Inject
    lateinit var sharedPreferenceManager :SharedPreferenceManager
    @Inject
    lateinit var firebaseStore:FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        Log.d("fodnfdf",firebaseViewmodel.toString())

        val manager = MyConnectivityManager(this)

         job =  lifecycleScope.launch {
             manager.observe().collect{ state ->
                  Log.d("dfiidnfd",state.toString())
             }
         }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("dfidnfd","bina wala")
        if(sharedPreferenceManager.getBoolean(Constants.IS_SIGNED_IN)){

            if(findNavController.currentDestination?.id == R.id.loginFragment){
                Log.d("dfidnfd","login wala")
                  finish()
            }else{
                Log.d("dfidnfd","bina login wala")
                super.onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        firebaseStore.collection(Constants.KEY_COLLECTION_USER).document(sharedPreferenceManager.getString(Constants.KEY_USER_ID).toString()).update("id",System.currentTimeMillis().toString())
    }

    override fun onStop() {
        super.onStop()
        firebaseStore.collection(Constants.KEY_COLLECTION_USER).document(sharedPreferenceManager.getString(Constants.KEY_USER_ID).toString()).update("id",System.currentTimeMillis().toString())
    }

}