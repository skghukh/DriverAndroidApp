package com.rodafleets.rodadriver;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rodafleets.rodadriver.model.FBDriver;
import com.rodafleets.rodadriver.services.FirebaseReferenceService;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

public class ParentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Typeface poppinsRegular;
    Typeface poppinsMedium;
    Typeface poppinsLight;
    Typeface poppinsBold;
    Typeface poppinsSemiBold;

    Typeface sintonyRegular;
    Typeface sintonyBold;

    protected DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar toolbar;

    private static String driverName;
    private static TextView driverNameView;
    private static Button goOfflineBtn;

    private AlphaAnimation changeStatusAnimation = new AlphaAnimation(1F, 0.75F);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    protected void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeSideMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(Gravity.LEFT);
        if (!isDrawerOpen) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        } else {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        return super.onOptionsItemSelected(item);
    }

    public void initializeSideMenu() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            Log.i("MENU", "DrawerLayout is null");
        }
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.open,  /* "open drawer" description */
                R.string.close  /* "close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
//                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                FBDriver fbDriver = null;
                if (null == driverNameView) {
                    driverNameView = findViewById(R.id.driverName);
                }
                if (null == goOfflineBtn) {
                    goOfflineBtn = findViewById(R.id.goOfflineBtn);
                }

                    driverNameView.setText(ApplicationSettings.getDriverName(ParentActivity.this));
                goOfflineBtn.setClickable(true);
                fbDriver = ApplicationSettings.getFbDriver();
                if (null == fbDriver || null == fbDriver.getOffline()) {
                    goOfflineBtn.setText("GO OFFLINE");
                    goOfflineBtn.setTag("ONLINE");
                } else if (fbDriver.getOffline().equalsIgnoreCase("TRUE")) {
                    goOfflineBtn.setText("GO ONLINE");
                    goOfflineBtn.setTag("OFFLINE");
                }

            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();

        ActionBar actionBar = this.getSupportActionBar();

        actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.actionBarPurple)));
        actionBar.setDisplayShowTitleEnabled(false);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.menu_trip: {
                Log.i("Menu", "trip clicked");
                break;
            }

            case R.id.menu_settings: {
                break;
            }
        }
        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFonts() {
        poppinsRegular = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_REGULAR);
        poppinsMedium = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_MEDIUM);
        poppinsLight = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_LIGHT);
        poppinsBold = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_BOLD);
        poppinsSemiBold = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_SEMI_BOLD);

        sintonyRegular = Typeface.createFromAsset(getAssets(), AppConstants.FONT_SINTONY_REGULAR);
        sintonyBold = Typeface.createFromAsset(getAssets(), AppConstants.FONT_SINTONY_BOLD);
    }


    public void openSettings(View view) {
        Intent intent = new Intent(this, DriverSettings.class);
        startActivity(intent);
    }

    public void openTripsPayments(View view) {
        Intent intent = new Intent(this, PaymentAndTrips.class);
        startActivity(intent);
    }

    public void onGoOfflineBtnClick(View view) {
        if (((String) view.getTag()).equalsIgnoreCase("OFFLINE")) {
            FirebaseReferenceService.goOnline(ParentActivity.this, ApplicationSettings.getDriverEid(ParentActivity.this));
            if (null != goOfflineBtn) goOfflineBtn.setText("online...");

        } else if (((String) view.getTag()).equalsIgnoreCase("ONLINE")) {
            ApplicationSettings.getFbDriver().setOffline("TRUE");
            FirebaseReferenceService.goOffline(ParentActivity.this, ApplicationSettings.getDriverEid(ParentActivity.this));
            if (null != goOfflineBtn) goOfflineBtn.setText("offline...");
        }
        goOfflineBtn.setClickable(false);
    }

    public void logOut(View view) {
        FirebaseReferenceService.logout(ParentActivity.this);
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }
}
