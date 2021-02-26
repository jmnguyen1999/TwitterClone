package com.example.twitterclone;
import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.example.twitterclone.database.MyDatabase;
import com.example.twitterclone.network.TwitterClient;
import com.facebook.stetho.Stetho;

/**
 * TwitterApp.java
 * Purpose:         	This is "RestApplication.java" from CodePath's RestClientTemplate. Used as a template for the TwitterClone application itself. Initializes a Twitter database and a singleton of the app.
 *
 * Classes used:        TwitterClient{}, MyDatabase{},
 *
 * @author CodePath
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
public class TwitterApp extends Application {

    MyDatabase myDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize database with Room{}:   given database class, name, application context
        //Then kill any original tables using fallbackToDestructiveMigration()
        myDatabase = Room.databaseBuilder(this, MyDatabase.class, MyDatabase.NAME).fallbackToDestructiveMigration().build();

        // use chrome://inspect to inspect SQL database
        Stetho.initializeWithDefaults(this);
    }

    /**
     * Purpose:     Returns a TwitterClient (an OAuthBaseClient) tied to the given context
     * @param context to be tied to
     * @return TwitterClient
     */
    public static TwitterClient getRestClient(Context context) {
        return (TwitterClient) TwitterClient.getInstance(TwitterClient.class, context);
    }

    public MyDatabase getMyDatabase() {
        return myDatabase;
    }
}