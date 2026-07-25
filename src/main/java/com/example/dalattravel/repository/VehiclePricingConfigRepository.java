package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.VehiclePricingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VehiclePricingConfigRepository extends JpaRepository<VehiclePricingConfig, Integer> {
    Optional<VehiclePricingConfig> findBySeatCapacityAndActiveTrue(int seatCapacity);
}
