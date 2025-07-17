package com.example.traffic;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("SmartCity");

        ActorRef controlCenter = system.actorOf(Props.create(CityControlCenterActor.class), "ControlCenter");

        // Create intersection actors
        ActorRef i1 = system.actorOf(Props.create(IntersectionActor.class, "I1", controlCenter), "I1");
        ActorRef i2 = system.actorOf(Props.create(IntersectionActor.class, "I2", controlCenter), "I2");
        ActorRef i3 = system.actorOf(Props.create(IntersectionActor.class, "I3", controlCenter), "I3");
        ActorRef i4 = system.actorOf(Props.create(IntersectionActor.class, "I4", controlCenter), "I4");
        ActorRef center = system.actorOf(Props.create(IntersectionActor.class, "Center", controlCenter), "Center");

        // Map string names to actor references
        Map<String, ActorRef> intersectionMap = new HashMap<>();
        intersectionMap.put("I1", i1);
        intersectionMap.put("I2", i2);
        intersectionMap.put("I3", i3);
        intersectionMap.put("I4", i4);
        intersectionMap.put("Center", center);

        // Simulate 15 vehicles with varying routes
        for (int i = 0; i < 15; i++) {
            String vehicleId = "V" + i;
            ArrayList<String> route;

            if (i % 2 == 0) {
                // Half enter center and leave via I2 or I4
                route = new ArrayList<>(List.of("I1", "Center", (i % 4 == 0) ? "I4" : "I2"));

            } else {
                // Others just cross the city (through vehicles)
                route = new ArrayList<>(List.of("I1", "I3"));

            }

            // Get start intersection actor
            String startIntersection = route.get(0);
            ActorRef startActor = intersectionMap.get(startIntersection);

            // Create vehicle actor
            ActorRef vehicle = system.actorOf(
                    Props.create(VehicleActor.class, vehicleId, route, intersectionMap, startActor),
                    vehicleId
            );
        }
    }


}
