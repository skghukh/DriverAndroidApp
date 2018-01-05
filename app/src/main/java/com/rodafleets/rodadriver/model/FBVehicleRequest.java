package com.rodafleets.rodadriver.model;

import java.util.Date;

/**
 * Created by sverma4 on 1/1/18.
 */

public class FBVehicleRequest {

    private long id;
    private long customerId;
    private long vehicleTypeId;
    private double originLat;
    private double originLng;
    private double destinationLat;
    private double destinationLng;
    private long loadingRequired;
    private long unloadingRequired;
    private long approxFareInCents;
    private String originAddress;
    private String destinationAddress;
    private String acceptedFare;
   // private String carrierId;
    private String customerName;
    private long timestamp;
    private String recName;
    private String recNum;
    private String status;

    public FBVehicleRequest() {
        //default Constructor
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(long vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(double originLat) {
        this.originLat = originLat;
    }

    public double getOriginLng() {
        return originLng;
    }

    public void setOriginLng(double originLng) {
        this.originLng = originLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public void setDestinationLat(double destinationLat) {
        this.destinationLat = destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public void setDestinationLng(double destinationLng) {
        this.destinationLng = destinationLng;
    }

    public long getLoadingRequired() {
        return loadingRequired;
    }

    public void setLoadingRequired(long loadingRequired) {
        this.loadingRequired = loadingRequired;
    }

    public long getUnloadingRequired() {
        return unloadingRequired;
    }

    public void setUnloadingRequired(long unloadingRequired) {
        this.unloadingRequired = unloadingRequired;
    }

    public long getApproxFareInCents() {
        return approxFareInCents;
    }

    public void setApproxFareInCents(long approxFareInCents) {
        this.approxFareInCents = approxFareInCents;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getAcceptedFare() {
        return acceptedFare;
    }

    public void setAcceptedFare(String acceptedFare) {
        this.acceptedFare = acceptedFare;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecName() {
        return recName;
    }

    public void setRecName(String recName) {
        this.recName = recName;
    }

    public String getRecNum() {
        return recNum;
    }

    public void setRecNum(String recNum) {
        this.recNum = recNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
