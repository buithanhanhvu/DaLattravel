package com.example.dalattravel.repository;

import com.example.dalattravel.model.PassengerGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerGroupRepository extends JpaRepository<PassengerGroup, Integer> {
}
