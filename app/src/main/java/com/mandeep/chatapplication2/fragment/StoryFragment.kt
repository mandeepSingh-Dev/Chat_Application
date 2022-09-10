package com.mandeep.chatapplication2.fragment

import android.animation.TimeInterpolator
import android.graphics.BitmapFactory
import android.graphics.Interpolator
import android.os.Build
import android.os.Bundle
import android.transition.PathMotion
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Path
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.*
import com.google.firebase.firestore.FirebaseFirestore
import com.mandeep.chatapplication2.Constants
import com.mandeep.chatapplication2.Firebase.Adapters.StoryAdapter
import com.mandeep.chatapplication2.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication2.Firebase.data_Classes.Story
import com.mandeep.chatapplication2.R
import com.mandeep.chatapplication2.SharedPreferenceManager
import com.mandeep.chatapplication2.Utils.MBitmap
import com.mandeep.chatapplication2.databinding.FragmentStoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class StoryFragment : Fragment() {

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore
    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    val firebaseViewmodel : FirebaseViewmodel by viewModels()

    val mainDispatcher = Dispatchers.Main


     var userId:String?=null
     var userName:String?=null

    var storyAdapter:StoryAdapter?=null

    lateinit var binding:FragmentStoryBinding
    lateinit var layoutView:ConstraintLayout
    lateinit var imageView: ImageView
    lateinit var progressBar : ProgressBar

    lateinit var navController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = sharedPreferenceManager.getString(Constants.KEY_USER_ID).toString()
        userName = sharedPreferenceManager.getString(Constants.KEY_USERNAME).toString()

        navController = findNavController()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedElementEnterTransition = getTransition()
        sharedElementReturnTransition = getTransition()

         //layoutView = binding.storyPresentationayoutInclude.root as ConstraintLayout
         imageView = view.findViewById(R.id.storyImagePresentation)
         progressBar = view.findViewById(R.id.progressBar)



        binding.addStoryLayout.setOnClickListener {
            launcher.launch("image/*")
        }

        storyAdapter = StoryAdapter(requireContext())
        setUpRecyclerView()

 lifecycleScope.launch{
     firebaseViewmodel.storyList.collect{
       storyAdapter?.setData(it as ArrayList<Story> /* = java.util.ArrayList<com.mandeep.chatapplication2.Firebase.data_Classes.Story> */)
         (view.parent as? ViewGroup)?.doOnPreDraw {
             startPostponedEnterTransition()
         }
     }
 }

    }

    val launcher = registerForActivityResult(ActivityResultContracts.GetContent(),
        ActivityResultCallback { uri ->
            uri?.let {

                val inputStream =  requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val bitmapString = MBitmap.encodeBitmap(bitmap)

                binding.addStoryImageView.setImageBitmap(bitmap)
                userId?.let { userid->

                    val map = hashMapOf(
                        Constants.KEY_STORY_IMAGE to bitmapString,
                        Constants.KEY_STORY_TIMING to System.currentTimeMillis().toString(),
                        Constants.KEY_USER_ID to userid,
                        Constants.KEY_STORY_USERNAME to userName
                    )

                    firebaseFirestore.collection(Constants.KEY_COLLECTION_STORY).add(map).addOnSuccessListener {
                        firebaseFirestore.collection(Constants.KEY_COLLECTION_STORY).document(it.id).update(Constants.KEY_STORY_DOC_ID,it.id.toString())
                    }
                }

            }
        })


    fun setUpRecyclerView()
    {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.storyRecyclerView.layoutManager = linearLayoutManager
        binding.storyRecyclerView.adapter = storyAdapter

        storyAdapter?.setStoryClickListener(object:StoryAdapter.StoryClickLIstener{
            @RequiresApi(Build.VERSION_CODES.N)
            override fun storyClick(story: Story,imageView : ImageView) {
                Log.d("fdnkfgdf",story.username.toString())

                //layoutView.visibility = View.VISIBLE
                    val bitmap = MBitmap.decodeBitmap(story.story_image.toString())

                    imageView.setImageBitmap(bitmap)


                val bundle = Bundle()
                bundle.putParcelable("STORY",story)
               // navController.se

                /**imagevIew - from Imageview , story.story_timing - it is transition name of target imageview (imageview in StoryPresentationFragment) */
                val extra = FragmentNavigatorExtras(imageView to story.story_timing.toString())
                navController.navigate(R.id.storyPresentationFragment,bundle,null,extra)

               // val extras = ActivityNavigatorExtras(options)


            }
        })
    }

    private fun getTransition(): Transition? {

        val transitionSet = TransitionSet()
        transitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
        transitionSet.addTransition(ChangeBounds())
        transitionSet.addTransition(ChangeClipBounds())
        transitionSet.addTransition(ChangeTransform())
        transitionSet.addTransition(ChangeImageTransform())
        transitionSet.setDuration(1000)

        return transitionSet
    }

}