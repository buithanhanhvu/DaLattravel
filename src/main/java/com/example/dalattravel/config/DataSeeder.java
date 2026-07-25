package com.example.DaLattravel.config;

import com.example.DaLattravel.model.*;
import com.example.DaLattravel.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(
            CategoryRepository categoryRepository,
            RegionRepository regionRepository,
            TouristPlaceRepository touristPlaceRepository,
            TransportOptionRepository transportOptionRepository,
            VehicleRepository vehicleRepository,
            HotelRepository hotelRepository,
            RestaurantRepository restaurantRepository,
            BlogPostRepository blogPostRepository) {
        return args -> {
            if (categoryRepository.count() == 0) {
                Category cat1 = categoryRepository.save(Category.builder().name("Khách sạn").build());
                Category cat2 = categoryRepository.save(Category.builder().name("Nhà hàng/Quán ăn").build());
                Category cat3 = categoryRepository.save(Category.builder().name("Địa điểm du lịch").build());

                Region r1 = regionRepository.save(Region.builder().name("Trung tâm TP. Đà Lạt").build());
                Region r2 = regionRepository.save(Region.builder().name("Hồ Tuyền Lâm & Cáp treo").build());
                Region r3 = regionRepository.save(Region.builder().name("Thung lũng Tình Yêu").build());

                TouristPlace tp1 = touristPlaceRepository.save(TouristPlace.builder()
                        .id("TP0001")
                        .name("Hồ Xuân Hương")
                        .category(cat3)
                        .region(r1)
                        .latitude(11.9404)
                        .longitude(108.4583)
                        .description("Hồ nước ngọt xinh đẹp nằm giữa trung tâm thành phố Đà Lạt.")
                        .rating(5)
                        .build());

                TouristPlace tp2 = touristPlaceRepository.save(TouristPlace.builder()
                        .id("TP0002")
                        .name("Hồ Tuyền Lâm")
                        .category(cat3)
                        .region(r2)
                        .latitude(11.8961)
                        .longitude(108.4239)
                        .description("Hồ nước ngọt rộng nhất Đà Lạt với cảnh quan thiên nhiên mộng mơ.")
                        .rating(5)
                        .build());

                TouristPlace tp3 = touristPlaceRepository.save(TouristPlace.builder()
                        .id("TP0003")
                        .name("Thung Lũng Tình Yêu")
                        .category(cat3)
                        .region(r3)
                        .latitude(11.9796)
                        .longitude(108.4503)
                        .description("Địa điểm du lịch lãng mạn nổi tiếng cho các cặp đôi và gia đình.")
                        .rating(4)
                        .build());

                TransportOption to1 = transportOptionRepository.save(TransportOption.builder()
                        .name("Xe giường nằm")
                        .type("Public")
                        .isSelfDrive(false)
                        .basePrice(BigDecimal.valueOf(250000))
                        .build());

                TransportOption to2 = transportOptionRepository.save(TransportOption.builder()
                        .name("Xe Limousine 9 chỗ")
                        .type("Private")
                        .isSelfDrive(false)
                        .basePrice(BigDecimal.valueOf(350000))
                        .build());

                TransportOption to3 = transportOptionRepository.save(TransportOption.builder()
                        .name("Ô tô cá nhân (Xăng)")
                        .type("SelfDrive")
                        .isSelfDrive(true)
                        .fuelConsumption(8.5)
                        .fuelPrice(BigDecimal.valueOf(24000))
                        .build());

                vehicleRepository.save(Vehicle.builder()
                        .driverName("Nguyễn Văn A")
                        .licensePlate("49A-123.45")
                        .pickupAddress("Bến xe Miền Đông, TP.HCM")
                        .pickupLatitude(10.8142)
                        .pickupLongitude(106.7118)
                        .dropoffAddress("Bến xe Đà Lạt, TP. Đà Lạt")
                        .dropoffLatitude(11.9360)
                        .dropoffLongitude(108.4447)
                        .departureTime(LocalDateTime.now().plusHours(2))
                        .totalSeats(4)
                        .availableSeats(3)
                        .costPerKm(BigDecimal.valueOf(15000))
                        .vehicleType("Xe 4 chỗ")
                        .active(true)
                        .build());

                vehicleRepository.save(Vehicle.builder()
                        .driverName("Trần Văn B")
                        .licensePlate("49B-678.90")
                        .pickupAddress("Sân bay Liên Khương, Lâm Đồng")
                        .pickupLatitude(11.7506)
                        .pickupLongitude(108.3742)
                        .dropoffAddress("Trung tâm TP. Đà Lạt")
                        .dropoffLatitude(11.9404)
                        .dropoffLongitude(108.4583)
                        .departureTime(LocalDateTime.now().plusHours(1))
                        .totalSeats(7)
                        .availableSeats(5)
                        .costPerKm(BigDecimal.valueOf(20000))
                        .vehicleType("Xe 7 chỗ")
                        .active(true)
                        .build());

                hotelRepository.save(Hotel.builder()
                        .name("Dalat Palace Heritage Hotel")
                        .address("1 Đường Trần Phú, Phường 3, Đà Lạt")
                        .phone("02633825444")
                        .pricePerNight(BigDecimal.valueOf(2500000))
                        .latitude(11.9365)
                        .longitude(108.4412)
                        .touristPlaceId("TP0001")
                        .build());

                restaurantRepository.save(Restaurant.builder()
                        .name("Lẩu Gà Lá É É É")
                        .address("5 Đường 3 Tháng 4, Đà Lạt")
                        .phone("0901234567")
                        .averagePricePerPerson(BigDecimal.valueOf(150000))
                        .touristPlaceId("TP0001")
                        .build());

                blogPostRepository.save(BlogPost.builder()
                        .title("Kinh nghiệm du lịch Đà Lạt tự túc 3 ngày 2 đêm")
                        .content("Đà Lạt luôn là điểm đến hấp dẫn du khách với khí hậu mát mẻ quanh năm và phong cảnh hữu tình...")
                        .author("Admin")
                        .build());
            }
        };
    }
}
