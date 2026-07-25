# 🌲 DaLatTravel - Hệ Thống Lập Lịch Trình & Ghép Xe Du Lịch Đà Lạt

Ứng dụng web du lịch Đà Lạt thông minh xây dựng trên nền tảng **Java 21 & Spring Boot**, giúp du khách lập lịch trình tham quan tự động tối ưu chi phí và đăng ký ghép xe đi chung kết nối hành khách cùng tuyến đường.

---

## 🚀 Tính Năng Nổi Bật

### 1. 📍 Lập Lịch Trình Du Lịch Tự Động & Bản Đồ Real-time (`/trip-planner`)
- **Tự động định vị GPS**: Nút `📍 Lấy vị trí hiện tại` sử dụng Geolocation API định vị tọa độ xuất phát.
- **Bộ chọn địa điểm đa năng (Multi-select Search Dropdown)**: Cho phép tìm kiếm, lọc theo loại hình du lịch và chọn/bỏ chọn địa điểm có trong CSDL.
- **Bản đồ tương tác Leaflet.js Real-time**: Cập nhật Marker điểm xuất phát và các địa điểm đã chọn theo thời gian thực.
- **Thuật toán đường đi ngắn nhất (TSP / Nearest Neighbor)**: Tìm chuỗi địa điểm tham quan có tổng khoảng cách di chuyển ngắn nhất.
- **Tự động đề xuất 3 Phương án (Tiết kiệm - Cân bằng - Cao cấp)**:
  - ⚡ **Tiết kiệm**: Xe máy tự lái + Homestay / Hostel.
  - ⚖️ **Cân bằng**: Ô tô 4 chỗ + Khách sạn 3 sao.
  - 💎 **Cao cấp**: Ô tô 7 chỗ VIP + Resort / Khách sạn 4-5 sao.
- **Phân bổ lịch trình chi tiết theo từng ngày (Timeline Schedule)**: Phân bổ địa điểm tham quan hợp lý theo từng khung giờ (Sáng / Trưa / Chiều) và tự động nhận diện loại địa điểm (Tham quan / Nhà hàng / Khách sạn).

### 2. 🚕 Ghép Xe Đi Chung Tối Ưu Chi Phí (`/carpool`)
- **Min-Cost Max-Flow (`MinCostMaxFlowService`)**: Thuật toán luồng cực đại chi phí cực tiểu giúp ghép các hành khách vào xe trống theo tiêu chí chi phí tối ưu nhất.
- **Lập lộ trình đón/trả (`PDPTWService`)**: Thuật toán PDPTW (Pickup & Delivery Problem with Time Windows) sắp xếp thứ tự các điểm dừng đón/trả theo khung giờ.
- **Khớp tuyến đường Polyline (`RouteMatchingService`)**: Snap tọa độ đón/trả lên tuyến chính của xe để kiểm tra cùng chiều và kiểm tra sức chứa ghế khả dụng từng đoạn đường.

### 3. 🗺️ Tích Hợp Bản Đồ & Định Tuyến OSRM (`OsrmRouteService`)
- Gọi REST API OpenStreetMap (OSRM) để lấy quãng đường thực tế (km), thời gian di chuyển và mảng tọa độ polyline hiển thị đường đi trên bản đồ Leaflet.

### 4. 🖼️ Giao Diện Hiện Đại & Đồng Bộ (Unified Responsive UI)
- **Thanh Navigation Fragment Đồng Bộ**: Tích hợp navbar thống nhất 100% trên tất cả các trang với 8 mục menu chính:
  - 🏠 **Trang chủ** (`/`)
  - 🗺️ **Lên lịch trình** (`/trip-planner`)
  - 🚗 **Ghép xe đi chung** (`/carpool`)
  - 🏔️ **Địa điểm** (`/tourist-places`)
  - 🏨 **Khách sạn** (`/hotels`)
  - 🍜 **Nhà hàng** (`/restaurants`)
  - 📝 **Bài viết** (`/blog`)
  - ✉️ **Liên hệ** (`/contact`)
- **Hình Ảnh Thumbnail Thực Tế (Rich Media Cards)**: Tự động nạp hình ảnh Unsplash sắc nét cho 50+ địa điểm du lịch, 10+ khách sạn, 12+ nhà hàng và các bài viết kinh nghiệm.

---

## 🛠️ Công Nghệ Sử Dụng

- **Ngôn ngữ**: Java 21 (LTS)
- **Framework**: Spring Boot 3.x / 4.x (Spring MVC, Spring Data JPA)
- **Cơ sở dữ liệu**: MySQL Server 8.0 (Database: `dalattravel_db`)
- **Frontend / Template**: Thymeleaf HTML5, Bootstrap 5.3, FontAwesome 6.4, Leaflet.js
- **Thư viện phụ trợ**: Lombok, Jackson JSON, RestTemplate

---

## ⚙️ Cấu Hình Cơ Sở Dữ Liệu MySQL

Đảm bảo dịch vụ MySQL đang chạy trên máy cục bộ `127.0.0.1:3306`. Cấu hình trong `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dalattravel_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true&characterEncoding=UTF-8
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=123456

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

Khi ứng dụng khởi động, `DataSeeder` sẽ tự động khởi tạo và nạp dữ liệu mẫu ban đầu vào MySQL.

---

## 💻 Hướng Dẫn Khởi Chạy Ứng Dụng

### 1. Biên dịch dự án:
```powershell
.\mvnw.cmd clean test-compile
```

### 2. Khởi chạy ứng dụng Spring Boot:
```powershell
.\mvnw.cmd spring-boot:run
```

### 3. Truy cập ứng dụng trên trình duyệt:
- **Trang chủ**: [http://localhost:8080](http://localhost:8080)
- **Lập lịch trình**: [http://localhost:8080/trip-planner](http://localhost:8080/trip-planner)
- **Ghép xe đi chung**: [http://localhost:8080/carpool](http://localhost:8080/carpool)
- **Địa điểm du lịch**: [http://localhost:8080/tourist-places](http://localhost:8080/tourist-places)
- **Khách sạn**: [http://localhost:8080/hotels](http://localhost:8080/hotels)
- **Nhà hàng**: [http://localhost:8080/restaurants](http://localhost:8080/restaurants)

---

## 🧪 Kết Quả Kiểm Thử & Xác Nhận Giao Diện

Tất cả các tính năng lập lịch trình, ghép xe, bản đồ định tuyến OSRM và giao diện web đồng bộ đã được kiểm thử tự động trực tiếp trên trình duyệt live và hoạt động ổn định.