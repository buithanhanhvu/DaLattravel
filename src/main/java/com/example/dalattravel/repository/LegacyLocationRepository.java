package com.example.dalattravel.repository;

import com.example.dalattravel.model.LegacyLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LegacyLocationRepository extends JpaRepository<LegacyLocation, Integer> {
    List<LegacyLocation> findByIsActiveTrue();
}
