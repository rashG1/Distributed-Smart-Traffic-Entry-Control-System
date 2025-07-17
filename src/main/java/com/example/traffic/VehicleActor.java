package com.example.traffic;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.ArrayList;

import java.util.Map;

public class VehicleActor extends AbstractActor {

    private final String vehicleId;
    private final ArrayList<String> path;
    private final Map<String, ActorRef> intersectionMap;
    private final ActorRef startIntersection;

    public VehicleActor(String vehicleId, ArrayList<String> path, Map<String, ActorRef> intersectionMap, ActorRef startIntersection) {
        this.vehicleId = vehicleId;
        this.path = path;
        this.intersectionMap = intersectionMap;
        this.startIntersection = startIntersection;
    }

    @Override
    public void preStart() {
        startIntersection.tell(new VehiclePath(vehicleId, path), getSelf());

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().build();
    }
}
