package com.example.DaLattravel.service;

import com.example.DaLattravel.dto.OsrmRouteResult;
import com.example.DaLattravel.dto.RouteMatchResult;
import com.example.DaLattravel.model.Passenger;
import com.example.DaLattravel.model.Vehicle;
import com.example.DaLattravel.repository.PassengerRepository;
import com.example.DaLattravel.repository.VehicleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteMatchingService {

    private final OsrmRouteService osrmService;
    private final VehicleRepository vehicleRepository;
    private final PassengerRepository passengerRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RouteMatchingService(OsrmRouteService osrmService, VehicleRepository vehicleRepository, PassengerRepository passengerRepository) {
        this.osrmService = osrmService;
        this.vehicleRepository = vehicleRepository;
        this.passengerRepository = passengerRepository;
    }

    public RouteMatchResult tryMatchRoute(
            Vehicle vehicle,
            double pickupLat, double pickupLon,
            double dropoffLat, double dropoffLon,
            int seatsNeeded) {

        RouteMatchResult result = new RouteMatchResult();
        result.setMatchedVehicle(vehicle);

        if (vehicle.getRoutePolyline() == null || vehicle.getRoutePolyline().trim().isEmpty()) {
            result.setReason("Xe chưa có tuyến đường được xác định");
            return result;
        }

        List<List<Double>> routeGeometry;
        try {
            routeGeometry = objectMapper.readValue(vehicle.getRoutePolyline(), new TypeReference<List<List<Double>>>() {});
            if (routeGeometry == null || routeGeometry.size() < 2) {
                result.setReason("Tuyến đường không hợp lệ");
                return result;
            }
        } catch (Exception e) {
            result.setReason("Lỗi parse tuyến đường");
            return result;
        }

        SnapResult snapPickup = findClosestPointOnRouteWithDistance(routeGeometry, pickupLat, pickupLon);
        SnapResult snapDropoff = findClosestPointOnRouteWithDistance(routeGeometry, dropoffLat, dropoffLon);

        final double maxSnapDistanceKm = 5.0;

        if (snapPickup.distanceKm > maxSnapDistanceKm) {
            result.setReason(String.format("Điểm đón quá xa tuyến đường (cách %.1f km)", snapPickup.distanceKm));
            return result;
        }

        if (snapDropoff.distanceKm > maxSnapDistanceKm) {
            result.setReason(String.format("Điểm đến quá xa tuyến đường (cách %.1f km)", snapDropoff.distanceKm));
            return result;
        }

        if (snapPickup.index >= snapDropoff.index) {
            if (snapDropoff.index == routeGeometry.size() - 1 && snapDropoff.distanceKm <= 2.0) {
                // Allowed
            } else {
                result.setReason("Hành trình không cùng chiều với tuyến xe");
                return result;
            }
        }

        boolean canFit = checkSegmentOccupancy(vehicle, snapPickup.index, snapDropoff.index, seatsNeeded);
        if (!canFit) {
            result.setReason("Không còn đủ ghế trên các đoạn đường của hành trình");
            return result;
        }

        result.setCanMatch(true);
        result.setPickupOrder(snapPickup.index);
        result.setDropoffOrder(snapDropoff.index);
        return result;
    }

    private static class SnapResult {
        int index;
        double distanceKm;

        SnapResult(int index, double distanceKm) {
            this.index = index;
            this.distanceKm = distanceKm;
        }
    }

    private SnapResult findClosestPointOnRouteWithDistance(List<List<Double>> routeGeometry, double lat, double lon) {
        double minDistance = Double.MAX_VALUE;
        int closestIndex = 0;

        for (int i = 0; i < routeGeometry.size(); i++) {
            List<Double> point = routeGeometry.get(i);
            if (point.size() < 2) continue;

            double pointLon = point.get(0);
            double pointLat = point.get(1);

            double distance = calculateHaversineDistance(lat, lon, pointLat, pointLon);

            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }

        return new SnapResult(closestIndex, minDistance);
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EarthRadius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(rLat1) * Math.cos(rLat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EarthRadius * c;
    }

    public int calculateMinAvailableSeats(int vehicleId, int totalSeats) {
        List<Passenger> passengers = passengerRepository.findByMatchedVehicleIdAndMatchedTrue(vehicleId);

        int minOrder = passengers.isEmpty() ? 0 : passengers.stream().mapToInt(p -> p.getPickupOrder() != null ? p.getPickupOrder() : 0).min().orElse(0);
        int maxOrder = passengers.isEmpty() ? 0 : passengers.stream().mapToInt(p -> p.getDropoffOrder() != null ? p.getDropoffOrder() : 100).max().orElse(0);

        int maxOccupancy = 0;

        for (int i = minOrder; i < maxOrder; i++) {
            int currentSegmentOccupancy = 0;
            for (Passenger p : passengers) {
                if (p.getPickupOrder() != null && p.getDropoffOrder() != null && p.getPickupOrder() <= i && p.getDropoffOrder() > i) {
                    currentSegmentOccupancy += (p.getGroup() != null ? p.getGroup().getRequiredSeats() : 1);
                }
            }

            if (currentSegmentOccupancy > maxOccupancy) {
                maxOccupancy = currentSegmentOccupancy;
            }
        }

        int passengerSeats = totalSeats > 1 ? totalSeats - 1 : 0;
        return Math.max(0, passengerSeats - maxOccupancy);
    }

    private boolean checkSegmentOccupancy(Vehicle vehicle, int pickupOrder, int dropoffOrder, int seatsNeeded) {
        List<Passenger> passengers = passengerRepository.findByMatchedVehicleIdAndMatchedTrue(vehicle.getId());

        Map<String, Integer> segmentOccupancy = new HashMap<>();

        for (Passenger passenger : passengers) {
            if (passenger.getPickupOrder() == null || passenger.getDropoffOrder() == null) continue;

            int pStart = passenger.getPickupOrder();
            int pEnd = passenger.getDropoffOrder();
            int seats = passenger.getGroup() != null ? passenger.getGroup().getRequiredSeats() : 1;

            for (int i = pStart; i < pEnd; i++) {
                String segmentKey = i + "-" + (i + 1);
                segmentOccupancy.put(segmentKey, segmentOccupancy.getOrDefault(segmentKey, 0) + seats);
            }
        }

        for (int i = pickupOrder; i < dropoffOrder; i++) {
            String segmentKey = i + "-" + (i + 1);
            int currentOccupancy = segmentOccupancy.getOrDefault(segmentKey, 0);

            int maxPassengerSeats = vehicle.getTotalSeats() > 1 ? vehicle.getTotalSeats() - 1 : 0;

            if (currentOccupancy + seatsNeeded > maxPassengerSeats) {
                return false;
            }
        }

        return true;
    }

    public List<List<Double>> determineMainRoute(List<double[]> routes) {
        if (routes.isEmpty()) return null;

        double maxDistance = 0;
        double[] longestRoute = routes.get(0);

        for (double[] route : routes) {
            Double distance = osrmService.getDistanceKm(route[0], route[1], route[2], route[3], "driving");
            if (distance != null && distance > maxDistance) {
                maxDistance = distance;
                longestRoute = route;
            }
        }

        OsrmRouteResult routeResult = osrmService.getRoute(longestRoute[0], longestRoute[1], longestRoute[2], longestRoute[3], "driving");
        return routeResult != null ? routeResult.getGeometry() : null;
    }

    public boolean updateVehicleRoutePolyline(int vehicleId) {
        List<Passenger> passengers = passengerRepository.findByMatchedVehicleIdAndMatchedTrue(vehicleId);
        if (passengers.isEmpty()) return false;

        Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
        if (vehicleOpt.isEmpty()) return false;
        Vehicle vehicle = vehicleOpt.get();

        Passenger earliestPickupPassenger = passengers.stream()
                .filter(p -> p.getPickupOrder() != null)
                .min(Comparator.comparingInt(Passenger::getPickupOrder))
                .orElse(passengers.get(0));

        Passenger latestDropoffPassenger = passengers.stream()
                .filter(p -> p.getDropoffOrder() != null)
                .max(Comparator.comparingInt(Passenger::getDropoffOrder))
                .orElse(passengers.get(passengers.size() - 1));

        OsrmRouteResult routeResult = osrmService.getRoute(
                earliestPickupPassenger.getPickupLatitude(), earliestPickupPassenger.getPickupLongitude(),
                latestDropoffPassenger.getDropoffLatitude(), latestDropoffPassenger.getDropoffLongitude(),
                "driving");

        if (routeResult == null || routeResult.getGeometry().isEmpty()) return false;

        try {
            vehicle.setRoutePolyline(objectMapper.writeValueAsString(routeResult.getGeometry()));

            List<Double> firstPoint = routeResult.getGeometry().get(0);
            List<Double> lastPoint = routeResult.getGeometry().get(routeResult.getGeometry().size() - 1);

            vehicle.setPickupLongitude(firstPoint.get(0));
            vehicle.setPickupLatitude(firstPoint.get(1));
            vehicle.setDropoffLongitude(lastPoint.get(0));
            vehicle.setDropoffLatitude(lastPoint.get(1));

            if (earliestPickupPassenger.getPickupAddress() != null) {
                vehicle.setPickupAddress(earliestPickupPassenger.getPickupAddress());
            }
            if (latestDropoffPassenger.getDropoffAddress() != null) {
                vehicle.setDropoffAddress(latestDropoffPassenger.getDropoffAddress());
            }

            for (Passenger passenger : passengers) {
                SnapResult sPick = findClosestPointOnRouteWithDistance(routeResult.getGeometry(), passenger.getPickupLatitude(), passenger.getPickupLongitude());
                SnapResult sDrop = findClosestPointOnRouteWithDistance(routeResult.getGeometry(), passenger.getDropoffLatitude(), passenger.getDropoffLongitude());

                passenger.setPickupOrder(sPick.index);
                passenger.setDropoffOrder(sDrop.index);
            }

            vehicleRepository.save(vehicle);
            passengerRepository.saveAll(passengers);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
