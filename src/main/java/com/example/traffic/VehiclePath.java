package com.example.traffic;

import java.util.ArrayList;
import java.util.ArrayList;

public class VehiclePath {
    public final String vehicleId;
    public final ArrayList<String> path;
    public final boolean entersCenter;
    public Object route;

    public VehiclePath(String vehicleId, ArrayList<String> path) {
        this.vehicleId = vehicleId;
        this.path = path;
        this.entersCenter = path.contains("Center"); // auto check if center involved
    }
}

