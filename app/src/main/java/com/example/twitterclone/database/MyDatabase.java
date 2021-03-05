package com.example.twitterclone.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.twitterclone.models.Tweet;
import com.example.twitterclone.models.TweetDao;
import com.example.twitterclone.models.User;

/**
 * MyDatabase.java
 * Purpose:             Creates a database of Tweet and User objects in order to easily search for specific Tweets.
 *
 * Classes used:        Tweet{}, TweetDao{}, User{}
 *
 * @author Josephine Mai Nguyen
 * @version 2.0
 */
@Database(entities={Tweet.class, User.class}, version=3)
public abstract class MyDatabase extends RoomDatabase {
    public abstract TweetDao TweetDao();

    public static final String NAME = "MyDatabase";
}
