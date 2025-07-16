package com.example.traffic;

import java.util.List;

public class VehiclePath {
    public final String vehicleId;
    public final List<String> path;
    public final boolean entersCenter;
    public Object route;

    public VehiclePath(String vehicleId, List<String> path) {
        this.vehicleId = vehicleId;
        this.path = path;
        this.entersCenter = path.contains("Center"); // auto check if center involved
    }
}

