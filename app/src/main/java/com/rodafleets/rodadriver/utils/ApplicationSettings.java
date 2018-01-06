package com.rodafleets.rodadriver.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.rodafleets.rodadriver.model.FBDriver;
import com.rodafleets.rodadriver.model.FBVehicleRequest;
import com.rodafleets.rodadriver.model.TripRequest;
import com.rodafleets.rodadriver.model.FBVehicleRequest;

import org.json.JSONObject;

public final class ApplicationSettings {

    private static final String SETTINGS_NAME = "RodaDriverSettings";
    private static final String APP_LANGUAGE = "APP_LANGUAGE";
    private static final String DRIVER_ID = "DRIVER_ID";
    private static final String DRIVER = "DRIVER";
    private static final String VERIFIED = "VERIFIED";
    private static final String VEHICLE_INFO_SAVED = "VEHICLE_INFO_SAVED";
    private static final String LOGGED_IN = "LOGGED_IN";
    private static final String REGISTRATION_ID = "REGISTRATION_ID";
    private static final String VEHICLE_REQUEST = "VEHICLE_REQUEST";
    private static long REQUEST_ID;
    private static long TRIP_ID;
    private static TripRequest tripReq;
    private static final String DRIVER_UID = "DRIVER_UID";
    private static final String DRIVER_EID = "DRIVER_EID";
    private static final String DRIVER_NAME = "DRIVER_NAME";
    private static FBDriver fbDriver;
    private static FBVehicleRequest fbVehicleRequest;

    public static FBVehicleRequest getVehicleRequest() {
        return vehicleRequest;
    }

    public static void setVehicleRequest(FBVehicleRequest vehicleRequest) {
        ApplicationSettings.vehicleRequest = vehicleRequest;
    }

    private static FBVehicleRequest vehicleRequest;

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SETTINGS_NAME, 0);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    public static void clearAllSettings(Context context) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.clear();
        editor.commit();
    }

    public static void setAppLanguage(Context context, String language) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(APP_LANGUAGE, language);
        editor.commit();
    }

    public static String getAppLanguage(Context context) {
        return getSharedPreferences(context).getString(APP_LANGUAGE, "");
    }

    public static void setDriverId(Context context, int driverId) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putInt(DRIVER_ID, driverId);
        editor.commit();
    }

    public static String getDriverUId(Context context) {
        return getSharedPreferences(context).getString(DRIVER_ID, null);
    }

    public static void setDriverUId(Context context, String uid) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(DRIVER_UID, uid);
        editor.commit();
    }

    public static void setDriverEid(Context context, String eid) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(DRIVER_EID, eid);
        editor.commit();
    }

    public static String getDriverEid(Context context) {
        return getSharedPreferences(context).getString(DRIVER_EID, null);
    }

    public static void setDriverName(Context context, String name) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(DRIVER_NAME, name);
        editor.commit();
    }

    public static String getDriverName(Context context) {
        return getSharedPreferences(context).getString(DRIVER_NAME, null);
    }

    public static int getDriverId(Context context) {
        return getSharedPreferences(context).getInt(DRIVER_ID, 0);
    }

    public static void setDriver(Context context, JSONObject jsonObject) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(DRIVER, jsonObject.toString());
        editor.commit();
    }

    public static String getDriver(Context context) {
        return getSharedPreferences(context).getString(DRIVER, "");
    }

    public static void setVerified(Context context, Boolean verified) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(VERIFIED, verified);
        editor.commit();
    }

    public static Boolean getVerified(Context context) {
        return getSharedPreferences(context).getBoolean(VERIFIED, false);
    }

    public static void setVehicleInfoSaved(Context context, Boolean saved) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(VEHICLE_INFO_SAVED, saved);
        editor.commit();
    }

    public static Boolean getVehicleInfoSaved(Context context) {
        return getSharedPreferences(context).getBoolean(VEHICLE_INFO_SAVED, false);
    }

    public static void setLoggedIn(Context context, Boolean loggedIn) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(LOGGED_IN, loggedIn);
        editor.commit();
    }

    public static Boolean getLoggedIn(Context context) {
        return getSharedPreferences(context).getBoolean(LOGGED_IN, false);
    }

    public static void setRegistrationId(Context context, String token) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(REGISTRATION_ID, token);
        editor.commit();
    }

    public static String getRegistrationId(Context context) {
        return getSharedPreferences(context).getString(REGISTRATION_ID, "");
    }

    public static void setVehicleRequest(Context context, JSONObject jsonObject) {
        SharedPreferences.Editor editor = getEditor(context);
        if (jsonObject != null) {
            editor.putString(VEHICLE_REQUEST, jsonObject.toString());
        } else {
            editor.putString(VEHICLE_REQUEST, "");
        }
        editor.commit();
    }

    public static String getVehicleRequest(Context context) {
        return getSharedPreferences(context).getString(VEHICLE_REQUEST, "");
    }

    public static long getRequestId() {
        return REQUEST_ID;
    }

    public static void setRequestId(long requestId) {
        REQUEST_ID = requestId;
    }

    public static long getTripId() {
        return TRIP_ID;

    }

    public static TripRequest getTripReq() {
        return tripReq;
    }

    public static void setTripReq(TripRequest tripReq) {
        ApplicationSettings.tripReq = tripReq;
    }

    public static void setTripId(long tripId) {
        TRIP_ID = tripId;
    }

    public static FBDriver getFbDriver() {
        return fbDriver;
    }

    public static void setFbDriver(FBDriver fbDriver) {
        ApplicationSettings.fbDriver = fbDriver;
    }

    public static FBVehicleRequest getFbVehicleRequest() {
        return fbVehicleRequest;
    }

    public static void setFbVehicleRequest(FBVehicleRequest fbVehicleRequest) {
        ApplicationSettings.fbVehicleRequest = fbVehicleRequest;
    }
}
