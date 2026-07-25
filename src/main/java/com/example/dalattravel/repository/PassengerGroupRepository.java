package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.PassengerGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerGroupRepository extends JpaRepository<PassengerGroup, Integer> {
}
