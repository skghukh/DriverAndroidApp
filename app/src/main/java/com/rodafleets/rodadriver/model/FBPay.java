package com.rodafleets.rodadriver.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sverma4 on 1/21/18.
 */

public class FBPay {
    String tripId;
    String custId;
    Object timestamp;
    long amount;

    public FBPay() {
        //default constructor
    }

    public FBPay(String custId, String tripId,long amount) {
        this.custId = custId;
        this.tripId = tripId;
        this.amount = amount;
        timestamp = ServerValue.TIMESTAMP;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    @Exclude
    public long getTimestampLong() {
        return (long) timestamp;
    }

    public java.util.Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }
}
