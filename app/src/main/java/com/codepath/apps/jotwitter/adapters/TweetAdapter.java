package com.codepath.apps.jotwitter.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.jotwitter.R;
import com.codepath.apps.jotwitter.activities.ComposeActivity;
import com.codepath.apps.jotwitter.activities.TimelineActivity;
import com.codepath.apps.jotwitter.databinding.ItemTweetBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.apps.jotwitter.models.User;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private static final String TAG = "TweetAdapter";

    ItemTweetBinding binding;
    List<Tweet> tweets;
    Context context;
    private TweetAdapterListener listener;

    public interface TweetAdapterListener{
        void onTweetClick(Tweet tweetClicked);
        void onCommentClick(Tweet tweetClicked);
    }
    public TweetAdapter(Context context, List<Tweet> tweets, TweetAdapterListener listener){
        this.tweets = tweets;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public TweetAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        binding = ItemTweetBinding.inflate(LayoutInflater.from(context), parent, false);
        return new TweetAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TweetAdapter.ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet, position);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list){
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout rlTweetContainer;
        ImageView ivProfilePic;
        TextView tvName;
        TextView tvTimeCreated;
        TextView tvBody;
        ImageView ivEmbeddedImage;
        //ListView embeddedImageContainer;

        ImageView ivComment;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivProfilePic = binding.ivOtherProfile;
            tvName = binding.tvOtherName;
            tvTimeCreated = binding.tvTimeCreated;
            tvBody = binding.tvBody;
         //   embeddedImageContainer = binding.embeddedImagesContainer;
            ivEmbeddedImage = binding.ivEmbeddedImage;
            ivComment = binding.icCommentIcon;
            rlTweetContainer = binding.rlTweetContainer;
        }

        public void bind(Tweet tweet, int position){
            //1.) Populate data to the views:
            User user = tweet.getUser();
            if(!(tweet.getHasMedia())){
                ivEmbeddedImage.setVisibility(View.GONE);
            }
            else{
             //   embeddedImageContainer.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.embedded_image_height);
                //ivEmbeddedImage.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.embedded_image_height);
                ArrayList<String> embeddedImageUrls = tweet.getEmbeddedImages();
                Log.d(TAG, "media urls = " + embeddedImageUrls.toString());
                Glide.with(context).load(embeddedImageUrls.get(0)).into(ivEmbeddedImage);
                //Set up ListView:
              /*  EmbeddedImageAdapter imageAdapter = new EmbeddedImageAdapter(context, embeddedImageUrls);
                embeddedImageContainer.setAdapter(imageAdapter);*/
            }

            tvName.setText(user.getName());
            tvBody.setText(tweet.getBody());
            tvTimeCreated.setText(tweet.getFormattedTimestamp());
            Glide.with(context)
                    .load(user.getProfileUrl())
                    .into(ivProfilePic);

            //2.) Set on click listeners:
            //2a.) On comment button --> go to ComposeActivity to reply to the given user:
            ivComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   listener.onCommentClick(tweet);
                }
            });

            rlTweetContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTweetClick(tweet);
                }
            });
        }
    }
}
