package com.rodafleets.rodadriver;

import android.app.Application;
import android.graphics.Typeface;

import com.rodafleets.rodadriver.custom.FontsOverride;
import com.rodafleets.rodadriver.services.NotificationService;
import com.rodafleets.rodadriver.utils.AppConstants;

public class RodaDriverApplication extends Application {

    public static NotificationService vehicleRequestService;
}
