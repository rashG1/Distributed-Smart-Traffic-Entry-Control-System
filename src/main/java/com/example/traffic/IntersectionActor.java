package com.example.traffic;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;

public class IntersectionActor extends AbstractActor {


    private final ActorRef controlCenter;

    // Use LinkedList as a simple ordered list
    private final LinkedList<VehiclePath> waitingList = new LinkedList<>();
    private boolean isProcessing = false;

    public IntersectionActor(ActorRef controlCenter) {
        this.controlCenter = controlCenter;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()

                // RECEIVED VEHICLE PATH
                .match(VehiclePath.class, path -> {
                    waitingList.addLast(path); // Add vehicle to end of list
                    processNext();
                })

                // RESPONSE FROM CONTROL CENTER
                .match(EnterResponse.class, response -> {
                    if (response.status == EnterResponse.Status.ALLOW) {
                        System.out.println("Vehicle " + response.vehicleId + " allowed to enter city center.");

                        // Simulate vehicle in city center for 8 seconds
                        getContext().system().scheduler().scheduleOnce(
                                Duration.ofSeconds(8),
                                () -> {
                                    controlCenter.tell(new CityControlCenterActor.ExitNotice(response.vehicleId), getSelf());
                                    System.out.println("Vehicle " + response.vehicleId + " exited city center.");

                                    // Remove the front vehicle from list after exit
                                    if (!waitingList.isEmpty() && waitingList.getFirst().vehicleId.equals(response.vehicleId)) {
                                        waitingList.removeFirst();
                                    }

                                    isProcessing = false;
                                    processNext();
                                },
                                getContext().system().dispatcher()
                        );

                    } else if (response.status == EnterResponse.Status.WAIT) {
                        System.out.println("Vehicle " + response.vehicleId + " waiting due to congestion.");
                        isProcessing = false;
                        // Do not remove vehicle, keep waiting in list
                    }
                })
                .build();
    }

    private void processNext() {
        if (isProcessing || waitingList.isEmpty()) {
            return;
        }

        isProcessing = true;
        VehiclePath nextVehicle = waitingList.getFirst();

        if (nextVehicle.entersCenter) {
            System.out.println("Vehicle " + nextVehicle.vehicleId + " requests to enter city center.");
            controlCenter.tell(new EnterRequest(nextVehicle.vehicleId, getSelf().path().name(), nextVehicle.route), getSelf());
        } else {
            System.out.println("Vehicle " + nextVehicle.vehicleId + " is passing through, no center entry.");

            // Simulate immediate pass-through with delay of 3 seconds
            getContext().system().scheduler().scheduleOnce(
                    Duration.ofSeconds(3),
                    () -> {
                        System.out.println("Vehicle " + nextVehicle.vehicleId + " passed through.");

                        // Remove vehicle from front of list after passing
                        if (!waitingList.isEmpty() && waitingList.getFirst().vehicleId.equals(nextVehicle.vehicleId)) {
                            waitingList.removeFirst();
                        }

                        isProcessing = false;
                        processNext();
                    },
                    getContext().system().dispatcher()
            );
        }
    }
}
