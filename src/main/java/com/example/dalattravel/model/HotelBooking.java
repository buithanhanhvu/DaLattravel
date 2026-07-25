package com.example.dalattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, unique = true)
    private String bookingCode;

    @Column(nullable = false, length = 100)
    private String customerName;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String email;

    @Column(nullable = false)
    private Integer hotelId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotelId", insertable = false, updatable = false)
    private Hotel hotel;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;

    @Column(precision = 18, scale = 2)
    private BigDecimal totalPrice;

    @Builder.Default
    @Column(length = 20)
    private String status = "PENDING"; // PENDING, CONFIRMED, CANCELLED

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
