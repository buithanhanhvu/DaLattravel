package com.example.DaLattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "attractions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(precision = 18, scale = 2)
    private BigDecimal ticketPrice;

    @Column(length = 6, nullable = false)
    private String touristPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "touristPlaceId", insertable = false, updatable = false)
    private TouristPlace touristPlace;
}
