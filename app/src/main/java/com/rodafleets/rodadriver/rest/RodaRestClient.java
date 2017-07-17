package com.rodafleets.rodadriver.rest;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rodafleets.rodadriver.utils.AppConstants;

public class RodaRestClient {

    private static final String API_VERSION = "0.1";

    private static final String API_BASE_URL = "https://api.rodafleets.com/" + API_VERSION;
    //private static final String API_BASE_URL = "http://192.168.1.236:8080/" + API_VERSION;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void GET(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        if(params != null) {
            Log.i(AppConstants.APP_NAME, "Api params = " + params.toString());
        }
        client.setMaxRetriesAndTimeout(AppConstants.HTTP_CONNECTION_RETRIES, AsyncHttpClient.DEFAULT_RETRY_SLEEP_TIME_MILLIS);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void POST(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setMaxRetriesAndTimeout(AppConstants.HTTP_CONNECTION_RETRIES, AsyncHttpClient.DEFAULT_RETRY_SLEEP_TIME_MILLIS);
        client.setTimeout(20*1000);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        String apiUrl = API_BASE_URL + relativeUrl;
        Log.i(AppConstants.APP_NAME, "Api Url = " + apiUrl);
        return apiUrl;
    }

    //eventually change to this
    public static void GET(String url, RequestParams params, RestResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void POST(String url, RequestParams params, RestResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    /* //============example usage ============

    private void fucntionName() {
        RodaRestClient.GET("/relative/path/to/api", params, responseHandler);
    }

    private JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            // If the response is JSONObject instead of expected JSONArray
            Log.i(AppConstants.APP_NAME, "response = " + response.toString());
        }

        public void onSuccess(int statusCode, Header[] headers, JSONArray jsonResponseArray) {
            Log.i(AppConstants.APP_NAME, "response2 = " + responseArray.toString());

        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            // super.onFailure(statusCode,headers, throwable, errorResponse);

            switch (statusCode) {

        }
    };*/


    public static void signUp(String phoneNumber, String firstName, String lastName, String gender, JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("phonenumber", phoneNumber);
        params.put("firstname", firstName);
        params.put("lastname", lastName);
        params.put("gender", gender);
        RodaRestClient.POST("/drivers", params, responseHandler);
    }

    public static void updateToken(int driverId, String token,JsonHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("token", token);
        RodaRestClient.POST("/drivers/" + driverId + "/updateandroidtoken", params, responseHandler);
    }
}
