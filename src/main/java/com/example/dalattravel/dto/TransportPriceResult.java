package com.example.dalattravel.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportPriceResult {

    private BigDecimal price;

    @Builder.Default
    private String priceType = "Calculated";

    private String locationName;
    private Integer locationId;
    private String oldLocationName;
    private Double distanceFromLocation;
    private double distanceToDalat;

    @Builder.Default
    private String note = "";

    private boolean isMergedLocation;
}
