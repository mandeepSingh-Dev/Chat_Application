package com.mandeep.chatapplication2.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mandeep.chatapplication2.Constants
import com.mandeep.chatapplication2.Firebase.data_Classes.Chat
import com.mandeep.chatapplication2.Firebase.FirebaseViewmodel
import com.mandeep.chatapplication2.R
import com.mandeep.chatapplication2.Utils.MBitmap
import com.mandeep.chatapplication2.databinding.BottomSheetImageDialodueBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class BottomSheetDialogue: BottomSheetDialogFragment() {

    lateinit var binding: BottomSheetImageDialodueBinding

    var chatt: Chat?=null
    var imageString:String?=null

    val firebaseViewmodel:FirebaseViewmodel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("dfjbdfd","onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetImageDialodueBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        arguments?.let {
           imageString = it.getString(Constants.KEY_IMAGE).toString()
            imageString?.let {
                val bitmap = MBitmap.decodeBitmap(it)
                val newBitmap = bitmap?.let {
                    resizeBitmap(it)
                }
                binding.imageVIewDialogue.setImageBitmap(bitmap)


                Log.d("sffkdnfd",bitmap?.width.toString()+"heigth"+bitmap?.height.toString())

                binding.checkImageDialogue.setOnClickListener {
                    bitmap?.let {
                       val isSaved = MBitmap.saveToStorage(requireContext(),bitmap)
                        firebaseViewmodel._isSaved.value = isSaved
                        firebaseViewmodel._downloadText.value = "Image Downloaded"
                    }
                }
            }

            lifecycleScope.launch {
                firebaseViewmodel._isSaved.collect {
                    if(it){
                        binding.checkImageDialogue.setImageDrawable(resources.getDrawable(R.drawable.ic_outline_file_download_done_24,null))
                        firebaseViewmodel.downloadText.collect{
                            binding.SelectedImageTextView.text = it
                        }
                    }else{
                        binding.checkImageDialogue.setImageDrawable(resources.getDrawable(R.drawable.ic_outline_file_download_24,null))
                        firebaseViewmodel.downloadText.collect{
                            binding.SelectedImageTextView.text = it
                        }
                    }
                }
            }


        }




    }

    override fun onPause() {
        super.onPause()
        Log.d("difindgd","onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("difindgd","onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("difindgd","onDestroy")
    }

    fun resizeBitmap(bitmap: Bitmap):Bitmap{

        val bitmap = if(bitmap.height > 400)
        {
            val bOut = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bOut)
            val byetArray = bOut.toByteArray()
            val b = BitmapFactory.decodeByteArray(byetArray,0,byetArray.size)
            val scaledBitmap = Bitmap.createScaledBitmap(b,400,400,false)
              scaledBitmap
        }else{
            bitmap
        }
        return bitmap
    }
}
