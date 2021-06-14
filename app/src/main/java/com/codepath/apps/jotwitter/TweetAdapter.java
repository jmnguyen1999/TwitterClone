package com.codepath.apps.jotwitter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.jotwitter.databinding.ItemTweetBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.apps.jotwitter.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private static final String TAG = "TweetAdapter";

    ItemTweetBinding binding;
    List<Tweet> tweets;
    Context context;

    public TweetAdapter(Context context, List<Tweet> tweets){
        this.tweets = tweets;
        this.context = context;
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
        holder.bind(tweet);
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

        ImageView ivProfilePic;
        TextView tvName;
        TextView tvTimeCreated;
        TextView tvBody;
        ImageView ivEmbeddedImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivProfilePic = binding.ivOtherProfile;
            tvName = binding.tvOtherName;
            tvTimeCreated = binding.tvTimeCreated;
            tvBody = binding.tvBody;
            ivEmbeddedImage = binding.ivEmbeddedImage;
        }

        public void bind(Tweet tweet){
            User user = tweet.getUser();

            Log.d(TAG, "hasMedia = " + tweet.getHasMedia());
            if(tweet.getHasMedia()){
                ivEmbeddedImage.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.embedded_image_height);
                List<String> embeddedImageUrls = tweet.getEmbeddedImages();
                Log.d(TAG, "media urls = " + embeddedImageUrls.toString());
                //Just populate the first one:
                Glide.with(context).load(embeddedImageUrls.get(0)).into(ivEmbeddedImage);
            }

            tvName.setText(user.getName());
            tvBody.setText(tweet.getBody());
            tvTimeCreated.setText(tweet.getFormattedTimestamp());
            Glide.with(context)
                    .load(user.getProfileUrl())
                    .into(ivProfilePic);
        }
    }
}
