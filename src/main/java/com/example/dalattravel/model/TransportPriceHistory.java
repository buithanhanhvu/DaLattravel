package com.example.DaLattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transport_price_histories", uniqueConstraints = {
    @UniqueConstraint(name = "IX_TransportPrice_LocationTransport", columnNames = {"legacyLocationId", "transportOptionId"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer legacyLocationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legacyLocationId", insertable = false, updatable = false)
    private LegacyLocation legacyLocation;

    @Column(nullable = false)
    private Integer transportOptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transportOptionId", insertable = false, updatable = false)
    private TransportOption transportOption;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal price;

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
