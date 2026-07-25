package com.example.DaLattravel.service;

import com.example.DaLattravel.dto.OsrmLeg;
import com.example.DaLattravel.dto.OsrmRouteResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class OsrmRouteService {

    private static final String OSRM_BASE_URL = "https://router.project-osrm.org";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OsrmRouteResult getRoute(
            double pickupLat, double pickupLon,
            double dropoffLat, double dropoffLon,
            String profile) {
        if (profile == null) profile = "driving";

        String url = String.format(Locale.US,
                "%s/route/v1/%s/%.6f,%.6f;%.6f,%.6f?overview=full&alternatives=false&steps=true&geometries=geojson",
                OSRM_BASE_URL, profile, pickupLon, pickupLat, dropoffLon, dropoffLat);

        try {
            String payload = restTemplate.getForObject(url, String.class);
            if (payload == null) return null;

            JsonNode data = objectMapper.readTree(payload);
            if (!"Ok".equalsIgnoreCase(data.path("code").asText())) return null;

            JsonNode routes = data.path("routes");
            if (!routes.isArray() || routes.isEmpty()) return null;

            JsonNode route = routes.get(0);
            OsrmRouteResult result = new OsrmRouteResult();
            result.setDistanceMeters(route.path("distance").asDouble(0));
            result.setDurationSeconds(route.path("duration").asDouble(0));

            JsonNode geometry = route.path("geometry");
            JsonNode coordinates = geometry.path("coordinates");
            if (coordinates.isArray()) {
                for (JsonNode coord : coordinates) {
                    if (coord.isArray() && coord.size() >= 2) {
                        List<Double> point = new ArrayList<>();
                        point.add(coord.get(0).asDouble()); // lon
                        point.add(coord.get(1).asDouble()); // lat
                        result.getGeometry().add(point);
                    }
                }
            }

            JsonNode legs = route.path("legs");
            if (legs.isArray()) {
                for (JsonNode leg : legs) {
                    OsrmLeg legObj = new OsrmLeg();
                    legObj.setDistanceMeters(leg.path("distance").asDouble(0));
                    legObj.setDurationSeconds(leg.path("duration").asDouble(0));

                    JsonNode steps = leg.path("steps");
                    if (steps.isArray()) {
                        for (JsonNode step : steps) {
                            JsonNode stepCoords = step.path("geometry").path("coordinates");
                            if (stepCoords.isArray()) {
                                for (JsonNode stepCoord : stepCoords) {
                                    if (stepCoord.isArray() && stepCoord.size() >= 2) {
                                        List<Double> stepPoint = new ArrayList<>();
                                        stepPoint.add(stepCoord.get(0).asDouble());
                                        stepPoint.add(stepCoord.get(1).asDouble());
                                        legObj.getSteps().add(stepPoint);
                                    }
                                }
                            }
                        }
                    }
                    result.getLegs().add(legObj);
                }
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public Double getDistanceKm(
            double pickupLat, double pickupLon,
            double dropoffLat, double dropoffLon,
            String profile) {
        OsrmRouteResult route = getRoute(pickupLat, pickupLon, dropoffLat, dropoffLon, profile);
        if (route == null || route.getDistanceMeters() <= 0) return null;
        return route.getDistanceMeters() / 1000.0;
    }
}
