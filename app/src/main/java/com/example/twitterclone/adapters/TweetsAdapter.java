package com.example.twitterclone.adapters;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.twitterclone.R;
import com.example.twitterclone.models.Tweet;

import java.util.List;
/**
 * TweetsAdapter.java
 * Purpose:                         The adapter for the RecyclerView used in TimelineActivity. Formats every Tweet in a given position of the RecyclerView. Uses own implementation of a ViewHolder{}
 *
 * Classes Used:                    Tweet{}, User{}
 * Corresponding Layout file:       item_item.xml
 *
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{
    Context context;
    List<Tweet> tweets;

    //Constructor:  Pass in the context and model (List of Tweet objects)
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    /**
     * Purpose:     To create and return an instance of TweetsAdapter.ViewHolder tied to the layout file, "item_tweet.xml". Inflates this layout to every row of the RecyclerView
     */
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    /**
     * Purpose:     Called automatically to bind data to the ViewHolder given a row position of the RecyclerView. Calls the ViewHolder{}'s bind() instead of actually binding it here.
     * @param holder the ViewHolder to use
     * @param position, the row position to bind data to
     */
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    /**
     * Purpose:         Erases/resets the List<Tweets> being used a model to populate. Intended to be used in pair with addAll() to "refresh" the data.
     */
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    /**
     * Purpose:         To update the List<Tweets>"tweets" being used as the model with more recent data.
     * @param tweetList, the more recent data to save
     */
    public void addAll(List<Tweet> tweetList){
        tweets.addAll(tweetList);
        notifyDataSetChanged();
    }


    /**
     * Purpose:                         The ViewHolder{} implementation to use to populate data from Tweet objects. Obtains access to the Views used in "item_tweet.xml" to update them.
     * Classes used:                    Tweet{}, User{}
     * Corresponding Layout File:       "item_tweet.xml"
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProfile;
        TextView tvScreenName;
        TextView tvBody;
        TextView tvTimestamp;
        TextView tvName;

        //Constructor:      Connects all Views with the Views used the corresponding layout file: "item_tweet.xml"
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvName = itemView.findViewById(R.id.tvName);
        }

        /**
         * Purpose:         Binds given Tweet data to each corresponding View to display on the RecyclerView
         * @param tweet, the Tweet object of which data to bind
         */
        public void bind(Tweet tweet){
            tvBody.setText(tweet.getBody());
            tvScreenName.setText("(@"+tweet.getUser().getScreenName()+")");
            tvTimestamp.setText(tweet.getFormattedTimestamp());
            tvName.setText(tweet.getUser().getName());
            Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(ivProfile);     //gets profile image from the Tweet object's User field
        }
    }
}
