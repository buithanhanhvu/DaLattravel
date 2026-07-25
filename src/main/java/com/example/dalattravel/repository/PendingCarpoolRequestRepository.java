package com.example.dalattravel.repository;

import com.example.dalattravel.model.PendingCarpoolRequest;
import com.example.dalattravel.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PendingCarpoolRequestRepository extends JpaRepository<PendingCarpoolRequest, Integer> {
    List<PendingCarpoolRequest> findByStatus(RequestStatus status);
}
