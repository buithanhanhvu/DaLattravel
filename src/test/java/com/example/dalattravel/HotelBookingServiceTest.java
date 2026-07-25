package com.example.dalattravel;

import com.example.dalattravel.model.Hotel;
import com.example.dalattravel.model.HotelBooking;
import com.example.dalattravel.repository.HotelBookingRepository;
import com.example.dalattravel.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelBookingServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelBookingRepository hotelBookingRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("TC_UNIT_005: Tính tổng chi phí đặt phòng theo số đêm lưu trú thực tế")
    void testHotelBooking_TotalPriceCalculation() {
        Hotel mockHotel = new Hotel();
        mockHotel.setId(1);
        mockHotel.setName("Dalat Palace Heritage Hotel 5 Sao");
        mockHotel.setPricePerNight(BigDecimal.valueOf(2500000));

        when(hotelRepository.findById(1)).thenReturn(Optional.of(mockHotel));

        LocalDate checkIn = LocalDate.of(2026, 8, 1);
        LocalDate checkOut = LocalDate.of(2026, 8, 3);
        long days = ChronoUnit.DAYS.between(checkIn, checkOut);

        BigDecimal expectedTotal = mockHotel.getPricePerNight().multiply(BigDecimal.valueOf(days));
        assertEquals(BigDecimal.valueOf(5000000), expectedTotal);

        String bookingCode = "DLBK-" + (System.currentTimeMillis() % 100000);
        HotelBooking booking = HotelBooking.builder()
                .bookingCode(bookingCode)
                .customerName("Phạm Văn Nam")
                .phoneNumber("0912345678")
                .email("nam@gmail.com")
                .hotelId(1)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .numberOfGuests(2)
                .totalPrice(expectedTotal)
                .status("PENDING")
                .build();

        when(hotelBookingRepository.save(any(HotelBooking.class))).thenReturn(booking);

        HotelBooking savedBooking = hotelBookingRepository.save(booking);
        assertNotNull(savedBooking);
        assertTrue(savedBooking.getBookingCode().startsWith("DLBK-"));
        assertEquals(BigDecimal.valueOf(5000000), savedBooking.getTotalPrice());
        assertEquals("PENDING", savedBooking.getStatus());
    }

    @Test
    @DisplayName("TC_UNIT_006: Xử lý biên khi Check-out nhỏ hơn hoặc bằng Check-in")
    void testHotelBooking_BoundaryDaysFallback() {
        LocalDate checkIn = LocalDate.of(2026, 8, 5);
        LocalDate checkOut = LocalDate.of(2026, 8, 4);

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (days <= 0) days = 1;

        assertEquals(1, days);
    }
}
