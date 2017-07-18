package com.rodafleets.rodadriver;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.rodafleets.rodadriver.custom.SwipeButton;
import com.rodafleets.rodadriver.custom.SwipeButtonCustomItems;
import com.rodafleets.rodadriver.model.VehicleRequest;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONObject;

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
    private SwipeButton startLoadingBtn;
    private TextView startLoadingTxt;

    // Start Trip View
    private CardView startTripView;
    private SwipeButton startTripBtn;
    private TextView startTripTxt;

    //Start Unloading View
    private CardView startUnloadingView;
    private TextView arrivedAtDestinationTxt;
    private SwipeButton startUnloadingBtn;
    private TextView startUnloadingTxt;

    // End Trip View
    private CardView endTripView;
    private SwipeButton endTripBtn;
    private TextView endTripTxt;

    // Fare Summary View
    private CardView fareSummaryView;
    private TextView fareSummaryTxt;
    private TextView paidByTxt;
    private TextView fareTxt;
    private TextView rateCustomerTxt;
    private SwipeButton goOnlineBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_trip_progress);

        initComponents();
    }

    private void initComponents() {

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

        navigate  = (TextView) findViewById(R.id.navigate);
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

        startLoadingBtn = (SwipeButton) findViewById(R.id.startLoadingBtn);
        startTripBtn = (SwipeButton) findViewById(R.id.startTripBtn);
        startUnloadingBtn = (SwipeButton) findViewById(R.id.startUnloadingBtn);
        endTripBtn = (SwipeButton) findViewById(R.id.endTripBtn);
        goOnlineBtn = (SwipeButton) findViewById(R.id.goOnlineBtn);

        setFonts();
        initSwipeButtonsEvents();

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

        startLoadingBtn.setTypeface(poppinsSemiBold);
        startTripBtn.setTypeface(poppinsSemiBold);
        startUnloadingBtn.setTypeface(poppinsSemiBold);
        endTripBtn.setTypeface(poppinsSemiBold);
        goOnlineBtn.setTypeface(poppinsSemiBold);
    }

    private void initSwipeButtonsEvents() {

        SwipeButtonCustomItems startLoadingBtnSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                hideAllViews();
                startTripView.setVisibility(View.VISIBLE);
            }
        };

        if (startLoadingBtn != null) {
            startLoadingBtn.setSwipeButtonCustomItems(startLoadingBtnSettings);
        }



        SwipeButtonCustomItems startTripBtnSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                hideAllViews();
                startUnloadingView.setVisibility(View.VISIBLE);
            }
        };
        if (startTripBtn != null) {
            startTripBtn.setSwipeButtonCustomItems(startTripBtnSettings);
        }



        SwipeButtonCustomItems startUnloadingBtnSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                hideAllViews();
                endTripView.setVisibility(View.VISIBLE);
            }
        };
        if (startUnloadingBtn != null) {
            startUnloadingBtn.setSwipeButtonCustomItems(startUnloadingBtnSettings);
        }



        SwipeButtonCustomItems endTripBtnSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                hideAllViews();
                fareSummaryView.setVisibility(View.VISIBLE);
            }
        };
        if (endTripBtn != null) {
            endTripBtn.setSwipeButtonCustomItems(endTripBtnSettings);
        }

        SwipeButtonCustomItems goOnlineBtnSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                //
            }
        };
        if (goOnlineBtn != null) {
            goOnlineBtn.setSwipeButtonCustomItems(goOnlineBtnSettings);
        }

    }

    private void hideAllViews() {
        customerView.setVisibility(View.GONE);
        addressView.setVisibility(View.GONE);
        startLoadingView.setVisibility(View.GONE);
        startTripView.setVisibility(View.GONE);
        startUnloadingView.setVisibility(View.GONE);
        endTripView.setVisibility(View.GONE);
    }
}
