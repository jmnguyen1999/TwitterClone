package com.codepath.apps.jotwitter;

import android.content.Context;
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivProfilePic;
        TextView tvName;
        TextView tvTimeCreated;
        TextView tvBody;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivProfilePic = binding.ivOtherProfile;
            tvName = binding.tvOtherName;
            tvTimeCreated = binding.tvTimeCreated;
            tvBody = binding.tvBody;
        }

        public void bind(Tweet tweet){
            User user = tweet.getUser();

            tvName.setText(user.getName());
            tvBody.setText(tweet.getBody());
            tvTimeCreated.setText(tweet.getFormattedTimestamp());
            Glide.with(context)
                    .load(user.getProfileUrl())
                    .into(ivProfilePic);
        }
    }
}
