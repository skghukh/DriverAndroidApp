package com.rodafleets.rodadriver.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodadriver.rest.RodaRestClient;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LocationUpdateService extends Service {
    public static final String BROADCAST_ACTION = "Location_Service_Started";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;
    int counter = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, listener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }


    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {
            Log.i("**********", "Location changed");
            if (null == previousBestLocation || null == loc) {
                previousBestLocation = loc;
            } else {
                float distance = loc.distanceTo(previousBestLocation);
                if (distance > 1) {
                    updateDriverLocation(loc);
                    //Below line should be done on success
                    previousBestLocation = loc;
                }
            }
        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

    private void updateDriverLocation(Location loc) {
        RodaRestClient.updateDriverLocation(ApplicationSettings.getDriverId(this), loc.getLatitude(), loc.getLongitude(), updateLocationResponsetHandler);
    }

    private JsonHttpResponseHandler updateLocationResponsetHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.e(AppConstants.APP_NAME, "response = " + errorResponse.toString());
        }
    };

}