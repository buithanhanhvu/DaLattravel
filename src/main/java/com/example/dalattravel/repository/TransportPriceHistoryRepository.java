package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.TransportPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TransportPriceHistoryRepository extends JpaRepository<TransportPriceHistory, Integer> {
    Optional<TransportPriceHistory> findByLegacyLocationIdAndTransportOptionId(Integer legacyLocationId, Integer transportOptionId);
}
