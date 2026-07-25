package com.example.DaLattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transport_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String type; // "Public", "Private", "SelfDrive"

    private boolean isSelfDrive;

    private double fuelConsumption; // Số lít / 100km

    @Column(precision = 18, scale = 2)
    private BigDecimal fuelPrice; // Giá nhiên liệu đ/lít

    @Column(precision = 18, scale = 2)
    private BigDecimal basePrice;

    @OneToMany(mappedBy = "transportOption", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TransportPriceHistory> priceHistories = new ArrayList<>();
}
