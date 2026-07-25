package com.example.DaLattravel.repository;

import com.example.DaLattravel.model.PendingCarpoolRequest;
import com.example.DaLattravel.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PendingCarpoolRequestRepository extends JpaRepository<PendingCarpoolRequest, Integer> {
    List<PendingCarpoolRequest> findByStatus(RequestStatus status);
}
