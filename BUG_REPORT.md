# 🐛 NHẬT KÝ THEO DÕI & QUẢN LÝ LỖI (DEFECT LOG / BUG REPORT)
**Dự án:** DaLatTravel - Hệ Thống Du Lịch Đà Lạt Thông Minh  
**Quy trình quản lý lỗi:** Chuẩn MantisBT / Jira Defect Tracking  
**Tác giả:** QA/QC Automation & Manual Testing Team  

---

## 📌 QUY TRÌNH PHÂN LOẠI MỨC ĐỘ NGHIÊM TRỌNG (SEVERITY) & ƯU TIÊN (PRIORITY)

- **Severity (Mức độ nghiêm trọng):**
  - 🔴 **Critical:** Hệ thống sụp đổ, gián đoạn luồng đặt phòng hoặc hổng bảo mật truy cập trái phép.
  - 🟠 **High:** Lỗi sai lệch tính toán tiền đặt phòng, sai thuật toán định tuyến đường đi.
  - 🟡 **Medium:** Lỗi vỡ giao diện, tràn dòng văn bản trên Navbar.
  - 🟢 **Low:** Lỗi chính tả, lỗi khoảng cách hiển thị.

- **Status:** `NEW` $\rightarrow$ `OPEN` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `RESOLVED` $\rightarrow$ `CLOSED`.

---

## 📋 MẪU BÁO CÁO LỖI VÀ ĐÃ KHẮC PHỤC (DEFECT REPORTS)

### 🔴 BUG-001: Người dùng không có quyền ADMIN vẫn truy cập được trang Quản trị khi gõ URL trực tiếp

- **Bug ID:** `BUG-001`
- **Tiêu đề:** Khách vỡ lòng hoặc tài khoản có role `USER` nhập URL `/admin` trên trình duyệt vẫn xem được Dashboard.
- **Phân hệ:** Security & Authorization (RBAC).
- **Severity:** 🔴 Critical | **Priority:** P1 (Highest).
- **Trạng thái:** `CLOSED` (Đã khắc phục 100%).
- **Các bước tái hiện (Steps to Reproduce):**
  1. Đăng nhập tài khoản `user` (role `USER`).
  2. Mở tab mới gõ trực tiếp URL `http://localhost:8080/admin`.
- **Kết quả thực tế (trước khi sửa):** Trang Admin Dashboard nạp thành công, cho phép xem danh sách đơn đặt phòng.
- **Kết quả mong đợi:** Từ chối truy cập, chuyển hướng về `/login?error=forbidden` kèm thông báo lỗi.
- **Giải pháp khắc phục:** Tạo class `AuthInterceptor.java` triển khai `HandlerInterceptor`, kiểm tra `session.loggedInUser` và `user.getRole().equalsIgnoreCase("ADMIN")`. Đăng ký interceptor trong `WebMvcConfig.java` cho đường dẫn `/admin/**`.

---

### 🟠 BUG-002: Sai lệch tổng chi phí đặt phòng khi người dùng chọn ngày Check-out nhỏ hơn Check-in

- **Bug ID:** `BUG-002`
- **Tiêu đề:** Tổng tiền đặt phòng bị tính âm hoặc bằng 0 khi chọn ngày trả phòng trước ngày nhận phòng.
- **Phân hệ:** Hotel Booking Module.
- **Severity:** 🟠 High | **Priority:** P2 (High).
- **Trạng thái:** `CLOSED` (Đã khắc phục 100%).
- **Các bước tái hiện:**
  1. Mở modal đặt phòng tại khách sạn bất kỳ.
  2. Chọn ngày Check-in: `05/08/2026`, Check-out: `03/08/2026`.
  3. Bấm "Xác nhận đặt phòng".
- **Kết quả thực tế (trước khi sửa):** Tổng tiền lưu vào CSDL bị âm hoặc bằng 0 VNĐ.
- **Kết quả mong đợi:** Hệ thống tự động tính tối thiểu 1 đêm lưu trú, tổng tiền luôn chính xác >= giá 1 đêm.
- **Giải pháp khắc phục:** Bổ sung logic kiểm tra `ChronoUnit.DAYS.between(checkIn, checkOut)` trong `HotelController.java`. Nếu `days <= 0`, tự động gán `days = 1`.

---

### 🟡 BUG-003: Tên người dùng từ Google OAuth2 bị vỡ dòng làm hỏng tỉ lệ Navbar

- **Bug ID:** `BUG-003`
- **Tiêu đề:** Tên hiển thị tài khoản Google quá dài (như `4251_Lê Trúc Thanh`) làm vỡ các nút menu Navbar xuống 2 hàng.
- **Phân hệ:** UI / Navbar Header Component.
- **Severity:** 🟡 Medium | **Priority:** P3 (Medium).
- **Trạng thái:** `CLOSED` (Đã khắc phục 100%).
- **Các bước tái hiện:**
  1. Đăng nhập bằng tài khoản Google có tên dài.
  2. Quan sát thanh Navigation Bar trên màn hình Desktop.
- **Kết quả thực tế (trước khi sửa):** Các liên kết "Lên lịch trình", "Ghép xe đi chung" bị vỡ dòng thành 2 hàng chữ xấu xí.
- **Kết quả mong đợi:** Thanh Navbar luôn giữ trên 1 dòng duy nhất, tên tài khoản được cắt ngắn gọn bằng dấu `...`.
- **Giải pháp khắc phục:** Bổ sung CSS `white-space: nowrap !important;` cho `.nav-link` và tạo class `.user-name-badge` với `max-width: 150px; text-overflow: ellipsis; overflow: hidden;` trong `fragments/header.html`.
