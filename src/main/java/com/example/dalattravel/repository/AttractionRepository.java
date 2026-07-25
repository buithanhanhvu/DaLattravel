package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Integer> {
    List<Attraction> findByTouristPlaceId(String touristPlaceId);
}
