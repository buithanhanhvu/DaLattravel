package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.TransportOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportOptionRepository extends JpaRepository<TransportOption, Integer> {
}
