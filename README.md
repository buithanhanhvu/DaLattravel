# 🌲 DaLatTravel - Hệ Thống Du Lịch Đà Lạt Thông Minh & Kết Nối Ghép Xe (Smart Tourism & Carpooling Platform)

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.0-green?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-emerald?style=for-the-badge&logo=thymeleaf)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple?style=for-the-badge&logo=bootstrap)
![QA Testing](https://img.shields.io/badge/QA_Test-36%2F36_PASSED-brightgreen?style=for-the-badge&logo=githubactions)

---

## 📌 GIỚI THIỆU DỰ ÁN (PROJECT OVERVIEW)

**DaLatTravel** là giải pháp nền tảng du lịch thông minh toàn diện dành cho thành phố Đà Lạt, kết hợp giữa **Thuật toán Tối ưu Lịch trình Du lịch (Traveling Salesman Problem - TSP)**, **Định tuyến thực tế (OSRM API)**, **Hệ thống Ghép xe đi chung tiết kiệm**, **Đặt phòng Khách sạn thời gian thực**, **Đăng nhập Google OAuth2** và **Trang Quản trị Admin phân quyền (RBAC)**.

Dự án được thiết kế chuẩn kiến trúc **Spring MVC**, áp dụng các tiêu chuẩn phát triển phần mềm hiện đại cùng bộ **36 Test Cases kiểm thử tự động & thủ công (PASSED 100%)**.

---

## 📸 THƯ VIỆN GIAO DIỆN & TÍNH NĂNG (FEATURE GALLERY)

### 1. 🏠 Trang Chủ Du Lịch Đà Lạt (Homepage)
*Giao diện hiện đại, chuẩn Responsive, hiển thị danh mục Địa điểm nổi bật, Khách sạn sang trọng, Quán ăn đặc sản và Cẩm nang du lịch.*

![Homepage](docs/screenshots/01_homepage.png)

---

### 2. 🗺️ Thuật Toán Lên Lịch Trình Tự Động & Bản Đồ OSRM (Trip Planner & Route Optimization)
*Tự động lập lịch trình du lịch tối ưu dựa trên thời gian, sở thích và vị trí GPS. Định tuyến đường đi thực tế (km, phút) với OpenStreetMap OSRM và hiển thị Polyline trên bản đồ tương tác LeafletJS.*

| 📋 Form Chọn Tiêu Chí Lịch Trình | 📍 Kết Quả Lập Lịch Trình & Bản Đồ OSRM |
| :---: | :---: |
| ![Trip Planner Form](docs/screenshots/02_trip_planner_form.png) | ![Trip Planner Result](docs/screenshots/03_trip_planner_result.png) |

---

### 3. 🚗 Nền Tảng Ghép Xe Đi Chung (Carpooling System)
*Kết nối tài xế và du khách có chung lộ trình (TP.HCM, Sân bay Liên Khương, Đà Lạt), hỗ trợ lọc theo tuyến và tiết kiệm chi phí di chuyển.*

![Carpooling](docs/screenshots/04_carpool.png)

---

### 4. 🏔️ Khám Phá Địa Điểm, Khách Sạn & Nhà Hàng (Discovery Modules)
*Hiển thị 50+ địa điểm tham quan, 10 khách sạn resort và 12 nhà hàng ẩm thực với hình ảnh HD sắc nét, số sao đánh giá và bộ lọc tìm kiếm.*

| 🏞️ Địa Điểm Du Lịch (50+ Spots) | 🏨 Khách Sạn & Resort | 🍲 Nhà Hàng & Quán Ăn |
| :---: | :---: | :---: |
| ![Tourist Places](docs/screenshots/05_tourist_places.png) | ![Hotels List](docs/screenshots/06_hotels_list.png) | ![Restaurants](docs/screenshots/09_restaurants.png) |

---

### 5. 🏨 Chức Năng Đặt Phòng Khách Sạn (Hotel Booking & Instant Calculation)
*Modal đặt phòng trực quan: Chọn ngày Check-in/Check-out, số khách, tự động tính tổng tiền theo đêm và cấp mã đặt phòng duy nhất dạng `DLBK-XXXX`.*

| 📝 Form Modal Đặt Phòng | ✅ Đặt Phòng Thành Công (Code DLBK-XXXX) |
| :---: | :---: |
| ![Booking Modal](docs/screenshots/07_hotel_booking_modal.png) | ![Booking Success Alert](docs/screenshots/08_booking_success.png) |

---

### 6. 🔐 Đăng Nhập Google OAuth2 & Phân Quyền Admin (Authentication & Security)
*Đăng nhập an toàn bằng Google Identity Services (OAuth2 JWT) hoặc tài khoản thường. Chặn bảo mật `AuthInterceptor` kiểm soát quyền truy cập tài nguyên `/admin`.*

| 🔑 Trang Đăng Nhập & Google Sign-In | 🛡️ Bảng Điều Khiển Admin Dashboard |
| :---: | :---: |
| ![Login Google](docs/screenshots/10_login_google.png) | ![Admin Dashboard](docs/screenshots/11_admin_dashboard.png) |

| 📋 Duyệt Đơn Đặt Phòng Admin | 🏨 Quản Lý Khách Sạn CRUD | 👥 Phân Quyền Người Dùng (RBAC) |
| :---: | :---: | :---: |
| ![Admin Bookings](docs/screenshots/12_admin_bookings.png) | ![Admin Hotels](docs/screenshots/13_admin_hotels.png) | ![Admin Users](docs/screenshots/14_admin_users.png) |

---

## 🛠️ CÔNG NGHỆ & KIẾN TRÚC HỆ THỐNG (TECH STACK & ARCHITECTURE)

- **Backend Core**: Java 21 LTS, Spring Boot 4.1.0, Spring Data JPA, Hibernate ORM.
- **Security & Auth**: Google Identity Services (OAuth2 JWT), SHA-256 Password Hashing, `AuthInterceptor` (Session-based RBAC protection).
- **Algorithms & APIs**: TSP (Traveling Salesman Problem) Greedy Route Optimization, OpenStreetMap OSRM REST Routing API.
- **Frontend & UI**: HTML5, Vanilla CSS, Bootstrap 5.3, FontAwesome 6, LeafletJS Interactive Maps.
- **Database**: MySQL 8.0 với InnoDB, UTF-8 Encoding.
- **Build Tool & Testing**: Maven (`mvnw`), Automated Browser End-to-End Testing (`browser_subagent`).

---

## 📊 BẢNG TỔNG HỢP KIỂM THỬ ĐÁNH GIÁ CHẤT LƯỢNG (QA/QC TEST MATRIX)

Dự án đã trải qua quá trình kiểm thử toàn diện với **36 Test Cases (PASSED 100%)** phủ khắp các phân hệ. Tham khảo tài liệu kiểm thử chi tiết tại:
👉 [Tài Liệu Full Test Cases Suite (TESTCASES_DALAT_TRAVEL.md)](TESTCASES_DALAT_TRAVEL.md)  
👉 [Tài Liệu Hướng Dẫn Kịch Bản Test (testcase hướng dẫn .md)](testcase%20h%C6%B0%E1%BB%9Bng%20d%E1%BA%ABn%20.md)

| Phân hệ / Mô-đun | Số Lượng TC | PASSED | FAILED | Tỷ Lệ Thành Công |
| :--- | :---: | :---: | :---: | :---: |
| **1. Xác thực & Bảo mật (Auth, Google OAuth2, Security Interceptor)** | 10 | 10 | 0 | **100%** |
| **2. Lên Lịch Trình Tự Động (Trip Planner, TSP & OSRM API)** | 5 | 5 | 0 | **100%** |
| **3. Ghép Xe Đi Chung (Carpooling System)** | 3 | 3 | 0 | **100%** |
| **4. Danh Mục Địa Điểm, Khách Sạn & Nhà Hàng** | 5 | 5 | 0 | **100%** |
| **5. Đặt Phòng Khách Sạn (Booking & Calculation)** | 4 | 4 | 0 | **100%** |
| **6. Quản Trị Hệ Thống (Admin Dashboard & CRUD)** | 7 | 7 | 0 | **100%** |
| **7. Bài Viết & Liên Hệ (Blog & Contact Form)** | 2 | 2 | 0 | **100%** |
| **TỔNG CỘNG** | **36** | **36** | **0** | **100%** |

---

## 🚀 HƯỚNG DẪN CÀI ĐẶT & CHẠY DỰ ÁN (SETUP INSTRUCTIONS)

### 1. Yêu Cầu Tiền Điều Kiện (Prerequisites)
- **Java Development Kit (JDK)**: Java 21 trở lên.
- **Database**: MySQL 8.0+ đang hoạt động tại `localhost:3306`.

### 2. Cấu Hình CSDL (Database Setup)
Tạo CSDL MySQL:
```sql
CREATE DATABASE dalattravel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Cấu hình tài khoản CSDL trong `src/main/resources/application.properties` (Mặc định: `root` / password trống hoặc tự chỉnh).

### 3. Biên Dịch & Chạy Ứng Dụng (Run Application)
Tải dependency và biên dịch dự án:
```powershell
.\mvnw.cmd clean test-compile
```

Khởi chạy ứng dụng Spring Boot:
```powershell
.\mvnw.cmd spring-boot:run
```

Sau khi ứng dụng khởi chạy thành công, mở trình duyệt truy cập:
- **Trang chủ**: `http://localhost:8080`
- **Tài khoản Admin mẫu**: `admin` / `admin123`
- **Tài khoản User mẫu**: `user` / `user123`

---

## 📁 CẤU TRÚC THƯ MỤC DỰ ÁN (PROJECT STRUCTURE)

```
DaLattravel/
├── docs/screenshots/               # Thư viện ảnh chụp tính năng cho README
├── src/main/java/com/example/dalattravel/
│   ├── config/                     # WebMvcConfig, AuthInterceptor, DataSeeder
│   ├── controller/                 # HomeController, TripPlannerController, HotelController, AdminController, AuthController...
│   ├── model/                      # TouristPlace, Hotel, Restaurant, HotelBooking, User, Carpool...
│   ├── repository/                 # Spring Data JPA Repositories
│   └── service/                    # AuthService, OsrmRouteService, TripPlannerService...
├── src/main/resources/
│   ├── templates/                  # Thymeleaf HTML Views (admin/, auth/, hotels/, trip-planner/, fragments/)
│   └── application.properties     # Config MySQL, Port 8080
├── TESTCASES_DALAT_TRAVEL.md      # Full 36 QA Test Cases Suite Document
├── testcase hướng dẫn .md          # Kịch bản kiểm thử hướng dẫn chi tiết
└── README.md                       # Tài liệu giới thiệu dự án cho Nhà tuyển dụng
```

---

## 📝 GIẤY PHÉP & TÁC GIẢ (AUTHOR & LICENSE)

- **Đơn vị phát triển**: DaLatTravel Dev & QA Team.
- **Repository**: [https://github.com/buithanhanhvu/DaLattravel.git](https://github.com/buithanhanhvu/DaLattravel.git)
- **Bản quyền**: © 2026 Đà Lạt Travel System. All rights reserved.