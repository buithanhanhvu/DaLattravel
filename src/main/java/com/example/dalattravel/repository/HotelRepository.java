package com.example.dalattravel.repository;

import com.example.dalattravel.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {
    List<Hotel> findByTouristPlaceId(String touristPlaceId);
}
