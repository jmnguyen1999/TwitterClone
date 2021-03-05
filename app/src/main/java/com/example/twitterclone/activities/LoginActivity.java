package com.example.twitterclone.activities;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.twitterclone.R;
import com.example.twitterclone.TwitterApp;
import com.example.twitterclone.network.TwitterClient;
import com.example.twitterclone.models.Tweet;
import com.example.twitterclone.models.TweetDao;
import com.codepath.oauth.OAuthLoginActionBarActivity;

/**
 * LoginActivity.java
 * Purpose:							This is "LoginActivity.java" from CodePath's RestClientTemplate. This is the first activity that is launched to start authentication of Twitter account. Handles the result of the
 * 									authentication: upon success --> launches homepage (TimelineActivity{}), upon fail --> show error. Uses TwitterClient{} as the OAuthBaseClient. Also obtains references to the TwitterDao{}
 * 									used for the application (TwitterApp{}).
 *
 * Classes used:					TwitterClient{}, TimelineActivity{}, Tweet{}, TweetDao{}, TwitterApp{}
 * Corresponding layout file:		activity_login.xml
 *
 * @author CodePath
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {
	private static final String TAG = "LoginActivity";			//For Log() statements
	TweetDao tweetDao;

	/**
	 * Purpose:			Called when this activity is launched. Initializes the "tweetDao" field with the TweetDao{} object used by the application (TwitterApp{}). Runs a thread to constantly update "tweetDao" with new Tweet objects.
	 * @param savedInstanceState, Bundle object passed in automatically by the system
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final Tweet tweet = new Tweet();

		// Initialize TweetDao{}: Get the application, convert it to a TwitterApp{}, obtain database and get corresponding TweetDao instance
		tweetDao = ((TwitterApp) getApplicationContext()).getMyDatabase().TweetDao();

	}

	/**
	 * Purpose:			Inflates the menu to add items to the action bar if needed
	 * @param menu the Menu object passed in automatically by the system
	 * @return whether the method executed successfully
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Purpose:			Called when the OAuth authentication was successful, will launch the TimelineActivity{} to begin actual TwitterClone activities
	 */
	@Override
	public void onLoginSuccess() {
		Log.i(TAG, "login success!");
		Intent intent = new Intent(this, TimelineActivity.class);
		startActivity(intent);
	}

	/**
	 * Purpose:			Called when the OAuth authentication failed, logs the occurence and exception corresponding to it, also shows a popup text to user (Toast{})
	 * @param e the exception that occurred durring failure
	 */
	@Override
	public void onLoginFailure(Exception e) {
		Toast.makeText(this, "Failed to authenticate.", Toast.LENGTH_SHORT).show();
		Log.e(TAG, "onLoginFailure(): Exception = ", e);
		e.printStackTrace();
	}

	/**
	 * Purpose:			The handler method for the Login button. Initiates OAuth authorization by using OAuthLoginActionBarActivity{}'s method getClient() and OAuthBaseClient{}'s connect().
	 * @param view
	 */
	public void loginToRest(View view) {
		getClient().connect();
	}

}
