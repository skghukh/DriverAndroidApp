package com.rodafleets.rodadriver;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.rodafleets.rodadriver.model.FBAccountDetails;
import com.rodafleets.rodadriver.services.FirebaseReferenceService;
import com.rodafleets.rodadriver.utils.ApplicationSettings;
import com.rodafleets.rodadriver.utils.Utils;

public class account_details_settings extends AppCompatActivity {

    private final EditText accountDetails[] = new EditText[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("ACCOUNT DETAILS");
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
        accountDetails[0] = findViewById(R.id.account_holder_name);
        accountDetails[1] = findViewById(R.id.bank_name);
        accountDetails[2] = findViewById(R.id.branch);
        accountDetails[3] = findViewById(R.id.account_number);
        accountDetails[4] = findViewById(R.id.ifsc_code);
        final DatabaseReference accountDetailsRef = FirebaseReferenceService.getAccountDetails(ApplicationSettings.getDriverEid(account_details_settings.this));
        accountDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null == dataSnapshot || null == dataSnapshot.getKey() || null == dataSnapshot.getValue()) {
                    return;
                } else {
                    final FBAccountDetails account = dataSnapshot.getValue(FBAccountDetails.class);
                    if (null != account) {
                        accountDetails[0].setText(account.getOwner());
                        accountDetails[1].setText(account.getBank());
                        accountDetails[2].setText(account.getBranch());
                        accountDetails[3].setText(account.getAccountNumber());
                        accountDetails[4].setText(account.getIfscCode());
                        for (int i = 0; i < 5; i++) {
                            accountDetails[i].setKeyListener(null);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void saveAccountDetails(View view) {
        final String holderName = accountDetails[0].getText().toString();
        final String bank_name = accountDetails[1].getText().toString();
        final String branch = accountDetails[2].getText().toString();
        final String account_number = accountDetails[3].getText().toString();
        final String ifsc_code = accountDetails[4].getText().toString();

        if (null == holderName || holderName.isEmpty()) {
            Snackbar.make(accountDetails[0], "check account holder name ", Snackbar.LENGTH_SHORT).show();
        } else if (null == bank_name || bank_name.isEmpty()) {
            Snackbar.make(accountDetails[0], "check bank name", Snackbar.LENGTH_SHORT).show();
        } else if (null == branch || branch.isEmpty()) {
            Snackbar.make(accountDetails[0], "check branch name", Snackbar.LENGTH_SHORT).show();
        } else if (null == account_number || account_number.isEmpty()) {
            Snackbar.make(accountDetails[0], "check account number", Snackbar.LENGTH_SHORT).show();
        } else if (null == ifsc_code || ifsc_code.isEmpty()) {
            Snackbar.make(accountDetails[0], "check branch IFSC code ", Snackbar.LENGTH_SHORT).show();
        } else {
            FirebaseReferenceService.saveAccountDetails(ApplicationSettings.getDriverEid(account_details_settings.this), new FBAccountDetails(holderName, bank_name, branch, account_number, ifsc_code));
            Snackbar.make(accountDetails[0], "Account will be verified", Snackbar.LENGTH_SHORT).show();
            finish();
        }
    }

}


