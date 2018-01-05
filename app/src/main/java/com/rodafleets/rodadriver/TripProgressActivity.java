package com.rodafleets.rodadriver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodadriver.custom.SwipeButton;
import com.rodafleets.rodadriver.custom.SwipeButtonCustomItems;
import com.rodafleets.rodadriver.custom.slideview.SlideView;
import com.rodafleets.rodadriver.model.FBVehicleRequest;
import com.rodafleets.rodadriver.model.TripRequest;
import com.rodafleets.rodadriver.model.VehicleRequest;
import com.rodafleets.rodadriver.rest.RodaRestClient;
import com.rodafleets.rodadriver.services.FirebaseReferenceService;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class TripProgressActivity extends MapActivity {

    public static final String TAG = AppConstants.APP_NAME;

    // Customer View
    private CardView customerView;
    private TextView customerName;
    private TextView acceptanceStatus;

    // Address View
    private CardView addressView;
    private TextView navigate;
    private TextView fromAddress;

    //Start Loading View
    private CardView startLoadingView;
    private TextView arrivedAtOriginTxt;
    private SlideView startLoadingBtn;
    private TextView startLoadingTxt;

    // Start Trip View
    private CardView startTripView;
    private SlideView startTripBtn;
    private TextView startTripTxt;

    //Start Unloading View
    private CardView startUnloadingView;
    private TextView arrivedAtDestinationTxt;
    private SlideView startUnloadingBtn;
    private TextView startUnloadingTxt;

    // End Trip View
    private CardView endTripView;
    private SlideView endTripBtn;
    private TextView endTripTxt;

    // Fare Summary View
    private CardView fareSummaryView;
    private TextView fareSummaryTxt;
    private TextView paidByTxt;
    private TextView fareTxt;
    private TextView rateCustomerTxt;
    private SlideView goOnlineBtn;

    private VehicleRequest vehicleRequest;
    private TripRequest tripReq;

    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_progress);
        final String tripstatus = getIntent().getStringExtra("tripstatus");
        initComponents();
        this.tripReq = ApplicationSettings.getTripReq();
        if (null == tripstatus) {
            setTripStatusListener();
        } else {
            resumeViewForCurrentTrip(tripstatus);
        }
    }

    private void resumeViewForCurrentTrip(String tripstatus) {
        tripstatus = tripstatus.toLowerCase();
        if (tripstatus.equalsIgnoreCase("loading")) {
            resumeStartLoading();
        }
        if (tripstatus.equalsIgnoreCase("inprogress")) {
            resumeStartTrip();
        }
        if (tripstatus.equalsIgnoreCase("unloading")) {
            resumestartUnloading();
        }
        if (tripstatus.equalsIgnoreCase("completed")) {
            resumeFinishTrip();
        }
    }

    private void setTripStatusListener() {
        final DatabaseReference statusRef = tripReq.getStatusRef();
        final ValueEventListener tripStatusListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null == dataSnapshot || null == dataSnapshot.getValue()) {
                    return;
                }
                final String newStatus = (String) dataSnapshot.getValue();
                final String[] status = newStatus.split("\\_");
                if (status[1].equalsIgnoreCase(ApplicationSettings.getDriverEid(TripProgressActivity.this)) && (status[0].equalsIgnoreCase("scheduled") || status[0].equalsIgnoreCase("inprogress") || status[0].equalsIgnoreCase("completed"))) {
                    //Check if this driver is carrier.
                    onTripConfirmOperation();
                    statusRef.removeEventListener(this);
                } else if (newStatus == "cancelled" || newStatus.equalsIgnoreCase("expired")) {
                    //Stop listening for this trip.
                    //TODO:- remove current trip from driver.
                    statusRef.removeEventListener(this);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        statusRef.addValueEventListener(tripStatusListener);
    }

    protected void initComponents() {

        super.initComponents();

        initMap();

        customerView = (CardView) findViewById(R.id.customerView);
        addressView = (CardView) findViewById(R.id.addressView);
        startLoadingView = (CardView) findViewById(R.id.startLoadingView);
        startTripView = (CardView) findViewById(R.id.startTripView);
        startUnloadingView = (CardView) findViewById(R.id.startUnloadingView);
        endTripView = (CardView) findViewById(R.id.endTripView);
        fareSummaryView = (CardView) findViewById(R.id.fareSummaryView);

        customerName = (TextView) findViewById(R.id.customerName);
        acceptanceStatus = (TextView) findViewById(R.id.acceptanceStatus);

        navigate = (TextView) findViewById(R.id.navigate);
        fromAddress = (TextView) findViewById(R.id.fromAddress);

        arrivedAtOriginTxt = (TextView) findViewById(R.id.arrivedAtOriginTxt);
        startLoadingTxt = (TextView) findViewById(R.id.startLoadingTxt);

        startTripTxt = (TextView) findViewById(R.id.startTripTxt);

        arrivedAtDestinationTxt = (TextView) findViewById(R.id.arrivedAtDestinationTxt);
        startUnloadingTxt = (TextView) findViewById(R.id.startUnloadingTxt);

        endTripTxt = (TextView) findViewById(R.id.endTripTxt);

        fareSummaryTxt = (TextView) findViewById(R.id.fareSummaryTxt);
        paidByTxt = (TextView) findViewById(R.id.paidByTxt);
        fareTxt = (TextView) findViewById(R.id.fareTxt);
        rateCustomerTxt = (TextView) findViewById(R.id.rateCustomerTxt);

        startLoadingBtn = (SlideView) findViewById(R.id.startLoadingBtn);
        startTripBtn = (SlideView) findViewById(R.id.startTripBtn);
        startUnloadingBtn = (SlideView) findViewById(R.id.startUnloadingBtn);
        endTripBtn = (SlideView) findViewById(R.id.endTripBtn);
        goOnlineBtn = (SlideView) findViewById(R.id.goOnlineBtn);

        setFonts();
        initSwipeButtonEvents();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Bid_Accepted"));

        try {
            JSONObject jsonObject = new JSONObject(ApplicationSettings.getVehicleRequest(TripProgressActivity.this));
            VehicleRequest vehicleRequest = new VehicleRequest(jsonObject);
            Log.e(TAG, vehicleRequest.toString());
            //₹
            customerName.setText(vehicleRequest.getCustomerName().toUpperCase());
            fromAddress.setText(vehicleRequest.getOriginAddress());

        } catch (Exception e) {
            //handle error
            Log.e(TAG, "vehicleRequest jsonException = " + e.getMessage());
        }
    }

    private void setFonts() {
        loadFonts();

        customerName.setTypeface(poppinsMedium);
        acceptanceStatus.setTypeface(poppinsSemiBold);
        navigate.setTypeface(poppinsRegular);
        fromAddress.setTypeface(poppinsRegular);

        arrivedAtOriginTxt.setTypeface(poppinsSemiBold);
        startLoadingTxt.setTypeface(poppinsRegular);

        startTripTxt.setTypeface(poppinsRegular);

        arrivedAtDestinationTxt.setTypeface(poppinsSemiBold);
        startUnloadingTxt.setTypeface(poppinsRegular);

        endTripTxt.setTypeface(poppinsRegular);

        fareSummaryTxt.setTypeface(poppinsRegular);
        paidByTxt.setTypeface(poppinsSemiBold);
        fareTxt.setTypeface(poppinsLight);
        rateCustomerTxt.setTypeface(poppinsSemiBold);

//        startLoadingBtn.setTypeface(poppinsSemiBold);
//        startTripBtn.setTypeface(poppinsSemiBold);
//        startUnloadingBtn.setTypeface(poppinsSemiBold);
//        endTripBtn.setTypeface(poppinsSemiBold);
//        goOnlineBtn.setTypeface(poppinsSemiBold);
    }

    private void initSwipeButtonEvents() {

        initStartLoadingBtn();
        initStartTripBtn();
        initStartUnloadingBtn();
        initEndTripBtn();
        initGoOnlineBtn();

    }

    private void initStartLoadingBtn() {

        startLoadingBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                v.onTouchEvent(event);

                startLoadingBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        startLoadingBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                hideAllViews();
                startTripView.setVisibility(View.VISIBLE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateTripStatus(ApplicationSettings.getTripReq().getCustId(), ApplicationSettings.getTripReq().getTripId(), "Loading");
                        //RodaRestClient.updateTripStatus(ApplicationSettings.getTripId(), ApplicationSettings.getRequestId(), 1, tripStatusUpdateHandler);
                    }
                });

            }
        });
    }

    private void initStartTripBtn() {

        startTripBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                v.onTouchEvent(event);

                startTripBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        startTripBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                hideAllViews();
                startUnloadingView.setVisibility(View.VISIBLE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateTripStatus(ApplicationSettings.getTripReq().getCustId(), ApplicationSettings.getTripReq().getTripId(), "Inprogress");
                        //RodaRestClient.updateTripStatus(ApplicationSettings.getTripId(), ApplicationSettings.getRequestId(), 2, tripStatusUpdateHandler);
                    }
                });
            }
        });
    }


    private void initStartUnloadingBtn() {

        startUnloadingBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                v.onTouchEvent(event);

                startUnloadingBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        startUnloadingBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                hideAllViews();
                endTripView.setVisibility(View.VISIBLE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateTripStatus(ApplicationSettings.getTripReq().getCustId(), ApplicationSettings.getTripReq().getTripId(), "UnLoading");
                        //RodaRestClient.updateTripStatus(ApplicationSettings.getTripId(), ApplicationSettings.getRequestId(), 3, tripStatusUpdateHandler);
                    }
                });
            }
        });
    }

    private void initEndTripBtn() {

        endTripBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                v.onTouchEvent(event);

                endTripBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        endTripBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                hideAllViews();
                final VehicleRequest vehicleRequest = ApplicationSettings.getVehicleRequest();
                rateCustomerTxt.setText("Rate " + vehicleRequest.getCustomerName());
                paidByTxt.setText("Payment made by e-wallet");
                long fare = vehicleRequest.getApproxFareInCents() / 100;
                fareTxt.setText("₹" + fare);
                fareSummaryView.setVisibility(View.VISIBLE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateTripStatus(ApplicationSettings.getTripReq().getCustId(), ApplicationSettings.getTripReq().getTripId(), "Completed");
                        // RodaRestClient.updateTripStatus(ApplicationSettings.getTripId(), ApplicationSettings.getRequestId(), 10, tripStatusUpdateHandler);
                    }
                });
            }
        });
    }

    private void initGoOnlineBtn() {
        goOnlineBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                v.onTouchEvent(event);

                goOnlineBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        goOnlineBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                startNextActivity();
            }
        });
    }

    private void hideAllViews() {
        customerView.setVisibility(View.GONE);
        addressView.setVisibility(View.GONE);
        startLoadingView.setVisibility(View.GONE);
        startTripView.setVisibility(View.GONE);
        startUnloadingView.setVisibility(View.GONE);
        endTripView.setVisibility(View.GONE);
    }

    private void startNextActivity() {
        ApplicationSettings.setVehicleRequest(this, null);
        this.startActivity(new Intent(this, VehicleRequestActivity.class));
        finish();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.i(TAG, "-------AAAAAA------");

                JSONObject jsonObject = new JSONObject(ApplicationSettings.getVehicleRequest(TripProgressActivity.this));

                vehicleRequest = new VehicleRequest(jsonObject);
                final String tripId = intent.getStringExtra("tripId");
                final String requestId = intent.getStringExtra("requestId");
                ApplicationSettings.setRequestId(Long.parseLong(requestId));
                ApplicationSettings.setTripId(Long.parseLong(tripId));
                customerName.setText(vehicleRequest.getCustomerName().toUpperCase());
                fromAddress.setText(vehicleRequest.getOriginAddress());
                acceptanceStatus.setText("ACCEPTED");
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    public void run() {
                        customerView.setVisibility(View.GONE);
                        addressView.setVisibility(View.VISIBLE);
                        startLoadingView.setVisibility(View.VISIBLE);
                    }
                }, 2000);


            } catch (Exception e) {
                //handle error
                Log.e(TAG, "vehicleRequest jsonException = " + e.getMessage());
            }
        }
    };

    private void onTripConfirmOperation() {
        final VehicleRequest vehicleRequest = ApplicationSettings.getVehicleRequest();
        customerName.setText(vehicleRequest.getCustomerName().toUpperCase());
        fromAddress.setText(vehicleRequest.getOriginAddress());
        acceptanceStatus.setText("ACCEPTED");
        FirebaseReferenceService.addDriversCurrentTrip(ApplicationSettings.getDriverEid(TripProgressActivity.this), tripReq.getTripId(), tripReq.getCustId());
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                customerView.setVisibility(View.GONE);
                addressView.setVisibility(View.VISIBLE);
                startLoadingView.setVisibility(View.VISIBLE);
            }
        }, 2000);

    }

    private void resumeStartLoading() {
        customerView.setVisibility(View.GONE);
        addressView.setVisibility(View.VISIBLE);
        startLoadingView.setVisibility(View.VISIBLE);
    }

    private void resumeStartTrip() {
        customerView.setVisibility(View.GONE);
        addressView.setVisibility(View.VISIBLE);
        startTripView.setVisibility(View.VISIBLE);
    }

    private void resumestartUnloading() {
        customerView.setVisibility(View.GONE);
        addressView.setVisibility(View.VISIBLE);
        startUnloadingView.setVisibility(View.VISIBLE);
    }

    private void resumeFinishTrip() {
        customerView.setVisibility(View.GONE);
        addressView.setVisibility(View.VISIBLE);
        endTripView.setVisibility(View.VISIBLE);
    }

    private void updateTripStatus(String custId, String tripId, String status) {
        DatabaseReference tripStatusRef = FirebaseReferenceService.getFBInstance().getReference("vehicleRequests/" + custId + "/" + tripId + "/status");//getString(R.string.firebase_path));
        tripStatusRef.setValue(status + "_" + ApplicationSettings.getDriverEid(TripProgressActivity.this));
    }

    private JsonHttpResponseHandler tripStatusUpdateHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            Toast.makeText(TripProgressActivity.this, "Trip Status Updated", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            Toast.makeText(TripProgressActivity.this, "Not able to update Trip Status!", Toast.LENGTH_SHORT).show();
        }

    };
}
