package com.example.dalattravel.repository;

import com.example.dalattravel.model.HotelBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {
    List<HotelBooking> findAllByOrderByCreatedAtDesc();
    Optional<HotelBooking> findByBookingCode(String bookingCode);
    List<HotelBooking> findByPhoneNumber(String phoneNumber);
}
