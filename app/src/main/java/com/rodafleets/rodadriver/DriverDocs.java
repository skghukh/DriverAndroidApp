package com.rodafleets.rodadriver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rodafleets.rodadriver.rest.ResponseCode;
import com.rodafleets.rodadriver.rest.RodaRestClient;
import com.rodafleets.rodadriver.services.DocumentService;
import com.rodafleets.rodadriver.utils.AppConstants;
import com.rodafleets.rodadriver.utils.ApplicationSettings;
import com.rodafleets.rodadriver.utils.ImageUtils;
import com.rodafleets.rodadriver.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

public class DriverDocs extends AppCompatActivity implements ImageUtils.ImageAttachmentListener {

    private TextView driverDocsText;

    private EditText document1ToUpload;
    private EditText document2ToUpload;
    private EditText document3ToUpload;

    private ImageView document1ImageView;
    private ImageView document2ImageView;
    private ImageView document3ImageView;

    private TextInputLayout document1Layout;
    private TextInputLayout document2Layout;
    private TextInputLayout document3Layout;

    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;

    TextView existingDriver;

    private ImageView imageViewToUpdate;

    private ImageUtils imageUtils;

    File[] filesToUpload;
    int position = 0;

    Uri[] uris;
    AtomicInteger uploadCount = new AtomicInteger(0);
    private String tempFileName = "";
    private String driverId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
//        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_driver_docs);
        initComponents();
        getDriverIdFromIntent(getIntent());
    }

    private void getDriverIdFromIntent(Intent intent) {
        if (null == intent) {
            finish();
        } else {
            driverId = intent.getStringExtra("driverNumber");
        }
    }

    private void initComponents() {

        driverDocsText = findViewById(R.id.driverDocsText);

        document1ToUpload = findViewById(R.id.document1ToUpload);
        document2ToUpload = findViewById(R.id.document2ToUpload);
        document3ToUpload = findViewById(R.id.document3ToUpload);

        document1Layout = findViewById(R.id.document1Layout);
        document2Layout = findViewById(R.id.document2Layout);
        document3Layout = findViewById(R.id.document3Layout);

        constraintLayout = findViewById(R.id.constraintLayout);
        progressBar = findViewById(R.id.loading);

        existingDriver = findViewById(R.id.existingDriver);

        Typeface poppinsRegular = Typeface.createFromAsset(getAssets(), AppConstants.FONT_POPPINS_REGULAR);
        Typeface sintonyBold = Typeface.createFromAsset(getAssets(), AppConstants.FONT_SINTONY_BOLD);

        document1ToUpload.setTypeface(poppinsRegular);
        document2ToUpload.setTypeface(poppinsRegular);
        document3ToUpload.setTypeface(poppinsRegular);

        driverDocsText.setTypeface(sintonyBold);

        imageUtils = new ImageUtils(this);

        document1ImageView = findViewById(R.id.document1ImageView);
        document2ImageView = findViewById(R.id.document2ImageView);
        document3ImageView = findViewById(R.id.document3ImageView);

        document1ToUpload.setOnTouchListener(uploadImageListener1);
        document2ToUpload.setOnTouchListener(uploadImageListener2);
        document3ToUpload.setOnTouchListener(uploadImageListener3);

        filesToUpload = new File[3];
        uris = new Uri[3];
    }

    public void onUploadDriverDocs(View view) {
        upload();
    }

    private void startNextActivity() {
        this.startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    View.OnTouchListener uploadImageListener1 = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            tempFileName = document1ToUpload.getText().toString();
            //check if ref name for the document to be uploaded is entered, if not don't allow them to proceed.
            if (tempFileName.equals("")) {
                document1Layout.setError(getString(R.string.sign_up_document_name_required_error));
            } else {
                document1Layout.setErrorEnabled(false);
                imageViewToUpdate = document1ImageView;
                position = 0;
                return callImagePicker(document1ToUpload, event);
            }
            return false;
        }
    };

    View.OnTouchListener uploadImageListener2 = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            tempFileName = document2ToUpload.getText().toString();
            //check if ref name for the document to be uploaded is entered, if not don't allow them to proceed.
            if (tempFileName.equals("")) {
                document2Layout.setError(getString(R.string.sign_up_document_name_required_error));
            } else {
                document2Layout.setErrorEnabled(false);
                imageViewToUpdate = document2ImageView;
                position = 1;
                return callImagePicker(document2ToUpload, event);
            }
            return false;
        }
    };
    View.OnTouchListener uploadImageListener3 = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            tempFileName = document3ToUpload.getText().toString();
            //check if ref name for the document to be uploaded is entered, if not don't allow them to proceed.
            if (tempFileName.equals("")) {
                document3Layout.setError(getString(R.string.sign_up_document_name_required_error));
            } else {
                document3Layout.setErrorEnabled(false);
                imageViewToUpdate = document3ImageView;
                position = 2;
                return callImagePicker(document3ToUpload, event);
            }
            return false;
        }
    };

    private Boolean callImagePicker(EditText v, MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (v.getRight() - v.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                //we are not handling camera in this case. so call launchGallery instead of picker.
                //imageUtils.imagepicker(1);
                imageUtils.launchGallery(1);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUtils.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        imageUtils.request_permission_result(requestCode, permissions, grantResults);
    }

    @Override
    public void image_attachment(int from, String filename, Bitmap bitmap, Uri uri) {
        tempFileName = tempFileName.trim() + filename.substring(filename.lastIndexOf("."));
        String path = Environment.getExternalStorageDirectory() + File.separator + AppConstants.IMAGES_DIRECTORY_NAME + File.separator;
        File imageFile = imageUtils.createImage(bitmap, tempFileName, path);

        filesToUpload[position] = imageFile;
        uris[position] = uri;
        imageViewToUpdate.setImageBitmap(bitmap);

        Log.i(AppConstants.APP_NAME, "file name from File object = " + imageFile.getName());
    }

    private void upload() {
        if (imageViewToUpdate == null) {
            Snackbar sb = Snackbar.make(constraintLayout, getString(R.string.sign_up_document_required_error), Snackbar.LENGTH_LONG);
            sb.show();
        } else {
            Utils.enableWindowActivity(getWindow(), false);
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
            try {
                for (int i = 0; i < 3; i++) {
                    final Uri docUri = this.uris[i];
                    if (null != docUri) {
                        uploadCount.getAndIncrement();
                        final StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = DocumentService.uploadDriverLicense(docUri, driverId, i);
                        taskSnapshotStorageTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                final int remaining = uploadCount.decrementAndGet();
                                if (remaining <= 0) {
                                    progressBar.setVisibility(View.GONE);
                                    startNextActivity();
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(constraintLayout, "Something went wrong", Snackbar.LENGTH_LONG).show();
                Log.i(AppConstants.APP_NAME, "Doc upload exception = " + e.getMessage());
                Utils.enableWindowActivity(getWindow(), true);
            }
        }
        uploadCount = new AtomicInteger(0);
    }

    private JsonHttpResponseHandler saveDriverDocsResponseHandler = new JsonHttpResponseHandler() {
        public void onSuccess(int statusCode, Header[] headers, JSONObject jsonResponseObject) {
            Log.i(AppConstants.APP_NAME, "response = " + jsonResponseObject.toString());
            progressBar.setVisibility(View.GONE);
            Snackbar sb = Snackbar.make(constraintLayout, getString(R.string.sign_up_successful_message), Snackbar.LENGTH_LONG);
            sb.setCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    //see Snackbar.Callback docs for event details
                    startNextActivity();
                }

                @Override
                public void onShown(Snackbar snackbar) {
                }
            });

            sb.show();
        }

        public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Snackbar sb;
            try {
                Log.i(AppConstants.APP_NAME, "errorResponse = " + errorResponse.toString());
                int errorCode = errorResponse.getInt("errorCode");
                Log.i(AppConstants.APP_NAME, "errorCode = " + errorCode);

                if (errorCode == ResponseCode.DUPLICATE_ENTRY) {
                    sb = Snackbar.make(constraintLayout, getString(R.string.sign_up_vehicle_number_already_added_error), Snackbar.LENGTH_LONG);
                } else {
                    sb = Snackbar.make(constraintLayout, getString(R.string.sign_up_default_error), Snackbar.LENGTH_LONG);
                }
                sb.show();
            } catch (Exception e) {
                Log.e(AppConstants.APP_NAME, "api exception = " + e.getMessage());
                if (e.getCause() instanceof ConnectTimeoutException) {
                    sb = Snackbar.make(constraintLayout, getString(R.string.internet_error), Snackbar.LENGTH_LONG);
                } else {
                    sb = Snackbar.make(constraintLayout, getString(R.string.sign_up_default_error), Snackbar.LENGTH_LONG);
                }
                sb.show();
            }
            progressBar.setVisibility(View.GONE);
        }
    };
}