package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.TouristPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TouristPlaceRepository extends JpaRepository<TouristPlace, String> {
    List<TouristPlace> findByCategoryId(Integer categoryId);
    List<TouristPlace> findByRegionId(Integer regionId);
}
