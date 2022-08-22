package com.mandeep.chatapplication2

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.mandeep.chatapplication2.Connectivity.MyConnectivityManager
import com.mandeep.chatapplication2.Firebase.FirebaseViewmodel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var job:Job
    val firebaseViewmodel by viewModels<FirebaseViewmodel>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("fodnfdf",firebaseViewmodel.toString())

        val manager = MyConnectivityManager(this)

      job =  lifecycleScope.launchWhenResumed {
            Log.d("doindffd","Resumed")
        manager.observe().collect{ state ->
            Log.d("dfdnkfd",state.toString())
            manager.state.collect{
                Log.d("idnfdf",it.toString())
                Toast.makeText(this@MainActivity,it.toString(),Toast.LENGTH_LONG).show()
            }
            Log.d("dfindfd",manager.state.toString())
        }
        }

        lifecycleScope.launchWhenCreated {
            manager.observe().collect{
Log.d("dfidnfd",it.toString())
            }
        }

    }

}