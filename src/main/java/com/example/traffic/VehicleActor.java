package com.example.traffic;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.List;

public class VehicleActor extends AbstractActor {
    private final List<String> path;
    private final ActorRef firstIntersection;

    public VehicleActor(String vehicleId, List<String> path, ActorRef firstIntersection) {
        this.path = path;
        this.firstIntersection = firstIntersection;
    }

    @Override
    public void preStart() {
        firstIntersection.tell(new VehiclePath(getSelf().path().name(), path), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().build(); // optional future behavior
    }
}
