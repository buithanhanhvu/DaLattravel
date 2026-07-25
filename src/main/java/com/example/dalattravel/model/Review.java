package com.example.dalattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 6, nullable = false)
    private String touristPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "touristPlaceId", insertable = false, updatable = false)
    private TouristPlace touristPlace;

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
