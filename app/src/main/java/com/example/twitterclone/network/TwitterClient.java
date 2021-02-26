package com.example.twitterclone.network;
import android.content.Context;

import com.example.twitterclone.BuildConfig;
import com.example.twitterclone.R;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

/**
 * TwitterClient.java
 * Purpose:               	This is "RestClient.java" from CodePath's RestClientTemplate. Used as a template in order to authenticate the user's Twitter account and login through OAuth.
 *
 * Classes used:				None
 *
 * @author CodePath
 * @author Josephine Mai Nguyen
 * @version 1.0
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";			//the base URL
	public static final String END_POINT = "statuses/home_timeline.json";
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET;

	// Handle the OAuth on successful authentication:
	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	//Constructor: Initializes an OAuthBaseClient using constants above
	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  			// OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	/**
	 * Purpose:			Network request to Twitter API to obtain the list of tweets on the user's home timeline. The handler passed in will handles the response from this request.
	 * @param handler determines response to the request
	 */
	public void getHomeTimeline(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl(END_POINT);
		client.get(apiUrl, handler);
	}

	/**
	 *Purpose:			Used to get older tweets on a user's Twitter feed. Makes another request to Twitter with params, "max_id", to get all tweets with id less than it.
	 * @param handler determines response to the request
	 * @param maxId the last tweet id
	 */
	public void getNextPageOfTweets(long maxId, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("max_id", maxId);				//used to get the older tweets after id #
		client.get(apiUrl, params, handler);
	}

}
