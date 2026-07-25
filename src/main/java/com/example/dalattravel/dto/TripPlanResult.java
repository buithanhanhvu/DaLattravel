package com.example.dalattravel.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripPlanResult {

    private BigDecimal userBudget;
    private int requestedDays;
    private String startLocationName;
    private double startLatitude;
    private double startLongitude;
    private String budgetWarning; // Canh bao neu ngan sach khong du

    @Builder.Default
    private List<PlanTier> plans = new ArrayList<>();

    // ============================================================
    //  PLAN TIER
    // ============================================================
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlanTier {
        private String tierType; // "tiet_kiem", "can_bang", "cao_cap"
        private String tierTitle;
        private String badgeClass;
        private String transportName;
        private String accommodationType;

        // 5 khoản mục chi phí
        private BigDecimal totalCost;
        private BigDecimal transportCost;      // thue xe + xang
        private BigDecimal foodCost;           // an uong toan chuyen
        private BigDecimal ticketCost;         // ve tham quan
        private BigDecimal accommodationCost;  // khach san
        private BigDecimal contingencyCost;    // du phong 10%

        private double totalDistanceKm;
        private boolean isRecommended;
        private String recommendationNote;

        @Builder.Default
        private List<DaySchedule> days = new ArrayList<>();

        @Builder.Default
        private List<Waypoint> routeWaypoints = new ArrayList<>();
    }

    // ============================================================
    //  DAY SCHEDULE — them chi phi xe tung ngay
    // ============================================================
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DaySchedule {
        private int dayNumber;
        private String title;
        private double dailyKm;                    // km di chuyen trong ngay
        private BigDecimal dailyTransportCost;     // chi phi xe ngay do (thue + xang)
        private BigDecimal dailyFoodCost;          // chi phi an uong 3 bua

        @Builder.Default
        private List<ScheduleItem> items = new ArrayList<>();
    }

    // ============================================================
    //  SCHEDULE ITEM — them costEstimate
    // ============================================================
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScheduleItem {
        private String time;
        private String activity;
        private String locationName;
        private Double latitude;
        private Double longitude;
        private Integer durationMinutes;
        private BigDecimal costEstimate;    // chi phi uoc tinh cua hoat dong nay
        private String note;
        private String icon; // "fa-car", "fa-camera", "fa-utensils", "fa-bed", "fa-coffee"
    }

    // ============================================================
    //  WAYPOINT
    // ============================================================
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Waypoint {
        private int stepNumber;
        private String name;
        private double latitude;
        private double longitude;
        private String type; // "START", "DESTINATION"
    }
}
