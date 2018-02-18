package com.rodafleets.rodadriver.services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rodafleets.rodadriver.MapActivity;
import com.rodafleets.rodadriver.R;
import com.rodafleets.rodadriver.SignInActivity;
import com.rodafleets.rodadriver.SplashScreenActivity;
import com.rodafleets.rodadriver.VehicleRequestActivity;
import com.rodafleets.rodadriver.model.DriverTrip;
import com.rodafleets.rodadriver.model.FBAccountDetails;
import com.rodafleets.rodadriver.model.FBDriver;
import com.rodafleets.rodadriver.model.FBPay;
import com.rodafleets.rodadriver.model.FBTrip;
import com.rodafleets.rodadriver.model.FBVehicle;
import com.rodafleets.rodadriver.model.FBVehicleRequest;
import com.rodafleets.rodadriver.utils.ApplicationSettings;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.support.v4.content.ContextCompat.startActivity;

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
        final DatabaseReference vehicleDetailsRef = getFBInstance().getReference(driverPath + "/" + driverId + "/" + "vehicleDetails");
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

    public static void removeCurrentTrip(String driverId) {
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

    public static DatabaseReference getDriverRef(String driverId) {
        return getFBInstance().getReference(driverPath + "/" + driverId);
    }

    public static DatabaseReference getAccountDetails(String driverId) {
        return FirebaseReferenceService.getFBInstance().getReference("accounts/" + driverId);
    }

    public static void saveAccountDetails(String driverId, FBAccountDetails account) {
        final DatabaseReference accountReference = getFBInstance().getReference("accounts" + "/" + driverId);
        final Task<Void> addAccount = accountReference.setValue(account);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                addAccount.getResult();
            }
        });
    }

    public static void onTripCompleted(final String driverId, final String custId, final String tripId) {
        final DatabaseReference tripReferece = getTripReferece(custId, tripId);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                tripReferece.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final FBVehicleRequest request = dataSnapshot.getValue(FBVehicleRequest.class);
                        FBTrip trip = new FBTrip(custId, tripId);
                        trip.setAmount(request.getAcceptedFare());
                        trip.setCustName(request.getCustomerName());
                        trip.setSource(request.getOriginAddress());
                        trip.setDest(request.getDestinationAddress());
                        trip.setDate(new Date().toString());
                        trip.setDistance(request.getDistanceKM() + " KM");
                        trip.setStatus("completed");
                        trip.setVehicleType((int) request.getVehicleTypeId());
                        final DatabaseReference tripReference = getFBInstance().getReference("trips_compl" + "/" + driverId + "/" + trip.getTripId());
                        tripReference.setValue(trip);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

    }

    public static void onTripCancelled(final String driverId, final String custId, final String tripId) {
        final DatabaseReference tripReferece = getTripReferece(custId, tripId);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                tripReferece.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final FBVehicleRequest request = dataSnapshot.getValue(FBVehicleRequest.class);
                        FBTrip trip = new FBTrip(custId, tripId);
                        trip.setAmount(request.getAcceptedFare());
                        trip.setCustName(request.getCustomerName());
                        trip.setSource(request.getOriginAddress());
                        trip.setDest(request.getDestinationAddress());
                        trip.setDate((new Date()).toString());
                        trip.setDistance(request.getDistanceKM());
                        trip.setStatus("cancelled");
                        trip.setVehicleType((int) request.getVehicleTypeId());
                        final DatabaseReference tripReference = getFBInstance().getReference("trips_cancel" + "/" + driverId + "/" + trip.getTripId());
                        final Task<Void> addTrip = tripReference.setValue(trip);
                        addTrip.getResult();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

    }

    public static void addDriverCompletedTrip(String driverId, FBTrip trip) {
        final DatabaseReference tripReference = getFBInstance().getReference("trips_compl" + "/" + driverId + "/" + trip.getTripId());
        final Task<Void> addTrip = tripReference.setValue(trip);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                addTrip.getResult();
            }
        });
    }

    public static void addDriverCancelledTrip(String driverId, FBTrip trip) {
        final DatabaseReference tripReference = getFBInstance().getReference("trips_cancel" + "/" + driverId + "/" + trip.getTripId());
        final Task<Void> addTrip = tripReference.setValue(trip);
        firebaseOperationThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                addTrip.getResult();
            }
        });
    }

    public static void markTripToCompleted(String driverId, String tripId) {
        final DatabaseReference tripStatusReference = getFBInstance().getReference("trips_compl" + "/" + driverId + "/" + tripId + "/status");
        tripStatusReference.setValue("completed");
    }


    public static DatabaseReference getDriverCompletedTripRef(String driverId) {
        return getFBInstance().getReference("trips_compl" + "/" + driverId);
    }

    public static DatabaseReference getDriverCancelledTripRef(String driverId) {
        return getFBInstance().getReference("trips_cancel" + "/" + driverId);
    }

    public static void goOffline(Context context, final String driverId) {
        final DatabaseReference driverRef = FirebaseReferenceService.getDriverRef(ApplicationSettings.getDriverEid(context));
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot && null != dataSnapshot.getValue()) {
                    final FBDriver fbDriver = dataSnapshot.getValue(FBDriver.class);
                    if (null != fbDriver && null == fbDriver.getCurrentTrip()) {
                        final DatabaseReference isOffline = getFBInstance().getReference(driverPath + "/" + driverId + "/offline");
                        final Task<Void> aTrue = isOffline.setValue("TRUE");
                        aTrue.addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                ApplicationSettings.getFbDriver().setOffline("TRUE");
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void goOnline(Context context, final String driverId) {
        final DatabaseReference driverRef = FirebaseReferenceService.getDriverRef(ApplicationSettings.getDriverEid(context));
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (null != dataSnapshot && null != dataSnapshot.getValue()) {
                    final DatabaseReference isOffline = getFBInstance().getReference(driverPath + "/" + driverId + "/offline");
                    final Task<Void> voidTask = isOffline.setValue(null);
                    voidTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            ApplicationSettings.getFbDriver().setOffline(null);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void addCashPaid(final String driverId, final String custId, final String tripId, final long amount) {
        final DatabaseReference payReference = getFBInstance().getReference("cash" + "/" + driverId).push();
        FBPay pay = new FBPay(custId, tripId, amount);
        payReference.setValue(pay).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("Added cash pay successfully " + custId + " : " + tripId + " : " + amount);
            }
        });
    }

    public static DatabaseReference getDriverLastSettlementTime(String driverId){
        return getFBInstance().getReference(driverPath + "/" + driverId + "/lastSettledDate");
    }


    public static void logout(Context context){
        ApplicationSettings.setLoggedIn(context,false);
        FirebaseAuth.getInstance().signOut();

    }



}
