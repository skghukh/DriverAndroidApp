package com.rodafleets.rodadriver;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rodafleets.rodadriver.model.FBDriver;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

public class vehicle_details_settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("VEHICLE DETAILS");
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        TextView vehicleNumber = (TextView) findViewById(R.id.vehicle_number_value);
        TextView vehicleType = (TextView) findViewById(R.id.vehicle_type_value);
        TextView vehicleManufacturer = (TextView) findViewById(R.id.vehicle_manufacturer_value);
        TextView vehicleModel = (TextView) findViewById(R.id.vehicle_model_value);

        final FBDriver fbDriver = ApplicationSettings.getFbDriver();
        vehicleNumber.setText(fbDriver.getVehicleDetails().getVehicleNumber());
        vehicleType.setText(getVehicleType(fbDriver.getVehicleDetails().getVehicleType()));
        vehicleManufacturer.setText(fbDriver.getVehicleDetails().getManufacturer());
        vehicleModel.setText(fbDriver.getVehicleDetails().getModel());
    }

    private String getVehicleType(int vehicleType) {
        String vehicleTypeValue = "NA";
        switch (vehicleType) {
            case 1:
                vehicleTypeValue = "SMALL LOAD";
                break;
            case 2:
                vehicleTypeValue = "MEDIUM LOAD";
                break;
            case 3:
                vehicleTypeValue = "LARGE LOAD";
                break;
            default:
                vehicleTypeValue = "NA";
        }
        return vehicleTypeValue;
    }
}
