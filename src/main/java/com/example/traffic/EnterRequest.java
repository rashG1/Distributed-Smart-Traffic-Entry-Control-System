package com.example.traffic;

public class EnterRequest {
    public final String vehicleId;
    public final String intersectionId;

    public EnterRequest(String vehicleId, String intersectionId, Object route) {
        this.vehicleId = vehicleId;
        this.intersectionId = intersectionId;
    }
}
