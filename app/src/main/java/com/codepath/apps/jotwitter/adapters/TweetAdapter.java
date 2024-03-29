package com.codepath.apps.jotwitter.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.jotwitter.databinding.ItemTweetBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.apps.jotwitter.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private static final String TAG = "TweetAdapter";

    ItemTweetBinding binding;
    List<Tweet> tweets;
    Context context;
    private TweetAdapterListener listener;

    public interface TweetAdapterListener{
        void onTweetClick(Tweet tweetClicked);
        void onCommentClick(Tweet tweetClicked);
        void onHeartClick(Tweet tweetClicked, ImageView heartIcon);
        void onProfilePicClicked(Tweet tweetClicked);
        void onRetweetClicked(Tweet tweetClicked, ImageView retweetIcon);
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
        Log.d(TAG, "onCreatieView");
        binding = ItemTweetBinding.inflate(LayoutInflater.from(context), parent, false);
        return new TweetAdapter.ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TweetAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        Tweet tweet = tweets.get(position);
        holder.bind(tweet, position);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount(): size = " + tweets.size());
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
        TextView tvUsername;
        //ListView embeddedImageContainer;

        ImageView ivComment;
        ImageView ivHeart;
        ImageView ivRetweet;

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
            ivHeart = binding.ivFavoriteIcon;
            ivRetweet = binding.ivRetweetIcon;
            tvUsername = binding.tvUsername;
        }

        public void bind(Tweet tweet, int position){
            Log.d(TAG, "binding postion: " + position);
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
                Glide.with(context)
                        .load(embeddedImageUrls.get(0))
                        .transform(new RoundedCornersTransformation(40, 10))
                        .into(ivEmbeddedImage);
                //Set up ListView:
              /*  EmbeddedImageAdapter imageAdapter = new EmbeddedImageAdapter(context, embeddedImageUrls);
                embeddedImageContainer.setAdapter(imageAdapter);*/
            }

            tvName.setText(user.getName());
            tvBody.setText(tweet.getBody());
            tvUsername.setText("@"+user.getUsername());
            tvTimeCreated.setText(tweet.getFormattedTimestamp());
            Glide.with(context)
                    .load(user.getProfileUrl())
                    .centerCrop() // scale image to fill the entire ImageView
                    .transform(new CircleCrop())
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

            ivHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onHeartClick(tweet, ivHeart);
                }
            });
            ivProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onProfilePicClicked(tweet);
                }
            });
            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRetweetClicked(tweet, ivRetweet);
                }
            });
        }
    }
}
