package com.example.traffic;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("SmartCity");

        ActorRef controlCenter = system.actorOf(Props.create(CityControlCenterActor.class), "ControlCenter");

        ActorRef i1 = system.actorOf(Props.create(IntersectionActor.class, controlCenter), "I1");
        ActorRef i2 = system.actorOf(Props.create(IntersectionActor.class, controlCenter), "I2");
        ActorRef i3 = system.actorOf(Props.create(IntersectionActor.class, controlCenter), "I3");
        ActorRef i4 = system.actorOf(Props.create(IntersectionActor.class, controlCenter), "I4");

        // Simulate 15 vehicles
        List<ActorRef> intersections = List.of(i1, i2, i3, i4);

        for (int i = 0; i < 15; i++) {
            String id = "V" + i;
            List<String> route;
            if (i % 2 == 0) {
                // Half enter center and leave via I2 or I4
                route = List.of("I1", "Center", (i % 4 == 0) ? "I4" : "I2");
            } else {
                // Others just cross the city (through vehicles)
                route = List.of("I1", "I3");
            }

            ActorRef start = intersections.get(i % intersections.size());
            ActorRef vehicle = system.actorOf(Props.create(VehicleActor.class, id, route, start), id);
        }


    }
}
