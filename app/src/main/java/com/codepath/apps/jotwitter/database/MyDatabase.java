package com.codepath.apps.jotwitter.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.codepath.apps.jotwitter.models.Tweet;
import com.codepath.apps.jotwitter.models.TwitterDao;
import com.codepath.apps.jotwitter.models.User;

@Database(entities={Tweet.class, User.class}, version=3)
public abstract class MyDatabase extends RoomDatabase {
    public abstract TwitterDao tweetDao();

    // Database name to be used
    public static final String NAME = "MyDataBase";
}
