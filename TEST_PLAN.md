# 📄 KẾ HOẠCH KIỂM THỬ TỔNG THỂ (MASTER TEST PLAN)
**Dự án:** DaLatTravel - Hệ Thống Du Lịch Đà Lạt Thông Minh & Ghép Xe  
**Phiên bản:** 2.0  
**Tác giả:** QA/QC Test Lead & Automation Team  
**Ngày cập nhật:** 25/07/2026  

---

## 1. GIỚI THIỆU & MỤC TIÊU (INTRODUCTION & OBJECTIVES)

Tài liệu Kế hoạch Kiểm thử này xác định chiến lược, phạm vi, môi trường, nguồn lực và lịch trình kiểm thử cho toàn bộ hệ thống du lịch thông minh **DaLatTravel** (Backend Spring Boot 4.1 + MySQL 8.0 + Frontend Thymeleaf/Bootstrap 5).

### 🎯 Mục tiêu chất lượng (Quality Goals):
1. **Tính đúng đắn về chức năng (Functional Correctness):** Đảm bảo 100% các luồng nghiệp vụ Lập lịch trình TSP, Ghép xe đi chung, Đặt phòng khách sạn và Quản trị Admin chính xác theo yêu cầu.
2. **Tính toàn vẹn dữ liệu & Tính giá (Data Integrity & Calculation):** Ngăn chặn lỗi sai lệch tổng tiền đặt phòng, tính toán số ngày lưu trú chính xác và mã đơn `DLBK-XXXX` duy nhất.
3. **Tính an toàn & bảo mật (Security & RBAC):** Kiểm tra cơ chế phân quyền (`AuthInterceptor`), bảo mật tài nguyên `/admin`, băm mật khẩu SHA-256 và xác thực Google Identity OAuth2.
4. **Độ tin cậy của thuật toán & API (Algorithm & API Reliability):** Đảm bảo thuật toán TSP sắp xếp lịch trình tối ưu và API OSRM trả về thông tin định tuyến (km, phút) chuẩn xác.

---

## 2. PHẠM VI KIỂM THỬ (TEST SCOPE)

### 2.1 Trong phạm vi (In-Scope)

| Phân hệ (Module) | Các tính năng chi tiết cần kiểm thử |
| :--- | :--- |
| **Xác thực & Bảo mật (Auth & Security)** | Đăng ký tài khoản, Đăng nhập thường & Google Identity Services (OAuth2 JWT), Mã hóa mật khẩu SHA-256, Chặn `AuthInterceptor` bảo mật `/admin`, Đăng xuất giải phóng Session. |
| **Lên Lịch Trình (Trip Planner)** | Lập lịch trình 1-3 ngày tự động bằng thuật toán TSP, Định tuyến OSRM thực tế (km/phút), Định vị GPS hiện tại, Hiển thị đường đi Polyline trên bản đồ LeafletJS. |
| **Ghép Xe Đi Chung (Carpooling)** | Tìm kiếm chuyến xe ghép theo lộ trình (TPHCM - Đà Lạt), Bộ lọc điểm đi/đến, Đăng tin tìm xe / ghép chuyến. |
| **Danh Mục Địa Điểm / Khách Sạn / Nhà Hàng** | Xem danh sách 50+ địa điểm, 10 khách sạn, 12 nhà hàng với ảnh HD Unsplash, Đánh giá sao, Bộ lọc tìm kiếm theo từ khóa. |
| **Đặt Phòng Khách Sạn (Hotel Booking)** | Form modal đặt phòng trực tiếp, Tự động tính tổng tiền theo số đêm, Cấp mã đơn `DLBK-XXXX`, Tự động điền thông tin người dùng đã đăng nhập. |
| **Quản Trị Viên (Admin Dashboard & CRUD)** | Dashboard thống kê chỉ số thời gian thực, Duyệt/Hủy đơn đặt phòng (`PENDING` -> `CONFIRMED`/`CANCELLED`), CRUD Khách sạn, Địa điểm du lịch, Nhà hàng, Quản lý phân quyền tài khoản (RBAC `USER` <-> `ADMIN`). |

### 2.2 Ngoài phạm vi (Out-of-Scope)
- Kiểm thử tải cực lớn (Stress/Load Testing trên 500,000 CCU đồng thời).
- Cổng thanh toán quốc tế trực tiếp thực tế (Visa/Mastercard) - sử dụng luồng xác nhận đơn và thanh toán trả sau.

---

## 3. CHIẾN LƯỢC KIỂM THỬ (TEST STRATEGY & APPROACH)

Hệ thống áp dụng phương pháp kiểm thử toàn diện 3 cấp độ (Testing Pyramid):

```
       / \
      / UI\      <- 3. Kiểm thử Giao diện & Luồng E2E (Selenium/Playwright)
     /-----\
    /  API  \    <- 2. Kiểm thử API & Security (Postman/MockMvc & Interceptor)
   /---------\
  / Unit Tests\  <- 1. Kiểm thử Đơn vị (JUnit 5 + Mockito Service Tests)
 /-------------\
```

### 3.1 Kiểm thử Hộp trắng (White-box Testing / Unit Testing)
- **Công cụ:** JUnit 5, Mockito.
- **Đối tượng:** Tầng Service xử lý logic nghiệp vụ (`AuthServiceTest`, `HotelBookingServiceTest`).
- **Tiêu chí:** 100% các phương thức cốt lõi (tính tiền đặt phòng, băm mật khẩu, phân quyền) vượt qua thành công.

### 3.2 Kiểm thử Hộp xám (Gray-box Testing / API & Security Testing)
- **Công cụ:** Postman, Spring Boot MockMvc.
- **Nội dung:** Kiểm tra HTTP Status Code (200 OK, 400 Bad Request, 401 Unauthorized, 403 Forbidden), cấu trúc response và cơ chế chặn `AuthInterceptor`.

### 3.3 Kiểm thử Hộp đen (Black-box Testing / System & UI E2E Testing)
- **Công cụ:** Browser Subagent (Playwright/Selenium), Manual Testing.
- **Kỹ thuật áp dụng:**
  - **Phân vùng tương đương (Equivalence Partitioning):** Dữ liệu hợp lệ / không hợp lệ cho Đăng ký, Đặt phòng, Tìm kiếm.
  - **Phân tích giá trị biên (Boundary Value Analysis):** Kiểm thử ngày Check-out <= Check-in, mật khẩu < 6 ký tự.

---

## 4. MÔI TRƯỜNG KIỂM THỬ (TEST ENVIRONMENT)

| Thành phần | Cấu hình / Thông số |
| :--- | :--- |
| **Hệ điều hành** | Windows 11 / Linux |
| **Backend Runtime** | Java OpenJDK 21, Spring Boot 4.1.0 |
| **Database** | MySQL Server 8.0 (Database: `dalattravel_db`, Port: 3006) |
| **Bản đồ & Routing** | OpenStreetMap OSRM REST Routing API, LeafletJS v1.9.4 |
| **Trình duyệt kiểm thử** | Google Chrome Version 120+ |

---

## 5. TIÊU CHÍ DỪNG & CHUYỂN GIAO (ENTRY / EXIT CRITERIA)

### 5.1 Tiêu chí bắt đầu (Entry Criteria)
- Mã nguồn Backend đã được biên dịch thành công không có lỗi syntax (`BUILD SUCCESS`).
- CSDL MySQL `dalattravel_db` đã nạp đầy đủ dữ liệu mẫu qua `DataSeeder` (50 địa điểm, 10 khách sạn, 12 nhà hàng).

### 5.2 Tiêu chí hoàn thành (Exit Criteria)
- **Unit Test Backend:** 100% các test case trong JUnit/Mockito vượt qua (`6/6 Tests PASSED`).
- **QA Test Suite Matrix:** 100% các kịch bản kiểm thử (36/36 Test Cases) trong ma trận đạt trạng thái **PASSED**.
- Không còn bất kỳ lỗi nào ở mức độ Critical hoặc High chưa được khắc phục.

---

## 6. QUẢN LÝ LỖI (DEFECT MANAGEMENT PROCESS)

Các lỗi phát hiện trong quá trình kiểm thử được ghi nhận vào tài liệu **`BUG_REPORT.md`** theo mẫu quy chuẩn MantisBT / Jira Defect Log bao gồm:
- **Bug ID & Title**: Mã định danh và tiêu đề lỗi.
- **Module**: Phân hệ phát sinh lỗi.
- **Severity**: Critical / High / Medium / Low.
- **Priority**: P1 (Khẩn cấp) -> P4 (Thấp).
- **Steps to Reproduce**: Các bước tái hiện lỗi.
- **Expected vs Actual Result**: Đối chiếu kết quả thực tế với mong đợi.
- **Fix Solution**: Giải pháp đã khắc phục trong mã nguồn.
