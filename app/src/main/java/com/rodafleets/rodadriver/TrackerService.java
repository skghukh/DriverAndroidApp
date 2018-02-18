package com.rodafleets.rodadriver;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/*public class TrackerService extends Service {
    public TrackerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}*/

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rodafleets.rodadriver.services.FirebaseReferenceService;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.Manifest;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();
    private static FirebaseUser currentUser;
    private static String driverId;
    private FirebaseAuth mAuth;
    private static Location lastLocation;

    @Override
    public IBinder onBind(Intent intent) {
        driverId = intent.getStringExtra("driverEId");
        System.out.println("OnBind driverId is " + driverId);
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        if (extras == null)
            Log.d("Service", "null");
        else {
            Log.d("Service", "not null");
            driverId = (String) extras.get("driverEId");
            Log.d("Service", "In Service driverid is " + driverId);
            buildNotification();
            loginToFirebase();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.ic_tracker);
        startForeground(1, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };


    public static Location getLastLocation() {
        return lastLocation;
    }

    private void loginToFirebase() {
        // String email = getString(R.string.firebase_email);
        // String password = getString(R.string.firebase_password);
        // FirebaseAuth.getInstance().signInWithEmailAndPassword(
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (null == currentUser) {
            //Do login
            //TODO :- Handler logout here. And display Message you are not logged in
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "firebase auth success");
                        currentUser = mAuth.getCurrentUser();
                        requestLocationUpdates();
                    } else {
                        Log.d(TAG, "firebase auth failed " + task.getResult() + " " + task.getException());
                    }
                }
            });
        } else {
            //Toast.makeText(this, "User " + currentUser.getUid() + "  is already loged in ", Toast.LENGTH_SHORT).show();
            requestLocationUpdates();
        }

    }

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = getString(R.string.firebase_path);// + "/" + driverId;//getString(R.string.transport_id);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseReferenceService.getFBInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        lastLocation = location;
                        GeoFire geoFire = new GeoFire(ref);
                        geoFire.setLocation(String.valueOf(driverId), new GeoLocation(location.getLatitude(), location.getLongitude()));
                        Log.d(TAG, "location update " + location);
                    }
                }
            }, null);
        }
    }

}

