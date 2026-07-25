package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.LegacyLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LegacyLocationRepository extends JpaRepository<LegacyLocation, Integer> {
    List<LegacyLocation> findByIsActiveTrue();
}
