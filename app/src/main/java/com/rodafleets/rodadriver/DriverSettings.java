package com.rodafleets.rodadriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rodafleets.rodadriver.utils.ApplicationSettings;

public class DriverSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("SETTINGS");
        initViews();
    }

    private void initViews() {
        TextView driver_name = (TextView) findViewById(R.id.personal_details_name);
        TextView driver_number = (TextView) findViewById(R.id.personal_detials_number);
        driver_name.setText(ApplicationSettings.getDriverName(DriverSettings.this));
        driver_number.setText(ApplicationSettings.getDriverEid(DriverSettings.this));

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

    public void openPersonalDetails(View view) {
        Intent personalDetails = new Intent(this, PersonalInfoSettings.class);
        startActivity(personalDetails);

    }

    public void openVehicleDetails(View view) {
        Intent vehicleDetails = new Intent(this, vehicle_details_settings.class);
        startActivity(vehicleDetails);
    }

    public void openDocumentSelector(View view) {
        Intent personalDetails = new Intent(this, PersonalInfoSettings.class);
        startActivity(personalDetails);
    }

    public void openAccountDetails(View view) {
        Intent accountdetails = new Intent(this, account_details_settings.class);
        startActivity(accountdetails);
    }
}
