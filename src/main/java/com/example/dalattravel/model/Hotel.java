package com.example.dalattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "hotels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 300)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(precision = 18, scale = 2)
    private BigDecimal pricePerNight;

    private double latitude;
    private double longitude;

    @Column(length = 300)
    private String imageUrl;

    @Column(length = 6)
    private String touristPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "touristPlaceId", insertable = false, updatable = false)
    private TouristPlace touristPlace;
}
