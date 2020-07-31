package com.athishworks.ccc.pojomodels;

public class HospitalDetails {

    String name, address, keyId;
    int totalBeds, availableBeds;
    Double latitude, longitude;

    public HospitalDetails() {
        // Required
    }

    public HospitalDetails(String name, String address, int totalBeds, int availableBeds, Double latitude, Double longitude) {
        this.name = name;
        this.address = address;
        this.totalBeds = totalBeds;
        this.availableBeds = availableBeds;
        this.latitude = latitude;
        this.longitude = longitude;
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
}
