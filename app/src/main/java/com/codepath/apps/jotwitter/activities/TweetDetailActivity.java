package com.codepath.apps.jotwitter.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.jotwitter.ComposeDialog;
import com.codepath.apps.jotwitter.R;
import com.codepath.apps.jotwitter.TwitterApp;
import com.codepath.apps.jotwitter.TwitterClient;
import com.codepath.apps.jotwitter.adapters.TweetAdapter;
import com.codepath.apps.jotwitter.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.apps.jotwitter.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

//Purpose:      Displays details of a given Tweet object!
public class TweetDetailActivity extends AppCompatActivity implements ComposeDialog.Listener{
    private static final String TAG = "TweetDetailActivity";
    private static final String KEY_DETAIL_ACT = "tweetForDetailActivity";

    ActivityTweetDetailBinding binding;
    TwitterClient client;
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
        client = TwitterApp.getRestClient(this);

        //Get the received Tweet:
        Intent receivedData = getIntent();
        Tweet originalTweet = Parcels.unwrap(receivedData.getParcelableExtra(KEY_DETAIL_ACT));

        //Connect Views:
        rvReplies = binding.rvReplies;
        ivProfilePic = binding.ivOtherProfile;
        tvName = binding.tvOtherName;
        tvBody = binding.tvBody;
        //   embeddedImageContainer = binding.embeddedImagesContainer;
        ivEmbeddedImage = binding.ivEmbeddedImage;
        //ivComment = binding.icCommentIcon;

        populateOriginalTweet(originalTweet);

        //Set up RecyclerView:
        TweetAdapter.TweetAdapterListener listener = new TweetAdapter.TweetAdapterListener() {
            @Override
            public void onTweetClick(Tweet tweetClicked) {}

            @Override
            public void onCommentClick(Tweet tweetClicked) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ComposeDialog composeDialog = ComposeDialog.newInstance(ComposeDialog.REPLY_FUNCTION, tweetClicked);
                composeDialog.show(fragmentManager, "dialog_compose");
            }

            @Override
            public void onHeartClick(Tweet tweetClicked, ImageView heartIcon) {

            }

            @Override
            public void onProfilePicClicked(Tweet tweetClicked) {

            }

            @Override
            public void onSharedButtonClicked(Tweet tweetClicked) {

            }
        };

        replyAdapter = new TweetAdapter(TweetDetailActivity.this, replies, listener);
        rvReplies.setAdapter(replyAdapter);
        rvReplies.setLayoutManager(new LinearLayoutManager(this));

        String tweetId = originalTweet.getId();
        Log.d(TAG, "tweetId given = " + tweetId);
        getAllReplies(originalTweet.getUser().getUsername(), tweetId);
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
            Glide.with(this)
                    .load(embeddedImageUrls.get(0))
                    .transform(new RoundedCornersTransformation(30, 10))
                    .into(ivEmbeddedImage);
            //Set up ListView:
              /*  EmbeddedImageAdapter imageAdapter = new EmbeddedImageAdapter(context, embeddedImageUrls);
                embeddedImageContainer.setAdapter(imageAdapter);*/
        }

        tvName.setText(user.getName());
        tvBody.setText(tweet.getBody());
        Glide.with(this)
                .load(user.getProfileUrl())
                .centerCrop() // scale image to fill the entire ImageView
                .transform(new CircleCrop())
                .into(ivProfilePic);
    }

    //Purpose:      Get all the replies to the given tweet id.
    public void getAllReplies(String username, String tweetId){
        client.getRepliesToUser(username, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "successful getting replies");
                JSONObject jsonResponse = json.jsonObject;
                try {
                    JSONArray jsonStatuses = jsonResponse.getJSONArray("statuses");

                    //Only save those whose "in_reply_to_status_id_str" key is the same as the given tweetId
                    //  (all the tweets in reply to this specific tweet)
                    List<JSONObject> jsonReplies = new ArrayList<>();
                    Log.d(TAG, Tweet.fromJsonArray(jsonStatuses).toString());
                    for(int i = 0; i < jsonStatuses.length(); i++){
                        JSONObject jsonTweet = jsonStatuses.getJSONObject(i);
                        String otherTweetId = jsonTweet.getString("in_reply_to_status_id_str");
                        Log.d(TAG, "tweetId given = " + tweetId + "    thisTweetId = " + otherTweetId);
                        if(otherTweetId.equals(tweetId)) {
                            jsonReplies.add(jsonTweet);
                        }
                    }

                    //Convert all jsonReplies into Tweet objects:
                    for(int i = 0; i < jsonReplies.size(); i++){
                        replies.add(Tweet.fromJson(jsonReplies.get(i)));
                    }
                    //replies.addAll(Tweet.fromJsonArray(jsonStatuses));
                    replyAdapter.notifyDataSetChanged();
                    Log.d(TAG, "replies = " + replies.toString());
                } catch (JSONException e){
                    Log.e(TAG, "Error getting statuses array:", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "failure getting replies");
            }
        });
    }

    @Override
    public void composeFinished(Tweet newTweet) {}

    @Override
    public void replyFinished(Tweet tweetRepliedTo) {
        Log.d(TAG, "compose activity for reply was a success!");
        Intent toDetailAct = new Intent(TweetDetailActivity.this, TweetDetailActivity.class);
        toDetailAct.putExtra(KEY_DETAIL_ACT, Parcels.wrap(tweetRepliedTo));
        startActivity(toDetailAct);
        finish();
    }
}