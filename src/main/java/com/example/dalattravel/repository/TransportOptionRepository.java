package com.example.dalattravel.repository;

import com.example.dalattravel.model.TransportOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransportOptionRepository extends JpaRepository<TransportOption, Integer> {
}
