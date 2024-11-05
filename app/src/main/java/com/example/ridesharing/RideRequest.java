package com.example.ridesharing;

public class RideRequest {
    private String rideId;
    private String userId; // User who posted the ride
    private String phone; // Phone number of the requesting user
    private String status; // "pending", "accepted", "denied"
    private long timestamp; // Timestamp for when the request was created

    // Default constructor for Firebase
    public RideRequest() {}

    public RideRequest(String rideId, String userId, String phone, String status, long timestamp) {
        this.rideId = rideId;
        this.userId = userId;
        this.phone = phone;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters and Setters
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
