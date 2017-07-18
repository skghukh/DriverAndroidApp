package com.rodafleets.rodadriver;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.rodafleets.rodadriver.utils.AppConstants;

public class ParentActivity extends AppCompatActivity {

    Typeface poppinsRegular;
    Typeface poppinsMedium;
    Typeface poppinsLight;
    Typeface poppinsBold;
    Typeface poppinsSemiBold;

    Typeface sintonyRegular;
    Typeface sintonyBold;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.settings:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            // action with ID action_settings was selected
            case R.id.info:
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }
        return true;
    }

    public void initializeSideMenu() {

//        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (ListView) findViewById(R.id.left_drawer);
//
//        // Set the adapter for the list view
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
//                R.layout.drawer_list_item, mPlanetTitles));
//        // Set the list's click listener
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
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

}
