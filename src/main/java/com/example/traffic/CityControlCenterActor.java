package com.example.traffic;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.*;

public class CityControlCenterActor extends AbstractActor {

    private final int MAX_CENTER_VEHICLES = 5;
    private final Set<String> vehiclesInCenter = new HashSet<>();
    private final Queue<String> waitingQueue = new LinkedList<>();
    private final Map<String, ActorRef> waitingSenders = new HashMap<>();

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

                    // Serve next in queue if any
                    if (!waitingQueue.isEmpty()) {
                        String nextVehicleId = waitingQueue.poll();
                        ActorRef nextSender = waitingSenders.remove(nextVehicleId);
                        vehiclesInCenter.add(nextVehicleId);

                        System.out.println("Now allowing waiting vehicle " + nextVehicleId);
                        nextSender.tell(new EnterResponse(nextVehicleId, EnterResponse.Status.ALLOW), getSelf());
                    }
                })

                .build();
    }

    public static class ExitNotice {
        public final String vehicleId;

        public ExitNotice(String vehicleId) {
            this.vehicleId = vehicleId;
        }
    }
}
