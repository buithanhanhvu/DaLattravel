package com.example.dalattravel.service;

import com.example.dalattravel.model.Passenger;
import com.example.dalattravel.model.PassengerGroup;
import com.example.dalattravel.model.Vehicle;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MinCostMaxFlowService {

    @Getter
    @Setter
    public static class FlowEdge {
        private int from;
        private int to;
        private int capacity;
        private BigDecimal cost;
        private int flow;
        private FlowEdge reverse;
    }

    @Getter
    @Setter
    public static class FlowResult {
        private int maxFlow;
        private BigDecimal minCost = BigDecimal.ZERO;
        private Map<Integer, List<Integer>> assignments = new HashMap<>(); // VehicleId -> List of PassengerIds
    }

    public FlowResult solve(List<Vehicle> vehicles, List<Passenger> passengers, List<PassengerGroup> groups) {
        int source = 0;
        int sink = 1;
        int vehicleStart = 2;
        int passengerStart = vehicleStart + vehicles.size();
        int groupStart = passengerStart + passengers.size();

        Map<Integer, List<FlowEdge>> graph = new HashMap<>();
        List<FlowEdge> allEdges = new ArrayList<>();

        int totalNodes = groupStart + groups.size() + 2;
        for (int i = 0; i < totalNodes; i++) {
            graph.put(i, new ArrayList<>());
        }

        // Source -> Vehicles
        for (int i = 0; i < vehicles.size(); i++) {
            int vehicleNode = vehicleStart + i;
            FlowEdge edge = createEdge(source, vehicleNode, vehicles.get(i).getAvailableSeats(), BigDecimal.ZERO);
            graph.get(source).add(edge);
            graph.get(vehicleNode).add(edge.getReverse());
            allEdges.add(edge);
        }

        // Vehicles -> Passengers
        for (int v = 0; v < vehicles.size(); v++) {
            int vehicleNode = vehicleStart + v;
            Vehicle vehicle = vehicles.get(v);

            for (int p = 0; p < passengers.size(); p++) {
                Passenger passenger = passengers.get(p);
                if (passenger.getGroupId() != null) continue;

                int passengerNode = passengerStart + p;
                BigDecimal cost = calculateCost(vehicle, passenger);
                FlowEdge edge = createEdge(vehicleNode, passengerNode, 1, cost);
                graph.get(vehicleNode).add(edge);
                graph.get(passengerNode).add(edge.getReverse());
                allEdges.add(edge);
            }
        }

        // Vehicles -> Groups
        for (int v = 0; v < vehicles.size(); v++) {
            int vehicleNode = vehicleStart + v;
            Vehicle vehicle = vehicles.get(v);

            for (int g = 0; g < groups.size(); g++) {
                PassengerGroup group = groups.get(g);
                if (vehicle.getAvailableSeats() < group.getRequiredSeats()) continue;

                int groupNode = groupStart + g;
                BigDecimal cost = calculateGroupCost(vehicle, group);
                FlowEdge edge = createEdge(vehicleNode, groupNode, 1, cost);
                graph.get(vehicleNode).add(edge);
                graph.get(groupNode).add(edge.getReverse());
                allEdges.add(edge);
            }
        }

        // Groups -> Group Passengers
        for (int g = 0; g < groups.size(); g++) {
            int groupNode = groupStart + g;
            PassengerGroup group = groups.get(g);
            List<Passenger> groupPassengers = passengers.stream()
                    .filter(p -> Objects.equals(p.getGroupId(), group.getId()))
                    .collect(Collectors.toList());

            for (Passenger passenger : groupPassengers) {
                int passengerNode = passengerStart + passengers.indexOf(passenger);
                FlowEdge edge = createEdge(groupNode, passengerNode, 1, BigDecimal.ZERO);
                graph.get(groupNode).add(edge);
                graph.get(passengerNode).add(edge.getReverse());
                allEdges.add(edge);
            }
        }

        // Passengers -> Sink
        for (int p = 0; p < passengers.size(); p++) {
            int passengerNode = passengerStart + p;
            FlowEdge edge = createEdge(passengerNode, sink, 1, BigDecimal.ZERO);
            graph.get(passengerNode).add(edge);
            graph.get(sink).add(edge.getReverse());
            allEdges.add(edge);
        }

        return minCostMaxFlow(graph, source, sink, vehicleStart, passengerStart, groupStart, vehicles, passengers, groups);
    }

    private FlowEdge createEdge(int from, int to, int capacity, BigDecimal cost) {
        FlowEdge edge = new FlowEdge();
        edge.setFrom(from);
        edge.setTo(to);
        edge.setCapacity(capacity);
        edge.setCost(cost);
        edge.setFlow(0);

        FlowEdge reverse = new FlowEdge();
        reverse.setFrom(to);
        reverse.setTo(from);
        reverse.setCapacity(0);
        reverse.setCost(cost.negate());
        reverse.setFlow(0);

        edge.setReverse(reverse);
        reverse.setReverse(edge);

        return edge;
    }

    private BigDecimal calculateCost(Vehicle vehicle, Passenger passenger) {
        double distance = calculateDistance(
                vehicle.getPickupLatitude(), vehicle.getPickupLongitude(),
                passenger.getPickupLatitude(), passenger.getPickupLongitude());

        double dropoffDistance = calculateDistance(
                passenger.getPickupLatitude(), passenger.getPickupLongitude(),
                passenger.getDropoffLatitude(), passenger.getDropoffLongitude());

        double totalDistance = distance + dropoffDistance;
        BigDecimal costPerKm = vehicle.getCostPerKm() != null ? vehicle.getCostPerKm() : BigDecimal.ZERO;
        return BigDecimal.valueOf(totalDistance).multiply(costPerKm);
    }

    private BigDecimal calculateGroupCost(Vehicle vehicle, PassengerGroup group) {
        List<Passenger> groupPassengers = group.getPassengers();
        if (groupPassengers == null || groupPassengers.isEmpty()) return BigDecimal.valueOf(Double.MAX_VALUE);

        BigDecimal totalCost = BigDecimal.ZERO;
        for (Passenger passenger : groupPassengers) {
            totalCost = totalCost.add(calculateCost(vehicle, passenger));
        }

        return totalCost;
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

    private FlowResult minCostMaxFlow(
            Map<Integer, List<FlowEdge>> graph,
            int source, int sink,
            int vehicleStart, int passengerStart, int groupStart,
            List<Vehicle> vehicles, List<Passenger> passengers, List<PassengerGroup> groups) {

        FlowResult result = new FlowResult();
        Map<Integer, List<Integer>> assignments = new HashMap<>();

        int maxIterations = 1000;
        for (int iteration = 0; iteration < maxIterations; iteration++) {
            Map<Integer, BigDecimal> distances = new HashMap<>();
            Map<Integer, FlowEdge> parents = new HashMap<>();
            Set<Integer> inQueue = new HashSet<>();

            for (int i = 0; i < graph.size(); i++) {
                distances.put(i, i == source ? BigDecimal.ZERO : BigDecimal.valueOf(Double.MAX_VALUE));
            }

            Queue<Integer> queue = new LinkedList<>();
            queue.add(source);
            inQueue.add(source);

            while (!queue.isEmpty()) {
                int u = queue.poll();
                inQueue.remove(u);

                for (FlowEdge edge : graph.get(u)) {
                    if (edge.getCapacity() - edge.getFlow() > 0) {
                        BigDecimal newDist = distances.get(u).add(edge.getCost());
                        if (newDist.compareTo(distances.get(edge.getTo())) < 0) {
                            distances.put(edge.getTo(), newDist);
                            parents.put(edge.getTo(), edge);

                            if (!inQueue.contains(edge.getTo())) {
                                queue.add(edge.getTo());
                                inQueue.add(edge.getTo());
                            }
                        }
                    }
                }
            }

            if (distances.get(sink).compareTo(BigDecimal.valueOf(Double.MAX_VALUE - 1000)) >= 0)
                break;

            List<FlowEdge> path = new ArrayList<>();
            int current = sink;
            int minFlow = Integer.MAX_VALUE;

            while (current != source) {
                if (!parents.containsKey(current))
                    break;

                FlowEdge edge = parents.get(current);
                path.add(edge);
                minFlow = Math.min(minFlow, edge.getCapacity() - edge.getFlow());
                current = edge.getFrom();
            }

            if (path.isEmpty())
                break;

            for (FlowEdge edge : path) {
                edge.setFlow(edge.getFlow() + minFlow);
                edge.getReverse().setFlow(edge.getReverse().getFlow() - minFlow);
            }

            result.setMaxFlow(result.getMaxFlow() + minFlow);
            result.setMinCost(result.getMinCost().add(distances.get(sink).multiply(BigDecimal.valueOf(minFlow))));

            for (FlowEdge edge : path) {
                if (edge.getFrom() >= vehicleStart && edge.getFrom() < passengerStart) {
                    int vehicleIndex = edge.getFrom() - vehicleStart;
                    int vehicleId = vehicles.get(vehicleIndex).getId();

                    if (edge.getTo() >= passengerStart && edge.getTo() < groupStart) {
                        int passengerIndex = edge.getTo() - passengerStart;
                        int passengerId = passengers.get(passengerIndex).getId();

                        assignments.computeIfAbsent(vehicleId, k -> new ArrayList<>()).add(passengerId);
                    } else if (edge.getTo() >= groupStart) {
                        int groupIndex = edge.getTo() - groupStart;
                        PassengerGroup group = groups.get(groupIndex);
                        List<Passenger> groupPassengers = passengers.stream()
                                .filter(p -> Objects.equals(p.getGroupId(), group.getId()))
                                .collect(Collectors.toList());

                        assignments.computeIfAbsent(vehicleId, k -> new ArrayList<>());
                        for (Passenger p : groupPassengers) {
                            assignments.get(vehicleId).add(p.getId());
                        }
                    }
                }
            }
        }

        result.setAssignments(assignments);
        return result;
    }
}
