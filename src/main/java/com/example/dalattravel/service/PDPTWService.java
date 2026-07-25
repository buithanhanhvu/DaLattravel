package com.example.dalattravel.service;

import com.example.dalattravel.dto.PDPTWNode;
import com.example.dalattravel.dto.PDPTWRoute;
import com.example.dalattravel.model.Passenger;
import com.example.dalattravel.model.Vehicle;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PDPTWService {

    public PDPTWRoute optimizeRoute(Vehicle vehicle, List<Passenger> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            PDPTWRoute route = new PDPTWRoute();
            route.setNodes(new ArrayList<>());
            route.setTotalDistance(0);
            route.setStartTime(vehicle.getDepartureTime());
            route.setEndTime(vehicle.getDepartureTime());
            route.setTotalCost(BigDecimal.ZERO);
            return route;
        }

        List<PDPTWNode> nodes = new ArrayList<>();

        for (Passenger passenger : passengers) {
            PDPTWNode pickup = new PDPTWNode();
            pickup.setLatitude(passenger.getPickupLatitude());
            pickup.setLongitude(passenger.getPickupLongitude());
            pickup.setAddress(passenger.getPickupAddress() != null ? passenger.getPickupAddress() : "");
            pickup.setType("pickup");
            pickup.setPassengerId(passenger.getId());
            pickup.setEarliestTime(passenger.getPreferredDepartureTime().minusMinutes(30));
            pickup.setLatestTime(passenger.getPreferredDepartureTime().plusHours(2));
            pickup.setServiceTime(5);
            nodes.add(pickup);

            PDPTWNode dropoff = new PDPTWNode();
            dropoff.setLatitude(passenger.getDropoffLatitude());
            dropoff.setLongitude(passenger.getDropoffLongitude());
            dropoff.setAddress(passenger.getDropoffAddress() != null ? passenger.getDropoffAddress() : "");
            dropoff.setType("dropoff");
            dropoff.setPassengerId(passenger.getId());
            dropoff.setEarliestTime(passenger.getPreferredArrivalTime() != null ? 
                    passenger.getPreferredArrivalTime().minusMinutes(30) : passenger.getPreferredDepartureTime().plusHours(1));
            dropoff.setLatestTime(passenger.getPreferredArrivalTime() != null ? 
                    passenger.getPreferredArrivalTime().plusHours(2) : passenger.getPreferredDepartureTime().plusHours(4));
            dropoff.setServiceTime(5);
            nodes.add(dropoff);
        }

        PDPTWRoute route = new PDPTWRoute();
        route.setNodes(new ArrayList<>());
        route.setStartTime(vehicle.getDepartureTime());

        List<PDPTWNode> unvisited = new ArrayList<>(nodes);
        LocalDateTime currentTime = vehicle.getDepartureTime();
        double currentLat = vehicle.getPickupLatitude();
        double currentLng = vehicle.getPickupLongitude();
        Set<Integer> pickedUpPassengers = new HashSet<>();

        while (!unvisited.isEmpty()) {
            PDPTWNode nextNode = null;
            double minDistance = Double.MAX_VALUE;

            for (PDPTWNode node : unvisited) {
                if ("dropoff".equals(node.getType()) && !pickedUpPassengers.contains(node.getPassengerId()))
                    continue;

                if (currentTime.isAfter(node.getLatestTime()))
                    continue;

                double distance = calculateDistance(currentLat, currentLng, node.getLatitude(), node.getLongitude());
                double tempTravelTimeMinutes = (distance / 50.0) * 60;

                LocalDateTime tempArrivalTime = currentTime.plusMinutes((long) tempTravelTimeMinutes);
                if (tempArrivalTime.isBefore(node.getEarliestTime()))
                    tempArrivalTime = node.getEarliestTime();

                if (!tempArrivalTime.isAfter(node.getLatestTime())) {
                    double score = distance;
                    if (tempArrivalTime.isBefore(node.getEarliestTime()))
                        score += 1000;

                    if (score < minDistance) {
                        minDistance = score;
                        nextNode = node;
                    }
                }
            }

            if (nextNode == null) {
                final double cLat = currentLat;
                final double cLng = currentLng;
                nextNode = unvisited.stream()
                        .min(Comparator.comparingDouble(n -> calculateDistance(cLat, cLng, n.getLatitude(), n.getLongitude())))
                        .orElse(null);

                if (nextNode == null) break;
            }

            double distanceToNode = calculateDistance(currentLat, currentLng, nextNode.getLatitude(), nextNode.getLongitude());
            double travelTime = (distanceToNode / 50.0) * 60;
            LocalDateTime arrivalTime = currentTime.plusMinutes((long) travelTime);

            if (arrivalTime.isBefore(nextNode.getEarliestTime()))
                arrivalTime = nextNode.getEarliestTime();

            currentTime = arrivalTime.plusMinutes(nextNode.getServiceTime());
            currentLat = nextNode.getLatitude();
            currentLng = nextNode.getLongitude();

            route.getNodes().add(nextNode);
            unvisited.remove(nextNode);

            if ("pickup".equals(nextNode.getType()))
                pickedUpPassengers.add(nextNode.getPassengerId());
            else if ("dropoff".equals(nextNode.getType()))
                pickedUpPassengers.remove(nextNode.getPassengerId());
        }

        route.setTotalDistance(0);
        double routeLat = vehicle.getPickupLatitude();
        double routeLng = vehicle.getPickupLongitude();

        for (PDPTWNode node : route.getNodes()) {
            route.setTotalDistance(route.getTotalDistance() + calculateDistance(routeLat, routeLng, node.getLatitude(), node.getLongitude()));
            routeLat = node.getLatitude();
            routeLng = node.getLongitude();
        }

        route.setEndTime(currentTime);
        BigDecimal costPerKm = vehicle.getCostPerKm() != null ? vehicle.getCostPerKm() : BigDecimal.ZERO;
        route.setTotalCost(BigDecimal.valueOf(route.getTotalDistance()).multiply(costPerKm));

        return route;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
