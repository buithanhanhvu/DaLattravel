package com.example.dalattravel.controller;

import com.example.dalattravel.model.*;
import com.example.dalattravel.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final TouristPlaceRepository touristPlaceRepository;
    private final HotelRepository hotelRepository;
    private final RestaurantRepository restaurantRepository;
    private final HotelBookingRepository hotelBookingRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RegionRepository regionRepository;

    public AdminController(
            TouristPlaceRepository touristPlaceRepository,
            HotelRepository hotelRepository,
            RestaurantRepository restaurantRepository,
            HotelBookingRepository hotelBookingRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            RegionRepository regionRepository) {
        this.touristPlaceRepository = touristPlaceRepository;
        this.hotelRepository = hotelRepository;
        this.restaurantRepository = restaurantRepository;
        this.hotelBookingRepository = hotelBookingRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.regionRepository = regionRepository;
    }

    // ========= DASHBOARD OVERVIEW =========
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalPlaces", touristPlaceRepository.count());
        model.addAttribute("totalHotels", hotelRepository.count());
        model.addAttribute("totalRestaurants", restaurantRepository.count());
        model.addAttribute("totalBookings", hotelBookingRepository.count());
        model.addAttribute("totalUsers", userRepository.count());

        List<HotelBooking> recentBookings = hotelBookingRepository.findAllByOrderByCreatedAtDesc();
        if (recentBookings.size() > 5) recentBookings = recentBookings.subList(0, 5);
        model.addAttribute("recentBookings", recentBookings);

        return "admin/index";
    }

    // ========= HOTEL MANAGEMENT =========
    @GetMapping("/hotels")
    public String listHotels(Model model) {
        model.addAttribute("hotels", hotelRepository.findAll());
        return "admin/hotels";
    }

    @PostMapping("/hotels/save")
    public String saveHotel(
            @RequestParam(required = false) Integer id,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String phone,
            @RequestParam BigDecimal pricePerNight,
            @RequestParam(required = false) String imageUrl,
            RedirectAttributes redirectAttributes) {

        Hotel hotel = id != null ? hotelRepository.findById(id).orElse(new Hotel()) : new Hotel();
        hotel.setName(name);
        hotel.setAddress(address);
        hotel.setPhone(phone);
        hotel.setPricePerNight(pricePerNight);
        if (imageUrl != null && !imageUrl.isBlank()) {
            hotel.setImageUrl(imageUrl);
        } else if (hotel.getImageUrl() == null) {
            hotel.setImageUrl("https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=600&q=80");
        }

        hotelRepository.save(hotel);
        redirectAttributes.addFlashAttribute("successMessage", "Đã lưu thông tin khách sạn thành công!");
        return "redirect:/admin/hotels";
    }

    @PostMapping("/hotels/delete/{id}")
    public String deleteHotel(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        hotelRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa khách sạn khỏi hệ thống!");
        return "redirect:/admin/hotels";
    }

    // ========= TOURIST PLACE MANAGEMENT =========
    @GetMapping("/tourist-places")
    public String listPlaces(Model model) {
        model.addAttribute("places", touristPlaceRepository.findAll());
        return "admin/tourist-places";
    }

    @PostMapping("/tourist-places/save")
    public String savePlace(
            @RequestParam(required = false) String id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam BigDecimal ticketPrice,
            @RequestParam(defaultValue = "5") Integer rating,
            @RequestParam(required = false) String imageUrl,
            RedirectAttributes redirectAttributes) {

        TouristPlace place;
        if (id != null && !id.isBlank() && touristPlaceRepository.existsById(id)) {
            place = touristPlaceRepository.findById(id).get();
        } else {
            place = new TouristPlace();
            place.setId("TP" + (System.currentTimeMillis() % 1000));
        }
        place.setName(name);
        place.setDescription(description);
        place.setTicketPrice(ticketPrice);
        place.setRating(rating);
        if (imageUrl != null && !imageUrl.isBlank()) {
            place.setImageUrl(imageUrl);
        }

        touristPlaceRepository.save(place);
        redirectAttributes.addFlashAttribute("successMessage", "Đã lưu địa điểm du lịch thành công!");
        return "redirect:/admin/tourist-places";
    }

    @PostMapping("/tourist-places/delete/{id}")
    public String deletePlace(@PathVariable String id, RedirectAttributes redirectAttributes) {
        touristPlaceRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa địa điểm thành công!");
        return "redirect:/admin/tourist-places";
    }

    // ========= RESTAURANT MANAGEMENT =========
    @GetMapping("/restaurants")
    public String listRestaurants(Model model) {
        model.addAttribute("restaurants", restaurantRepository.findAll());
        return "admin/restaurants";
    }

    @PostMapping("/restaurants/save")
    public String saveRestaurant(
            @RequestParam(required = false) Integer id,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String phone,
            @RequestParam BigDecimal averagePricePerPerson,
            @RequestParam(required = false) String imageUrl,
            RedirectAttributes redirectAttributes) {

        Restaurant restaurant = id != null ? restaurantRepository.findById(id).orElse(new Restaurant()) : new Restaurant();
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setPhone(phone);
        restaurant.setAveragePricePerPerson(averagePricePerPerson);
        if (imageUrl != null && !imageUrl.isBlank()) {
            restaurant.setImageUrl(imageUrl);
        }

        restaurantRepository.save(restaurant);
        redirectAttributes.addFlashAttribute("successMessage", "Đã lưu thông tin nhà hàng thành công!");
        return "redirect:/admin/restaurants";
    }

    @PostMapping("/restaurants/delete/{id}")
    public String deleteRestaurant(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        restaurantRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa nhà hàng!");
        return "redirect:/admin/restaurants";
    }

    // ========= BOOKING MANAGEMENT =========
    @GetMapping("/bookings")
    public String listBookings(Model model) {
        model.addAttribute("bookings", hotelBookingRepository.findAllByOrderByCreatedAtDesc());
        return "admin/bookings";
    }

    @PostMapping("/bookings/update-status")
    public String updateBookingStatus(
            @RequestParam Long bookingId,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        HotelBooking booking = hotelBookingRepository.findById(bookingId).orElse(null);
        if (booking != null) {
            booking.setStatus(status);
            hotelBookingRepository.save(booking);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái đơn đặt phòng " + booking.getBookingCode() + " thành " + status);
        }
        return "redirect:/admin/bookings";
    }

    // ========= USER MANAGEMENT =========
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/users/update-role")
    public String updateUserRole(
            @RequestParam String userId,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setRole(role);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Đã phân quyền tài khoản " + user.getUsername() + " thành " + role);
        }
        return "redirect:/admin/users";
    }
}
