package com.example.dalattravel.service;

import com.example.dalattravel.dto.TripPlanResult;
import com.example.dalattravel.dto.TripPlanResult.*;
import com.example.dalattravel.model.TouristPlace;
import com.example.dalattravel.repository.HotelRepository;
import com.example.dalattravel.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class TripPlannerService {

    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;

    public TripPlannerService(HotelRepository hotelRepository,
                               RestaurantRepository restaurantRepository) {
        this.hotelRepository = hotelRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // ============================================================
    //  ENTRY POINT
    // ============================================================
    public TripPlanResult generatePlans(
            BigDecimal userBudget,
            int days,
            String selectedTransportType,
            double startLat,
            double startLng,
            String startName,
            List<TouristPlace> selectedPlaces) {

        if (days <= 0) days = 3;
        if (userBudget == null || userBudget.compareTo(BigDecimal.ZERO) <= 0) {
            userBudget = BigDecimal.valueOf(3_000_000);
        }
        if (startName == null || startName.isBlank()) {
            startName = "Trung tam TP. Da Lat";
        }

        // STEP 1: TSP Nearest-Neighbor — toi uu tuyen duong truoc
        List<TouristPlace> orderedPlaces = solveTSP(startLat, startLng, selectedPlaces);

        // STEP 2: Nhom diem gan nhau vao cung 1 ngay (simple sequential grouping)
        List<List<TouristPlace>> dayGroups = groupByDay(orderedPlaces, days);

        // STEP 3: Validate ngan sach toi thieu
        double minTicketCost = orderedPlaces.stream()
                .mapToDouble(p -> p.getTicketPrice() != null ? p.getTicketPrice().doubleValue() : 20_000)
                .sum();
        double minCost = (150_000 * days) + (80_000 * days * 3) + (200_000 * Math.max(days - 1, 0)) + minTicketCost;
        String budgetWarning = null;
        if (userBudget.doubleValue() < minCost) {
            budgetWarning = String.format(
                "⚠️ Ngân sách %,.0f VNĐ hiện không đủ cho %d ngày với %d địa điểm. Chi phí tối thiểu ước tính là %,.0f VNĐ. Gợi ý: giảm số địa điểm hoặc tăng ngân sách.",
                userBudget.doubleValue(), days, selectedPlaces.size(), minCost);
        }

        // STEP 4: Build 3 tier plans
        PlanTier tietKiem = buildTier("tiet_kiem", "⚡ Phương án Tiết kiệm", "bg-success",
                "Xe máy tự lái", 150_000, 2_000, 80_000, 200_000,
                days, userBudget, startLat, startLng, startName, orderedPlaces, dayGroups);

        PlanTier canBang = buildTier("can_bang", "⚖️ Phương án Cân bằng", "bg-primary",
                "Ô tô 4 chỗ gia đình", 900_000, 2_500, 180_000, 600_000,
                days, userBudget, startLat, startLng, startName, orderedPlaces, dayGroups);

        PlanTier caoCap = buildTier("cao_cap", "💎 Phương án Cao cấp", "bg-warning text-dark",
                "Ô tô 7 chỗ VIP", 1_400_000, 3_000, 350_000, 1_500_000,
                days, userBudget, startLat, startLng, startName, orderedPlaces, dayGroups);

        markBestRecommendation(userBudget, tietKiem, canBang, caoCap);

        return TripPlanResult.builder()
                .userBudget(userBudget)
                .requestedDays(days)
                .startLocationName(startName)
                .startLatitude(startLat)
                .startLongitude(startLng)
                .budgetWarning(budgetWarning)
                .plans(Arrays.asList(tietKiem, canBang, caoCap))
                .build();
    }

    // ============================================================
    //  BUILD TIER — theo dung dac ta: tinh chi phi xe TUNG NGAY
    // ============================================================
    private PlanTier buildTier(
            String tierType,
            String tierTitle,
            String badgeClass,
            String transportName,
            double rentalPerDay,       // tien thue xe co dinh/ngay
            double fuelRatePerKm,      // xang phi bien dong
            double foodCostPerDay,     // an uong/ngay
            double accommodationPerNight,
            int days,
            BigDecimal userBudget,
            double startLat,
            double startLng,
            String startName,
            List<TouristPlace> orderedPlaces,
            List<List<TouristPlace>> dayGroups) {

        int nights = Math.max(0, days - 1);

        // --- Phan bo ngan sach 5 khoan muc ---
        double contingency = userBudget.doubleValue() * 0.10;  // 10% du phong

        // --- Chi phi xe: tinh TUNG NGAY rieng biet ---
        double totalRentalCost = rentalPerDay * days;
        double totalFuelCost = 0;
        List<Double> dailyKmList = new ArrayList<>();
        List<Double> dailyFuelList = new ArrayList<>();

        // Tinh km va xang phi moi ngay dua tren nhom dia diem cua ngay do
        double prevLat = startLat, prevLng = startLng;
        for (List<TouristPlace> group : dayGroups) {
            double dayKm = 0;
            double curLat = prevLat, curLng = prevLng;
            for (TouristPlace p : group) {
                dayKm += haversineDistance(curLat, curLng, p.getLatitude(), p.getLongitude());
                curLat = p.getLatitude();
                curLng = p.getLongitude();
            }
            // Tru ve khach san cuoi ngay (uoc tinh ~3km)
            dayKm += 3.0;
            dailyKmList.add(dayKm);
            double dayFuel = dayKm * fuelRatePerKm;
            dailyFuelList.add(dayFuel);
            totalFuelCost += dayFuel;
            if (!group.isEmpty()) {
                prevLat = group.get(group.size() - 1).getLatitude();
                prevLng = group.get(group.size() - 1).getLongitude();
            }
        }

        BigDecimal transportCost = BigDecimal.valueOf(totalRentalCost + totalFuelCost).setScale(0, RoundingMode.HALF_UP);
        BigDecimal foodCost = BigDecimal.valueOf(foodCostPerDay * days * 3).setScale(0, RoundingMode.HALF_UP); // 3 bua/ngay
        BigDecimal accommodationCost = BigDecimal.valueOf(accommodationPerNight * nights).setScale(0, RoundingMode.HALF_UP);

        BigDecimal ticketCost = BigDecimal.ZERO;
        for (TouristPlace p : orderedPlaces) {
            ticketCost = ticketCost.add(
                p.getTicketPrice() != null && p.getTicketPrice().compareTo(BigDecimal.ZERO) > 0
                    ? p.getTicketPrice()
                    : BigDecimal.valueOf(20_000)
            );
        }

        BigDecimal contingencyBD = BigDecimal.valueOf(contingency).setScale(0, RoundingMode.HALF_UP);
        BigDecimal totalCost = transportCost.add(foodCost).add(accommodationCost).add(ticketCost).add(contingencyBD);

        // --- Build lich trinh tung ngay ---
        List<DaySchedule> daySchedules = buildDaySchedules(
                days, startLat, startLng, startName, dayGroups,
                dailyKmList, dailyFuelList, tierType, rentalPerDay, foodCostPerDay);

        // --- Waypoints cho ban do ---
        List<Waypoint> waypoints = new ArrayList<>();
        waypoints.add(Waypoint.builder().stepNumber(0).name(startName)
                .latitude(startLat).longitude(startLng).type("START").build());
        for (int i = 0; i < orderedPlaces.size(); i++) {
            TouristPlace p = orderedPlaces.get(i);
            waypoints.add(Waypoint.builder().stepNumber(i + 1).name(p.getName())
                    .latitude(p.getLatitude()).longitude(p.getLongitude()).type("DESTINATION").build());
        }

        String accType = switch (tierType) {
            case "can_bang" -> "Khách sạn 3 sao trung tâm";
            case "cao_cap"  -> "Resort / Khách sạn 4-5 sao";
            default         -> "Homestay / Hostel";
        };

        double totalDistKm = dailyKmList.stream().mapToDouble(Double::doubleValue).sum();

        return PlanTier.builder()
                .tierType(tierType)
                .tierTitle(tierTitle)
                .badgeClass(badgeClass)
                .transportName(transportName)
                .accommodationType(accType)
                .totalCost(totalCost)
                .transportCost(transportCost)
                .foodCost(foodCost)
                .ticketCost(ticketCost)
                .accommodationCost(accommodationCost)
                .contingencyCost(contingencyBD)
                .totalDistanceKm(Math.round(totalDistKm * 10.0) / 10.0)
                .days(daySchedules)
                .routeWaypoints(waypoints)
                .build();
    }

    // ============================================================
    //  BUILD LICH TRINH: khung gio sang/trua/toi + chi phi xe/ngay
    // ============================================================
    private List<DaySchedule> buildDaySchedules(
            int totalDays,
            double startLat, double startLng, String startName,
            List<List<TouristPlace>> dayGroups,
            List<Double> dailyKmList,
            List<Double> dailyFuelList,
            String tierType,
            double rentalPerDay,
            double foodCostPerDay) {

        List<DaySchedule> schedules = new ArrayList<>();
        List<com.example.dalattravel.model.Hotel> allHotels = hotelRepository.findAll();
        List<com.example.dalattravel.model.Restaurant> allRestaurants = restaurantRepository.findAll();

        for (int day = 1; day <= totalDays; day++) {
            List<TouristPlace> group = day <= dayGroups.size() ? dayGroups.get(day - 1) : Collections.emptyList();
            double dayKm = day <= dailyKmList.size() ? dailyKmList.get(day - 1) : 0;
            double dayFuel = day <= dailyFuelList.size() ? dailyFuelList.get(day - 1) : 0;
            double dayTransportTotal = rentalPerDay + dayFuel;

            // Centroid cua cum dia diem trong ngay
            double centroidLat = group.isEmpty() ? startLat : group.stream().mapToDouble(TouristPlace::getLatitude).average().orElse(startLat);
            double centroidLng = group.isEmpty() ? startLng : group.stream().mapToDouble(TouristPlace::getLongitude).average().orElse(startLng);

            // Tim khach san va nha hang phu hop gan nhat
            com.example.dalattravel.model.Restaurant lunchRest = findNearestRestaurant(allRestaurants, centroidLat, centroidLng, tierType);
            com.example.dalattravel.model.Restaurant dinnerRest = findNearestRestaurant(allRestaurants, 11.9416, 108.4375, tierType); // gan trung tam / cho dem
            com.example.dalattravel.model.Hotel recommendedHotel = findNearestHotel(allHotels, centroidLat, centroidLng, tierType);

            DaySchedule daySchedule = new DaySchedule();
            daySchedule.setDayNumber(day);
            daySchedule.setDailyKm(Math.round(dayKm * 10.0) / 10.0);
            daySchedule.setDailyTransportCost(BigDecimal.valueOf(dayTransportTotal).setScale(0, RoundingMode.HALF_UP));
            daySchedule.setDailyFoodCost(BigDecimal.valueOf(foodCostPerDay * 3).setScale(0, RoundingMode.HALF_UP));

            List<ScheduleItem> items = new ArrayList<>();

            // 06:30 — An sang
            String breakfastName = recommendedHotel != null ? "Ăn sáng tại " + recommendedHotel.getName() : "Quán ăn sáng gần chỗ ở";
            items.add(ScheduleItem.builder()
                    .time("06:30 - 07:30")
                    .activity("Ăn sáng & Cà phê sáng Đà Lạt")
                    .locationName(breakfastName)
                    .latitude(recommendedHotel != null ? recommendedHotel.getLatitude() : startLat)
                    .longitude(recommendedHotel != null ? recommendedHotel.getLongitude() : startLng)
                    .durationMinutes(60).icon("fa-coffee")
                    .note("Bánh căn / Bánh mì xíu mại nóng hổi khởi đầu ngày mới")
                    .costEstimate(BigDecimal.valueOf(foodCostPerDay))
                    .build());

            // 07:30 — Dia diem 1
            if (!group.isEmpty()) {
                TouristPlace p1 = group.get(0);
                boolean isRest1 = p1.getId() != null && p1.getId().startsWith("R_");
                boolean isHotel1 = p1.getId() != null && p1.getId().startsWith("H_");
                String activity1 = isRest1 ? "Ghé quán & Thưởng thức: " + p1.getName()
                                 : isHotel1 ? "Nghỉ ngơi & Check-in: " + p1.getName()
                                 : "Tham quan & Check-in: " + p1.getName();
                String ticket1 = p1.getTicketPrice() != null && p1.getTicketPrice().compareTo(BigDecimal.ZERO) > 0
                        ? String.format("Vé vào cổng: %,.0f VNĐ/người", p1.getTicketPrice().doubleValue())
                        : "Miễn phí vé vào cổng";
                items.add(ScheduleItem.builder()
                        .time("07:30 - 11:30")
                        .activity(activity1)
                        .locationName(p1.getName())
                        .latitude(p1.getLatitude()).longitude(p1.getLongitude())
                        .durationMinutes(p1.getAvgVisitDurationMin() != null ? p1.getAvgVisitDurationMin() : 120)
                        .icon(isRest1 ? "fa-utensils" : (isHotel1 ? "fa-hotel" : "fa-camera"))
                        .note((p1.getDescription() != null ? p1.getDescription() : "Khám phá địa điểm nổi tiếng") + " | " + ticket1)
                        .costEstimate(p1.getTicketPrice() != null ? p1.getTicketPrice() : BigDecimal.valueOf(20_000))
                        .build());
                daySchedule.setTitle(String.format("Ngày %d: Khám phá %s", day, p1.getName()));
            } else {
                items.add(ScheduleItem.builder()
                        .time("07:30 - 11:30")
                        .activity("Dạo phố & Khám phá Quảng trường Lâm Viên")
                        .locationName("Quảng trường Lâm Viên")
                        .latitude(11.9365).longitude(108.4412)
                        .durationMinutes(150).icon("fa-camera")
                        .note("Thưởng ngoạn không khí trong lành Đà Lạt")
                        .costEstimate(BigDecimal.ZERO)
                        .build());
                daySchedule.setTitle(String.format("Ngày %d: Nghỉ ngơi & Tham quan tự do", day));
            }

            // 11:30 — An trua (gan dia diem 1)
            String lunchName = lunchRest != null ? lunchRest.getName() : "Nhà hàng ẩm thực Đà Lạt";
            double lunchLat = lunchRest != null ? lunchRest.getLatitude() : centroidLat;
            double lunchLng = lunchRest != null ? lunchRest.getLongitude() : centroidLng;
            items.add(ScheduleItem.builder()
                    .time("11:30 - 13:00")
                    .activity("Ăn trưa: " + lunchName)
                    .locationName(lunchName)
                    .latitude(lunchLat).longitude(lunchLng)
                    .durationMinutes(90).icon("fa-utensils")
                    .note((lunchRest != null && lunchRest.getAddress() != null) ? lunchRest.getAddress() : "Thưởng thức đặc sản vùng miền")
                    .costEstimate(lunchRest != null && lunchRest.getAveragePricePerPerson() != null ? lunchRest.getAveragePricePerPerson() : BigDecimal.valueOf(foodCostPerDay))
                    .build());

            // 13:00 — Dia diem 2 (neu co)
            if (group.size() >= 2) {
                TouristPlace p2 = group.get(1);
                boolean isRest2 = p2.getId() != null && p2.getId().startsWith("R_");
                boolean isHotel2 = p2.getId() != null && p2.getId().startsWith("H_");
                String activity2 = isRest2 ? "Ghé quán & Trải nghiệm ẩm thực: " + p2.getName()
                                 : isHotel2 ? "Nghỉ dưỡng & Check-in: " + p2.getName()
                                 : "Tham quan & Khám phá: " + p2.getName();
                String ticket2 = p2.getTicketPrice() != null && p2.getTicketPrice().compareTo(BigDecimal.ZERO) > 0
                        ? String.format("Vé vào cổng: %,.0f VNĐ/người", p2.getTicketPrice().doubleValue())
                        : "Miễn phí";
                items.add(ScheduleItem.builder()
                        .time("13:00 - 16:30")
                        .activity(activity2)
                        .locationName(p2.getName())
                        .latitude(p2.getLatitude()).longitude(p2.getLongitude())
                        .durationMinutes(p2.getAvgVisitDurationMin() != null ? p2.getAvgVisitDurationMin() : 120)
                        .icon(isRest2 ? "fa-utensils" : (isHotel2 ? "fa-hotel" : "fa-map-marker-alt"))
                        .note((p2.getDescription() != null ? p2.getDescription() : "Khám phá cảnh đẹp Đà Lạt") + " | " + ticket2)
                        .costEstimate(p2.getTicketPrice() != null ? p2.getTicketPrice() : BigDecimal.valueOf(20_000))
                        .build());
            } else {
                items.add(ScheduleItem.builder()
                        .time("13:00 - 16:30")
                        .activity("Trà chiều & Ngắm hoàng hôn tại Quán Cà phê Đồi Cỏ")
                        .locationName("Quán Cà Phê Đồi Cỏ Ngắm Hoàng Hôn")
                        .latitude(11.9567).longitude(108.4712)
                        .durationMinutes(150).icon("fa-sun")
                        .note("Ngắm toàn cảnh thành phố sương mờ buổi chiều")
                        .costEstimate(BigDecimal.valueOf(60_000))
                        .build());
            }

            // 17:00 — An toi + Cho Dem
            String dinnerName = dinnerRest != null ? dinnerRest.getName() : "Quán ăn đặc sản tối Đà Lạt";
            double dinnerLat = dinnerRest != null ? dinnerRest.getLatitude() : 11.9404;
            double dinnerLng = dinnerRest != null ? dinnerRest.getLongitude() : 108.4383;
            items.add(ScheduleItem.builder()
                    .time("17:00 - 18:30")
                    .activity("Ăn tối đặc sản: " + dinnerName)
                    .locationName(dinnerName)
                    .latitude(dinnerLat).longitude(dinnerLng)
                    .durationMinutes(90).icon("fa-utensils")
                    .note((dinnerRest != null && dinnerRest.getAddress() != null) ? dinnerRest.getAddress() : "Không gian ấm cúng đêm cao nguyên")
                    .costEstimate(dinnerRest != null && dinnerRest.getAveragePricePerPerson() != null ? dinnerRest.getAveragePricePerPerson() : BigDecimal.valueOf(foodCostPerDay))
                    .build());

            items.add(ScheduleItem.builder()
                    .time("18:30 - 21:00")
                    .activity("Khám phá Chợ Đêm Đà Lạt & Ẩm thực đường phố")
                    .locationName("Chợ Đêm Đà Lạt")
                    .latitude(11.9416).longitude(108.4375)
                    .durationMinutes(150).icon("fa-moon")
                    .note("Bánh tráng nướng, sữa đậu nành nóng, mua đặc sản làm quà")
                    .costEstimate(BigDecimal.valueOf(50_000))
                    .build());

            // Chi phi di chuyen ngay nay
            items.add(ScheduleItem.builder()
                    .time("Tổng kết")
                    .activity(String.format("Di chuyển ngày %d: %.1f km — Chi phí xe: %,.0f VNĐ", day, dayKm, dayTransportTotal))
                    .locationName("Chi phí di chuyển")
                    .durationMinutes(0).icon("fa-car")
                    .note(String.format("Thuê xe: %,.0f VNĐ/ngày + Xăng phí: %,.0f VNĐ (%.1f km × %s/km)",
                            rentalPerDay, dayFuel, dayKm,
                            tierType.equals("tiet_kiem") ? "~800đ" : tierType.equals("can_bang") ? "~2,500đ" : "~3,000đ"))
                    .costEstimate(BigDecimal.valueOf(dayTransportTotal).setScale(0, RoundingMode.HALF_UP))
                    .build());

            daySchedule.setItems(items);
            schedules.add(daySchedule);
        }

        return schedules;
    }

    private com.example.dalattravel.model.Hotel findNearestHotel(List<com.example.dalattravel.model.Hotel> hotels, double lat, double lng, String tierType) {
        if (hotels == null || hotels.isEmpty()) return null;
        return hotels.stream()
                .min(Comparator.comparingDouble(h -> haversineDistance(lat, lng, h.getLatitude(), h.getLongitude())))
                .orElse(hotels.get(0));
    }

    private com.example.dalattravel.model.Restaurant findNearestRestaurant(List<com.example.dalattravel.model.Restaurant> restaurants, double lat, double lng, String tierType) {
        if (restaurants == null || restaurants.isEmpty()) return null;
        return restaurants.stream()
                .min(Comparator.comparingDouble(r -> haversineDistance(lat, lng, r.getLatitude(), r.getLongitude())))
                .orElse(restaurants.get(0));
    }

    // ============================================================
    //  TSP: Nearest Neighbor Heuristic
    // ============================================================
    private List<TouristPlace> solveTSP(double startLat, double startLng, List<TouristPlace> places) {
        if (places == null || places.isEmpty()) return new ArrayList<>();
        List<TouristPlace> remaining = new ArrayList<>(places);
        List<TouristPlace> result = new ArrayList<>();
        double currLat = startLat, currLng = startLng;

        while (!remaining.isEmpty()) {
            final double cLat = currLat, cLng = currLng;
            TouristPlace nearest = remaining.stream()
                    .min(Comparator.comparingDouble(p -> haversineDistance(cLat, cLng, p.getLatitude(), p.getLongitude())))
                    .orElse(remaining.get(0));
            result.add(nearest);
            remaining.remove(nearest);
            currLat = nearest.getLatitude();
            currLng = nearest.getLongitude();
        }
        return result;
    }

    // ============================================================
    //  NHOM DIA DIEM THEO NGAY (moi ngay toi da 2 dia diem)
    // ============================================================
    private List<List<TouristPlace>> groupByDay(List<TouristPlace> orderedPlaces, int days) {
        List<List<TouristPlace>> groups = new ArrayList<>();
        int placesPerDay = orderedPlaces.isEmpty() ? 0 : (int) Math.ceil((double) orderedPlaces.size() / days);
        if (placesPerDay < 1) placesPerDay = 1;
        if (placesPerDay > 2) placesPerDay = 2; // toi da 2 dia diem/ngay theo khung gio

        int idx = 0;
        for (int d = 0; d < days; d++) {
            List<TouristPlace> group = new ArrayList<>();
            for (int p = 0; p < placesPerDay && idx < orderedPlaces.size(); p++, idx++) {
                group.add(orderedPlaces.get(idx));
            }
            groups.add(group);
        }
        return groups;
    }

    // ============================================================
    //  DANH DẤU PHUONG AN PHU HOP NHAT
    // ============================================================
    private void markBestRecommendation(BigDecimal userBudget, PlanTier tietKiem, PlanTier canBang, PlanTier caoCap) {
        PlanTier best = tietKiem;
        if (canBang.getTotalCost().compareTo(userBudget) <= 0) best = canBang;
        if (caoCap.getTotalCost().compareTo(userBudget) <= 0) best = caoCap;

        best.setRecommended(true);
        best.setRecommendationNote(String.format("Phu hop nhat voi ngan sach %,.0fd cua ban!", userBudget.doubleValue()));

        for (PlanTier tier : Arrays.asList(tietKiem, canBang, caoCap)) {
            if (tier != best) {
                tier.setRecommendationNote(tier.getTotalCost().compareTo(userBudget) > 0
                        ? String.format("Chi phi %,.0fd vuot ngan sach du kien.", tier.getTotalCost().doubleValue())
                        : String.format("Tiet kiem them %,.0fd so voi ngan sach.", userBudget.subtract(tier.getTotalCost()).doubleValue()));
            }
        }
    }

    // ============================================================
    //  HAVERSINE DISTANCE
    // ============================================================
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
