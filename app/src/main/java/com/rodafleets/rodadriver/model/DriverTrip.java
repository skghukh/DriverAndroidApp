package com.rodafleets.rodadriver.model;

/**
 * Created by sverma4 on 1/1/18.
 */

public class DriverTrip {
    private String custId;
    private String tripid;
    private String paidBy;
    private String fare;
    private String status;

    public DriverTrip() {
        //default constructor
    }

    public DriverTrip(String custId, String tripId) {
        this.custId = custId;
        this.tripid = tripId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getTripid() {
        return tripid;
    }

    public void setTripid(String tripid) {
        this.tripid = tripid;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
