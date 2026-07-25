package com.example.DaLattravel.service;

import com.example.DaLattravel.model.Passenger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KMeansClusteringService {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClusterPoint {
        private double latitude;
        private double longitude;
        private int passengerId;
        private Passenger passenger;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cluster {
        private double centerLatitude;
        private double centerLongitude;
        private List<ClusterPoint> points = new ArrayList<>();
    }

    public List<Cluster> clusterPassengers(List<Passenger> passengers, int k) {
        if (passengers == null || passengers.isEmpty()) {
            return new ArrayList<>();
        }

        List<ClusterPoint> points = passengers.stream().map(p -> {
            ClusterPoint cp = new ClusterPoint();
            cp.setLatitude(p.getPickupLatitude());
            cp.setLongitude(p.getPickupLongitude());
            cp.setPassengerId(p.getId());
            cp.setPassenger(p);
            return cp;
        }).collect(Collectors.toList());

        if (points.size() <= k) {
            return points.stream().map(p -> {
                Cluster c = new Cluster();
                c.setCenterLatitude(p.getLatitude());
                c.setCenterLongitude(p.getLongitude());
                c.getPoints().add(p);
                return c;
            }).collect(Collectors.toList());
        }

        Random random = new Random();
        List<Cluster> clusters = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            ClusterPoint randomPoint = points.get(random.nextInt(points.size()));
            Cluster c = new Cluster();
            c.setCenterLatitude(randomPoint.getLatitude());
            c.setCenterLongitude(randomPoint.getLongitude());
            clusters.add(c);
        }

        boolean changed = true;
        int maxIterations = 100;
        int iteration = 0;

        while (changed && iteration < maxIterations) {
            iteration++;
            changed = false;

            for (Cluster cluster : clusters) {
                cluster.getPoints().clear();
            }

            for (ClusterPoint point : points) {
                Cluster nearestCluster = clusters.stream()
                        .min(Comparator.comparingDouble(c -> calculateDistance(
                                point.getLatitude(), point.getLongitude(),
                                c.getCenterLatitude(), c.getCenterLongitude())))
                        .orElse(clusters.get(0));

                nearestCluster.getPoints().add(point);
            }

            for (Cluster cluster : clusters) {
                if (!cluster.getPoints().isEmpty()) {
                    double newLat = cluster.getPoints().stream().mapToDouble(ClusterPoint::getLatitude).average().orElse(0);
                    double newLng = cluster.getPoints().stream().mapToDouble(ClusterPoint::getLongitude).average().orElse(0);

                    if (Math.abs(cluster.getCenterLatitude() - newLat) > 0.0001 ||
                        Math.abs(cluster.getCenterLongitude() - newLng) > 0.0001) {
                        changed = true;
                        cluster.setCenterLatitude(newLat);
                        cluster.setCenterLongitude(newLng);
                    }
                }
            }
        }

        return clusters.stream().filter(c -> !c.getPoints().isEmpty()).collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Ban kinh Trai Dat trung binh (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
