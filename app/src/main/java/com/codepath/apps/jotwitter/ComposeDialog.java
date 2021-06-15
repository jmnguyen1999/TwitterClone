package com.codepath.apps.jotwitter;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.jotwitter.databinding.FragmentComposeBinding;
import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeDialog extends DialogFragment {
    private static final String TAG = "ComposeFragment";
    public static final String KEY_PURPOSE = "are we replying or composing a new tweet?";
    public static final String KEY_REPLY_TO = "who are we replying to then?";

    private static final String KEY_COMPOSE_TWEET = "tweetFromComposeActivity";
    public static final String KEY_TWEET_REPLY_FOR = "what tweet are we replying to?";

    private static final int MAX_LENGTH = 280;

    //Constants to ensure value for KEY_PURPOSE --> one of these.
    public static final String COMPOSE_FUNCTION = "function = to compose!";
    public static final String REPLY_FUNCTION = "function = to reply!";

    FragmentComposeBinding binding;
    TwitterClient client;
    boolean functionIsReply;
    Listener listener;
    Tweet tweetReplyTo;

    //Views:
    ImageView ivUserProfile;
    EditText etBody;
    Button btnCompose;
    RelativeLayout replyFeaturesContainer;


    public interface Listener{
        void composeFinished(Tweet newTweet);
        void replyFinished(Tweet tweetRepliedTo);
    }
    public ComposeDialog() {}

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        if(context instanceof ComposeDialog.Listener){
            listener = (ComposeDialog.Listener) context;
        }
        else{
            Log.e(TAG, "Context that called me is not an instance of ComposeDialog.Listener");
        }
    }

    public static ComposeDialog newInstance(String purpose, Tweet tweetReplyTo) {
        ComposeDialog fragment = new ComposeDialog();
        Bundle args = new Bundle();
        args.putString(KEY_PURPOSE, purpose);
        args.putParcelable(KEY_TWEET_REPLY_FOR, Parcels.wrap(tweetReplyTo));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "onCreate(): there were arguments");
            String purpose = getArguments().getString(KEY_PURPOSE);
            if(purpose.equals(REPLY_FUNCTION)){
                Log.d(TAG, "purpose = for replying");
                functionIsReply = true;
                tweetReplyTo = Parcels.unwrap(getArguments().getParcelable(KEY_TWEET_REPLY_FOR));
                Log.d(TAG, "tweet received = " + (tweetReplyTo == null));
            }
            else if(purpose.equals(COMPOSE_FUNCTION)){
                Log.d(TAG, "purpose = for oompose");
                functionIsReply = false;
            }
            else{
                Log.d(TAG, "purpose received not acceptale, purpose = " + purpose);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentComposeBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        client = TwitterApp.getRestClient(getContext());

        //1.) Connect views:
        ivUserProfile = binding.ivUserPic;
        etBody = binding.etBody;
        btnCompose = binding.btnCompose;
        replyFeaturesContainer = binding.replyFeatureContainer;

        if(functionIsReply){
            setUpReplyFunctions();
        }
        else{
            setUpComposeFunctions();
        }

        getCurrentUser();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onResume() {
        int width = getResources().getDimensionPixelSize(R.dimen.compose_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.compose_dialog_height);
        getDialog().getWindow().setLayout(width, height);
        // Call super onResume after sizing
        super.onResume();
    }

    //Purpose:          Sets up the activity to only do compose functions!
    public void setUpComposeFunctions(){
        Log.d(TAG, "setUpCompose!");
        replyFeaturesContainer.setVisibility(View.GONE);        //Do not show any of the reply features

        //onclick() btnCompose --> error check the inputted string + post it!
        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String attemptedBody = etBody.getText().toString();
                if(attemptedBody.isEmpty()){
                    Toast.makeText(getContext(), "You must input a message!", Toast.LENGTH_SHORT).show();
                }
                else if(attemptedBody.length() > MAX_LENGTH){
                    Toast.makeText(getContext(), "Your post went over the character limit!", Toast.LENGTH_SHORT).show();
                }
                else{
                    client.publishTweet(attemptedBody, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "successful post!");
                            //1.) get the tweet out:
                            try {
                                Tweet newTweet = Tweet.fromJson(json.jsonObject);
                                listener.composeFinished(newTweet);
                                getDialog().dismiss();
                            } catch (JSONException e) {
                                Log.e(TAG, "Error parsing the jsonObject=", e);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "unsuccessful post :(", throwable);
                        }
                    });
                }
            }
        });
    }

    //Purpose:      Sets up the activity to do reply functions!
    public void setUpReplyFunctions(){
        Log.d(TAG, "setUpReply!");
        //1.) Get the necessary info:
        String replyToUsername = tweetReplyTo.getUser().getUsername();
        String replyTweetId = tweetReplyTo.getId();                              //used to post the reply!

        //2.) Populate views:
        TextView tvReplyTo = binding.tvWhoReply;
        tvReplyTo.setText("@"+ replyToUsername);
        etBody.setText("@" + replyToUsername + " ");              //autofill the @username in the body text field

        //3.) onclick() compose button --> error check the body
        btnCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3a.) Error check the input:
                String attemptedBody = etBody.getText().toString();
                if(attemptedBody.length() == replyToUsername.length()+1){
                    Toast.makeText(getContext(), "You must input a message!", Toast.LENGTH_SHORT).show();
                }
                else if(attemptedBody.length() > MAX_LENGTH){
                    Toast.makeText(getContext(), "Your post went over the character limit!", Toast.LENGTH_SHORT).show();
                }

                //3b.) We good --> try to post the reply: uses the replyTweet's id
                else{
                    client.postReply(replyTweetId, attemptedBody, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.d(TAG, "successful posting the reply!");
                            //1.) Send back the tweet we responded to:
                            listener.replyFinished(tweetReplyTo);
                            getDialog().dismiss();
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.d(TAG, "Error posting reply = ", throwable);
                        }
                    });
                }
            }
        });
    }


    //Purpose:      Obtains the current user's profile url + loads it!
    public void getCurrentUser(){
        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess getting current user profile!");
                JSONObject jsonResponse = json.jsonObject;
                try {
                    //user = User.fromJson(jsonResponse);
                    //Log.d(TAG, "Name = " + jsonResponse.getString("name"));
                    Log.d(TAG, "profile url = " + jsonResponse.getString("profile_image_url_https"));
                    Glide.with(getContext()).load(jsonResponse.getString("profile_image_url_https")).into(ivUserProfile);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure error=", throwable);
            }
        });
    }

}