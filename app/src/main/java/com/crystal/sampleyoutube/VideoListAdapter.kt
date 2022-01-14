package com.crystal.sampleyoutube

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoListAdapter(val callback: (sourceUrl:String, title:String) -> Unit):ListAdapter<VideoModel, VideoListAdapter.ViewHolder>(diffUtil) {
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: VideoModel){
            view.findViewById<TextView>(R.id.titleTextView).text =item.title
            view.findViewById<TextView>(R.id.subtitleTextView).text =item.subtitle

            val thumbnailImageView = view.findViewById<ImageView>(R.id.videoThumbImageView)
            Glide.with(thumbnailImageView.context)
                .load(item.thumb)
                .into(thumbnailImageView)

            view.setOnClickListener {
                callback(item.source, item.title)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video,parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object:DiffUtil.ItemCallback<VideoModel>(){
            override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}