package com.mandeep.chatapplication4.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mandeep.chatapplication4.databinding.WallpapersDialgueBinding
import com.mandeep.chatapplication4.retrofit.DataClass.Hit
import com.mandeep.chatapplication4.retrofit.WallpaperAdapter


class BottomSheetDialogue3(val hitList:ArrayList<Hit>,val fragment : ChatsFragment):BottomSheetDialogFragment()
{
    lateinit var binding : WallpapersDialgueBinding
    var wallpaperAdapter : WallpaperAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         // wallpaperListener = fragment as WallpaperListener_fragment

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = WallpapersDialgueBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wallpaperAdapter = WallpaperAdapter(requireContext())
        wallpaperAdapter?.setData(hitList)

        val layoutManager = GridLayoutManager(requireContext(),2)
        binding.wallpapersRecycelrView.layoutManager = layoutManager
        binding.wallpapersRecycelrView.adapter = wallpaperAdapter

        wallpaperAdapter?.setWallpaperListenr(fragment/*object:WallpaperAdapter.WallpaperListener{
            override fun wallpaperClickk(hit: Hit) {
                Log.d("48th34gt",hit.toString())
            // wallpaperListener.wallpaperClick(hit)
            }
        }*/)
    }

   /* lateinit var wallpaperListener :WallpaperListener_fragment
    interface WallpaperListener_fragment{
        fun wallpaperClick(hit:Hit)
    }*/
}