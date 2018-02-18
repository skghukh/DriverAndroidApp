package com.rodafleets.rodadriver;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodadriver.model.FBDriver;
import com.rodafleets.rodadriver.model.FBTrip;
import com.rodafleets.rodadriver.rest.RodaRestClient;
import com.rodafleets.rodadriver.services.FirebaseReferenceService;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class PaymentAndTrips extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;
    private FirebaseListAdapter<FBTrip> tripAdapter;


    private static final String TAG = "RD";
    private static long cashDue = -1l;
    private static long paytmDue = -1l;
    private static TextView cash_earning_val;
    private static TextView wallet_earning_value;
    private Date currentDate;
    private static String billPeriod;
    private static TextView billPeriodText;
    private static TextView lastDateOfPayment;
    private static TextView totalEarningTextView;
    private static TextView youOweTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_and_trips);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("TRIPS AND PAYMENTS");
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        currentDate = new Date();
        final int currentDate = this.currentDate.getDate();
        if (currentDate <= 15) billPeriod = "1-15";
        else
            billPeriod = "16-30";
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private static FirebaseListAdapter getTripAdapter(Context context, int sectionNumber) {
        // final ListView driverTrips = findViewById(R.id.driverTrips);

        if (sectionNumber == 1) {
            final DatabaseReference driverTripsReference = FirebaseReferenceService.getDriverCompletedTripRef(ApplicationSettings.getDriverEid(context));
            return new FirebaseListAdapter<FBTrip>(context, FBTrip.class, R.layout.trip_list_element, driverTripsReference.limitToLast(25)) {

                @Override
                protected void populateView(View v, FBTrip model, int position) {
                    TextView custName = v.findViewById(R.id.custName);
                    TextView time = v.findViewById(R.id.date);
                    TextView dist = v.findViewById(R.id.distance);
                    TextView fare = v.findViewById(R.id.fare);
                    TextView source = v.findViewById(R.id.sourceLoc);
                    TextView dest = v.findViewById(R.id.destLoc);
                    custName.setText(model.getCustName());
                    time.setText(model.getDate());
                    dist.setText(model.getDistance());
                    fare.setText(model.getAmount());
                    source.setText(model.getSource());
                    dest.setText(model.getDest());
                }
            };
        } else {
            final DatabaseReference driverTripsReference = FirebaseReferenceService.getDriverCancelledTripRef(ApplicationSettings.getDriverEid(context));
            return new FirebaseListAdapter<FBTrip>(context, FBTrip.class, R.layout.trip_list_element, driverTripsReference.limitToLast(25)) {

                @Override
                protected void populateView(View v, FBTrip model, int position) {
                    TextView custName = v.findViewById(R.id.custName);
                    TextView time = v.findViewById(R.id.date);
                    TextView dist = v.findViewById(R.id.distance);
                    TextView fare = v.findViewById(R.id.fare);
                    TextView source = v.findViewById(R.id.sourceLoc);
                    TextView dest = v.findViewById(R.id.destLoc);
                    custName.setText(model.getCustName());
                    time.setText(model.getDate());
                    dist.setText(model.getDistance());
                    fare.setText(model.getAmount());
                    source.setText(model.getSource());
                    dest.setText(model.getDest());
                }
            };
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment_and_trips, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.fragment_payment_and_trips, container, false);
            final int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;
            if (sectionNumber == 1 || sectionNumber == 2) {
                rootView = inflater.inflate(R.layout.fragment_trips, container, false);
                ListView listView = rootView.findViewById(R.id.driverTrips);
                listView.setAdapter(getTripAdapter(inflater.getContext(), sectionNumber));
            } else if (sectionNumber == 3) {
                final DatabaseReference driverLastSettlementTimeReference = FirebaseReferenceService.getDriverLastSettlementTime(ApplicationSettings.getDriverEid(inflater.getContext()));
                rootView = inflater.inflate(R.layout.fragment_payment, container, false);
                wallet_earning_value = rootView.findViewById(R.id.wallet_earning_value);
                cash_earning_val = rootView.findViewById(R.id.cash_earning_val);
                billPeriodText = rootView.findViewById(R.id.bill_period_value);
                Calendar calendar = Calendar.getInstance();
                billPeriodText.setText(billPeriod+ new SimpleDateFormat("MMM").format(calendar.getTime()));
                lastDateOfPayment = rootView.findViewById(R.id.last_date_of_payment_value);
                lastDateOfPayment.setText(calendar.getActualMaximum(Calendar.DATE) + " " + new SimpleDateFormat("MMM").format(calendar.getTime()));
                youOweTextView = rootView.findViewById(R.id.you_owe_value);
                totalEarningTextView = rootView.findViewById(R.id.total_earnings_value);
                driverLastSettlementTimeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    final Context mFinalContext = inflater.getContext();

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long lastTimeStamp = (Long) dataSnapshot.getValue();
                        if (null == lastTimeStamp || lastTimeStamp < 0) {
                            lastTimeStamp = 0l;
                        }
                        RodaRestClient.getMoneySettlement(ApplicationSettings.getDriverEid(mFinalContext), lastTimeStamp, mPaymentDueResponseHandler);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            return rootView;
        }


        private JsonHttpResponseHandler mPaymentDueResponseHandler = new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
                try {
                    cashDue = jsonResponseObject.getLong("first");
                    paytmDue = jsonResponseObject.getLong("second");
                    if (cashDue > -1) {
                        cash_earning_val.setText("INR " + cashDue);
                        youOweTextView.setText(cashDue / 10 + "");
                        totalEarningTextView.setText("INR " + cashDue);
                    }
                    if (paytmDue > -1) {
                        wallet_earning_value.setText("INR " + paytmDue);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception while fetching settlement amount " + e);
                    Snackbar.make(mViewPager, "Something went rent", Snackbar.LENGTH_SHORT);
                }

            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Snackbar.make(mViewPager, "Something went wrong", Snackbar.LENGTH_SHORT);
                Log.e(TAG, errorResponse + " " + throwable);
            }

        };

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
