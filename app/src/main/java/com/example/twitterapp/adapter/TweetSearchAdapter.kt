package com.example.twitterapp.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import com.bumptech.glide.Glide
import com.example.twitterapp.R
import com.example.twitterapp.model.MediaModel
import com.example.twitterapp.model.TweetModel


class TweetSearchAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var tweetList: ArrayList<TweetModel> = ArrayList()

    internal class TweetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgTweet: ImageView = view.findViewById(R.id.img_tweet)
        val tvTweetDate: TextView = view.findViewById(R.id.tv_tweet_date)
        val tvTweetUserHandle: TextView = view.findViewById(R.id.tv_tweet_user_handle)
        val tvUserDescription: TextView = view.findViewById(R.id.tv_user_description)
        val tvTweetText: TextView = view.findViewById(R.id.tv_tweet_text)

        val vvTweet: VideoView = view.findViewById(R.id.vvTweet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TweetViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.tweet_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val tweetModel = tweetList[position]

        (holder as TweetViewHolder).tvTweetDate.text = tweetModel.createdAt
        holder.tvTweetUserHandle.text = tweetModel.user!!.screenName
        holder.tvUserDescription.text = tweetModel.user.description
        holder.tvTweetText.text = tweetModel.text

        if (tweetModel.entities?.media != null && tweetModel.entities.media.size > 0) {
            val mediaModel: MediaModel = tweetModel.entities.media[0]
            if (mediaModel.type.equals("photo")) {
                Glide.with(context).load(mediaModel.mediaUrl).error(R.mipmap.ic_launcher_round).into(holder.imgTweet)
            }
        }

        if (tweetModel.extendedEntities?.media != null && tweetModel.extendedEntities.media.size > 0) {
            val mediaModel: MediaModel = tweetModel.extendedEntities.media[0]
            if (mediaModel.type.equals("video") && mediaModel.videoInfo != null) {

                holder.vvTweet.visibility = View.VISIBLE

                val tweetUrl = mediaModel.videoInfo.variants[0].url

                val mediaController = MediaController(context)
                holder.vvTweet.setMediaController(mediaController)
                holder.vvTweet.setVideoURI(Uri.parse(tweetUrl))
            }
        }
    }

    override fun getItemCount(): Int {
        return tweetList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
