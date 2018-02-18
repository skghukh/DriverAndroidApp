package com.rodafleets.rodadriver.model;

/**
 * Created by sverma4 on 1/1/18.
 */

public class FBVehicle {
    private String vehicleNumber;
    private int VehicleType;
    private String manufacturer;
    private String model;
    private String ownerFirstName;
    private String ownerLastName;
    private String ownerPhoneNumber;

    public FBVehicle() {
        //Default Constructor
    }

    public FBVehicle(String vehicleNumber, int vehicleType, String manufacturer, String model, String ownerFirstName, String ownerLastName, String ownerPhoneNumber) {
        this.vehicleNumber = vehicleNumber;
        VehicleType = vehicleType;
        this.manufacturer = manufacturer;
        this.model = model;
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.ownerPhoneNumber = ownerPhoneNumber;
    }

    public String getVehicleNumber() {
        return null == vehicleNumber ? "NA" : vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public int getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(int vehicleType) {
        VehicleType = vehicleType;
    }

    public String getManufacturer() {
        return null == manufacturer ? "NA" : manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model == null ? "NA" : model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOwnerFirstName() {
        return ownerFirstName == null ? "NA" : ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName == null ? "NA" : ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerPhoneNumber() {
        return null == ownerPhoneNumber ? "NA" : ownerPhoneNumber;
    }

    public void setOwnerPhoneNumber(String ownerPhoneNumber) {
        this.ownerPhoneNumber = ownerPhoneNumber;
    }
}
