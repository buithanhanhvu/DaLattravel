package com.example.dalattravel.config;

import com.example.dalattravel.model.*;
import com.example.dalattravel.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
            BlogPostRepository blogPostRepository,
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            com.example.dalattravel.service.AuthService authService) {
        return args -> {
            // Seed Admin and User accounts if missing
            if (!userRepository.existsByUsername("admin")) {
                userRepository.save(com.example.dalattravel.model.User.builder()
                        .username("admin")
                        .email("admin@dalattravel.vn")
                        .password(authService.hashPassword("admin123"))
                        .fullName("Quản Trị Viên Hệ Thống")
                        .phoneNumber("0900000000")
                        .role("ADMIN")
                        .build());
            }

            if (!userRepository.existsByUsername("user")) {
                userRepository.save(com.example.dalattravel.model.User.builder()
                        .username("user")
                        .email("user@gmail.com")
                        .password(authService.hashPassword("user123"))
                        .fullName("Nguyễn Văn A")
                        .phoneNumber("0901234567")
                        .role("USER")
                        .build());
            }

            // Re-seed when data is incomplete or missing image URLs
            if (categoryRepository.count() < 3 || touristPlaceRepository.count() < 25 || hotelRepository.findAll().stream().anyMatch(h -> h.getImageUrl() == null)) {

                // Clear in FK-safe order: children first
                reviewRepository.deleteAll();
                restaurantRepository.deleteAll();
                hotelRepository.deleteAll();
                touristPlaceRepository.deleteAll();
                regionRepository.deleteAll();
                categoryRepository.deleteAll();
                transportOptionRepository.deleteAll();
                vehicleRepository.deleteAll();
                blogPostRepository.deleteAll();

                // ========= CATEGORIES =========
                Category catKhachSan = categoryRepository.save(Category.builder().name("Khách sạn").build());
                Category catNhaHang  = categoryRepository.save(Category.builder().name("Nhà hàng / Quán ăn").build());
                Category catDiaDiem  = categoryRepository.save(Category.builder().name("Địa điểm du lịch").build());

                // ========= REGIONS =========
                Region r1 = regionRepository.save(Region.builder().name("Trung tâm TP Đà Lạt").build());
                Region r2 = regionRepository.save(Region.builder().name("Hồ Tuyền Lâm - Cáp treo").build());
                Region r3 = regionRepository.save(Region.builder().name("Thung lũng Tình Yêu").build());
                Region r4 = regionRepository.save(Region.builder().name("Trại Mát - Cầu Đất").build());
                Region r5 = regionRepository.save(Region.builder().name("Langbiang - Lạc Dương").build());
                Region r6 = regionRepository.save(Region.builder().name("Đèo Prenn - Datanla").build());

                String imgLake = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=600&q=80";
                String imgForest = "https://images.unsplash.com/photo-1511497584788-8767611136f6?auto=format&fit=crop&w=600&q=80";
                String imgFlower = "https://images.unsplash.com/photo-1490750967868-88aa4486c946?auto=format&fit=crop&w=600&q=80";
                String imgWaterfall = "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?auto=format&fit=crop&w=600&q=80";
                String imgCoffee = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=600&q=80";
                String imgMarket = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=600&q=80";
                String imgHotel1 = "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=600&q=80";
                String imgHotel2 = "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=600&q=80";
                String imgFood1 = "https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&w=600&q=80";
                String imgFood2 = "https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=600&q=80";

                // ========= 50 TOURIST PLACES =========
                List<TouristPlace> places = Arrays.asList(
                    tp("TP001", "Hồ Xuân Hương",             r1, catDiaDiem, 11.9404, 108.4383, "Hồ nước ngọt xinh đẹp nằm giữa trung tâm thành phố Đà Lạt, biểu tượng lãng mạn nhất.", 5, 0, 60, imgLake),
                    tp("TP002", "Hồ Tuyền Lâm",               r2, catDiaDiem, 11.8961, 108.4239, "Hồ nước ngọt rộng nhất Đà Lạt với cảnh quan thiên nhiên mộng mơ và cáp treo dài nhất Đông Nam Á.", 5, 0, 90, imgLake),
                    tp("TP003", "Thung Lũng Tình Yêu",        r3, catDiaDiem, 11.9796, 108.4503, "Địa điểm du lịch lãng mạn nổi tiếng cho các cặp đôi và gia đình tại Đà Lạt.", 5, 250000, 120, imgFlower),
                    tp("TP004", "Đồi Chè Cầu Đất",            r4, catDiaDiem, 11.8542, 108.5521, "Đồi chè xanh bạt ngàn tầm mắt, địa điểm săn mây tuyệt đẹp vào sáng sớm Đà Lạt.", 5, 0, 120, imgForest),
                    tp("TP005", "Quảng Trường Lâm Viên",      r1, catDiaDiem, 11.9365, 108.4412, "Biểu tượng nụ hoa dã quỳ và bông hoa dã quỳ khổng lồ bên hồ Xuân Hương.", 5, 0, 45, imgFlower),
                    tp("TP006", "Chợ Đêm Đà Lạt",              r1, catDiaDiem, 11.9416, 108.4375, "Thiên đường ẩm thực đường phố và mua sắm quà lưu niệm Đà Lạt nhộn nhịp về đêm.", 5, 0, 90, imgMarket),
                    tp("TP007", "Vườn Hoa Thành Phố Đà Lạt",  r1, catDiaDiem, 11.9489, 108.4506, "Quy tụ hàng ngàn loài hoa muôn sắc màu rực rỡ tại trung tâm Đà Lạt.", 4, 100000, 90, imgFlower),
                    tp("TP008", "Thác Datanla",                r6, catDiaDiem, 11.9042, 108.4475, "Hệ thống máng trượt xuyên rừng dài nhất Đông Nam Á tại chân đèo Prenn.", 5, 50000, 120, imgWaterfall),
                    tp("TP009", "Thiền Viện Trúc Lâm",        r2, catDiaDiem, 11.9039, 108.4347, "Thiền viện thanh tĩnh trên đỉnh núi Phụng Hoàng ngắm toàn cảnh Hồ Tuyền Lâm.", 5, 0, 90, imgForest),
                    tp("TP010", "Đỉnh Langbiang",              r5, catDiaDiem, 12.0433, 108.4394, "Nóc nhà Đà Lạt với góc nhìn toàn cảnh thung lũng và suối Vàng suối Bạc hùng vĩ.", 5, 50000, 150, imgForest),
                    tp("TP011", "Ga Đà Lạt",                   r1, catDiaDiem, 11.9414, 108.4542, "Nhà ga xe lửa cổ đẹp nhất Việt Nam mang kiến trúc Pháp độc đáo.", 4, 10000, 45, imgHotel2),
                    tp("TP012", "Dinh Bảo Đại King Palace",   r1, catDiaDiem, 11.9333, 108.4639, "Dinh biệt thự cổ sang trọng của vua Bảo Đại nằm giữa rừng thông Đà Lạt.", 4, 90000, 90, imgHotel1),
                    tp("TP013", "Dinh 3 Bảo Đại",              r1, catDiaDiem, 11.9306, 108.4300, "Nơi ở và làm việc của gia đình vị vua cuối cùng triều Nguyễn tại Đà Lạt.", 4, 40000, 60, imgHotel2),
                    tp("TP014", "Nhà Thờ Con Gà Đà Lạt",      r1, catDiaDiem, 11.9372, 108.4372, "Nhà thờ Chánh Tòa Đà Lạt kiến trúc Roman cổ kính, biểu tượng thành phố.", 4, 0, 45, imgHotel1),
                    tp("TP015", "Chùa Linh Phước Ve Chai",    r4, catDiaDiem, 11.9442, 108.4989, "Ngôi chùa khảm sành sứ độc đáo đạt nhiều kỷ lục Việt Nam.", 5, 0, 60, imgFlower),
                    tp("TP016", "Cà Phê Đồi Cỏ Hoàng Hôn",    r1, catDiaDiem, 11.9567, 108.4712, "Quán cà phê đồi cúc họa mi ngắm thung lũng lồng kính đêm rực rỡ.", 5, 60000, 90, imgCoffee),
                    tp("TP017", "Thung Lũng Vàng",             r5, catDiaDiem, 11.9875, 108.3833, "Cảnh quan đồi thông suối nhỏ và hồ nước thơ mộng ngoài ngoại ô.", 4, 70000, 120, imgForest),
                    tp("TP018", "Làng Cù Lần",                 r5, catDiaDiem, 12.0125, 108.3583, "Ngôi làng nhỏ xinh nằm lọt thỏm giữa thung lũng rừng nguyên sinh Đà Lạt.", 4, 100000, 150, imgForest),
                    tp("TP019", "Đồi Mộng Mơ",                 r3, catDiaDiem, 11.9767, 108.4489, "Khu du lịch sinh thái kết hợp văn hóa Tây Nguyên độc đáo.", 4, 100000, 90, imgFlower),
                    tp("TP020", "Cánh Đồng Hoa Cẩm Tú Cầu",   r4, catDiaDiem, 11.9217, 108.5139, "Cánh đồng hoa cẩm tú cầu rộng lớn bạt ngàn màu sắc tại Đà Lạt.", 5, 50000, 90, imgFlower),
                    tp("TP021", "Nông Trại Cún Puppy Farm",   r1, catDiaDiem, 11.9611, 108.4028, "Nông trại nuôi hàng trăm chú cún đáng yêu và vườn dâu công nghệ cao.", 5, 100000, 120, imgFlower),
                    tp("TP022", "Fresh Garden Đà Lạt",         r1, catDiaDiem, 11.9542, 108.4111, "Ngọn đồi hoa ngập tràn màu sắc và mô hình cối xay gió khổng lồ.", 4, 120000, 90, imgFlower),
                    tp("TP023", "Thác Voi Đà Lạt",             r5, catDiaDiem, 11.8242, 108.2678, "Dòng thác hùng vĩ hoang sơ bậc nhất Lâm Đồng.", 4, 30000, 90, imgWaterfall),
                    tp("TP024", "Thác Prenn",                  r6, catDiaDiem, 11.8792, 108.4619, "Thác nước êm đềm ở cửa ngõ vào thành phố Đà Lạt.", 4, 50000, 90, imgWaterfall),
                    tp("TP025", "Vườn Dâu Tây Biofresh",      r3, catDiaDiem, 11.9750, 108.4517, "Trải nghiệm tự tay hái dâu tây Pháp và New Zealand tươi ngon tại Đà Lạt.", 4, 30000, 60, imgFlower),
                    tp("TP026", "Hồ Đankia Suối Vàng",       r5, catDiaDiem, 12.0317, 108.4072, "Hồ nước thanh bình yên tĩnh giữa rừng thông và đồi núi xanh tươi.", 4, 0, 60, imgLake),
                    tp("TP027", "Vườn Thú Zoodoo Đà Lạt",     r5, catDiaDiem, 12.1122, 108.5865, "Công viên thú thân thiện với thiên nhiên trẻ em vui chơi thoải mái.", 4, 150000, 120, imgForest),
                    tp("TP028", "Samten Hills Đà Lạt",        r2, catDiaDiem, 11.9222, 108.4423, "Khu nghỉ dưỡng thiền định kết hợp cảnh quan Tây Tạng đặc sắc.", 5, 0, 90, imgHotel1),
                    tp("TP029", "Bích Cầu Đạo Quán Đà Lạt",   r3, catDiaDiem, 11.9769, 108.4454, "Nơi thư giãn tĩnh tâm giữa đồi thông Đà Lạt.", 4, 50000, 60, imgForest),
                    tp("TP030", "Đường Hầm Đất Sét Đà Lạt",   r1, catDiaDiem, 11.9400, 108.4420, "Đường hầm nghệ thuật bằng đất sét độc đáo nhất Việt Nam.", 4, 100000, 60, imgHotel2),
                    tp("TP031", "Cao Nguyên Hoa Đà Lạt",      r4, catDiaDiem, 11.9268, 108.3717, "Đồng hoa bất tận ngắm nhìn từ trên cao, điểm sống ảo tuyệt đẹp.", 5, 80000, 90, imgFlower),
                    tp("TP032", "Làng Hoa Vạn Thành",         r1, catDiaDiem, 11.9472, 108.4137, "Làng hoa trồng rau chuyên nghiệp với hàng ngàn loại hoa đẹp.", 4, 0, 60, imgFlower),
                    tp("TP033", "Thác Cam Ly",                r1, catDiaDiem, 11.9403, 108.4209, "Thác nước ngay trong lòng thành phố Đà Lạt gắn bao hoài niệm.", 4, 20000, 45, imgWaterfall),
                    tp("TP034", "Đồi Cù Đà Lạt",              r1, catDiaDiem, 11.9435, 108.4428, "Sân golf và đồi cỏ xanh mượt ngay trung tâm thành phố.", 4, 0, 60, imgForest),
                    tp("TP035", "Hồ Than Thở",                r3, catDiaDiem, 11.9625, 108.4528, "Hồ thơ mộng bao quanh bởi rừng thông với bao câu chuyện huyền bí.", 4, 20000, 60, imgLake),
                    tp("TP036", "Khu Vui Chơi Wonderland",   r2, catDiaDiem, 11.8961, 108.4133, "Khu giải trí tổng hợp cho gia đình và trẻ em gần hồ Tuyền Lâm.", 4, 120000, 120, imgHotel1),
                    tp("TP037", "Đình Prenn Thiền Viện",     r6, catDiaDiem, 11.8850, 108.4380, "Ngắm toàn cảnh Đà Lạt từ trên cao qua cáp treo và đường mòn.", 4, 0, 90, imgForest),
                    tp("TP038", "Vườn Rau Tiến Vua Đà Lạt",  r4, catDiaDiem, 11.9100, 108.4900, "Trang trại rau sạch công nghệ cao độc đáo nhất Đà Lạt.", 3, 50000, 60, imgFlower),
                    tp("TP039", "Phố Đi Bộ Trần Phú",        r1, catDiaDiem, 11.9360, 108.4395, "Con phố thương mại sầm uất nhất Đà Lạt với quán cà phê và shop.", 4, 0, 45, imgCoffee),
                    tp("TP040", "Nhà Thờ Dòng Chúa Cứu Thế", r1, catDiaDiem, 11.9480, 108.4420, "Nhà thờ kiến trúc Châu Âu cổ kính bên đồi thông yên tĩnh.", 3, 0, 30, imgHotel1),
                    tp("TP041", "Dinh Yersin Đà Lạt",        r4, catDiaDiem, 11.9442, 108.4989, "Di tích lịch sử kỷ niệm người sáng lập thành phố Đà Lạt.", 3, 30000, 45, imgHotel2),
                    tp("TP042", "Rừng Thông Đà Lạt",         r1, catDiaDiem, 11.9350, 108.4500, "Khu rừng thông bạt ngàn giữa lòng thành phố Đà Lạt lộng gió.", 4, 0, 60, imgForest),
                    tp("TP043", "Đồi Chè Bảo Lộc Lâm Đồng",  r4, catDiaDiem, 11.5167, 107.8167, "Đồi chè xanh mượt trải dài ngút tầm mắt tại Bảo Lộc Lâm Đồng.", 4, 0, 90, imgForest),
                    tp("TP044", "Euro Garden Đà Lạt",        r1, catDiaDiem, 11.9542, 108.3997, "Ngọn đồi hoa ngập tràn màu sắc theo phong cách Châu Âu.", 4, 100000, 90, imgFlower),
                    tp("TP045", "Pink Valley Đà Lạt",        r1, catDiaDiem, 11.9619, 108.3997, "Thung lũng hoa màu hồng mộng mơ cực hot dành cho giới trẻ.", 5, 80000, 90, imgFlower),
                    tp("TP046", "Hồ Prenn Đà Lạt",           r6, catDiaDiem, 11.8875, 108.4550, "Hồ nước bình yên ngay cạnh đèo Prenn thơ mộng.", 3, 0, 45, imgLake),
                    tp("TP047", "Trang Trại Bò Sữa Đà Lạt",  r5, catDiaDiem, 11.9500, 108.3600, "Trang trại bò sữa nổi tiếng với sữa tươi ngon và phong cảnh đẹp.", 4, 60000, 60, imgForest),
                    tp("TP048", "Làng Cà Phê Mê Linh",       r1, catDiaDiem, 11.9200, 108.4100, "Cánh đồng cà phê Arabica thơm lừng đặc trưng cao nguyên.", 4, 30000, 60, imgCoffee),
                    tp("TP049", "Cổng Trời Đà Lạt",          r6, catDiaDiem, 11.8900, 108.4400, "Điểm dừng chân đèo Prenn với view toàn cảnh thành phố Đà Lạt.", 4, 0, 30, imgForest),
                    tp("TP050", "Hồ Đầm Tròn Đà Lạt",        r1, catDiaDiem, 11.9550, 108.4250, "Hồ nước bình yên ẩn giữa rừng thông xanh mượt Đà Lạt.", 3, 0, 45, imgLake)
                );
                touristPlaceRepository.saveAll(places);

                // ========= 10 HOTELS =========
                hotelRepository.save(Hotel.builder().name("Dalat Palace Heritage Hotel 5 Sao").address("1 Trần Phú, Phường 3, Đà Lạt").phone("02633825444").pricePerNight(BigDecimal.valueOf(2500000)).latitude(11.9365).longitude(108.4412).imageUrl(imgHotel1).build());
                hotelRepository.save(Hotel.builder().name("Ana Mandara Villas Dalat Resort Spa 5 Sao").address("Đường Lê Lai, Phường 5, Đà Lạt").phone("02633555888").pricePerNight(BigDecimal.valueOf(3200000)).latitude(11.9425).longitude(108.4215).imageUrl(imgHotel2).build());
                hotelRepository.save(Hotel.builder().name("Terracotta Hotel Resort Da Lat 4 Sao").address("Phân khu 7.9 Hồ Tuyền Lâm, Đà Lạt").phone("02633883888").pricePerNight(BigDecimal.valueOf(1800000)).latitude(11.8920).longitude(108.4310).imageUrl(imgHotel1).build());
                hotelRepository.save(Hotel.builder().name("Swiss-Belresort Tuyền Lâm 4 Sao").address("KDL Hồ Tuyền Lâm, Phường 3, Đà Lạt").phone("02633799999").pricePerNight(BigDecimal.valueOf(1600000)).latitude(11.8880).longitude(108.4250).imageUrl(imgHotel2).build());
                hotelRepository.save(Hotel.builder().name("Khách Sạn Mường Thanh Holiday 4 Sao").address("42 Phan Bội Châu, Phường 1, Đà Lạt").phone("02633578888").pricePerNight(BigDecimal.valueOf(1200000)).latitude(11.9430).longitude(108.4390).imageUrl(imgHotel1).build());
                hotelRepository.save(Hotel.builder().name("Khách Sạn Tulip 3 Đà Lạt 3 Sao").address("57 Hai Bà Trưng, Phường 6, Đà Lạt").phone("02633510999").pricePerNight(BigDecimal.valueOf(650000)).latitude(11.9450).longitude(108.4350).imageUrl(imgHotel2).build());
                hotelRepository.save(Hotel.builder().name("Khách Sạn Du Parc Đà Lạt 3 Sao").address("15 Trần Phú, Phường 3, Đà Lạt").phone("02633825777").pricePerNight(BigDecimal.valueOf(750000)).latitude(11.9360).longitude(108.4395).imageUrl(imgHotel1).build());
                hotelRepository.save(Hotel.builder().name("Túi Mơ To Homestay Đà Lạt").address("Hẻm 31 Sào Nam, Phường 11, Đà Lạt").phone("0987654321").pricePerNight(BigDecimal.valueOf(350000)).latitude(11.9567).longitude(108.4712).imageUrl(imgHotel2).build());
                hotelRepository.save(Hotel.builder().name("Legris Boutique Hotel Đà Lạt").address("12 Yersin, Phường 10, Đà Lạt").phone("0912345678").pricePerNight(BigDecimal.valueOf(280000)).latitude(11.9410).longitude(108.4520).imageUrl(imgHotel1).build());
                hotelRepository.save(Hotel.builder().name("Dreams Hotel Đà Lạt 3 Sao").address("151 Phan Đình Phùng, Phường 2, Đà Lạt").phone("02633827999").pricePerNight(BigDecimal.valueOf(500000)).latitude(11.9400).longitude(108.4450).imageUrl(imgHotel2).build());

                // ========= 12 RESTAURANTS =========
                restaurantRepository.save(Restaurant.builder().name("Lẩu Gà Lá É Đà Lạt").address("5 Đường 3 Tháng 4, Phường 3, Đà Lạt").phone("0901234567").averagePricePerPerson(BigDecimal.valueOf(150000)).latitude(11.9305).longitude(108.4410).imageUrl(imgFood1).build());
                restaurantRepository.save(Restaurant.builder().name("Lẩu Bò Ba Toa Nhà Gỗ").address("1/29 Hoàng Diệu, Phường 5, Đà Lạt").phone("02633826333").averagePricePerPerson(BigDecimal.valueOf(180000)).latitude(11.9408).longitude(108.4312).imageUrl(imgFood2).build());
                restaurantRepository.save(Restaurant.builder().name("Bánh Căn Nhà Chung").address("13 Nhà Chung, Phường 3, Đà Lạt").phone("0934567890").averagePricePerPerson(BigDecimal.valueOf(50000)).latitude(11.9360).longitude(108.4370).imageUrl(imgFood1).build());
                restaurantRepository.save(Restaurant.builder().name("Nem Nướng Bà Hùng Đà Lạt").address("328 Phan Đình Phùng, Phường 2, Đà Lạt").phone("02633825888").averagePricePerPerson(BigDecimal.valueOf(70000)).latitude(11.9475).longitude(108.4385).imageUrl(imgFood2).build());
                restaurantRepository.save(Restaurant.builder().name("Bánh Mì Xíu Mại Hoàng Diệu").address("26 Hoàng Diệu, Phường 5, Đà Lạt").phone("0978901234").averagePricePerPerson(BigDecimal.valueOf(35000)).latitude(11.9412).longitude(108.4310).imageUrl(imgFood1).build());
                restaurantRepository.save(Restaurant.builder().name("Horizon Coffee Ngắm Đồi Thông").address("31B Đường 3 Tháng 4, Phường 3, Đà Lạt").phone("0909888777").averagePricePerPerson(BigDecimal.valueOf(70000)).latitude(11.9250).longitude(108.4435).imageUrl(imgCoffee).build());
                restaurantRepository.save(Restaurant.builder().name("Bánh Tráng Nướng Dì Đình").address("26 Hoàng Diệu, Phường 5, Đà Lạt").phone("0945678901").averagePricePerPerson(BigDecimal.valueOf(30000)).latitude(11.9412).longitude(108.4310).imageUrl(imgFood1).build());
                restaurantRepository.save(Restaurant.builder().name("Quán Gà Rừng Đà Lạt").address("18 Trương Công Định, Phường 1, Đà Lạt").phone("02633822777").averagePricePerPerson(BigDecimal.valueOf(120000)).latitude(11.9415).longitude(108.4365).imageUrl(imgFood2).build());
                restaurantRepository.save(Restaurant.builder().name("Kem Bơ Đà Lạt Gia Truyền").address("9 Khu Hòa Bình, Phường 1, Đà Lạt").phone("0912111222").averagePricePerPerson(BigDecimal.valueOf(40000)).latitude(11.9420).longitude(108.4370).imageUrl(imgFood1).build());
                restaurantRepository.save(Restaurant.builder().name("Cafe Tùng Đà Lạt").address("6 Khu Hòa Bình, Phường 1, Đà Lạt").phone("02633822193").averagePricePerPerson(BigDecimal.valueOf(50000)).latitude(11.9418).longitude(108.4372).imageUrl(imgCoffee).build());
                restaurantRepository.save(Restaurant.builder().name("Quán Phở Lúa Cao Nguyên").address("12 Đống Đa, Phường 3, Đà Lạt").phone("0901555666").averagePricePerPerson(BigDecimal.valueOf(80000)).latitude(11.9280).longitude(108.4420).imageUrl(imgFood2).build());
                restaurantRepository.save(Restaurant.builder().name("Nhà Hàng Thanh Thủy Đà Lạt").address("2 Nguyễn Thị Minh Khai, Phường 2, Đà Lạt").phone("02633822929").averagePricePerPerson(BigDecimal.valueOf(200000)).latitude(11.9395).longitude(108.4380).imageUrl(imgFood1).build());

                // ========= TRANSPORT OPTIONS =========
                transportOptionRepository.save(TransportOption.builder().name("Xe may tu lai").type("Private").isSelfDrive(true).basePrice(BigDecimal.valueOf(150000)).fuelConsumption(2.5).fuelPrice(BigDecimal.valueOf(24000)).build());
                transportOptionRepository.save(TransportOption.builder().name("Oto 4 cho thue").type("Rental").isSelfDrive(false).basePrice(BigDecimal.valueOf(900000)).fuelConsumption(7.0).fuelPrice(BigDecimal.valueOf(24000)).build());
                transportOptionRepository.save(TransportOption.builder().name("Oto 7 cho VIP").type("Rental").isSelfDrive(false).basePrice(BigDecimal.valueOf(1400000)).fuelConsumption(9.0).fuelPrice(BigDecimal.valueOf(24000)).build());
                transportOptionRepository.save(TransportOption.builder().name("Xe khach tu TPHCM").type("Public").isSelfDrive(false).basePrice(BigDecimal.valueOf(250000)).fuelConsumption(0).fuelPrice(BigDecimal.ZERO).build());

                // ========= SAMPLE VEHICLES =========
                vehicleRepository.save(Vehicle.builder().driverName("Nguyen Van A").licensePlate("49A-123.45").pickupAddress("Ben xe Mien Dong, TPHCM").pickupLatitude(10.8142).pickupLongitude(106.7118).dropoffAddress("Ben xe Da Lat").dropoffLatitude(11.9360).dropoffLongitude(108.4447).departureTime(LocalDateTime.now().plusHours(2)).totalSeats(4).availableSeats(3).costPerKm(BigDecimal.valueOf(15000)).vehicleType("Xe 4 cho").active(true).build());
                vehicleRepository.save(Vehicle.builder().driverName("Tran Van B").licensePlate("49B-678.90").pickupAddress("San bay Lien Khuong, Lam Dong").pickupLatitude(11.7506).pickupLongitude(108.3742).dropoffAddress("Trung tam TP Da Lat").dropoffLatitude(11.9404).dropoffLongitude(108.4583).departureTime(LocalDateTime.now().plusHours(1)).totalSeats(7).availableSeats(5).costPerKm(BigDecimal.valueOf(20000)).vehicleType("Xe 7 cho").active(true).build());

                // ========= BLOG POSTS =========
                blogPostRepository.save(BlogPost.builder().title("Kinh nghiệm du lịch Đà Lạt 3 ngày 2 đêm tự túc").content("Đà Lạt luôn là điểm đến hấp dẫn với khí hậu mát mẻ quanh năm và phong cảnh hữu tình tuyệt vời. Bài viết chia sẻ kinh nghiệm du lịch tự túc tại Đà Lạt chi tiết nhất.").author("Admin").imageUrl(imgForest).build());
                blogPostRepository.save(BlogPost.builder().title("Top 10 địa điểm check-in đẹp nhất Đà Lạt 2026").content("Đà Lạt có rất nhiều địa điểm đẹp cho giới trẻ check-in. Từ Hồ Xuân Hương đến Thung Lũng Tình Yêu, mỗi nơi đều mang lại những bức ảnh đẹp tuyệt vời.").author("Admin").imageUrl(imgFlower).build());

                System.out.println("[DataSeeder] SUCCESS: Seeded 50 tourist places, 10 hotels, 12 restaurants, 4 transports, 2 vehicles, 2 blogs!");
            } else {
                System.out.println("[DataSeeder] Data already exists (" + touristPlaceRepository.count() + " places). Skipping seed.");
            }
        };
    }

    private TouristPlace tp(String id, String name, Region region, Category category,
                             double lat, double lng, String desc, int rating, double ticket, int duration, String imageUrl) {
        return TouristPlace.builder()
                .id(id)
                .name(name)
                .region(region)
                .category(category)
                .latitude(lat)
                .longitude(lng)
                .description(desc)
                .rating(rating)
                .ticketPrice(BigDecimal.valueOf(ticket))
                .avgVisitDurationMin(duration)
                .imageUrl(imageUrl)
                .build();
    }
}
