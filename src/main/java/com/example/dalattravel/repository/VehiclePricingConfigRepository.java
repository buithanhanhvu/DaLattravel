package com.example.dalattravel.repository;

import com.example.dalattravel.model.VehiclePricingConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VehiclePricingConfigRepository extends JpaRepository<VehiclePricingConfig, Integer> {
    Optional<VehiclePricingConfig> findBySeatCapacityAndActiveTrue(int seatCapacity);
}
