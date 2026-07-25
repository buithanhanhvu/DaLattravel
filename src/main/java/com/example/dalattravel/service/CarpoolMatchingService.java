package com.example.DaLattravel.service;

import com.example.DaLattravel.dto.*;
import com.example.DaLattravel.model.Passenger;
import com.example.DaLattravel.model.PassengerGroup;
import com.example.DaLattravel.model.Vehicle;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarpoolMatchingService {

    private final KMeansClusteringService kmeansService;
    private final MinCostMaxFlowService flowService;
    private final PDPTWService pdptwService;

    public CarpoolMatchingService(KMeansClusteringService kmeansService, MinCostMaxFlowService flowService, PDPTWService pdptwService) {
        this.kmeansService = kmeansService;
        this.flowService = flowService;
        this.pdptwService = pdptwService;
    }

    public List<CarpoolMatchResult> matchPassengersToVehicles(
            List<Vehicle> vehicles,
            List<Passenger> passengers,
            List<PassengerGroup> groups) {

        List<CarpoolMatchResult> results = new ArrayList<>();

        if (vehicles.isEmpty() || passengers.isEmpty()) {
            return results;
        }

        int k = Math.min(5, Math.max(1, passengers.size() / 3));
        kmeansService.clusterPassengers(passengers, k);

        MinCostMaxFlowService.FlowResult flowResult = flowService.solve(vehicles, passengers, groups);

        for (Map.Entry<Integer, List<Integer>> assignment : flowResult.getAssignments().entrySet()) {
            int vehicleId = assignment.getKey();
            Vehicle vehicle = vehicles.stream().filter(v -> v.getId() == vehicleId).findFirst().orElse(null);
            if (vehicle == null) continue;

            List<Passenger> rawVehiclePassengers = passengers.stream()
                    .filter(p -> assignment.getValue().contains(p.getId()))
                    .collect(Collectors.toList());

            List<Passenger> vehiclePassengers = new ArrayList<>();
            int usedSeats = 0;

            for (Passenger p : rawVehiclePassengers) {
                int seatsForPassenger = 1;

                if (p.getGroupId() != null) {
                    PassengerGroup g = groups.stream().filter(x -> Objects.equals(x.getId(), p.getGroupId())).findFirst().orElse(null);
                    seatsForPassenger = g != null ? g.getRequiredSeats() : 1;
                }

                if (usedSeats + seatsForPassenger > vehicle.getTotalSeats()) continue;

                vehiclePassengers.add(p);
                usedSeats += seatsForPassenger;
            }

            if (vehiclePassengers.isEmpty()) continue;

            PDPTWRoute optimizedRoute = pdptwService.optimizeRoute(vehicle, vehiclePassengers);

            BigDecimal totalCost = (vehicle.getFixedPrice() != null && vehicle.getFixedPrice().compareTo(BigDecimal.ZERO) > 0)
                    ? vehicle.getFixedPrice()
                    : optimizedRoute.getTotalCost();

            int occupiedSeats = usedSeats;
            int availableSeats = Math.max(0, vehicle.getTotalSeats() - occupiedSeats);

            BigDecimal costPerSeat = occupiedSeats > 0
                    ? totalCost.divide(BigDecimal.valueOf(occupiedSeats), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            CarpoolMatchResult matchResult = CarpoolMatchResult.builder()
                    .vehicleId(vehicle.getId())
                    .driverName(vehicle.getDriverName())
                    .licensePlate(vehicle.getLicensePlate())
                    .vehicleType(vehicle.getVehicleType())
                    .totalSeats(vehicle.getTotalSeats())
                    .availableSeats(availableSeats)
                    .occupiedSeats(occupiedSeats)
                    .totalCost(totalCost)
                    .costPerPassenger(costPerSeat)
                    .totalDistance(optimizedRoute.getTotalDistance())
                    .estimatedDepartureTime(optimizedRoute.getStartTime())
                    .estimatedArrivalTime(optimizedRoute.getEndTime())
                    .pickupAddress(vehicle.getPickupAddress() != null ? vehicle.getPickupAddress() : "")
                    .dropoffAddress(vehicle.getDropoffAddress() != null ? vehicle.getDropoffAddress() : "")
                    .matchedPassengers(new ArrayList<>())
                    .optimizedRoute(new ArrayList<>())
                    .build();

            int sequence = 1;

            for (PDPTWNode node : optimizedRoute.getNodes()) {
                if (node.getPassengerId() != null) {
                    Passenger passenger = vehiclePassengers.stream()
                            .filter(p -> p.getId().equals(node.getPassengerId()))
                            .findFirst().orElse(null);

                    if (passenger != null) {
                        PassengerMatch existingMatch = matchResult.getMatchedPassengers().stream()
                                .filter(m -> m.getPassengerId() == passenger.getId())
                                .findFirst().orElse(null);

                        if (existingMatch == null) {
                            int seatsForPassenger = 1;

                            if (passenger.getGroupId() != null) {
                                PassengerGroup g = groups.stream().filter(x -> Objects.equals(x.getId(), passenger.getGroupId())).findFirst().orElse(null);
                                seatsForPassenger = g != null ? g.getRequiredSeats() : 1;
                            }

                            BigDecimal passengerCost = costPerSeat.multiply(BigDecimal.valueOf(seatsForPassenger));

                            existingMatch = PassengerMatch.builder()
                                    .passengerId(passenger.getId())
                                    .passengerName(passenger.getName())
                                    .pickupAddress(passenger.getPickupAddress() != null ? passenger.getPickupAddress() : "")
                                    .dropoffAddress(passenger.getDropoffAddress() != null ? passenger.getDropoffAddress() : "")
                                    .cost(passengerCost)
                                    .sequenceOrder(sequence++)
                                    .build();

                            matchResult.getMatchedPassengers().add(existingMatch);
                        }

                        if ("pickup".equals(node.getType()))
                            existingMatch.setPickupTime(node.getEarliestTime());

                        if ("dropoff".equals(node.getType()))
                            existingMatch.setDropoffTime(node.getLatestTime());
                    }
                }
            }

            sequence = 1;
            for (PDPTWNode node : optimizedRoute.getNodes()) {
                matchResult.getOptimizedRoute().add(RoutePoint.builder()
                        .latitude(node.getLatitude())
                        .longitude(node.getLongitude())
                        .address(node.getAddress())
                        .type(node.getType())
                        .passengerId(node.getPassengerId())
                        .time(node.getEarliestTime())
                        .sequence(sequence++)
                        .build());
            }

            results.add(matchResult);
        }

        return results;
    }
}
