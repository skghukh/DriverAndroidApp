package com.rodafleets.rodadriver;

import android.content.BroadcastReceiver;
import android.content.Context;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodadriver.custom.SwipeButton;
import com.rodafleets.rodadriver.custom.SwipeButtonCustomItems;
import com.rodafleets.rodadriver.custom.VehicleTypeSpinnerAdapter;
import com.rodafleets.rodadriver.model.Driver;
import com.rodafleets.rodadriver.model.VehicleRequest;
import com.rodafleets.rodadriver.model.VehicleType;
import com.rodafleets.rodadriver.rest.ResponseCode;
import com.rodafleets.rodadriver.rest.RodaRestClient;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    private SwipeButton makeOfferBtn;

    private VehicleRequest vehicleRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_vehicle_request);

        initComponents();
    }

    private void initComponents() {
        requestView = (CardView) findViewById(R.id.requestView);

        customerName = (TextView) findViewById(R.id.customerName);
        fromAddress = (TextView) findViewById(R.id.fromAddress);
        toAddress = (TextView) findViewById(R.id.toAddress);
        distance = (TextView) findViewById(R.id.distance);
        loadingUnloadingTxt = (TextView) findViewById(R.id.loadingUnloadingTxt);
        makeOfferTxt = (TextView) findViewById(R.id.makeOfferTxt);

        callCustomerBtn = (Button) findViewById(R.id.callCustomerBtn);
        callAdmin = (TextView) findViewById(R.id.callAdmin);
        makeOfferBtn = (SwipeButton) findViewById(R.id.makeOfferBtn);

        initMap();
        setFonts();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Vehicle_Requested"));

        SwipeButtonCustomItems makeOfferBtnSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                RodaRestClient.bidRequest(vehicleRequest.getId(), vehicleRequest.getApproxFareInCents(), bidRequestResposeHandler);
            }
        };

        makeOfferBtnSettings
                .setButtonPressText("Making Offer");

        if (makeOfferBtn != null) {
            makeOfferBtn.setSwipeButtonCustomItems(makeOfferBtnSettings);
        }
    }

    private void setFonts() {
        loadFonts();
        customerName.setTypeface(poppinsMedium);
        fromAddress.setTypeface(poppinsRegular);
        toAddress.setTypeface(poppinsRegular);
        distance.setTypeface(poppinsLight);
        loadingUnloadingTxt.setTypeface(poppinsSemiBold);
        makeOfferBtn.setTypeface(poppinsSemiBold);
        makeOfferTxt.setTypeface(poppinsRegular);
        callCustomerBtn.setTypeface(poppinsMedium);
        callAdmin.setTypeface(poppinsRegular);
    }

    public void onRejectBtnClick(View view) {
        ApplicationSettings.setVehicleRequest(VehicleRequestActivity.this, null);
        requestView.setVisibility(View.GONE);
    }

    public void onCallCustomerBtnClick(View view) {

    }

    private void startNextActivity(){
        this.startActivity(new Intent(this, TripProgressActivity.class));
        finish();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                JSONObject jsonObject = new JSONObject(ApplicationSettings.getVehicleRequest(VehicleRequestActivity.this));
                vehicleRequest = new VehicleRequest(jsonObject);
                Log.e(TAG, vehicleRequest.toString());
                //₹
                customerName.setText(vehicleRequest.getCustomerName().toUpperCase());
                fromAddress.setText(vehicleRequest.getOriginAddress());
                toAddress.setText(vehicleRequest.getDestinationAddress());
                distance.setText(vehicleRequest.getDistance());

                long fare = vehicleRequest.getApproxFareInCents()/100;
                makeOfferBtn.setText("₹" + fare);
                requestView.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                //handle error
                Log.e(TAG, "vehicleRequest jsonException = " + e.getMessage());
            }
        }
    };

    private JsonHttpResponseHandler bidRequestResposeHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONArray responseArray) {
            Log.i(AppConstants.APP_NAME, "response = " + responseArray.toString());
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
}
