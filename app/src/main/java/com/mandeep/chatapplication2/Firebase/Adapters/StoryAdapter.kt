package com.mandeep.chatapplication2.Firebase.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.compose.ui.layout.Layout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.mandeep.chatapplication2.Firebase.data_Classes.Story
import com.mandeep.chatapplication2.R
import com.mandeep.chatapplication2.Utils.MBitmap

class StoryAdapter(val context:Context) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>(){


    var storylist = ArrayList<Story>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
      val view = LayoutInflater.from(context).inflate(R.layout.story_item,parent,false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
    val story = storylist[position]
        holder.bind(story)
     /*   val bitmap = MBitmap.decodeBitmap(story.story_image.toString())
        holder.storyImageView.setImageBitmap(bitmap)
        holder.storyTextview.text = story.username

        holder.itemView.setOnClickListener {
            storyClickLIstener?.storyClick(story)
        }*/
    }

    inner class StoryViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        val storyImageView = itemView.findViewById<ImageView>(R.id.storyImageView)
        val storyTextview = itemView.findViewById<TextView>(R.id.storyTextView)

        fun bind(story:Story){
            ViewCompat.setTransitionName(storyImageView,story.story_timing)
            val bitmap = MBitmap.decodeBitmap(story.story_image.toString())
            storyImageView.setImageBitmap(bitmap)
            storyTextview.text = story.username

            itemView.setOnClickListener {
                storyClickLIstener?.storyClick(story,storyImageView)
            }
        }
    }
    override fun getItemCount(): Int {
        return storylist.size
    }


    fun setData(storylist : ArrayList<Story>){
        this.storylist = storylist
        notifyDataSetChanged()
    }

    interface StoryClickLIstener{
        fun storyClick(story:Story,view:ImageView)
    }
    var storyClickLIstener : StoryClickLIstener?=null
    fun setStoryClickListener(storyClickLIstener: StoryClickLIstener){
        this.storyClickLIstener = storyClickLIstener
    }

}