package com.rodafleets.rodadriver.model;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by sverma4 on 20/12/17.
 */

public class TripRequest {
    DatabaseReference tripRef;
    String custId;
    String tripId;
    DatabaseReference statusRef;

    public TripRequest(String custId, String tripId, DatabaseReference ref) {
        this.custId = custId;
        this.tripId = tripId;
        this.tripRef = ref;
    }

    public DatabaseReference getTripRef() {
        return tripRef;
    }

    public void setTripRef(DatabaseReference tripRef) {
        this.tripRef = tripRef;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }


    public DatabaseReference getStatusRef() {
        return statusRef;
    }

    public void setStatusRef(DatabaseReference statusRef) {
        this.statusRef = statusRef;
    }
}
