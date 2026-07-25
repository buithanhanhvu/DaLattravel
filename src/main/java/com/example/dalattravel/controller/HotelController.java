package com.example.dalattravel.controller;

import com.example.dalattravel.model.Hotel;
import com.example.dalattravel.repository.HotelRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelRepository hotelRepository;
    private final com.example.dalattravel.repository.HotelBookingRepository hotelBookingRepository;

    public HotelController(
            HotelRepository hotelRepository,
            com.example.dalattravel.repository.HotelBookingRepository hotelBookingRepository) {
        this.hotelRepository = hotelRepository;
        this.hotelBookingRepository = hotelBookingRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("hotels", hotelRepository.findAll());
        return "hotels/index";
    }

    @PostMapping("/book")
    public String bookHotel(
            @RequestParam Integer hotelId,
            @RequestParam String customerName,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate,
            @RequestParam(defaultValue = "1") Integer numberOfGuests,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        Hotel hotel = hotelRepository.findById(hotelId).orElse(null);
        if (hotel == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin khách sạn!");
            return "redirect:/hotels";
        }

        java.time.LocalDate checkIn = java.time.LocalDate.parse(checkInDate);
        java.time.LocalDate checkOut = java.time.LocalDate.parse(checkOutDate);
        long days = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        if (days <= 0) days = 1;

        java.math.BigDecimal total = hotel.getPricePerNight().multiply(java.math.BigDecimal.valueOf(days));
        String bookingCode = "DLBK-" + (System.currentTimeMillis() % 100000);

        com.example.dalattravel.model.HotelBooking booking = com.example.dalattravel.model.HotelBooking.builder()
                .bookingCode(bookingCode)
                .customerName(customerName)
                .phoneNumber(phoneNumber)
                .email(email)
                .hotelId(hotelId)
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .numberOfGuests(numberOfGuests)
                .totalPrice(total)
                .status("PENDING")
                .build();

        hotelBookingRepository.save(booking);

        redirectAttributes.addFlashAttribute("bookingSuccess", true);
        redirectAttributes.addFlashAttribute("bookingCode", bookingCode);
        redirectAttributes.addFlashAttribute("hotelName", hotel.getName());
        redirectAttributes.addFlashAttribute("totalPrice", total);
        redirectAttributes.addFlashAttribute("successMessage", "Đặt phòng thành công! Mã đơn của bạn: " + bookingCode);

        return "redirect:/hotels";
    }
}
