package com.example.dalattravel.repository;

import com.example.dalattravel.model.CompletedTripPassenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompletedTripPassengerRepository extends JpaRepository<CompletedTripPassenger, Integer> {
    List<CompletedTripPassenger> findByCompletedTripId(Integer completedTripId);
}
