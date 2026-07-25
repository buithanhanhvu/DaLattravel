package com.example.dalattravel.service;

import com.example.dalattravel.dto.TransportPriceResult;
import com.example.dalattravel.model.LegacyLocation;
import com.example.dalattravel.model.TransportOption;
import com.example.dalattravel.model.TransportPriceHistory;
import com.example.dalattravel.repository.LegacyLocationRepository;
import com.example.dalattravel.repository.TransportOptionRepository;
import com.example.dalattravel.repository.TransportPriceHistoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TransportPriceCalculator {

    private static final double DALAT_LAT = 11.9404;
    private static final double DALAT_LNG = 108.4583;

    private final TransportOptionRepository transportOptionRepository;
    private final LegacyLocationRepository legacyLocationRepository;
    private final TransportPriceHistoryRepository transportPriceHistoryRepository;

    public TransportPriceCalculator(
            TransportOptionRepository transportOptionRepository,
            LegacyLocationRepository legacyLocationRepository,
            TransportPriceHistoryRepository transportPriceHistoryRepository) {
        this.transportOptionRepository = transportOptionRepository;
        this.legacyLocationRepository = legacyLocationRepository;
        this.transportPriceHistoryRepository = transportPriceHistoryRepository;
    }

    public TransportPriceResult getFinalPrice(double startLat, double startLng, int transportId) {
        TransportPriceResult result = new TransportPriceResult();
        result.setDistanceToDalat(calculateHaversineDistance(startLat, startLng, DALAT_LAT, DALAT_LNG));

        Optional<TransportOption> transportOpt = transportOptionRepository.findById(transportId);
        if (transportOpt.isEmpty()) {
            result.setPrice(BigDecimal.ZERO);
            result.setPriceType("Error");
            result.setNote("Không tìm thấy thông tin phương tiện.");
            return result;
        }

        TransportOption transport = transportOpt.get();

        if (transport.isSelfDrive() && transport.getFuelConsumption() > 0 && transport.getFuelPrice() != null && transport.getFuelPrice().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal fuelUsed = BigDecimal.valueOf(result.getDistanceToDalat())
                    .multiply(BigDecimal.valueOf(transport.getFuelConsumption()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal fuelCost = fuelUsed.multiply(transport.getFuelPrice());
            BigDecimal maintenanceCost = fuelCost.multiply(BigDecimal.valueOf(0.2));

            result.setPrice(fuelCost.add(maintenanceCost));
            result.setPriceType("Calculated");
            result.setNote(String.format("Chi phí tính theo nhiên liệu: %.1fkm × %.1fl/100km × %,dđ/lít + bảo dưỡng (20%%) = %,dđ",
                    result.getDistanceToDalat(), transport.getFuelConsumption(), transport.getFuelPrice().longValue(), result.getPrice().longValue()));

            LegacyLocation nearestLocation = findNearestLocation(startLat, startLng, 50);
            if (nearestLocation != null) {
                result.setLocationName(nearestLocation.getCurrentName());
                result.setLocationId(nearestLocation.getId());
                result.setOldLocationName(nearestLocation.getOldName());
                result.setMergedLocation(nearestLocation.isMergedLocation());
                result.setDistanceFromLocation(calculateHaversineDistance(startLat, startLng, nearestLocation.getLatitude(), nearestLocation.getLongitude()));
            }

            return result;
        }

        LegacyLocation nearestLocationForPublic = findNearestLocation(startLat, startLng, 50);

        if (nearestLocationForPublic != null) {
            result.setLocationName(nearestLocationForPublic.getCurrentName());
            result.setLocationId(nearestLocationForPublic.getId());
            result.setOldLocationName(nearestLocationForPublic.getOldName());
            result.setMergedLocation(nearestLocationForPublic.isMergedLocation());
            result.setDistanceFromLocation(calculateHaversineDistance(startLat, startLng, nearestLocationForPublic.getLatitude(), nearestLocationForPublic.getLongitude()));

            Optional<TransportPriceHistory> fixedPriceOpt = transportPriceHistoryRepository.findByLegacyLocationIdAndTransportOptionId(nearestLocationForPublic.getId(), transportId);

            if (fixedPriceOpt.isPresent() && fixedPriceOpt.get().getPrice() != null && fixedPriceOpt.get().getPrice().compareTo(BigDecimal.ZERO) > 0) {
                result.setPrice(fixedPriceOpt.get().getPrice());
                result.setPriceType("Fixed");

                if (nearestLocationForPublic.isMergedLocation()) {
                    result.setNote("Giá từ " + nearestLocationForPublic.getCurrentName() +
                            " (khu vực " + nearestLocationForPublic.getOldName() + " cũ) đến Đà Lạt. Giá cố định từ nhà xe.");
                } else {
                    result.setNote("Giá từ " + nearestLocationForPublic.getCurrentName() + " đến Đà Lạt. Giá cố định từ nhà xe.");
                }

                return result;
            }
        }

        result.setPrice(calculateByDistance(result.getDistanceToDalat(), transport));
        result.setPriceType("Calculated");

        if (nearestLocationForPublic != null && nearestLocationForPublic.isMergedLocation()) {
            result.setNote(String.format("Giá ước tính từ khu vực %s (hiện tại: %s) đến Đà Lạt, khoảng cách %.1fkm. Vui lòng liên hệ nhà xe để biết giá chính xác.",
                    nearestLocationForPublic.getOldName(), nearestLocationForPublic.getCurrentName(), result.getDistanceToDalat()));
        } else {
            result.setNote(String.format("Giá ước tính dựa trên khoảng cách %.1fkm. Vui lòng liên hệ nhà xe để biết giá chính xác.", result.getDistanceToDalat()));
        }

        return result;
    }

    private LegacyLocation findNearestLocation(double lat, double lng, double maxRadiusKm) {
        List<LegacyLocation> allLocations = legacyLocationRepository.findByIsActiveTrue();
        if (allLocations.isEmpty()) return null;

        return allLocations.stream()
                .map(loc -> new LocationDistance(loc, calculateHaversineDistance(lat, lng, loc.getLatitude(), loc.getLongitude())))
                .filter(ld -> ld.distance <= maxRadiusKm)
                .min(Comparator.comparingDouble(ld -> ld.distance))
                .map(ld -> ld.location)
                .orElse(null);
    }

    private static class LocationDistance {
        LegacyLocation location;
        double distance;

        LocationDistance(LegacyLocation location, double distance) {
            this.location = location;
            this.distance = distance;
        }
    }

    private BigDecimal calculateByDistance(double distanceKm, TransportOption transport) {
        double pricePerKm = 3000;
        if (transport != null && transport.getType() != null) {
            switch (transport.getType()) {
                case "Public": pricePerKm = 2500; break;
                case "Private": pricePerKm = 4000; break;
                default: pricePerKm = 3000; break;
            }
        }

        if (distanceKm > 300) pricePerKm *= 0.85;
        else if (distanceKm > 200) pricePerKm *= 0.9;

        return BigDecimal.valueOf(distanceKm * pricePerKm);
    }

    private double calculateHaversineDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
