package com.example.DaLattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "legacy_locations", indexes = {
    @Index(name = "IX_LegacyLocation_GPS", columnList = "latitude, longitude"),
    @Index(name = "IX_LegacyLocation_IsActive", columnList = "isActive"),
    @Index(name = "IX_LegacyLocation_IsMerged", columnList = "isMergedLocation")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegacyLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String currentName;

    @Column(length = 100)
    private String oldName;

    private boolean isMergedLocation;

    @Column(columnDefinition = "TEXT")
    private String mergeNote;

    private double latitude;
    private double longitude;

    @Builder.Default
    private boolean isActive = true;

    @OneToMany(mappedBy = "legacyLocation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TransportPriceHistory> priceHistories = new ArrayList<>();
}
