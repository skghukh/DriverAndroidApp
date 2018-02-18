package com.rodafleets.rodadriver;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.w3c.dom.Text;

public class PersonalInfoSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("PERSONAL INFO");
        initViews();
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

    private void initViews() {
        TextView driverName = (TextView) findViewById(R.id.driver_name);
        TextView driverNumber = (TextView) findViewById(R.id.phone_number_value);
        TextView password = (TextView) findViewById(R.id.password_value);
        driverName.setText(ApplicationSettings.getDriverName(PersonalInfoSettings.this));
        driverNumber.setText(ApplicationSettings.getDriverEid(PersonalInfoSettings.this));
        password.setText("*****");
    }

}
