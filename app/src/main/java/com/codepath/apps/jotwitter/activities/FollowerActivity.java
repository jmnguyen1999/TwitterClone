package com.codepath.apps.jotwitter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.codepath.apps.jotwitter.R;

//Purpose:          Displays the followers and following attributes of the given user.
public class FollowerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);
    }
}