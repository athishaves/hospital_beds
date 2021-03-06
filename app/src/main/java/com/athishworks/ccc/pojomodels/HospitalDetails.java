package com.athishworks.ccc.pojomodels;

import java.sql.Timestamp;

public class HospitalDetails {

    String name, address, keyId, phoneNo, updatedBy, updatedTime;
    int totalBeds, availableBeds;
    Double latitude, longitude;


    public HospitalDetails() {
        // Required
        this.name = "";
        this.address = "";
        this.phoneNo = "";
        this.totalBeds = 0;
        this.availableBeds = 0;
        this.latitude = 0.0;
        this.longitude = 0.0;
    }


    public HospitalDetails(String name, String address,
                           int totalBeds, int availableBeds,
                           Double latitude, Double longitude,
                           String phoneNo) {
        this.name = name;
        this.address = address;
        this.totalBeds = totalBeds;
        this.availableBeds = availableBeds;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNo = phoneNo;
    }


    public HospitalDetails(String name, String address,
                           int totalBeds, int availableBeds,
                           Double latitude, Double longitude,
                           String phoneNo, String updatedBy, String updatedTime) {
        this.name = name;
        this.address = address;
        this.totalBeds = totalBeds;
        this.availableBeds = availableBeds;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNo = phoneNo;
        this.updatedBy = updatedBy;
        this.updatedTime = updatedTime;
    }


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getTotalBeds() {
        return totalBeds;
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTotalBeds(int totalBeds) {
        this.totalBeds = totalBeds;
    }

    public void setAvailableBeds(int availableBeds) {
        this.availableBeds = availableBeds;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }
}
