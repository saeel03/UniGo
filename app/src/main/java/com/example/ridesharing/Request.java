package com.example.ridesharing;


public class Request {
    private String clientID;
    private String clientName;
    private String clientPhone;
    private String driverID;
    private String rideID;
    private String description;
    private String status;

    // No-argument constructor
    public Request() {}

    // Parameterized constructor
    public Request(String clientID, String clientName, String clientPhone, String driverID,
                   String rideID, String description, String status) {
        this.clientID = clientID;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.driverID = driverID;
        this.rideID = rideID;
        this.description = description;
        this.status = status;
    }

    // Getters
    public String getClientID() { return clientID; }
    public String getClientName() { return clientName; }
    public String getClientPhone() { return clientPhone; }
    public String getDriverID() { return driverID; }
    public String getRideID() { return rideID; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }

    public void setStatus(String newStatus) {
        this.status=newStatus;
    }
}