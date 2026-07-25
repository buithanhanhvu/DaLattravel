package com.example.dalattravel.repository;

import com.example.dalattravel.model.TransportPriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TransportPriceHistoryRepository extends JpaRepository<TransportPriceHistory, Integer> {
    Optional<TransportPriceHistory> findByLegacyLocationIdAndTransportOptionId(Integer legacyLocationId, Integer transportOptionId);
}
