# 📊 MA TRẬN KỊCH BẢN KIỂM THỬ CHI TIẾT (FULL TEST CASES MATRIX) - DALATTRAVEL

**Dự án:** DaLatTravel - Hệ Thống Du Lịch Đà Lạt Thông Minh & Kết Nối Ghép Xe  
**Áp dụng kỹ thuật:** Phân vùng tương đương (Equivalence Partitioning), Phân tích giá trị biên (Boundary Value Analysis), Bảng quyết định (Decision Table), Kiểm thử luồng E2E & API Security.  
**Tác giả:** QA/QC Automation & Manual Testing Team  
**Ngày cập nhật:** 25/07/2026  

---

## 👨‍💻 PHẦN 1: PHÂN HỆ XÁC THỰC & NGƯỜI DÙNG (AUTHENTICATION & USER)

| Test Case ID | Feature | Mô tả Kịch bản Kiểm thử | Điều kiện tiền đề (Pre-conditions) | Các bước thực hiện (Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Output) | Kỹ thuật áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_AUTH_001** | Đăng ký | Đăng ký tài khoản người dùng mới thành công | Chưa đăng nhập, Username chưa tồn tại | 1. Mở trang `/register`<br>2. Nhập thông tin hợp lệ<br>3. Bấm "Hoàn tất đăng ký" | Username: `qa_user_01`<br>Email: `qa01@gmail.com`<br>Pass: `123456`<br>Phone: `0901234567` | Đăng ký thành công, nhận thông báo, chuyển hướng về `/login`. Mật khẩu băm SHA-256. | Hộp đen (Normal Flow) |
| **TC_AUTH_002** | Đăng ký | Đăng ký thất bại khi trùng tên đăng nhập (Username) | Username `qa_user_01` đã tồn tại | 1. Mở trang `/register`<br>2. Nhập Username `qa_user_01`<br>3. Nhấn "Hoàn tất đăng ký" | Username: `qa_user_01`<br>Email: `qa_other@gmail.com` | Trả về trang `/register`, báo lỗi: "Tên đăng nhập đã tồn tại trong hệ thống!". | Phân vùng tương đương (Invalid) |
| **TC_AUTH_003** | Đăng ký | Đăng ký thất bại khi trùng Email đã sử dụng | Email `qa01@gmail.com` đã sử dụng | 1. Mở trang `/register`<br>2. Nhập Email `qa01@gmail.com`<br>3. Nhấn "Hoàn tất đăng ký" | Username: `new_qa_user`<br>Email: `qa01@gmail.com` | Báo lỗi "Email đã được sử dụng!". CSDL không tạo thêm dòng mới. | Phân vùng tương đương (Invalid) |
| **TC_AUTH_004** | Đăng nhập | Đăng nhập tài khoản USER thành công | Tài khoản `user` / `user123` tồn tại | 1. Mở trang `/login`<br>2. Điền username `user` & pass `user123`<br>3. Bấm "Đăng nhập" | User: `user`<br>Pass: `user123` | Đăng nhập thành công, Redirect về `/`. Navbar hiển thị tên người dùng và nút "Thoát". | Normal Flow |
| **TC_AUTH_005** | Đăng nhập | Đăng nhập tài khoản ADMIN thành công | Tài khoản `admin` có quyền `ROLE_ADMIN` | 1. Mở trang `/login`<br>2. Điền username `admin` & password hợp lệ<br>3. Bấm "Đăng nhập" | User: `admin`<br>Pass: `********` | Đăng nhập thành công, Redirect trực tiếp tới `/admin`. Navbar hiển thị nút badge "Admin" vàng. | Normal Flow (RBAC) |
| **TC_AUTH_006** | Đăng nhập | Đăng nhập thất bại khi nhập sai mật khẩu | Tài khoản `user` tồn tại | 1. Nhập username `user`<br>2. Nhập sai password `wrongpass`<br>3. Bấm "Đăng nhập" | User: `user`<br>Pass: `wrongpass` | Redirect về `/login?error=invalid`. Báo lỗi: "Tài khoản hoặc mật khẩu không chính xác!". | Negative Testing |
| **TC_AUTH_007** | Đăng nhập Google | Đăng nhập bằng Google Identity Services (OAuth2) | Client ID Google hợp lệ | 1. Mở trang `/login`<br>2. Nhấp nút "Đăng nhập bằng Google"<br>3. Google trả vể JWT Credential Token | Client ID: `your_google_client_id...`<br>Credential: `eyJhbGci...` | Gửi POST tới `/login/google`, decode JWT thành công, tự tạo/liên kết tài khoản Google & lập Session. | OAuth2 JWT Flow |
| **TC_AUTH_008** | Đăng xuất | Đăng xuất giải phóng Session an toàn | Người dùng đang trong trạng thái đăng nhập | 1. Nhấp nút "Thoát" trên Navbar (`/logout`) | N/A | Hủy bỏ Session (`session.invalidate()`). Redirect về `/login?logout=true`. Navbar trở lại trang khách. | Session Cleanup |

---

## 🗺️ PHẦN 2: PHÂN HỆ LÊN LỊCH TRÌNH TỰ ĐỘNG & BẢN ĐỒ (TRIP PLANNER, TSP & OSRM)

| Test Case ID | Feature | Mô tả Kịch bản Kiểm thử | Điều kiện tiền đề | Các bước thực hiện | Dữ liệu kiểm thử | Kết quả mong đợi | Kỹ thuật áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_TRIP_001** | Trip Planner | Lập lịch trình 1 ngày tối ưu tuyến đường bằng thuật toán TSP | CSDL chứa danh sách địa điểm Đà Lạt | 1. Truy cập `/trip-planner`<br>2. Chọn số ngày = 1<br>3. Chọn sở thích "Tham quan", "Check-in"<br>4. Nhấn "Tạo lịch trình" | Số ngày: 1<br>Sở thích: Tham quan, Check-in | Trả về trang kết quả `/trip-planner/plan`. Thuật toán TSP sắp xếp lịch trình các điểm theo thứ tự di chuyển ngắn nhất. | TSP Algorithm |
| **TC_TRIP_002** | Trip Planner | Lập lịch trình 3 ngày 2 đêm không bị trùng lặp | CSDL có đầy đủ địa điểm kèm tọa độ Lat/Lng | 1. Chọn số ngày = 3<br>2. Nhấn "Tạo lịch trình" | Số ngày: 3 | Thuật toán phân bổ địa điểm hợp lý theo Ngày 1, Ngày 2, Ngày 3, không bị lặp lại địa điểm giữa các ngày. | TSP Multi-day |
| **TC_TRIP_003** | Trip Planner | Lập lịch trình xuất phát từ vị trí GPS hiện tại của người dùng | Trình duyệt cấp quyền vị trí GPS | 1. Nhấn nút "Lấy vị trí hiện tại"<br>2. Nhấn "Tạo lịch trình" | Tọa độ GPS: `11.9404, 108.4583` | Hệ thống lấy tọa độ GPS làm mốc xuất phát (Start Point) cho thuật toán tính khoảng cách. | Geolocation Test |
| **TC_TRIP_004** | Trip Planner - OSRM | Gọi API OSRM định tuyến khoảng cách (km) và thời gian (phút) | Server OSRM hoạt động | 1. Xem bảng chi tiết hành trình tại trang kết quả | Route: Chợ Đà Lạt $\rightarrow$ Thung Lũng Tình Yêu | Hiển thị chính xác khoảng cách (km) và thời gian di chuyển ước tính (phút) thực tế từ API OpenStreetMap. | OSRM REST API |
| **TC_TRIP_005** | Trip Planner - Map | Hiển thị bản đồ tương tác LeafletJS & vẽ đường Polyline | Trang kết quả được nạp thành công | 1. Quan sát khu vực bản đồ Leaflet | Map ID: `map` | Bản đồ hiển thị các Marker đánh số thứ tự (1, 2, 3...) và đường vẽ Polyline nối liền các địa điểm trong lịch trình. | Interactive Map UI |

---

## 🚗 PHẦN 3: PHÂN HỆ GHÉP XE ĐI CHUNG & ĐẶT PHÒNG KHÁCH SẠN (CARPOOL & HOTEL BOOKING)

| Test Case ID | Feature | Mô tả Kịch bản Kiểm thử | Điều kiện tiền đề | Các bước thực hiện | Dữ liệu kiểm thử | Kết quả mong đợi | Kỹ thuật áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_CARPOOL_001**| Carpool | Xem danh sách các chuyến xe ghép đi chung | CSDL có dữ liệu chuyến xe | 1. Truy cập `/carpool` | Route: TPHCM - Đà Lạt | Hiển thị danh sách chuyến xe gồm điểm đi/đến, thời gian khởi hành, số ghế trống và giá tiền/ghế. | UI Listing Test |
| **TC_CARPOOL_002**| Carpool | Lọc chuyến xe ghép theo điểm xuất phát | Có chuyến xe từ TP.HCM | 1. Chọn bộ lọc điểm đi "TP.Hồ Chí Minh"<br>2. Nhấn Lọc | Điểm đi: "TP.Hồ Chí Minh" | Danh sách chỉ hiển thị các chuyến xe xuất phát từ TP.HCM đến Đà Lạt. | Filter API Test |
| **TC_BOOK_001** | Hotel Booking | Đặt phòng khách sạn thành công (Happy Path) | Khách sạn tồn tại trong CSDL | 1. Tại `/hotels`, bấm "Đặt phòng"<br>2. Điền Họ tên, SĐT, Email, Ngày Check-in/out<br>3. Nhấn "Xác nhận đặt phòng" | Customer: "Phạm Văn Nam"<br>Phone: "0912345678"<br>Check-in: 01/08/2026<br>Check-out: 03/08/2026 | Tạo đơn đặt phòng thành công với mã `DLBK-XXXX`. Trạng thái `PENDING`. Hiển thị Alert xanh xác nhận. | End-to-End Flow |
| **TC_BOOK_002** | Hotel Booking | Tự động tính tổng tiền theo số đêm lưu trú thực tế | Giá khách sạn: 2,500,000 VNĐ/đêm | 1. Chọn Check-in: 01/08/2026, Check-out: 03/08/2026 (2 đêm) | Số đêm: 2 | Tổng tiền hiển thị chính xác: `5,000,000 VNĐ` (`2,500,000 * 2`). | Calculation Logic |
| **TC_BOOK_003** | Hotel Booking | Xử lý biên khi chọn ngày Check-out nhỏ hơn Check-in | Người dùng chọn sai ngày | 1. Chọn Check-in: 05/08/2026, Check-out: 03/08/2026 | Check-in > Check-out | Hệ thống tự động quy đổi số đêm tối thiểu bằng 1 đêm, tổng tiền không bị âm. | Boundary Edge Case |
| **TC_BOOK_004** | Hotel Booking | Tự động điền thông tin nếu khách hàng đã đăng nhập | Khách hàng đã đăng nhập | 1. Đăng nhập tài khoản<br>2. Nhấn "Đặt phòng" tại khách sạn | LoggedUser: `QA User 01` | Modal đặt phòng tự động pre-fill sẵn Họ tên, SĐT và Email của tài khoản đang đăng nhập. | UX Convenience |

---

## 👑 PHẦN 4: PHÂN HỆ QUẢN TRỊ VIÊN & PHÂN QUYỀN (ADMIN & RBAC)

| Test Case ID | Feature | Mô tả Kịch bản Kiểm thử | Điều kiện tiền đề | Các bước thực hiện | Dữ liệu kiểm thử | Kết quả mong đợi | Kỹ thuật áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_ADM_001** | Admin Overview | Xem bảng thống kê chỉ số Dashboard Admin | Đăng nhập tài khoản Admin (`admin`) | 1. Truy cập `/admin` | Role: `ADMIN` | Hiển thị chính xác các thẻ chỉ số: Tổng số Khách sạn, Địa điểm, Nhà hàng, Đơn đặt phòng và Người dùng. | Dashboard Metrics |
| **TC_ADM_002** | Admin Bookings | Admin duyệt đơn đặt phòng từ `PENDING` $\rightarrow$ `CONFIRMED` | Có đơn đặt phòng `DLBK-8300` trạng thái `PENDING` | 1. Truy cập `/admin/bookings`<br>2. Tìm đơn `DLBK-8300`<br>3. Bấm nút "Duyệt" | Booking ID: `DLBK-8300` | Trạng thái đơn chuyển thành `CONFIRMED`. Hiển thị badge màu xanh lá. | State Transition |
| **TC_ADM_003** | Admin Bookings | Admin hủy đơn đặt phòng (`PENDING` $\rightarrow$ `CANCELLED`) | Có đơn đặt phòng trong CSDL | 1. Tại `/admin/bookings`, bấm nút "Hủy" | Action: `CANCELLED` | Trạng thái đơn cập nhật thành `CANCELLED`. Hiển thị badge màu đỏ. | State Transition |
| **TC_ADM_004** | Admin Hotels | Admin thêm mới Khách sạn vào CSDL | Đã đăng nhập Admin | 1. Truy cập `/admin/hotels`<br>2. Bấm "Thêm Khách Sạn Mới"<br>3. Nhập thông tin & Lưu | Name: "Khách Sạn Ngàn Hoa"<br>Price: 600000 | Khách sạn mới được lưu vào CSDL. Trang hiển thị thông báo thành công. | CRUD Create |
| **TC_ADM_005** | Admin Places | Admin xóa địa điểm du lịch khỏi hệ thống | Có địa điểm ID `TP999` trong CSDL | 1. Truy cập `/admin/tourist-places`<br>2. Bấm nút Xóa tại `TP999`<br>3. Xác nhận xóa | ID: `TP999` | Địa điểm bị xóa khỏi CSDL. Danh sách cập nhật ngay lập tức. | CRUD Delete |
| **TC_ADM_006** | Admin Users | Admin quản lý & phân quyền người dùng (RBAC) | Có tài khoản `qa_user_01` trong CSDL | 1. Truy cập `/admin/users`<br>2. Chọn Role `ADMIN` cho `qa_user_01`<br>3. Bấm "Lưu" | User: `qa_user_01`<br>Role: `ADMIN` | Phân quyền thành công. Tài khoản `qa_user_01` có thể truy cập trang Quản trị `/admin`. | RBAC Management |

---

## 💻 PHẦN 5: CÁC KỊCH BẢN KIỂM THỬ GIAO DIỆN & TRẢI NGHIỆM (UI/UX AUTOMATION TEST CASES)

| Test Case ID | Phân hệ / Màn hình | Kịch bản kiểm thử (Test Scenario) | Điều kiện tiền đề (Prerequisites) | Các bước thực hiện (Test Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Result) | Kỹ thuật áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_UI_001** | Đăng ký | Hiển thị thông báo lỗi validate form khi để trống trường | Đang ở trang `/register` | 1. Không nhập gì<br>2. Bấm nút "Hoàn tất đăng ký" | Form rỗng | Trình duyệt hiển thị nhắc nhở HTML5 `required` tại ô nhập liệu đầu tiên chưa điền. | Client Validation |
| **TC_UI_002** | Đăng ký | Kiểm tra mật khẩu ngắn dưới 6 ký tự | Đang ở trang `/register` | 1. Nhập username, email đúng<br>2. Nhập mật khẩu '123'<br>3. Bấm Đăng ký | Password: `123` | Hiển thị thông báo lỗi bối cảnh: "Mật khẩu phải từ 6 ký tự trở lên". | Boundary Analysis |
| **TC_UI_003** | Đăng nhập | Kiểm tra hiển thị thông báo lỗi khi đăng nhập sai thông tin | Đang ở trang `/login` | 1. Nhập username đúng<br>2. Nhập password sai<br>3. Bấm "Đăng nhập" | User: `user`<br>Pass: `wrongpass` | Redirect về `/login?error=invalid`. Alert đỏ hiển thị: "Tài khoản hoặc mật khẩu không chính xác!". | UX Notification |
| **TC_UI_004** | Đăng nhập | Duy trì phiên làm việc sau khi Refresh trang (Reload F5) | Đã đăng nhập tài khoản | 1. Nhấn nút F5 (Reload) trình duyệt | F5 Refresh | Session được duy trì. Navbar vẫn hiển thị tên tài khoản và nút "Thoát". | Session Persistence |
| **TC_UI_005** | Đăng xuất | Đăng xuất khỏi hệ thống và xóa sạch Session | Đã đăng nhập | 1. Nhấn nút "Thoát" trên Navbar | Click Logout | Chuyển về `/login?logout=true`. Nhấn nút Back trên trình duyệt không thể quay lại trạng thái đã đăng nhập. | Session Cleanup |
| **TC_UI_006** | Navbar | Kiểm tra hiển thị Navbar 1 dòng không bị rớt chữ trên màn hình Desktop | Màn hình resolution >= 1200px | 1. Mở trang chủ hoặc các trang con<br>2. Quan sát các nút trên Navbar | Desktop Viewport | Tất cả 8 mục menu ("Trang chủ", "Lên lịch trình", "Ghép xe đi chung"...) nằm trên 1 hàng ngang duy nhất. | Layout Responsive |
| **TC_UI_007** | Navbar | Tự động cắt ngắn tên người dùng quá dài (Truncate Badge) | Đăng nhập bằng Google có tên dài | 1. Đăng nhập Google tài khoản "4251_Lê Trúc Thanh"<br>2. Quan sát Navbar | Name: `4251_Lê Trúc Thanh` | Tên hiển thị gọn gàng dạng `4251_Lê Trúc...` với `max-width: 150px`, không kéo giãn hay làm rớt dòng Navbar. | UI Truncate Test |
| **TC_UI_008** | Khách sạn | Hiển thị Modal Đặt phòng với hiệu ứng trượt mượt mà | Đang ở trang `/hotels` | 1. Bấm nút "Đặt phòng" tại 1 khách sạn | Click "Đặt phòng" | Modal Bootstrap xuất hiện căn giữa màn hình với hiệu ứng mượt, background mờ tối màu. | Modal Animation |
| **TC_UI_009** | Khách sạn | Tự động điền ngày Check-in (hôm nay) và Check-out (sau 2 ngày) | Mở modal đặt phòng | 1. Quan sát 2 ô ngày Check-in và Check-out | Date Input | Ô Check-in tự động điền ngày hôm nay (YYYY-MM-DD), ô Check-out tự động điền ngày sau 2 ngày. | Default Values UX |
| **TC_UI_010** | Địa điểm | Tìm kiếm địa điểm theo từ khóa trực tiếp | Đang ở trang `/tourist-places` | 1. Nhập từ khóa "Thác" vào ô tìm kiếm<br>2. Bấm Tìm kiếm | Query: `Thác` | Giao diện hiển thị danh sách các địa điểm chứa từ "Thác" (Thác Datanla, Thác Pongour...). | Keyword Search UI |
| **TC_UI_011** | Admin | Chặn truy cập giao diện Admin đối với tài khoản Customer | Đăng nhập tài khoản `user` | 1. Gõ URL `http://localhost:8080/admin` trên thanh địa chỉ | URL: `/admin` | Chặn truy cập. Chuyển hướng về `/login?error=forbidden` kèm thông báo không có quyền Admin. | Protected Route UI |

---

## 🛡️ PHẦN 6: CÁC KỊCH BẢN KIỂM THỬ API, PHÂN QUYỀN & BẢO MẬT (API AUTOMATION & SECURITY TEST CASES)

| Test Case ID | Phân hệ / Chức năng | Kịch bản kiểm thử (Test Scenario) | Điều kiện tiền đề | Các bước thực hiện (Test Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Result) | Kỹ thuật áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_API_001** | Auth | API Đăng ký tài khoản thành công | Username & Email mới | 1. POST `/register`<br>2. Form params điền đủ | User: `api_user`<br>Email: `api@gmail.com` | HTTP 200/Redirect. Tài khoản tạo thành công trong CSDL. Mật khẩu mã hóa SHA-256. | API Normal Flow |
| **TC_API_002** | Auth | API Đăng nhập thành công thiết lập Session | Tài khoản `user` có trong CSDL | 1. POST `/login`<br>2. Params `username=user`, `password=user123` | User: `user`<br>Pass: `user123` | HTTP 302 Found/Redirect `/`. Session `loggedInUser` được khởi tạo thành công. | Session Auth API |
| **TC_API_003** | Auth - Google | API Đăng nhập Google xác thực JWT Token | Credential Token Google hợp lệ | 1. POST `/login/google`<br>2. Param `credential={jwt_token}` | Credential: `eyJhbGci...` | Decode JWT trích xuất email/name thành công, tạo User mới trong CSDL và cấp Session. | Google OAuth2 API |
| **TC_API_004** | Security | Truy cập API Admin không có Session | Session `loggedInUser == null` | 1. GET `/admin`<br>2. Không đính kèm Session Cookie | No Session | `AuthInterceptor` chặn. Response HTTP 302 Redirect về `/login?error=please_login`. | Security Filter API |
| **TC_API_005** | Security | Truy cập API Admin với Session `USER` không đủ quyền | Session user role `USER` | 1. GET `/admin`<br>2. Đính kèm Cookie Session của `USER` | Role: `USER` | `AuthInterceptor` chặn. Response HTTP 302 Redirect về `/login?error=forbidden`. | RBAC Security API |
| **TC_API_006** | Booking | API Đặt phòng khách sạn tạo mã `DLBK-XXXX` | Khách sạn ID 1 tồn tại | 1. POST `/hotels/book`<br>2. Form data điền thông tin đặt phòng | Hotel ID: 1<br>Checkin: 2026-08-01<br>Checkout: 2026-08-03 | HTTP 302 Redirect `/hotels`. Đơn lưu vào CSDL với mã `DLBK-XXXX`, status `PENDING`, tổng tiền tính chuẩn. | Booking Processing API |
| **TC_API_007** | Admin | API Duyệt đơn đặt phòng (`PENDING` $\rightarrow$ `CONFIRMED`) | Session `ADMIN`, đơn `DLBK-8300` tồn tại | 1. POST `/admin/bookings/update-status`<br>2. Params `bookingId=1`, `status=CONFIRMED` | Status: `CONFIRMED` | HTTP 302 Redirect `/admin/bookings`. Status đơn đổi thành `CONFIRMED` trong CSDL. | Admin Action API |
| **TC_API_008** | Security | Chống tấn công SQL Injection tại form tìm kiếm & đăng nhập | Không có | 1. Input query: `admin' OR '1'='1`<br>2. Thử đăng nhập hoặc tìm kiếm | SQL Payload | Hệ thống dùng Spring Data JPA Prepared Statements. Không bị SQL Injection, không lộ lỗi SQL. | SQL Injection Prev |
| **TC_API_009** | Security | Chống tấn công Cross-Site Scripting (XSS) | Form đặt phòng / đăng ký | 1. Input tên: `<script>alert('xss')</script>`<br>2. Gửi request | XSS Payload | Thymeleaf tự động escape HTML entities (`th:text`). Mã script không bị thực thi trên trình duyệt. | XSS Sanitization |
