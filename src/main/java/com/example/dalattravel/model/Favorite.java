package com.example.dalattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @Column(length = 6, nullable = false)
    private String touristPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "touristPlaceId", insertable = false, updatable = false)
    private TouristPlace touristPlace;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
