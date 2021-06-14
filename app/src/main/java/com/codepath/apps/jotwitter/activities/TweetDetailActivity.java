package com.codepath.apps.jotwitter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.codepath.apps.jotwitter.R;
//Purpose:      Displays details of a given Tweet object!
public class TweetDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
    }
}