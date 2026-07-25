package com.example.dalattravel.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tourist_places")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TouristPlace {

    @Id
    @Column(length = 6, nullable = false)
    private String id;

    @Column(length = 100, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @Column(name = "region_id", insertable = false, updatable = false)
    private Integer regionId;

    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "tourist_place_images", joinColumns = @JoinColumn(name = "tourist_place_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "category_id", insertable = false, updatable = false)
    private Integer categoryId;

    private double latitude;
    private double longitude;

    @Column(columnDefinition = "TEXT")
    private String reviewContent;

    private int rating;

    private java.math.BigDecimal ticketPrice;
    private Integer avgVisitDurationMin;

    @OneToMany(mappedBy = "touristPlace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "touristPlace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Hotel> hotels = new ArrayList<>();

    @OneToMany(mappedBy = "touristPlace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Restaurant> restaurants = new ArrayList<>();
}
