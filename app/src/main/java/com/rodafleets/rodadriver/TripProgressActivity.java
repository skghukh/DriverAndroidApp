package com.rodafleets.rodadriver;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.rodafleets.rodadriver.custom.SwipeButton;
import com.rodafleets.rodadriver.custom.SwipeButtonCustomItems;
import com.rodafleets.rodadriver.utils.AppConstants;

public class TripProgressActivity extends MapParentActivity {

    public static final String TAG = AppConstants.APP_NAME;

    // Customer View
    private CardView customerView;
    private TextView customerName;
    private TextView acceptanceStatus;

    // Address View
    private CardView addressView;
    private TextView toAddressLine1;
    private TextView toAddressLine2;

    //Start Loading View
    private CardView startLoadingView;
    private SwipeButton startLoadingBtn;

    // Start Trip View
    private CardView startTripView;
    private SwipeButton startTripBtn;

    //Start Unloading View
    private CardView startUnloadingView;
    private SwipeButton startUnloadingBtn;

    // End Trip View
    private CardView endTripView;
    private SwipeButton endTripBtn;

    // Fare Summary View
    private CardView fareSummaryView;
    private TextView paidByText;
    private TextView fareText;
    private TextView customerNameText;
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
        toAddressLine1 = (TextView) findViewById(R.id.toAddressLine1);
        toAddressLine2 = (TextView) findViewById(R.id.toAddressLine2);
        paidByText = (TextView) findViewById(R.id.paidByText);
        fareText = (TextView) findViewById(R.id.fareText);
        customerNameText = (TextView) findViewById(R.id.customerNameText);

        startLoadingBtn = (SwipeButton) findViewById(R.id.startLoadingBtn);
        startTripBtn = (SwipeButton) findViewById(R.id.startTripBtn);
        startUnloadingBtn = (SwipeButton) findViewById(R.id.startUnloadingBtn);
        endTripBtn = (SwipeButton) findViewById(R.id.endTripBtn);
        goOnlineBtn = (SwipeButton) findViewById(R.id.goOnlineBtn);


        Typeface poppinsRegular = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_REGULAR);
        Typeface poppinsSemiBold = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_SEMI_BOLD);
        Typeface sintonyBold = Typeface.createFromAsset(getAssets(), AppConstants.FONT_SINTONY_BOLD);

        initSwipeButtonsEvents();
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
