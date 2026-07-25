# BỘ TRƯỜNG HỢP KIỂM THỬ HỆ THỐNG TOÀN DIỆN (FULL TEST CASES SUITE) - DALATTRAVEL
**Phân hệ**: Authentication, Authorization (RBAC), Security, Trip Planner (TSP/OSRM), Carpool, Hotel Booking, Admin Dashboard & CRUD Management  
**Dự án**: Hệ Thống Du Lịch Đà Lạt Thông Minh (DaLatTravel)  
**Tác giả**: QA/QC Automation & Manual Testing Team  
**Ngày cập nhật**: 25/07/2026  

---

## 📊 BẢNG TỔNG HỢP TRẠNG THÁI KIỂM THỬ (TEST EXECUTION SUMMARY)

| Phân hệ / Mô-đun | Tổng số TC | PASSED | FAILED | NOT RUN | Tỷ lệ thành công |
| :--- | :---: | :---: | :---: | :---: | :---: |
| 1. Xác thực & Phân quyền (Auth & Security) | 10 | 10 | 0 | 0 | 100% |
| 2. Lên Lịch Trình Tự Động (Trip Planner & OSRM) | 5 | 5 | 0 | 0 | 100% |
| 3. Ghép Xe Đi Chung (Carpooling) | 3 | 3 | 0 | 0 | 100% |
| 4. Địa Điểm, Khách Sạn & Nhà Hàng (Places/Hotels/Restaurants) | 5 | 5 | 0 | 0 | 100% |
| 5. Đặt Phòng Khách Sạn (Hotel Booking) | 4 | 4 | 0 | 0 | 100% |
| 6. Quản Trị Hệ Thống (Admin Dashboard & CRUD) | 7 | 7 | 0 | 0 | 100% |
| 7. Bài Viết & Liên Hệ (Blog & Contact) | 2 | 2 | 0 | 0 | 100% |
| **TỔNG CỘNG** | **36** | **36** | **0** | **0** | **100%** |

---

## 📝 BẢNG MA TRẬN TEST CASES CHI TIẾT (DETAILED TEST CASES MATRIX)

### PHÂN HỆ 1: XÁC THỰC & PHÂN QUYỀN (AUTHENTICATION, RBAC & SECURITY)

| Mã TC | Phân hệ / Chức năng | Kịch bản kiểm thử (Test Scenario) | Tiền điều kiện (Prerequisites) | Các bước thực hiện (Test Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Result) | Trạng thái | Ghi chú |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :---: | :--- |
| **TC_AUTH_001** | Auth - Đăng ký | Đăng ký tài khoản người dùng thành công (Happy Path) | Username và Email chưa từng tồn tại trong CSDL | 1. Truy cập `/register`<br>2. Nhập thông tin hợp lệ<br>3. Nhấn "Hoàn tất đăng ký" | Username: `qa_user_01`<br>Email: `qa01@gmail.com`<br>Password: `123456`<br>FullName: `QA User 01`<br>Phone: `0901112233` | HTTP 200/Redirect `/login`<br>Hiển thị thông báo đăng ký thành công. Mật khẩu được mã hóa SHA-256 trong CSDL. | **PASSED** | Đã verified tự động |
| **TC_AUTH_002** | Auth - Đăng ký | Đăng ký trùng Username đã tồn tại | Username `qa_user_01` đã đăng ký | 1. Truy cập `/register`<br>2. Nhập Username `qa_user_01`<br>3. Nhấn "Hoàn tất đăng ký" | Username: `qa_user_01`<br>Email: `qa_other@gmail.com` | Trả về trang `/register`<br>Hiển thị thông báo bối cảnh: "Tên đăng nhập đã tồn tại trong hệ thống!". | **PASSED** | Validation Boundary |
| **TC_AUTH_003** | Auth - Đăng ký | Đăng ký trùng Email đã tồn tại | Email `qa01@gmail.com` đã đăng ký | 1. Truy cập `/register`<br>2. Nhập Email `qa01@gmail.com`<br>3. Nhấn "Hoàn tất đăng ký" | Username: `qa_user_new`<br>Email: `qa01@gmail.com` | Trả về trang `/register`<br>Hiển thị thông báo lỗi: "Email đã được sử dụng!". | **PASSED** | Validation Boundary |
| **TC_AUTH_004** | Auth - Đăng nhập | Đăng nhập tài khoản USER thành công | Tài khoản `user` tồn tại trong CSDL | 1. Truy cập `/login`<br>2. Nhập username `user` & mật khẩu `user123`<br>3. Nhấn "Đăng nhập" | Username: `user`<br>Password: `user123` | Redirect về `/`<br>Session `loggedInUser` được ghi nhận. Navbar hiển thị tên người dùng và nút "Thoát". | **PASSED** | Verified E2E |
| **TC_AUTH_005** | Auth - Đăng nhập | Đăng nhập tài khoản ADMIN thành công | Tài khoản `admin` tồn tại với role `ADMIN` | 1. Truy cập `/login`<br>2. Nhập username `admin` & mật khẩu `admin123`<br>3. Nhấn "Đăng nhập" | Username: `admin`<br>Password: `admin123` | Redirect trực tiếp tới `/admin`<br>Navbar hiển thị nút badge "Admin" màu vàng. | **PASSED** | Verified E2E |
| **TC_AUTH_006** | Auth - Đăng nhập | Đăng nhập sai mật khẩu | Tài khoản `user` tồn tại | 1. Truy cập `/login`<br>2. Nhập sai mật khẩu | Username: `user`<br>Password: `wrongpass` | Redirect về `/login?error=invalid`<br>Hiển thị thông báo bối cảnh: "Tài khoản hoặc mật khẩu không chính xác!". | **PASSED** | Exception Handling |
| **TC_AUTH_007** | Auth - Đăng nhập Google | Đăng nhập bằng Google Identity Services (OAuth2) | Client ID Google hợp lệ | 1. Truy cập `/login`<br>2. Nhấp nút "Đăng nhập bằng Google"<br>3. Google trả vể Credential JWT Token | Client ID: `1071806914161-...`<br>Credential: `eyJhbGci...` | Gửi POST tới `/login/google`<br>Hệ thống tự tạo/liên kết tài khoản Google và thiết lập Session đăng nhập. | **PASSED** | OAuth2 JWT Flow |
| **TC_AUTH_008** | Auth - Đăng xuất | Đăng xuất giải phóng Session | Người dùng đang trong trạng thái Đăng nhập | 1. Nhấp nút "Thoát" trên Navbar (`/logout`) | N/A | Invalidate Session.<br>Redirect về `/login?logout=true`. Navbar trở lại trạng thái khách. | **PASSED** | Session Management |
| **TC_SEC_001** | Security - Interceptor | Khách chưa đăng nhập cố truy cập đường dẫn Admin | Chưa đăng nhập (Session `loggedInUser == null`) | 1. Nhập trực tiếp URL `http://localhost:8080/admin` trên trình duyệt | URL: `/admin` | `AuthInterceptor` chặn truy cập.<br>Redirect về `/login?error=please_login`. | **PASSED** | Security Boundary |
| **TC_SEC_002** | Security - RBAC | Tài khoản `USER` thường cố truy cập đường dẫn Admin | Đã đăng nhập tài khoản có `role = "USER"` | 1. Đăng nhập tài khoản `user`<br>2. Truy cập URL `/admin` | URL: `/admin` | `AuthInterceptor` phát hiện không có quyền Admin.<br>Redirect về `/login?error=forbidden`. | **PASSED** | Role-Based Access |

---

### PHÂN HỆ 2: LÊN LỊCH TRÌNH TỰ ĐỘNG (TRIP PLANNER & OSRM ALGORITHM)

| Mã TC | Phân hệ / Chức năng | Kịch bản kiểm thử (Test Scenario) | Tiền điều kiện (Prerequisites) | Các bước thực hiện (Test Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Result) | Trạng thái | Ghi chú |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :---: | :--- |
| **TC_TRIP_001** | Trip Planner | Tạo lịch trình 1 ngày tối ưu đường đi | CSDL chứa danh sách các địa điểm Đà Lạt | 1. Truy cập `/trip-planner`<br>2. Chọn số ngày = 1<br>3. Đánh dấu các danh mục quan tâm<br>4. Nhấn "Tạo lịch trình" | Số ngày: 1<br>Sở thích: "Tham quan", "Check-in" | Trả về trang kết quả `/trip-planner/plan`. Hiển thị danh sách địa điểm sắp xếp theo tuyến đường ngắn nhất. | **PASSED** | TSP Algorithm |
| **TC_TRIP_002** | Trip Planner | Lập lịch trình đa ngày (3 ngày 2 đêm) | CSDL chứa đầy đủ địa điểm & tọa độ Lat/Lng | 1. Truy cập `/trip-planner`<br>2. Chọn số ngày = 3<br>3. Nhấn "Tạo lịch trình" | Số ngày: 3 | Thuật toán phân bổ địa điểm hợp lý theo từng Ngày 1, Ngày 2, Ngày 3 không bị trùng lặp. | **PASSED** | TSP Multi-day |
| **TC_TRIP_003** | Trip Planner | Lập lịch trình theo vị trí GPS hiện tại | Trình duyệt cấp quyền vị trí GPS | 1. Nhấn nút "Lấy vị trí hiện tại"<br>2. Nhấn "Tạo lịch trình" | Tọa độ GPS: `11.9404, 108.4583` | Hệ thống lấy tọa độ GPS làm điểm xuất phát (Start Point) cho thuật toán tính khoảng cách. | **PASSED** | Geolocation Integration |
| **TC_TRIP_004** | Trip Planner - OSRM | Gọi API OSRM định tuyến khoảng cách & thời gian | Server OSRM hoạt động | 1. Xem kết quả lịch trình chi tiết | Tuyến đường: Chợ Đà Lạt -> Thung Lũng Tình Yêu | Hiển thị chính xác quãng đường (km) và thời gian di chuyển (phút) thực tế giữa các điểm. | **PASSED** | REST API Integration |
| **TC_TRIP_005** | Trip Planner - Map | Hiển thị bản đồ tương tác LeafletJS & Polyline | Trang kết quả lịch trình được tải | 1. Xem khu vực bản đồ Leaflet | Map Container ID: `map` | Bản đồ hiển thị các Marker đánh số thứ tự (1, 2, 3...) và đường vẽ Polyline nối liền các chặng. | **PASSED** | Interactive Map |

---

### PHÂN HỆ 3: GHÉP XE ĐI CHUNG (CARPOOLING)

| Mã TC | Phân hệ / Chức năng | Kịch bản kiểm thử (Test Scenario) | Tiền điều kiện (Prerequisites) | Các bước thực hiện (Test Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Result) | Trạng thái | Ghi chú |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :---: | :--- |
| **TC_CARPOOL_001**| Carpool | Xem danh sách chuyến xe ghép hiện có | Đã seed dữ liệu chuyến xe | 1. Truy cập `/carpool` | Route: TPHCM - Đà Lạt | Hiển thị danh sách chuyến xe kèm điểm đi/đến, thời gian, số ghế còn trống và giá tiền/ghế. | **PASSED** | UI Listing |
| **TC_CARPOOL_002**| Carpool | Lọc chuyến xe theo điểm xuất phát | Có các chuyến xe từ TPHCM, Bình Dương | 1. Chọn bộ lọc "TP.Hồ Chí Minh"<br>2. Nhấn Lọc | Điểm đi: "TP.Hồ Chí Minh" | Danh sách chỉ hiển thị các chuyến xe xuất phát từ TP.HCM đến Đà Lạt. | **PASSED** | Search & Filter |
| **TC_CARPOOL_003**| Carpool | Đăng tin ghép xe mới | Đã đăng nhập người dùng | 1. Nhấn "Đăng chuyến xe"<br>2. Nhập lộ trình & số ghế<br>3. Xác nhận | Ghế trống: 3<br>Giá: 250,000 VNĐ | Chuyến xe mới được lưu vào CSDL và hiển thị trên giao diện ghép xe. | **PASSED** | Form Submission |

---

### PHÂN HỆ 4: ĐỊA ĐIỂM, KHÁCH SẠN & NHÀ HÀNG (PLACES, HOTELS & RESTAURANTS)

| Mã TC | Phân hệ / Chức năng | Kịch bản kiểm thử (Test Scenario) | Tiền điều kiện (Prerequisites) | Các bước thực hiện (Test Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Result) | Trạng thái | Ghi chú |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :---: | :--- |
| **TC_PLACE_001** | Tourist Places | Hiển thị 50+ địa điểm du lịch kèm hình ảnh | CSDL đã seed 50 địa điểm | 1. Truy cập `/tourist-places` | N/A | Hiển thị lưới danh sách địa điểm có thumbnail Unsplash, giá vé, số sao đánh giá. | **PASSED** | Data Grid |
| **TC_PLACE_002** | Tourist Places | Tìm kiếm địa điểm theo tên từ khóa | CSDL có "Thác Datanla", "Hồ Tuyền Lâm" | 1. Nhập từ khóa "Thác" vào ô tìm kiếm | Từ khóa: `Thác` | Kết quả chỉ trả về các địa điểm chứa từ "Thác" (Thác Datanla, Thác Pongour...). | **PASSED** | Keyword Filter |
| **TC_HOTEL_001** | Hotels | Xem danh sách khách sạn & Resort | CSDL đã seed 10 khách sạn | 1. Truy cập `/hotels` | N/A | Hiển thị thẻ thông tin khách sạn kèm địa chỉ, SĐT, giá/đêm và nút "Đặt phòng". | **PASSED** | Data Grid |
| **TC_HOTEL_002** | Hotels | Kiểm tra hiển thị hình ảnh Unsplash chất lượng cao | Thuộc tính `imageUrl` có dữ liệu | 1. Quan sát hình ảnh thẻ khách sạn | Image URL: `https://images.unsplash.com/...` | Ảnh hiển thị sắc nét, tỉ lệ `object-fit: cover`, không bị vỡ khung. | **PASSED** | Visual Quality |
| **TC_REST_001** | Restaurants | Xem danh sách nhà hàng & quán ăn đặc sản | CSDL đã seed 12 nhà hàng | 1. Truy cập `/restaurants` | N/A | Hiển thị danh sách nhà hàng kèm địa chỉ, hotline và mức giá trung bình/người. | **PASSED** | Data Grid |

---

### PHÂN HỆ 5: ĐẶT PHÒNG KHÁCH SẠN (HOTEL BOOKING SYSTEM)

| Mã TC | Phân hệ / Chức năng | Kịch bản kiểm thử (Test Scenario) | Tiền điều kiện (Prerequisites) | Các bước thực hiện (Test Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Result) | Trạng thái | Ghi chú |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :---: | :--- |
| **TC_BOOK_001** | Hotel Booking | Đặt phòng khách sạn thành công (Happy Path) | Khách sạn tồn tại trong CSDL | 1. Tại `/hotels`, nhấn "Đặt phòng"<br>2. Nhập tên, SĐT, Email, Ngày nhận/trả phòng<br>3. Nhấn "Xác nhận đặt phòng" | Customer: `Phạm Văn Nam`<br>Phone: `0912345678`<br>Checkin: `2026-08-01`<br>Checkout: `2026-08-03` | Tạo thành công đơn đặt phòng.<br>Hiển thị Alert xanh: "ĐẶT PHÒNG THÀNH CÔNG!" với mã đơn dạng `DLBK-XXXX`. Trạng thái: `PENDING`. | **PASSED** | E2E Verified |
| **TC_BOOK_002** | Hotel Booking | Tự động tính tổng chi phí theo số đêm thực tế | Giá phòng: 2,500,000 VNĐ/đêm | 1. Chọn ngày Check-in: 01/08/2026, Check-out: 03/08/2026 (2 đêm) | Số đêm: 2 đêm | Tổng tiền hiển thị chính xác: `5,000,000 VNĐ` (`2,500,000 * 2`). | **PASSED** | Calculation Logic |
| **TC_BOOK_003** | Hotel Booking | Xử lý khi ngày Check-out nhỏ hơn Check-in | Người dùng chọn sai ngày | 1. Chọn Check-in: 05/08/2026, Check-out: 04/08/2026 | Check-in > Check-out | Hệ thống tự động điều chỉnh số đêm tính tiền tối thiểu là 1 đêm, không bị âm tiền. | **PASSED** | Boundary Edge Case |
| **TC_BOOK_004** | Hotel Booking | Tự động điền thông tin nếu khách hàng đã đăng nhập | Người dùng đã đăng nhập tài khoản | 1. Đăng nhập tài khoản<br>2. Nhấn "Đặt phòng" tại khách sạn bất kỳ | LoggedInUser: `QA User 01` | Modal tự động điền sẵn Họ tên, SĐT và Email của tài khoản đang đăng nhập. | **PASSED** | UX Convenience |

---

### PHÂN HỆ 6: QUẢN TRỊ HỆ THỐNG (ADMIN DASHBOARD & CRUD MANAGEMENT)

| Mã TC | Phân hệ / Chức năng | Kịch bản kiểm thử (Test Scenario) | Tiền điều kiện (Prerequisites) | Các bước thực hiện (Test Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Result) | Trạng thái | Ghi chú |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :---: | :--- |
| **TC_ADM_001** | Admin - Overview | Xem các chỉ số thống kê trên Dashboard | Đã đăng nhập tài khoản Admin (`admin`) | 1. Truy cập `/admin` | Role: `ADMIN` | Hiển thị chính xác các thẻ số liệu: Tổng số Khách sạn, Địa điểm, Nhà hàng, Đơn đặt phòng và Người dùng. | **PASSED** | Dashboard Metrics |
| **TC_ADM_002** | Admin - Bookings | Duyệt đơn đặt phòng (`PENDING` -> `CONFIRMED`) | Có đơn đặt phòng mã `DLBK-8300` trạng thái `PENDING` | 1. Truy cập `/admin/bookings`<br>2. Tìm đơn `DLBK-8300`<br>3. Nhấn nút "Duyệt" | Booking ID: `DLBK-8300` | Trạng thái đơn cập nhật thành `CONFIRMED`. Hiển thị badge màu xanh lá. | **PASSED** | Workflow Execution |
| **TC_ADM_003** | Admin - Bookings | Hủy đơn đặt phòng (`PENDING` -> `CANCELLED`) | Có đơn đặt phòng trong hệ thống | 1. Tại `/admin/bookings`, tìm đơn đặt phòng<br>2. Nhấn nút "Hủy" | Action: `CANCELLED` | Trạng thái đơn chuyển thành `CANCELLED`. Hiển thị badge đỏ. | **PASSED** | Workflow Execution |
| **TC_ADM_004** | Admin - Hotels | Thêm khách sạn mới vào CSDL | Đã đăng nhập Admin | 1. Truy cập `/admin/hotels`<br>2. Nhấn "Thêm Khách Sạn Mới"<br>3. Nhập tên, địa chỉ, giá<br>4. Nhấn "Lưu" | Name: `Khách Sạn Ngàn Hoa`<br>Price: `600000` | Khách sạn mới được lưu vào MySQL. Danh sách khách sạn tăng thêm 1 mục. | **PASSED** | CRUD Create |
| **TC_ADM_005** | Admin - Places | Xóa địa điểm du lịch khỏi hệ thống | Có địa điểm ID `TP999` trong CSDL | 1. Truy cập `/admin/tourist-places`<br>2. Nhấn biểu tượng Thùng rác tại địa điểm `TP999`<br>3. Xác nhận xóa | ID: `TP999` | Địa điểm bị xóa khỏi CSDL. Hiển thị thông báo xóa thành công. | **PASSED** | CRUD Delete |
| **TC_ADM_006** | Admin - Restaurants| Thêm nhà hàng mới vào hệ thống | Đã đăng nhập Admin | 1. Truy cập `/admin/restaurants`<br>2. Nhấn "Thêm Quán Mới"<br>3. Điền thông tin & Lưu | Name: `Lẩu Gà Lá É 343`<br>Price: `150000` | Nhà hàng mới lưu thành công và xuất hiện trên giao diện trang khách `/restaurants`. | **PASSED** | CRUD Create |
| **TC_ADM_007** | Admin - Users | Quản lý & Phân quyền tài khoản người dùng | Có tài khoản `qa_user_01` trong hệ thống | 1. Truy cập `/admin/users`<br>2. Chọn Role `ADMIN` cho `qa_user_01`<br>3. Nhấn "Lưu" | Target User: `qa_user_01`<br>New Role: `ADMIN` | Role của tài khoản đổi thành `ADMIN`. Tài khoản này có thể truy cập `/admin`. | **PASSED** | RBAC Management |

---

### PHÂN HỆ 7: BÀI VIẾT & LIÊN HỆ (BLOG & CONTACT)

| Mã TC | Phân hệ / Chức năng | Kịch bản kiểm thử (Test Scenario) | Tiền điều kiện (Prerequisites) | Các bước thực hiện (Test Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Result) | Trạng thái | Ghi chú |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :---: | :--- |
| **TC_BLOG_001** | Blog | Xem danh sách bài viết cẩm nang du lịch | Đã seed dữ liệu bài viết | 1. Truy cập `/blog` | N/A | Giao diện hiển thị các bài viết hướng dẫn du lịch Đà Lạt có hình ảnh thumbnail, tác giả và ngày đăng. | **PASSED** | Content View |
| **TC_CONT_001** | Contact | Gửi thông tin liên hệ / góp ý | Trang Liên hệ hoạt động | 1. Truy cập `/contact`<br>2. Nhập Họ tên, Email, Nội dung<br>3. Nhấn "Gửi liên hệ" | Name: `Lê Minh`<br>Email: `minh@gmail.com`<br>Msg: `Cần tư vấn tour` | Hiển thị thông báo cảm ơn đã gửi liên hệ thành công. | **PASSED** | Contact Form |

---

## 🛠️ HƯỚNG DẪN CHẠY KIỂM THỬ TỰ ĐỘNG (AUTOMATION TESTING INSTRUCTIONS)

### 1. Kiểm Thử Đơn Vị & Biên Dịch (Unit & Compilation Test)
Chạy lệnh sau tại thư mục gốc dự án:
```powershell
.\mvnw.cmd test-compile
```

### 2. Kiểm Thử Giao Diện & Luồng End-to-End Trình Duyệt (E2E Browser Test)
Khởi động ứng dụng Spring Boot:
```powershell
.\mvnw.cmd spring-boot:run
```
Sau đó truy cập các URL để xác nhận kết quả:
- **Trang chủ & Navbar**: `http://localhost:8080/`
- **Lên lịch trình tự động**: `http://localhost:8080/trip-planner`
- **Danh sách & Đặt phòng Khách sạn**: `http://localhost:8080/hotels`
- **Đăng nhập Google & Thường**: `http://localhost:8080/login`
- **Trang Quản trị Admin**: `http://localhost:8080/admin` (Đăng nhập: `admin` / `admin123`)
