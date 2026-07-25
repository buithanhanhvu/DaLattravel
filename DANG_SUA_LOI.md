# 🔧 GHI CHÚ ĐANG SỬA LỖI — DaLatTravel

> Cập nhật: 25/07/2026 09:54
> Mục đích: Ghi lại trạng thái sửa lỗi để tiếp tục khi bị gián đoạn

---

## ✅ ĐÃ SỬA XONG

| # | Lỗi | File đã sửa |
|---|-----|-------------|
| 1 | Category pills hiển thị `Kh?ch s?n`, `Nh? h?ng` — lỗi encoding DB | `trip-planner/index.html` — hardcode pills trực tiếp HTML |
| 2 | `filterPlaces()` không ẩn card vì Bootstrap `d-flex !important` | `trip-planner/index.html` — đổi sang `classList.add/remove('d-none')` |
| 3 | GPS lấy tọa độ nhưng không điền vào ô input | `trip-planner/index.html` — fix `inputLocation.value` |

---

## 🔄 ĐANG SỬA — DỮ LIỆU DB BỊ HỎNG ENCODING

### Nguyên nhân gốc rễ
- Import bằng PowerShell pipe → MySQL CLI bị mất dấu tiếng Việt trên Windows
- Dữ liệu trong DB: `Th?c Datanla`, `Kh?ch s?n` (dấu ? thay cho ký tự Vietnamese)

### Giải pháp đã chọn
**Dùng Java DataSeeder** (không dùng SQL import) vì JDBC connection giữ đúng UTF-8mb4

### Trạng thái DataSeeder.java
File: `src/main/java/com/example/dalattravel/config/DataSeeder.java`

- ✅ Logic xóa dữ liệu cũ theo đúng thứ tự FK (reviews → restaurants → hotels → tourist_places → regions → categories)
- ✅ 50 địa điểm du lịch Đà Lạt (TP001–TP050)  
- ✅ 10 khách sạn (Dalat Palace, Ana Mandara, Terracotta, Swiss-Belresort, Mường Thanh, Tulip, Du Parc, Tui Mo To Homestay, Legris, Dreams)
- ✅ 12 nhà hàng (Lẩu Gà Lá É, Lẩu Bò Ba Toa, Bánh Căn, Nem Nướng Bà Hùng, Bánh Mì Xíu Mại, Horizon Coffee, Bánh Tráng Di Đinh, Gà Rừng, Kem Bơ, Cafe Tung, Phở Lúa, Nhà Hàng Thanh Thủy)
- ✅ 4 transport options + 2 vehicles + 2 blog posts

> **⚠️ CHƯA XONG:** Server chưa restart để áp dụng DataSeeder mới

---

## 🚀 CÁC BƯỚC CẦN LÀM TIẾP

### Bước 1 — Truncate dữ liệu cũ bị lỗi encoding

Chạy trong MySQL Workbench:
```sql
USE dalattravel_db;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE reviews;
TRUNCATE TABLE restaurants;
TRUNCATE TABLE hotels;
TRUNCATE TABLE tourist_place_images;
TRUNCATE TABLE tourist_places;
TRUNCATE TABLE regions;
TRUNCATE TABLE categories;
TRUNCATE TABLE transport_options;
TRUNCATE TABLE vehicles;
TRUNCATE TABLE blog_posts;
SET FOREIGN_KEY_CHECKS = 1;
```

### Bước 2 — Kill task server cũ + restart Spring Boot
```powershell
# Trong terminal DaLattravel:
.\mvnw.cmd spring-boot:run
```
DataSeeder sẽ tự chạy seed 50+10+12 records khi start.

### Bước 3 — Verify trên browser
- http://localhost:8080/trip-planner
- Filter **Khách sạn** → 10 khách sạn
- Filter **Nhà hàng/Quán ăn** → 12 nhà hàng  
- Filter **Địa điểm du lịch** → 50 địa điểm

---

## 📋 LỖI CÒN TỒN ĐỌNG (chưa xử lý)

| # | Lỗi | Mức độ |
|---|-----|--------|
| 1 | Thuật toán lịch trình — cần tối ưu tuyến (TSP) trước, rồi nhóm theo ngày, rồi tính chi phí xe | Quan trọng |
| 2 | Chi phí xe tính chưa đúng (tính theo km thực tế sau khi tối ưu tuyến) | Quan trọng |
| 3 | Bản đồ result.html chưa vẽ đường đi Leaflet polyline giữa các điểm | Trung bình |
| 4 | `Restaurant` model thiếu `latitude`/`longitude` → không hiển thị được trên bản đồ | Trung bình |

---

## 📁 FILE QUAN TRỌNG

| File | Trạng thái |
|------|-----------|
| `config/DataSeeder.java` | ⚠️ Đã viết xong — chờ restart server |
| `templates/trip-planner/index.html` | ✅ Đã fix 3 lỗi |
| `controller/TripPlannerController.java` | ✅ OK |
| `service/TripPlannerService.java` | ⚠️ Cần sửa thuật toán TSP |
| `logic lịch trình .md` | Đặc tả thuật toán của user |
| `lỗi và thứu cần cập nhật .md` | Danh sách lỗi chi tiết |

---

## 🗄️ DATABASE

```
DB: dalattravel_db  |  User: root  |  Pass: 123456
Charset: utf8mb4_unicode_ci
```
> ⚠️ KHÔNG import bằng PowerShell pipe — bị lỗi encoding trên Windows.
> Luôn dùng **Java DataSeeder** hoặc **MySQL Workbench** để nhập tiếng Việt.
