package com.codepath.apps.jotwitter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.jotwitter.R;
import com.codepath.apps.jotwitter.adapters.TweetAdapter;
import com.codepath.apps.jotwitter.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.apps.jotwitter.models.User;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

//Purpose:      Displays details of a given Tweet object!
public class TweetDetailActivity extends AppCompatActivity {
    private static final String TAG = "TweetDetailActivity";
    private static final String KEY_DETAIL_ACT = "tweetForDetailActivity";

    ActivityTweetDetailBinding binding;

    List<Tweet> replies;
    RecyclerView rvReplies;
    TweetAdapter replyAdapter;

    //Views:
    //For our original tweet replying to:
    ImageView ivProfilePic;
    TextView tvName;
    TextView tvTimeCreated;
    TextView tvBody;
    ImageView ivEmbeddedImage;
    //ListView embeddedImageContainer;
    //ImageView ivComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTweetDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replies = new ArrayList<>();

        //Get the received Tweet:
        Intent receivedData = getIntent();
        Tweet originalTweet = Parcels.unwrap(receivedData.getParcelableExtra(KEY_DETAIL_ACT));

        //Connect Views:
        rvReplies = binding.rvRelies;
        ivProfilePic = binding.ivOtherProfile;
        tvName = binding.tvOtherName;
        tvTimeCreated = binding.tvTimeCreated;
        tvBody = binding.tvBody;
        //   embeddedImageContainer = binding.embeddedImagesContainer;
        ivEmbeddedImage = binding.ivEmbeddedImage;
        //ivComment = binding.icCommentIcon;

        populateOriginalTweet(originalTweet);

        //Set up RecyclerView:
        TweetAdapter.TweetAdapterListener listener = new TweetAdapter.TweetAdapterListener() {
            @Override
            public void onClick(Tweet tweetClicked) {}
        };

        replyAdapter = new TweetAdapter(TweetDetailActivity.this, replies, listener);
        rvReplies.setAdapter(replyAdapter);
        rvReplies.setLayoutManager(new LinearLayoutManager(this));

        String tweetId = originalTweet.getId();
        Log.d(TAG, "tweetId given = " + tweetId);
        getAllReplies(tweetId);
    }

    public void populateOriginalTweet(Tweet tweet){
        User user = tweet.getUser();
        if(!(tweet.getHasMedia())){
            ivEmbeddedImage.setVisibility(View.GONE);
        }
        else{
            //   embeddedImageContainer.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.embedded_image_height);
            //ivEmbeddedImage.getLayoutParams().height = (int) context.getResources().getDimension(R.dimen.embedded_image_height);
            ArrayList<String> embeddedImageUrls = tweet.getEmbeddedImages();
            Log.d(TAG, "media urls = " + embeddedImageUrls.toString());
            Glide.with(this).load(embeddedImageUrls.get(0)).into(ivEmbeddedImage);
            //Set up ListView:
              /*  EmbeddedImageAdapter imageAdapter = new EmbeddedImageAdapter(context, embeddedImageUrls);
                embeddedImageContainer.setAdapter(imageAdapter);*/
        }

        tvName.setText(user.getName());
        tvBody.setText(tweet.getBody());
        tvTimeCreated.setText(tweet.getFormattedTimestamp());
        Glide.with(this)
                .load(user.getProfileUrl())
                .into(ivProfilePic);
    }

    //Purpose:      Get all the replies to the given tweet id.
    public void getAllReplies(String tweetId){
        Log.d(TAG, "getAllReplies");
    }
}