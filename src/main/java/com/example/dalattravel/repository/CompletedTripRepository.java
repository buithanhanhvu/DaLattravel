package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.CompletedTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedTripRepository extends JpaRepository<CompletedTrip, Integer> {
}
