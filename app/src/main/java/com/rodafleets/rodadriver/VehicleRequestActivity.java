package com.rodafleets.rodadriver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodadriver.custom.slideview.SlideView;
import com.rodafleets.rodadriver.model.FBDriver;
import com.rodafleets.rodadriver.model.FBVehicleRequest;
import com.rodafleets.rodadriver.model.TripRequest;
import com.rodafleets.rodadriver.model.FBVehicleRequest;
import com.rodafleets.rodadriver.model.VehicleRequestResponse;
import com.rodafleets.rodadriver.services.FirebaseReferenceService;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class VehicleRequestActivity extends MapActivity {

    public static final String TAG = AppConstants.APP_NAME;

    private CardView requestView;
    private TextView customerName;
    private TextView fromAddress;
    private TextView toAddress;
    private TextView distance;
    private TextView loadingUnloadingTxt;
    private TextView makeOfferTxt;
    private TextView callAdmin;

    private Button callCustomerBtn;

    private SlideView makeOfferBtn;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_request);
        initComponents();
        final Intent intent = this.getIntent();
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String custId = intent.getStringExtra("custId");
        String tripId = intent.getStringExtra("tripId");
        showVehicleRequestFromNotification(custId, tripId);
    }

    protected void initComponents() {
        super.initComponents();
        requestView = (CardView) findViewById(R.id.requestView);

        customerName = (TextView) findViewById(R.id.customerName);
        fromAddress = (TextView) findViewById(R.id.fromAddress);
        toAddress = (TextView) findViewById(R.id.toAddress);
        distance = (TextView) findViewById(R.id.distance);
        loadingUnloadingTxt = (TextView) findViewById(R.id.loadingUnloadingTxt);
        makeOfferTxt = (TextView) findViewById(R.id.makeOfferTxt);

        callCustomerBtn = (Button) findViewById(R.id.callCustomerBtn);
        callAdmin = (TextView) findViewById(R.id.callAdmin);
        makeOfferBtn = (SlideView) findViewById(R.id.makeOfferBtn);

        initMap();
        setFonts();
        initMakeOfferBtn();
        getDriverStatus();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Vehicle_Requested"));
        boolean fromNotification = getIntent().getBooleanExtra("FROM_NOTIFICATION", false);
        if (fromNotification) {
            Log.i(TAG, "opened from notification");
            showVehicleRequestFromReceiver();
        }
    }

    private void getDriverStatus() {
        final DatabaseReference driverRef = FirebaseReferenceService.getDriverRef(ApplicationSettings.getDriverEid(VehicleRequestActivity.this));
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot && null != dataSnapshot.getValue()) {
                    new PostTask().execute(dataSnapshot, null, null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class PostTask extends AsyncTask<DataSnapshot, Void, Void> {
        @Override
        protected void onPreExecute() {
            Snackbar.make(requestView, "fetching active trip details", Snackbar.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(DataSnapshot... dataSnapshots) {
            final FBDriver fbDriver = dataSnapshots[0].getValue(FBDriver.class);
            ApplicationSettings.setFbDriver(fbDriver);
            if (!fbDriver.isVerified()) {
                //TODO Show message here that driver is not verified here.
            } else {
                if (null != fbDriver.getCurrentTrip()) {
                    final DatabaseReference tripReferece = FirebaseReferenceService.getTripReferece(fbDriver.getCurrentTrip().getCustId(), fbDriver.getCurrentTrip().getTripid());
                    tripReferece.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final FBVehicleRequest listeningVehicleRequest = dataSnapshot.getValue(FBVehicleRequest.class);
                            if (null == listeningVehicleRequest.getStatus()) {
                                //TODO add expiration check login here.
                                return;
                            }
                            String[] status = listeningVehicleRequest.getStatus().split("\\_");
                            if (status.length>0 && status[1] !=null && status[1].equalsIgnoreCase(ApplicationSettings.getDriverEid(VehicleRequestActivity.this))) {
                                TripRequest req = new TripRequest(fbDriver.getCurrentTrip().getCustId(),fbDriver.getCurrentTrip().getTripid(),FirebaseReferenceService.getTripReferece(fbDriver.getCurrentTrip().getCustId(),fbDriver.getCurrentTrip().getTripid()));
                                req.setStatusRef(req.getTripRef().child("status"));
                                ApplicationSettings.setTripReq(req);
                                ApplicationSettings.setVehicleRequest(listeningVehicleRequest);
                                startNextActivityWithStatus(status[0]);
                            } else {
                                //TODO Remove Current trip reference.
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
            return null;
        }

    }




    private void setFonts() {
        loadFonts();
        customerName.setTypeface(poppinsMedium);
        fromAddress.setTypeface(poppinsRegular);
        toAddress.setTypeface(poppinsRegular);
        distance.setTypeface(poppinsLight);
        loadingUnloadingTxt.setTypeface(poppinsSemiBold);
//        makeOfferBtn.setTypeface(poppinsSemiBold);
        makeOfferTxt.setTypeface(poppinsRegular);
        callCustomerBtn.setTypeface(poppinsMedium);
        callAdmin.setTypeface(poppinsRegular);
    }

    private void initMakeOfferBtn() {
        makeOfferBtn.getSlider().setOnTouchListener(new AppCompatSeekBar.OnTouchListener() {
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

                makeOfferBtn.getSlider().onTouchEvent(event);

                return false;
            }
        });

        makeOfferBtn.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {
                bidRequest();
            }
        });
    }

    private void bidRequest() {
        if (null != tripReq) {
            final DatabaseReference tripRef = tripReq.getTripRef();
            final DatabaseReference statusRef = tripRef.child("status");
            tripReq.setStatusRef(statusRef);
            //TODO:- check here if statusRef is already scheduled, show msg here, and don't add response.
            final DatabaseReference responseRef = tripRef.child("responses/" + ApplicationSettings.getDriverEid(VehicleRequestActivity.this));
            VehicleRequestResponse response = new VehicleRequestResponse();
            response.setDistance("5");
            // response.setDriverId("1");
            //response.setDriverRating("5");
            response.setOfferedFare("10000");
            responseRef.setValue(response, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    System.out.println("Writing is successfull");
                    ApplicationSettings.setTripReq(tripReq);
                    startNextActivity();
                }
            });

         /*   DatabaseReference pushRef = responseRef.push();
           final Task<Void> voidTask = pushRef.setValue(response);
            if(voidTask.isSuccessful()){
                startNextActivity();
            }*/

        } else {
            //Show error message here.
        }

        /*
        int driverId = ApplicationSettings.getDriverId(VehicleRequestActivity.this);
        RodaRestClient.bidRequest(vehicleRequest.getId(), driverId, vehicleRequest.getApproxFareInCents(), bidRequestResponseHandler);
        */
    }

    public void onRejectBtnClick(View view) {

        onRejectHandler();

        /*
        int driverId = ApplicationSettings.getDriverId(VehicleRequestActivity.this);
        RodaRestClient.rejectRequest(vehicleRequest.getId(), driverId, rejectRequestResponseHandler);
        */
    }

    private void onRejectHandler() {
        clearMap();
        requestView.setVisibility(View.GONE);
        vehicleRequest = null;
    }


    public void onCallCustomerBtnClick(View view) {

    }

    private void startNextActivity() {
        this.startActivity(new Intent(this, TripProgressActivity.class));
        finish();
    }

    private void startNextActivityWithStatus(String status) {
        final Intent intent = new Intent(this, TripProgressActivity.class);
        intent.putExtra("tripstatus", status);
        this.startActivity(intent);
        finish();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showVehicleRequestFromReceiver();
        }
    };

    private void showVehicleRequestFromReceiver() {
        try {

            JSONObject jsonObject = new JSONObject(ApplicationSettings.getVehicleRequest(VehicleRequestActivity.this));
            //vehicleRequest = new FBVehicleRequest(jsonObject);
            showVehicleRequestOnUI();
        } catch (Exception e) {
            //handle error
            Log.e(TAG, "vehicleRequest jsonException = " + e.getMessage());
        }
    }

    private void showVehicleRequestFromNotification(String custId, String tripId) {
        subscribeForTripUpdate(custId, tripId, new Runnable() {
            @Override
            public void run() {
                if (null != vehicleRequest) {
                    showVehicleRequestOnUI();
                }
            }
        });

    }

    private void showVehicleRequestOnUI() {
        RodaDriverApplication.vehicleRequests.add(vehicleRequest);
        customerName.setText(vehicleRequest.getCustomerName().toUpperCase());
        fromAddress.setText(vehicleRequest.getOriginAddress());
        toAddress.setText(vehicleRequest.getDestinationAddress());
        distance.setText("3000");//vehicleRequest.getDistance());
        long fare = vehicleRequest.getApproxFareInCents() / 100;
        makeOfferBtn.setText("₹" + fare);
        requestView.setVisibility(View.VISIBLE);
        requestView.bringToFront();
    }

    private JsonHttpResponseHandler bidRequestResponseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
            startNextActivity();
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//            if(errorCode == ResponseCode.INVALID_CREDENTIALS) {
//                sb = Snackbar.make(constraintLayout, getString(R.string.sign_in_invalid_credentials_error), Snackbar.LENGTH_LONG);
//            } else {
//                sb = Snackbar.make(constraintLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG);
//            }
        }
    };

    private JsonHttpResponseHandler rejectRequestResponseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
            ApplicationSettings.setVehicleRequest(VehicleRequestActivity.this, null);
            clearMap();
            requestView.setVisibility(View.GONE);
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//            if(errorCode == ResponseCode.INVALID_CREDENTIALS) {
//                sb = Snackbar.make(constraintLayout, getString(R.string.sign_in_invalid_credentials_error), Snackbar.LENGTH_LONG);
//            } else {
//                sb = Snackbar.make(constraintLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG);
//            }
        }
    };
}
