package com.rodafleets.rodadriver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rodafleets.rodadriver.custom.SwipeButton;
import com.rodafleets.rodadriver.custom.SwipeButtonCustomItems;
import com.rodafleets.rodadriver.utils.AppConstants;

public class VehicleRequestActivity extends MapParentActivity {

    public static final String TAG = AppConstants.APP_NAME;

    private SwipeButton makeOfferBtn;
    private CardView requestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_vehicle_request);

        initComponents();
    }

    private void initComponents() {

        makeOfferBtn = (SwipeButton) findViewById(R.id.makeOfferBtn);
        requestView = (CardView) findViewById(R.id.requestView);

        initMap();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Vehicle_Requested"));
        Log.i(TAG, "Vehicle Request activity");

        makeOfferBtn.setText("₹650");

        SwipeButtonCustomItems swipeButtonSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                Log.d("NEW_STUFF", "New swipe confirm callback");

                startNextActivity();
            }
        };

        if (makeOfferBtn != null) {
            makeOfferBtn.setSwipeButtonCustomItems(swipeButtonSettings);
        }
    }

    public void onRejectClick(View view) {
        requestView.setVisibility(View.GONE);
    }

    private void startNextActivity(){
        this.startActivity(new Intent(this, TripProgressActivity.class));
        finish();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String message = intent.getStringExtra("message");
//            Log.i(TAG, "Message Received here!!");
//            Log.i(TAG, message);
//
//            startNextActivity();

            requestView.setVisibility(View.VISIBLE);

        }
    };
}
