package com.example.traffic;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

public class IntersectionActor extends AbstractActor {

    private final String intersectionId;
    private final ActorRef controlCenter;
    private final LinkedList<VehiclePath> waitingList = new LinkedList<>();
    private boolean isProcessing = false;

    public IntersectionActor(String intersectionId, ActorRef controlCenter) {
        this.intersectionId = intersectionId;
        this.controlCenter = controlCenter;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(VehiclePath.class, path -> {
                    waitingList.addLast(path);
                    processNext();
                })

                .match(EnterResponse.class, response -> {
                    if (response.status == EnterResponse.Status.ALLOW) {
                        System.out.println(" Vehicle " + response.vehicleId + " allowed to enter center.");

                        getContext().system().scheduler().scheduleOnce(
                                Duration.ofSeconds(8),
                                () -> {
                                    controlCenter.tell(new CityControlCenterActor.ExitNotice(response.vehicleId), getSelf());
                                    System.out.println(" Vehicle " + response.vehicleId + " exited city center.");
                                    removeVehicle(response.vehicleId);
                                    isProcessing = false;
                                    processNext();
                                },
                                getContext().system().dispatcher()
                        );
                    } else if (response.status == EnterResponse.Status.WAIT) {
                        System.out.println("Vehicle " + response.vehicleId + " waiting due to congestion.");
                        isProcessing = false;
                    }
                })
                .build();
    }

    private void processNext() {
        if (isProcessing || waitingList.isEmpty()) return;

        isProcessing = true;
        VehiclePath nextVehicle = waitingList.getFirst();
        String vehicleId = nextVehicle.vehicleId;

        if (nextVehicle.path.size() < 2) {
            System.out.println(" Vehicle " + vehicleId + " completed path.");
            waitingList.removeFirst();
            isProcessing = false;
            processNext();
            return;
        }

        String from = nextVehicle.path.get(0);
        String to = nextVehicle.path.get(1);
        String routeKey = from + "-" + to;

        System.out.println("Vehicle " + vehicleId + " moving from " + from + " to " + to);
        controlCenter.tell(new CityControlCenterActor.RouteUpdate(vehicleId, routeKey), getSelf());

        // If going to center, ask permission
        if (to.equals("Center")) {
            controlCenter.tell(new CityControlCenterActor.EnterRequest(vehicleId, intersectionId, routeKey, true), getSelf());
        }

        getContext().system().scheduler().scheduleOnce(
                Duration.ofSeconds(4),
                () -> {
                    // Notify CityControlCenter that vehicle left this segment
                    String routeKey1 = intersectionId + "-" + nextVehicle.path.get(0); // current to next
                    controlCenter.tell(
                            new CityControlCenterActor.RouteLeave(nextVehicle.vehicleId, routeKey1),
                            getSelf()
                    );

                    nextVehicle.path.remove(0);  // move to next segment
                    isProcessing = false;
                    processNext();
                },
                getContext().system().dispatcher()
        );

    }

    private void removeVehicle(String vehicleId) {
        if (!waitingList.isEmpty() && waitingList.getFirst().vehicleId.equals(vehicleId)) {
            waitingList.removeFirst();
        }
    }
}
