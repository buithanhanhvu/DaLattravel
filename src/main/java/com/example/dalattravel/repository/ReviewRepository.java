package com.example.dalattravel.repository;

import com.example.dalattravel.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByTouristPlaceId(String touristPlaceId);
}
