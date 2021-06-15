package com.codepath.apps.jotwitter;

import android.content.Context;

import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
	public static final String REST_URL = "https://api.twitter.com/1.1";
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET;

	public static final String POST_END_POINT = "statuses/update.json";

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}
	//Purpose:			Obtain the home timeline from Twitter APi
	public void getHomeTimeline(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		client.get(apiUrl, handler);

	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */

	public void getCurrentUser(JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");
		client.get(apiUrl, handler);
	}

	public void publishTweet(String tweetContent, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl(POST_END_POINT);
		RequestParams params = new RequestParams();
		params.put("status", tweetContent);
		client.post(apiUrl, params, "", handler);
	}

	public void postReply(String statusId, String tweetContent, JsonHttpResponseHandler handler){
		String apiUrl = getApiUrl(POST_END_POINT);
		RequestParams params = new RequestParams();
		params.put("status", tweetContent);
		params.put("in_reply_to_status_id", statusId);
		params.put("auto_populate_reply_metadata", true);
		client.post(apiUrl, params, "", handler);
	}

	public void getNextPageOfTweets(long maxId, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("max_id", maxId);				//used to get the older tweets after id #
		client.get(apiUrl, params, handler);
	}

	public void favoriteThisTweet(long tweetId, JsonHttpResponseHandler handler){
		String apiUrl =getApiUrl("favorites/create.json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		client.post(apiUrl, params, "", handler);
	}

	public void unfavoriteThisTweet(long tweetId, JsonHttpResponseHandler handler){
		String apiUrl =getApiUrl("favorites/destroy.json");
		RequestParams params = new RequestParams();
		params.put("id", tweetId);
		client.post(apiUrl, params, "", handler);
	}
}
