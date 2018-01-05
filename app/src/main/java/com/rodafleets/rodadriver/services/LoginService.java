package com.rodafleets.rodadriver.services;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import java.util.concurrent.TimeUnit;

/**
 * Created by sverma4 on 12/23/17.
 */

public class LoginService {

    private static final String TAG = AppConstants.APP_NAME;
    private static String mVerificationId;
    private static PhoneAuthProvider.ForceResendingToken codeResendToken;



    public static void signUpByGoogleAuth(Activity x, String mPhoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks phoneVerificationCallback) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                x, phoneVerificationCallback);
    }


    public static PhoneAuthProvider.OnVerificationStateChangedCallbacks getPhoneVerificationCallback() {
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
                    Log.e(TAG, "Invalid Creadentials");
                } else if (e instanceof FirebaseTooManyRequestsException) {
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
                mVerificationId = verificationId;
                codeResendToken = token;

                // ...
            }
        };
    }

    public static String getmVerificationId() {
        return mVerificationId;
    }

    public static void setmVerificationId(String mVerificationId) {
        LoginService.mVerificationId = mVerificationId;
    }

    public static PhoneAuthProvider.ForceResendingToken getCodeResendToken() {
        return codeResendToken;
    }

    public static void setCodeResendToken(PhoneAuthProvider.ForceResendingToken codeResendToken) {
        LoginService.codeResendToken = codeResendToken;
    }

    public static PhoneAuthCredential verifyCode(String verificationid, String code ){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        System.out.println("credentials are "+credential);
        credential.getSmsCode();
        return credential;
    }


}
