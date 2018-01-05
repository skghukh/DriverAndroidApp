package com.rodafleets.rodadriver.model;

/**
 * Created by sverma4 on 1/1/18.
 */

public class FBDriver {
    private long rating;
    private DriverTrip currentTrip;
    private boolean isVerified;
    private FBVehicle vehicleDetails;
    private String aToken;
    private String gender;

    public FBDriver(){
        //default Constructor
    }

    public FBDriver(String gender) {
        this.rating = 5;
        this.isVerified = false;
        this.gender = gender;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public DriverTrip getCurrentTrip() {
        return currentTrip;
    }

    public void setCurrentTrip(DriverTrip currentTrip) {
        this.currentTrip = currentTrip;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public FBVehicle getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(FBVehicle vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public String getaToken() {
        return aToken;
    }

    public void setaToken(String aToken) {
        this.aToken = aToken;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
