package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FestivalRepository extends JpaRepository<Festival, Integer> {
    List<Festival> findByActiveTrue();
}
