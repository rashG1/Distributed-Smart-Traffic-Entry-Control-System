package com.example.traffic;

import java.util.Optional;

public class EnterResponse {
    public enum Status { ALLOW, WAIT, REROUTE }

    public final String vehicleId;
    public final Status status;
    public final Optional<String> rerouteTo; // Optional reroute info

    public EnterResponse(String vehicleId, Status status) {
        this(vehicleId, status, Optional.empty());
    }

    public EnterResponse(String vehicleId, Status status, Optional<String> rerouteTo) {
        this.vehicleId = vehicleId;
        this.status = status;
        this.rerouteTo = rerouteTo;
    }
}

