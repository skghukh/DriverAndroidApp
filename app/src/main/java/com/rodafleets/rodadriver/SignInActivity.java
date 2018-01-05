package com.rodafleets.rodadriver;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rodafleets.rodadriver.model.Driver;
import com.rodafleets.rodadriver.rest.ResponseCode;
import com.rodafleets.rodadriver.services.FirebaseReferenceService;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;
import com.rodafleets.rodadriver.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "RD";

    TextView welcomeText;
    Button signInBtn;

    EditText phoneNumber;
    EditText password;

    TextView forgotPasswordText;
    TextView newDriver;

    ConstraintLayout constraintLayout;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initComponents();
    }

    private void initComponents() {
        welcomeText = (TextView) findViewById(R.id.welcomeText);
        signInBtn = (Button) findViewById(R.id.signInBtn);

        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        password = (EditText) findViewById(R.id.password);

        forgotPasswordText = (TextView) findViewById(R.id.forgotPasswordText);
        newDriver = (TextView) findViewById(R.id.newDriver);

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        progressBar = (ProgressBar) findViewById(R.id.loading);

        Typeface poppinsSemibold = Typeface.createFromAsset(getAssets(), "fonts/Poppins-SemiBold.ttf");
        Typeface poppinsRegular = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface sintonyBold = Typeface.createFromAsset(getAssets(), "fonts/Sintony-Bold.otf");

        welcomeText.setTypeface(poppinsSemibold);

        signInBtn.setTypeface(sintonyBold);

        phoneNumber.setTypeface(poppinsRegular);
        password.setTypeface(poppinsRegular);
        forgotPasswordText.setTypeface(poppinsRegular);

        newDriver.setTypeface(sintonyBold);
    }

    public void onSignUpClick(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
        finish();
    }

    public void onSignInBtnClick(View view) {
        signIn();
    }

    private void startNextActivity() {
        startActivity(new Intent(this, VehicleRequestActivity.class));
        finish();
    }

    private void signIn() {

        Boolean validated = true;
        String number = phoneNumber.getText().toString();
        String pwd = password.getText().toString();

        if (number.equals("")) {
            validated = false;
            phoneNumber.setError(getString(R.string.sign_in_phone_number_required_error));
        }

        if (pwd.equals("")) {
            validated = false;
            password.setError(getString(R.string.sign_in_password_required_error));
        }

        if (validated) {
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            Utils.enableWindowActivity(getWindow(), false);
            /*
            String token = ApplicationSettings.getRegistrationId(this);
            if (token.equals("")) {
                token = FirebaseInstanceId.getInstance().getToken();
                ApplicationSettings.setRegistrationId(this, token);
                FirebaseReferenceService.updateDriverToken(ApplicationSettings.getDriverEid(SignInActivity.this).split("\\@")[0],token);
            }*/
            signInUsingEmailAndPassword(number, pwd);


            //RodaRestClient.login(number, pwd, token, signInResponseHandler);
        }
    }

    private void signInUsingEmailAndPassword(String number, String pwd) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword((number.trim() + "@roda.com").trim(), pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "User login is successfull for " + user.getUid() + " : " + user.getDisplayName() + " : " + user.getPhoneNumber());
                            //updateUI(user);
                            // ApplicationSettings.setDriverId(SignInActivity.this,user.getUid());
                            ApplicationSettings.setDriverUId(SignInActivity.this, user.getUid());
                            ApplicationSettings.setDriverName(SignInActivity.this, user.getDisplayName());
                            ApplicationSettings.setDriverEid(SignInActivity.this, user.getEmail().split("\\@")[0]);
                            ApplicationSettings.setLoggedIn(SignInActivity.this, true);

                            String token = ApplicationSettings.getRegistrationId(SignInActivity.this);
                            if (!"".equalsIgnoreCase(token)) {
                                token = FirebaseInstanceId.getInstance().getToken();
                                ApplicationSettings.setRegistrationId(SignInActivity.this, token);
                                FirebaseReferenceService.updateDriverToken(ApplicationSettings.getDriverEid(SignInActivity.this).split("\\@")[0], token);
                            }
                            startNextActivity();
                        } else {
                            final String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            if (errorCode.equalsIgnoreCase("ERROR_WRONG_PASSWORD")) {
                                Snackbar.make(constraintLayout, " Incorrect Password or Id ", Snackbar.LENGTH_SHORT);
                            } else if (errorCode.equalsIgnoreCase("ERROR_USER_NOT_FOUND")) {
                                Snackbar.make(constraintLayout, "Invalid User, Please Sign UP", Snackbar.LENGTH_SHORT);
                            } else if (errorCode.equalsIgnoreCase("ERROR_USER_DISABLED")) {
                                Snackbar.make(constraintLayout, "Account Suspended - Contact Roda", Snackbar.LENGTH_SHORT);
                            } else {
                                Log.i(TAG, "Signin exception is " + errorCode);
                                Snackbar.make(constraintLayout, "OOPS! something went wrong", Snackbar.LENGTH_SHORT);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private JsonHttpResponseHandler signInResponseHandler = new JsonHttpResponseHandler() {

        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            try {
                Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
                JSONObject driverJson = jsonResponseObject.getJSONObject("driver");
                Driver driver = new Driver(driverJson);
                ApplicationSettings.setDriverId(SignInActivity.this, driver.getId());
                ApplicationSettings.setDriver(SignInActivity.this, driverJson);
                ApplicationSettings.setLoggedIn(SignInActivity.this, true);
                startNextActivity();
            } catch (JSONException e) {
                //handle error
                Log.e(AppConstants.APP_NAME, "jsonException = " + e.getMessage());
            }
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Snackbar sb;
            try {
                Log.i(AppConstants.APP_NAME, "errorResponse = " + errorResponse.toString());
                int errorCode = errorResponse.getInt("errorCode");
                Log.i(AppConstants.APP_NAME, "errorCode = " + errorCode);
                if (errorCode == ResponseCode.INVALID_CREDENTIALS) {
                    sb = Snackbar.make(constraintLayout, getString(R.string.sign_in_invalid_credentials_error), Snackbar.LENGTH_LONG);
                } else {
                    sb = Snackbar.make(constraintLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG);
                }
            } catch (Exception e) {
                Log.e(AppConstants.APP_NAME, "api exception = " + e.getMessage());
                if (e.getCause() instanceof ConnectTimeoutException) {
                    sb = Snackbar.make(constraintLayout, getString(R.string.internet_error), Snackbar.LENGTH_LONG);
                } else {
                    sb = Snackbar.make(constraintLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG);
                }
            }
            sb.show();
            progressBar.setVisibility(View.GONE);
            Utils.enableWindowActivity(getWindow(), true);
        }
    };


    public PhoneAuthProvider.OnVerificationStateChangedCallbacks getPhoneVerificationCallback() {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                //signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    System.out.println("Handle invalid sigin");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    System.out.println("Handle too many sign in");
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                String mVerificationId = verificationId;
                PhoneAuthProvider.ForceResendingToken codeResendToken = token;

                // ...
            }
        };
    }
}
