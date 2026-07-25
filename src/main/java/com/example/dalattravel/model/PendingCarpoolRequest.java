package com.example.dalattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pending_carpool_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingCarpoolRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String passengerName;

    @Column(length = 20)
    private String phoneNumber;

    private double pickupLatitude;
    private double pickupLongitude;

    @Column(length = 200)
    private String pickupAddress;

    private double dropoffLatitude;
    private double dropoffLongitude;

    @Column(length = 200)
    private String dropoffAddress;

    @Column(nullable = false)
    private LocalDateTime preferredDepartureTime;

    private LocalDateTime preferredArrivalTime;

    @Builder.Default
    private int requiredSeats = 1;

    @Builder.Default
    private int requestedVehicleSeats = 4;

    private boolean isGroup;
    private boolean privateGroup;

    private Integer groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId", insertable = false, updatable = false)
    private PassengerGroup group;

    @Column(precision = 18, scale = 2)
    private BigDecimal estimatedCost;

    @Column(precision = 18, scale = 2)
    private BigDecimal costPerSeat;

    private Double estimatedDistance;

    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    private Integer matchedVehicleId;
    private Integer passengerId;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime matchedAt;
    private LocalDateTime cancelledAt;

    private String cancellationReason;
}
