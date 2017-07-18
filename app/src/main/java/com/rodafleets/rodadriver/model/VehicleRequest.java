package com.rodafleets.rodadriver.model;

import org.json.JSONException;
import org.json.JSONObject;

public class VehicleRequest {

    private int id;
    private int customerId;
    private int vehicleTypeId;
    private double originLat;
    private double originLng;
    private double destinationLat;
    private double destinationLng;
    private int loadingRequired;
    private int unloadingRequired;
    private int approxFareInCents;
    private String originAddress;
    private String destinationAddress;
    private String distance;
    private String customerName;

    public VehicleRequest(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.originLat = jsonObject.getDouble("origin_lat");
        this.originLng = jsonObject.getDouble("origin_lng");
        this.destinationLat = jsonObject.getDouble("destination_lat");
        this.destinationLng = jsonObject.getDouble("destination_lng");
        this.approxFareInCents = jsonObject.getInt("approx_fare_in_cents");
        this.originAddress = jsonObject.getString("origin_address");
        this.destinationAddress = jsonObject.getString("destination_address");
        this.distance = jsonObject.getString("distance");
        this.customerName = jsonObject.getString("customer_name");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(int vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public double getPickupPointLat() {
        return originLat;
    }

    public void setPickupPointLat(double pickupPointLat) {
        this.originLat = pickupPointLat;
    }

    public double getPickupPointLng() {
        return originLng;
    }

    public void setPickupPointLng(double pickupPointLng) {
        this.originLng = pickupPointLng;
    }

    public double getDropPointLat() {
        return destinationLat;
    }

    public void setDropPointLat(double dropPointLat) {
        this.destinationLat = dropPointLat;
    }

    public double getDropPointLng() {
        return destinationLng;
    }

    public void setDropPointLng(double dropPointLng) {
        this.destinationLng = dropPointLng;
    }

    public int getLoadingRequired() {
        return loadingRequired;
    }

    public void setLoadingRequired(int loadingRequired) {
        this.loadingRequired = loadingRequired;
    }

    public int getUnloadingRequired() {
        return unloadingRequired;
    }

    public void setUnloadingRequired(int unloadingRequired) {
        this.unloadingRequired = unloadingRequired;
    }

    public int getApproxFareInCents() {
        return approxFareInCents;
    }

    public void setApproxFareInCents(int approxFareInCents) {
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
