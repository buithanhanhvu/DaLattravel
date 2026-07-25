package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    List<Hotel> findByTouristPlaceId(String touristPlaceId);
}
