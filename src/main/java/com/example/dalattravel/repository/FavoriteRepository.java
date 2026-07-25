package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUserId(String userId);
    boolean existsByUserIdAndTouristPlaceId(String userId, String touristPlaceId);
    void deleteByUserIdAndTouristPlaceId(String userId, String touristPlaceId);
}
