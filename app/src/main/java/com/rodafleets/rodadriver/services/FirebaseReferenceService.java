package com.rodafleets.rodadriver.services;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rodafleets.rodadriver.R;
import com.rodafleets.rodadriver.model.DriverTrip;
import com.rodafleets.rodadriver.model.FBDriver;
import com.rodafleets.rodadriver.model.FBVehicle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sverma4 on 12/24/17.
 */

public class FirebaseReferenceService {

    final static String driverPath = "drivers/";
    private static FirebaseDatabase fbInstance;

    private static ExecutorService firebaseOperationThreadPool = Executors.newFixedThreadPool(3);


    public static FirebaseDatabase getFBInstance() {
        if (fbInstance == null) {
            fbInstance = FirebaseDatabase.getInstance();
            fbInstance.setPersistenceEnabled(true);

        }
        return fbInstance;
    }

    public static void updateDriverToken(String driverId, String token) {
        DatabaseReference driverTokenReference = getFBInstance().getReference(driverPath + "/" + driverId + "/atoken");
        final Task<Void> updateToken = driverTokenReference.setValue(token);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                updateToken.getResult();
            }
        });
    }

    public static void addDriver(String driverId, FBDriver driver) {
        final DatabaseReference driverReference = getFBInstance().getReference(driverPath + "/" + driverId);
        final Task<Void> addDriverTask = driverReference.setValue(driver);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                addDriverTask.getResult();
            }
        });
    }

    public static void addVehicleDetails(String driverId, FBVehicle vehicle) {
        final DatabaseReference vehicleDetailsRef = getFBInstance().getReference(driverPath + "/" + driverId + "/"+"vehicleDetails");
        final Task<Void> addVehicle = vehicleDetailsRef.setValue(vehicle);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                addVehicle.getResult();
            }
        });
    }

    public static DatabaseReference getCurrentTripDetails(String driverId) {
        return getFBInstance().getReference(driverPath + "/" + driverId + "/currentTrip");
    }

    public static void removeCurrentTrip(String driverId){
        final Task<Void> removeCurrentTrip = getFBInstance().getReference(driverPath + "/" + driverId + "/currentTrip").removeValue();
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                removeCurrentTrip.getResult();
            }
        });
    }

    public static DatabaseReference getTripReferece(String custId, String tripId) {
        return FirebaseReferenceService.getFBInstance().getReference("vehicleRequests/" + custId + "/" + tripId);
    }

    public static void addDriversCurrentTrip(String driverId, String tripId, String custId) {
        final DatabaseReference driverReference = getFBInstance().getReference(driverPath + "/" + driverId + "/currentTrip");
        final Task<Void> currentTripRef = driverReference.setValue(new DriverTrip(custId, tripId));
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                currentTripRef.getResult();
            }
        });
    }

    public static DatabaseReference getDriverRef(String driverId){
        return getFBInstance().getReference(driverPath + "/" + driverId);
    }

}
