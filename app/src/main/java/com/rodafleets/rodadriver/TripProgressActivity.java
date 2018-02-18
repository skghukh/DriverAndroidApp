package com.rodafleets.rodadriver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodadriver.custom.slideview.SlideView;
import com.rodafleets.rodadriver.model.FBTrip;
import com.rodafleets.rodadriver.model.FBVehicleRequest;
import com.rodafleets.rodadriver.model.TripRequest;
import com.rodafleets.rodadriver.services.FirebaseReferenceService;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONObject;

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


    //Reached At Location
    private CardView reachedAtLocationView;
    private TextView reachedAtLocationTxt1;
    private SlideView reachedAtLocationBtn;
    private TextView reachedAtLocationTxt2;


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

    private FBVehicleRequest vehicleRequest;
    private TripRequest tripReq;

    private Handler mHandler = new Handler();
    private LatLng sourceLatLng;
    private LatLng destLatLng;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        addPicupPointDropPointMarkers(sourceLatLng, destLatLng);
    }

    private void addPicupPointDropPointMarkers(LatLng sourceLatLng, LatLng destLatLng) {
        if (null == sourceLatLng || null == destLatLng) return;
        if (mMarkers.get("p") != null) {
            mMarkers.get("p").setPosition(sourceLatLng);
        } else {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(sourceLatLng);
            markerOptions.icon(greenIcon);
            mMarkers.put("p", mGoogleMap.addMarker(markerOptions));
        }
        if (mMarkers.get("d") != null) {
            mMarkers.get("d").setPosition(destLatLng);
        } else {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(destLatLng);
            markerOptions.icon(redIcon);
            mMarkers.put("d", mGoogleMap.addMarker(markerOptions));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_progress);
        final String tripstatus = getIntent().getStringExtra("tripstatus");
        initComponents();
        this.tripReq = ApplicationSettings.getTripReq();
        if (null == tripstatus) {
            setTripStatusListener();
            customerName.setText(getIntent().getStringExtra("custName"));
            fromAddress.setText(getIntent().getStringExtra("source"));
        } else {
            resumeViewForCurrentTrip(tripstatus);
        }
    }

    private void resumeViewForCurrentTrip(String tripstatus) {
        tripstatus = tripstatus.toLowerCase();
        if (tripstatus.equalsIgnoreCase("scheduled")) {
            resumeReachedAtLocation();
        } else if (tripstatus.equalsIgnoreCase("arrived")) {
            resumeStartLoading();
        } else if (tripstatus.equalsIgnoreCase("loading")) {
            resumeStartTrip();
        } else if (tripstatus.equalsIgnoreCase("inprogress")) {
            resumestartUnloading();
        } else if (tripstatus.equalsIgnoreCase("unloading")) {
            resumeFinishTrip();
        } else if (tripstatus.equalsIgnoreCase("paydue")) {
            resumeFareSummaryView();
        }
        final FBVehicleRequest vehicleRequest = ApplicationSettings.getVehicleRequest();
        sourceLatLng = new LatLng(vehicleRequest.getOriginLat(), vehicleRequest.getOriginLng());
        destLatLng = new LatLng(vehicleRequest.getDestinationLat(), vehicleRequest.getDestinationLng());
        addPicupPointDropPointMarkers(vehicleRequest.getOriginLat(), vehicleRequest.getOriginLng(), vehicleRequest.getDestinationLat(), vehicleRequest.getDestinationLng());
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

                if (status[1].equalsIgnoreCase(ApplicationSettings.getDriverEid(TripProgressActivity.this))) {
                    //Trip belogs to this driver
                    if ("scheduled".equalsIgnoreCase(status[0])) {
                        onTripConfirmOperation();
                        //statusRef.removeEventListener(this);
                    } else if ("cancelled".equalsIgnoreCase(status[0]) || "expired".equalsIgnoreCase(status[0])) {
                        statusRef.removeEventListener(this);
                        FirebaseReferenceService.removeCurrentTrip(ApplicationSettings.getDriverEid(TripProgressActivity.this));
                        vehicleRequest = null;
                        tripReq = null;
                        finishAndLaunchMainActivity();
                    } else if ("loading".equalsIgnoreCase(status[0]) || "unloading".equalsIgnoreCase(status[0]) || "inprogress".equalsIgnoreCase(status[0])) {
                        statusRef.removeEventListener(this);
                    }
                } else {
                    //Trip doesn't below to this driver
                    statusRef.removeEventListener(this);
                    tripReq = null;
                    finishAndLaunchMainActivity();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        statusRef.addValueEventListener(tripStatusListener);
    }

    private void finishAndLaunchMainActivity() {
        startActivity(new Intent(getApplicationContext(), VehicleRequestActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    protected void initComponents() {

        super.initComponents();

        initMap();

        findRequiredViews();

        setFonts();
        initSwipeButtonEvents();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Bid_Accepted"));

        try {
            JSONObject jsonObject = new JSONObject(ApplicationSettings.getVehicleRequest(TripProgressActivity.this));
            //VehicleRequest vehicleRequest = new VehicleRequest(jsonObject);
            Log.e(TAG, vehicleRequest.toString());
            //₹
            customerName.setText(vehicleRequest.getCustomerName().toUpperCase());
            fromAddress.setText(vehicleRequest.getOriginAddress());

        } catch (Exception e) {
            //handle error
            Log.e(TAG, "vehicleRequest jsonException = " + e.getMessage());
        }
    }

    private void findRequiredViews() {
        customerView = findViewById(R.id.customerView);
        addressView = findViewById(R.id.addressView);
        startLoadingView = findViewById(R.id.startLoadingView);
        startTripView = findViewById(R.id.startTripView);
        startUnloadingView = findViewById(R.id.startUnloadingView);
        endTripView = findViewById(R.id.endTripView);
        fareSummaryView = findViewById(R.id.fareSummaryView);

        customerName = findViewById(R.id.customerName);
        acceptanceStatus = findViewById(R.id.acceptanceStatus);

        navigate = findViewById(R.id.navigate);
        fromAddress = findViewById(R.id.fromAddress);

        arrivedAtOriginTxt = findViewById(R.id.arrivedAtOriginTxt);
        startLoadingTxt = findViewById(R.id.startLoadingTxt);

        startTripTxt = findViewById(R.id.startTripTxt);

        arrivedAtDestinationTxt = findViewById(R.id.arrivedAtDestinationTxt);
        startUnloadingTxt = findViewById(R.id.startUnloadingTxt);

        endTripTxt = findViewById(R.id.endTripTxt);

        fareSummaryTxt = findViewById(R.id.fareSummaryTxt);
        paidByTxt = findViewById(R.id.paidByTxt);
        fareTxt = findViewById(R.id.fareTxt);
        rateCustomerTxt = findViewById(R.id.rateCustomerTxt);

        startLoadingBtn = findViewById(R.id.startLoadingBtn);
        startTripBtn = findViewById(R.id.startTripBtn);
        startUnloadingBtn = findViewById(R.id.startUnloadingBtn);
        endTripBtn = findViewById(R.id.endTripBtn);
        goOnlineBtn = findViewById(R.id.goOnlineBtn);

        reachedAtLocationBtn = findViewById(R.id.reachedAtLocationBtn);
        reachedAtLocationView = findViewById(R.id.reachedAtLocationView);
        reachedAtLocationTxt1 = findViewById(R.id.reachedAtLocationTxt1);
        reachedAtLocationTxt2 = findViewById(R.id.reachedAtLocationTxt2);
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
    }

    private void initSwipeButtonEvents() {
        initReachedAtLocationBtn();
        initStartLoadingBtn();
        initStartTripBtn();
        initStartUnloadingBtn();
        initEndTripBtn();
        initGoOnlineBtn();

    }


    private void initReachedAtLocationBtn() {
        reachedAtLocationBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
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

                reachedAtLocationBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        reachedAtLocationBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                hideAllViews();
                startLoadingView.setVisibility(View.VISIBLE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateTripStatus(ApplicationSettings.getTripReq().getCustId(), ApplicationSettings.getTripReq().getTripId(), "arrived");
                        //RodaRestClient.updateTripStatus(ApplicationSettings.getTripId(), ApplicationSettings.getRequestId(), 1, tripStatusUpdateHandler);
                    }
                });
            }
        });
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
                final FBVehicleRequest vehicleRequest = ApplicationSettings.getVehicleRequest();
                rateCustomerTxt.setText("Rate " + vehicleRequest.getCustomerName());
                paidByTxt.setText("Payment made by cash");//e-wallet
                long fare = vehicleRequest.getApproxFareInCents();
                fareTxt.setText("₹" + fare);
                fareSummaryView.setVisibility(View.VISIBLE);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateTripStatus(ApplicationSettings.getTripReq().getCustId(), ApplicationSettings.getTripReq().getTripId(), "paydue");
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
                //TODO remove current trip here. and update trip status to completed.
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        FirebaseReferenceService.addCashPaid(ApplicationSettings.getDriverEid(TripProgressActivity.this), ApplicationSettings.getTripReq().getCustId(), ApplicationSettings.getTripReq().getTripId(), 1000);
                        ApplicationSettings.getFbDriver().setCurrentTrip(null);
                        FirebaseReferenceService.removeCurrentTrip(ApplicationSettings.getDriverEid(TripProgressActivity.this));
                        updateTripStatus(ApplicationSettings.getTripReq().getCustId(), ApplicationSettings.getTripReq().getTripId(), "completed");
                        FirebaseReferenceService.onTripCompleted(ApplicationSettings.getDriverEid(TripProgressActivity.this), ApplicationSettings.getTripReq().getCustId(), ApplicationSettings.getTripReq().getTripId());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                startNextActivity();
                            }
                        });
                    }
                });

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
        reachedAtLocationView.setVisibility(View.GONE);
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

                //vehicleRequest = new FBVehicleRequest(jsonObject);
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
        final FBVehicleRequest vehicleRequest = ApplicationSettings.getVehicleRequest();
        addPicupPointDropPointMarkers(vehicleRequest.getOriginLat(), vehicleRequest.getOriginLng(), vehicleRequest.getDestinationLat(), vehicleRequest.getDestinationLng());
        customerName.setText(vehicleRequest.getCustomerName().toUpperCase());
        fromAddress.setText(vehicleRequest.getOriginAddress());
        acceptanceStatus.setText("ACCEPTED");
        final String driverEid = ApplicationSettings.getDriverEid(TripProgressActivity.this);
        FirebaseReferenceService.addDriversCurrentTrip(driverEid, tripReq.getTripId(), tripReq.getCustId());

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            public void run() {
                customerView.setVisibility(View.GONE);
                addressView.setVisibility(View.VISIBLE);
                reachedAtLocationView.setVisibility(View.VISIBLE);
            }
        }, 2000);

    }

    private void resumeReachedAtLocation() {
        customerView.setVisibility(View.GONE);
        addressView.setVisibility(View.VISIBLE);
        reachedAtLocationView.setVisibility(View.VISIBLE);
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

    private void resumeFareSummaryView() {
        customerView.setVisibility(View.GONE);
        addressView.setVisibility(View.VISIBLE);
        fareSummaryView.setVisibility(View.VISIBLE);
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
