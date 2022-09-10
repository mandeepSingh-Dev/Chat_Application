package com.mandeep.chatapplication2.retrofit

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mandeep.chatapplication2.R
import com.mandeep.chatapplication2.retrofit.DataClass.Hit

class WallpaperAdapter(val context:Context):RecyclerView.Adapter<WallpaperAdapter.MyWallViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyWallViewHolder {
    val view = LayoutInflater.from(context).inflate(R.layout.wallpapers_item,parent,false)
       return MyWallViewHolder(view)
    }

    @SuppressLint("PrivateResource")
    override fun onBindViewHolder(holder: MyWallViewHolder, position: Int) {
           val hit = hitlist[position]
        Glide.with(context).load(hit.previewURL).transition(DrawableTransitionOptions.withCrossFade()).placeholder(
            com.google.android.material.R.drawable.mtrl_ic_error).error(com.google.android.material.R.drawable.mtrl_ic_error).into(holder.wallpaperImageView)

        holder.itemView.setOnClickListener {
            wallpaperListener.wallpaperClickk(hit)
        }

    }

    override fun getItemCount(): Int {
      return   hitlist.size
    }

    inner class MyWallViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
      val wallpaperImageView = itemView.findViewById<ImageView>(R.id.wallpapersImageVIew)
    }

    var hitlist = ArrayList<Hit>()
    fun setData(list:ArrayList<Hit>)
    {
        hitlist = list
        notifyDataSetChanged()
    }

    interface WallpaperListener{
        fun wallpaperClickk(hit: Hit)
    }

    lateinit var wallpaperListener: WallpaperListener
    fun setWallpaperListenr(wallpaperListener: WallpaperListener){
        this.wallpaperListener = wallpaperListener
    }


}