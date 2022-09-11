package com.mandeep.chatapplication4.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mandeep.chatapplication4.Firebase.Adapters.ColorAdapter
import com.mandeep.chatapplication4.R
import com.mandeep.chatapplication4.databinding.FragmentBottomSheetDialogue2Binding


class BottomSheetDialogue2 : BottomSheetDialogFragment() {

    lateinit var binding : FragmentBottomSheetDialogue2Binding
    lateinit var bottomsheet2listener:Bottomsheet2listener
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentBottomSheetDialogue2Binding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list = listOf(R.color.teal_700,
        R.color.black,
        R.color.purple_200,
        R.color.purple_500,
        R.color.purple_700,
            R.color.teal_200,
            R.color.white)

        val colorAdapter = ColorAdapter(requireContext(),list)
        val layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.colorRecyclerView.layoutManager = layoutManager
        binding.colorRecyclerView.adapter = colorAdapter

        colorAdapter.setCustomClcikCListern(object: ColorAdapter.CustomClickListenr{
            override fun colorClick(color: Int) {
                bottomsheet2listener.colorClickBottomsheet(color)
            }
        })


    }
    var color = 0
    fun setColorr(color:Int){
        Log.d("difndf",color.toString())
    }

    interface Bottomsheet2listener{
        fun colorClickBottomsheet(color:Int)
    }

    fun setBottomSheet2Listener(bottomsheet2listener: Bottomsheet2listener){
        this.bottomsheet2listener = bottomsheet2listener
    }
}

