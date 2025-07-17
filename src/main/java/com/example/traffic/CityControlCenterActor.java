package com.example.traffic;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import com.example.traffic.EnterResponse;

import java.util.*;

public class CityControlCenterActor extends AbstractActor {

    private final int MAX_CENTER_VEHICLES = 5;
    private final Set<String> vehiclesInCenter = new HashSet<>();
    private final Queue<String> waitingQueue = new LinkedList<>();
    private final Map<String, ActorRef> waitingSenders = new HashMap<>();

    // New map to track route occupancy
    private final Map<String, List<String>> routeVehicleMap = new HashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()

                // ENTRY REQUEST
                .match(EnterRequest.class, request -> {
                    if (vehiclesInCenter.size() < MAX_CENTER_VEHICLES) {
                        vehiclesInCenter.add(request.vehicleId);
                        sender().tell(new EnterResponse(request.vehicleId, EnterResponse.Status.ALLOW), getSelf());
                    } else {
                        System.out.println("Vehicle " + request.vehicleId + " must wait.");
                        waitingQueue.add(request.vehicleId);
                        waitingSenders.put(request.vehicleId, sender());
                        sender().tell(new EnterResponse(request.vehicleId, EnterResponse.Status.WAIT), getSelf());
                    }
                })

                // EXIT NOTIFICATION
                .match(ExitNotice.class, exit -> {
                    System.out.println("Vehicle " + exit.vehicleId + " exited city center.");
                    vehiclesInCenter.remove(exit.vehicleId);

                    removeVehicleFromAllRoutes(exit.vehicleId); // optional cleanup

                    if (!waitingQueue.isEmpty()) {
                        String nextVehicleId = waitingQueue.poll();
                        ActorRef nextSender = waitingSenders.remove(nextVehicleId);
                        vehiclesInCenter.add(nextVehicleId);

                        System.out.println("Now allowing waiting vehicle " + nextVehicleId);
                        nextSender.tell(new EnterResponse(nextVehicleId, EnterResponse.Status.ALLOW), getSelf());
                    }
                })

                // VEHICLE ROUTE UPDATE
                .match(RouteUpdate.class, update -> {
                    removeVehicleFromAllRoutes(update.vehicleId); // clean previous route
                    routeVehicleMap.putIfAbsent(update.routeId, new ArrayList<>());
                    routeVehicleMap.get(update.routeId).add(update.vehicleId);
                    logRouteVehicleMap();

                    System.out.println("Updated route: " + update.routeId + " -> " + routeVehicleMap.get(update.routeId));
                })

                .match(RouteLeave.class, leave -> {
                    if (routeVehicleMap.containsKey(leave.routeId)) {
                        ArrayList<String> vehicles = (ArrayList<String>) routeVehicleMap.get(leave.routeId);
                        vehicles.remove(leave.vehicleId); // Removes first occurrence if exists
                        if (vehicles.isEmpty()) {
                            routeVehicleMap.remove(leave.routeId); // cleanup
                        }
                        System.out.println("Vehicle " + leave.vehicleId + " left route " + leave.routeId);
                        printRouteStatus();
                    }
                })

                .build();
    }

    private void printRouteStatus() {
        System.out.println("===== Current Vehicles on Routes =====");
        for (Map.Entry<String, List<String>> entry : routeVehicleMap.entrySet()) {
            System.out.println("Route " + entry.getKey() + " => " + entry.getValue());
        }
        System.out.println("======================================");
    }


    private void removeVehicleFromAllRoutes(String vehicleId) {
        for (List<String> vehicles : routeVehicleMap.values()) {
            vehicles.remove(vehicleId);
            logRouteVehicleMap();
        }
    }
    private void logRouteVehicleMap() {
        System.out.println("===== Current Vehicles on Routes =====");
        for (Map.Entry<String, List<String>> entry : routeVehicleMap.entrySet()) {
            System.out.println("Route " + entry.getKey() + " => " + entry.getValue());
        }
        System.out.println("======================================");
    }

    // Message classes

    public static class ExitNotice {
        public final String vehicleId;
        public ExitNotice(String vehicleId) {
            this.vehicleId = vehicleId;
        }
    }

    public static class RouteUpdate {
        public final String vehicleId;
        public final String routeId;
        public RouteUpdate(String vehicleId, String routeId) {
            this.vehicleId = vehicleId;
            this.routeId = routeId;
        }
    }

    public static class EnterRequest {
        public final String vehicleId;
        public final String intersectionId;
        public final String routeId;
        public final boolean entersCenter;

        public EnterRequest(String vehicleId, String intersectionId, String routeId, boolean entersCenter) {
            this.vehicleId = vehicleId;
            this.intersectionId = intersectionId;
            this.routeId = routeId;
            this.entersCenter = entersCenter;
        }
    }

    public static class EnterResponse {
        public enum Status { ALLOW, WAIT, REROUTE }
        public final String vehicleId;
        public final Status status;

        public EnterResponse(String vehicleId, Status status) {
            this.vehicleId = vehicleId;
            this.status = status;
        }
    }
    public static class RouteLeave {
        public final String vehicleId;
        public final String routeId;

        public RouteLeave(String vehicleId, String routeId) {
            this.vehicleId = vehicleId;
            this.routeId = routeId;
        }
    }

}
