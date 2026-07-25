package com.example.dalattravel.repository;

import com.example.dalattravel.model.CompletedTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedTripRepository extends JpaRepository<CompletedTrip, Integer> {
}
