package com.example.dalattravel.repository;

import com.example.dalattravel.model.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Integer> {
    List<Attraction> findByTouristPlaceId(String touristPlaceId);
}
