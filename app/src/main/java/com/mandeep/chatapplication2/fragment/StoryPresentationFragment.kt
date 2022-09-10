package com.mandeep.chatapplication2.fragment

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.transition.*
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mandeep.chatapplication2.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication2.Firebase.data_Classes.Story
import com.mandeep.chatapplication2.R
import com.mandeep.chatapplication2.Utils.MBitmap
import com.mandeep.chatapplication2.databinding.FragmentStoryPresentationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


@AndroidEntryPoint
class StoryPresentationFragment : Fragment() {



    lateinit var binding : FragmentStoryPresentationBinding
    var story: Story?=null
    var count = 0
    lateinit var navController:NavController

     val firebaseViewmodel: FirebaseViewmodel by viewModels()
         var job:Job?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            story = it.getParcelable("STORY")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStoryPresentationBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("digijfdgfd",story?.story_timing.toString() )

        binding.storyImagePresentation.transitionName = story?.story_timing

        sharedElementEnterTransition = getTransition()
        sharedElementReturnTransition = getTransition()

        navController =  findNavController()

            val bitmap = MBitmap.decodeBitmap(story?.story_image.toString())

            binding.storyImagePresentation.setImageBitmap(bitmap)


           job =   CoroutineScope(Dispatchers.Main).launch {
                          updateProgressBar()
            }

        lifecycleScope.launch {
            firebaseViewmodel.count.collect{
             it?.let {
                 Log.d("dgknjdgd",it.toString())
                 if(it == 10)
                 {
                     if(navController.currentDestination?.id == R.id.storyPresentationFragment) {


                         navController.navigate(R.id.action_storyPresentationFragment_to_storyFragment)
                     }
                 }
             }
            }
        }


        binding.root.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action)
            {
                MotionEvent.ACTION_BUTTON_PRESS ->{ Log.d("dkndfd","ACTION_BUTTON_PRESS") }
                MotionEvent.ACTION_UP -> { Log.d("dkndfd","ACTION_UP")
                   job = CoroutineScope(Dispatchers.Main).launch {
                        updateProgressBar()
                    }
                    firebaseViewmodel._update.value = true
                }
                MotionEvent.ACTION_DOWN -> { Log.d("dkndfd","ACTION_DOWN")
                    job?.cancel()
                    firebaseViewmodel._update.value = false
                }
                else -> {Log.d("dkndfd","else")}
            }
            return@setOnTouchListener true
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
   suspend fun updateProgressBar(){
        while(count < 10) {
            count++
               firebaseViewmodel._count.value = count
            binding.progressBar.setProgress(count,true)
            delay(500)
        }


    }

    override fun onStop() {
        super.onStop()
        firebaseViewmodel._count.value = 0

        Log.d("fidngdf","onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("dighdgfd","ondestory")
    }

    private fun getTransition(): Transition? {
        val transitionSet = TransitionSet()
        transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER)
        transitionSet.addTransition(ChangeBounds())
        transitionSet.addTransition(ChangeClipBounds())
        transitionSet.addTransition(ChangeTransform())
        transitionSet.addTransition(ChangeImageTransform())
        return transitionSet
    }

}