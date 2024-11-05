package com.example.ridesharing;
public class Ride {
    private String rideId;
    private String userId;
    private String phone;
    private String vehicleType;
    private String seats;
    private String time;
    private String date;
    private String vehicleNumber;
    private String startingLocation;
    private String endingLocation;
    private String cost; // Assuming you added a cost field

    // No-argument constructor required by Firebase
    public Ride() {}

    // Full constructor
    public Ride(String rideId, String userId, String phone, String vehicleType, String seats,
                String time, String date, String vehicleNumber, String startingLocation,
                String endingLocation, String cost) {
        this.rideId = rideId;
        this.userId = userId;
        this.phone = phone;
        this.vehicleType = vehicleType;
        this.seats = seats;
        this.time = time;
        this.date = date;
        this.vehicleNumber = vehicleNumber;
        this.startingLocation = startingLocation;
        this.endingLocation = endingLocation;
        this.cost = cost;
    }

    // Getters and Setters for all fields

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(String startingLocation) {
        this.startingLocation = startingLocation;
    }

    public String getEndingLocation() {
        return endingLocation;
    }

    public void setEndingLocation(String endingLocation) {
        this.endingLocation = endingLocation;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
