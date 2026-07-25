package com.example.dalattravel.repository;

import com.example.dalattravel.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    List<Passenger> findByMatchedVehicleIdAndMatchedTrue(Integer matchedVehicleId);
    List<Passenger> findByMatchedFalse();
}
