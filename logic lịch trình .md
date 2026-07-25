# Đặc tả chức năng: Lên Lịch Trình Du Lịch Tự Động (DaLatTravel)

## 1. Vấn đề của bản hiện tại

Form hiện tại (ảnh bạn gửi) chỉ đang **thu thập vài con số** (ngân sách, số ngày, tọa độ, checkbox địa điểm) rồi bấm nút — nhưng chưa có:

- Cách chọn điểm đến trực quan (dropdown/search) + **bản đồ hiển thị**
- Logic **tính toán tuyến đường tối ưu** giữa các điểm
- Logic **phân bổ ngân sách** theo phương tiện + hoạt động
- Logic **xếp lịch theo ngày** (ngày nào đi đâu, làm gì)
- Đầu ra là **nhiều phương án** (tiết kiệm / cân bằng / cao cấp)

Dưới đây là spec đầy đủ lại từ đầu.

---

## 2. Luồng người dùng (UX Flow)

```
Bước 1: Nhập ngân sách dự kiến (VNĐ)
Bước 2: Nhập số ngày du lịch
Bước 3: Chọn phương tiện di chuyển (xe máy / ô tô 4 chỗ / ô tô 7 chỗ / không thuê xe...)
         → có thể để trống, hệ thống sẽ tự đề xuất phương tiện phù hợp với ngân sách
Bước 4: Chọn điểm bắt đầu
         - Nút "Lấy vị trí hiện tại" (dùng Geolocation API)
         - Hoặc nhập địa chỉ / chọn từ danh sách khách sạn phổ biến ở Đà Lạt
Bước 5: Chọn nơi muốn đến
         - Bấm "Chọn nơi muốn đến" → sổ ra danh sách địa điểm du lịch (lấy từ DB)
         - Danh sách có thể lọc theo loại: thiên nhiên / tâm linh / check-in / vui chơi...
         - Mỗi lần chọn, điểm đó được thêm vào danh sách "địa điểm đã chọn"
Bước 6: BẢN ĐỒ hiển thị real-time
         - Marker điểm bắt đầu + tất cả điểm đã chọn
         - Khi người dùng chọn/bỏ chọn địa điểm → bản đồ cập nhật marker ngay
Bước 7: Bấm "Tạo Lịch Trình Tối Ưu"
Bước 8: Hệ thống trả về 3 phương án: Tiết Kiệm / Cân Bằng / Cao Cấp
         - Mỗi phương án gồm: lịch trình theo từng ngày, tuyến đường, chi phí chi tiết
```

---

## 3. Dữ liệu cần có trong Database

### Bảng `destinations` (địa điểm du lịch)

| Trường | Ví dụ |
|---|---|
| id | 1 |
| name | Hồ Xuân Hương |
| lat, lng | 11.9404, 108.4383 |
| category | thiên nhiên / tâm linh / check-in |
| avg_visit_duration | 90 (phút) |
| ticket_price | 0 |
| open_hours | 06:00 - 22:00 |
| description | ... |
| image_url | ... |

### Bảng `transport_options` (phương tiện)

| Trường | Ví dụ |
|---|---|
| type | xe máy / ô tô 4 chỗ / ô tô 7 chỗ |
| price_per_day | 150,000 / 900,000 / 1,200,000 |
| fuel_cost_per_km | ước tính theo loại xe |

### Bảng `accommodation` (chỗ ở - nếu muốn mở rộng)

- Có thể thêm sau để tính luôn chi phí lưu trú theo hạng (tiết kiệm/tiêu chuẩn/cao cấp)

### Bảng `meal_cost` (chi phí ăn uống ước tính theo mức)

- tiết kiệm: ~100k/ngày, cân bằng: ~250k/ngày, cao cấp: ~500k/ngày

---

## 4. Logic thuật toán (phần lõi — quan trọng nhất)

### Bước A — Tính ma trận khoảng cách

Dùng tọa độ (lat, lng) của: điểm bắt đầu + các điểm đã chọn
→ Gọi Google Maps Distance Matrix API (hoặc OSRM nếu muốn miễn phí) để lấy:

- Khoảng cách (km) và thời gian di chuyển giữa mọi cặp điểm

### Bước B — Giải bài toán tối ưu tuyến đường (TSP - Traveling Salesman Problem)

Vì số điểm thường nhỏ (3-10 điểm), có thể dùng:

- **Nearest Neighbor + 2-opt** (đơn giản, đủ tốt) — không cần thuật toán phức tạp như Genetic Algorithm
- Input: điểm bắt đầu + danh sách điểm cần ghé
- Output: thứ tự ghé thăm sao cho **tổng quãng đường ngắn nhất**

### Bước C — Xếp lịch theo ngày (Day Scheduling)

Logic phân bổ:

```
- Ước tính tổng thời gian cần: Σ(thời gian di chuyển) + Σ(thời gian tham quan mỗi điểm)
- Giới hạn mỗi ngày hoạt động: ví dụ 08:00 - 18:00 (10 tiếng)
- Thuật toán "bin packing" đơn giản:
  Với mỗi ngày, thêm điểm tiếp theo vào lịch nếu:
     (giờ hiện tại + thời gian di chuyển đến điểm + thời gian tham quan) <= giờ kết thúc ngày
  Nếu không vừa → điểm đó dồn sang ngày kế tiếp
- Nếu số điểm ít hơn số ngày (VD: 7 ngày, 3 điểm) → chèn thêm các buổi:
     + Ngày nghỉ ngơi tự do / dạo phố / chợ đêm Đà Lạt
     + Gợi ý thêm địa điểm phụ gần đó (không bắt buộc, có thể tick chọn thêm)
```

### Bước D — Tính chi phí theo 3 phương án

| Hạng mục | Tiết kiệm | Cân bằng | Cao cấp |
|---|---|---|---|
| Phương tiện | Xe máy tự lái | Ô tô 4 chỗ thuê + tài xế | Ô tô 7 chỗ đời mới + tài xế riêng |
| Ăn uống/ngày | ~100-150k | ~250-350k | ~500k+ |
| Vé tham quan | Điểm miễn phí ưu tiên | Mix | Ưu tiên trải nghiệm đặc biệt |
| Lưu trú (nếu có) | Homestay/hostel | Khách sạn 3 sao | Resort/khách sạn 4-5 sao |

```
Công thức tổng chi phí:
Tổng = (chi phí thuê xe × số ngày)
     + (chi phí xăng/km × tổng km theo tuyến tối ưu)
     + (chi phí ăn uống/ngày × số ngày)
     + Σ(vé vào cổng các điểm đã chọn)
     + (chi phí lưu trú/đêm × số đêm)   [nếu có]

→ So sánh Tổng với Ngân sách dự kiến
→ Nếu Tổng > Ngân sách: tự động hạ cấp phương tiện/ăn uống hoặc cảnh báo
   "Với ngân sách này, bạn nên chọn phương án Tiết Kiệm"
```

---

## 5. Cấu trúc dữ liệu trả về (JSON) — để hiển thị UI

```json
{
  "input": {
    "budget": 5000000,
    "days": 7,
    "start_point": {"lat": 11.94, "lng": 108.44, "name": "Vị trí hiện tại"},
    "destinations": ["Hồ Xuân Hương", "Hồ Tuyền Lâm", "Thung Lũng Tình Yêu"]
  },
  "plans": [
    {
      "type": "tiet_kiem",
      "total_cost": 3200000,
      "transport": "Xe máy tự lái",
      "route_order": ["Hồ Xuân Hương", "Thung Lũng Tình Yêu", "Hồ Tuyền Lâm"],
      "total_distance_km": 42,
      "days": [
        {
          "day": 1,
          "schedule": [
            {"time": "08:00", "activity": "Xuất phát", "location": "Điểm bắt đầu"},
            {"time": "08:30", "activity": "Tham quan Hồ Xuân Hương", "duration_min": 90},
            {"time": "12:00", "activity": "Ăn trưa", "location": "gần đó"}
          ]
        }
      ]
    },
    { "type": "can_bang", "...": "..." },
    { "type": "cao_cap", "...": "..." }
  ]
}
```

---

## 6. Về phần Bản đồ

- Dùng **Leaflet.js** (miễn phí, nhẹ) hoặc **Google Maps JavaScript API**
- Hiển thị:
  - Marker điểm bắt đầu (icon riêng)
  - Marker từng điểm đã chọn (đánh số theo thứ tự lộ trình tối ưu, không phải thứ tự chọn)
  - Vẽ polyline nối các điểm theo đúng tuyến tối ưu
- Cập nhật realtime mỗi khi người dùng thêm/bớt địa điểm ở dropdown

---

## 7. Gợi ý công nghệ cụ thể

| Thành phần | Gợi ý |
|---|---|
| Tính khoảng cách thực tế + tối ưu tuyến | OSRM (miễn phí, self-host) hoặc Google Distance Matrix API |
| Bản đồ | Leaflet + OpenStreetMap (miễn phí) |
| Thuật toán TSP nhỏ | Viết tay Nearest Neighbor + 2-opt (vài chục dòng code) |
| Backend | Node.js/Express hoặc Python/FastAPI — nhận input, query DB, gọi thuật toán, trả JSON |

---

## 8. Tóm tắt việc cần sửa trên form hiện tại

1. Đổi phần "checkbox địa điểm" → dropdown/multi-select có tìm kiếm, load từ DB
2. Thêm nút "📍 Lấy vị trí hiện tại" cạnh ô lat/lng
3. Thêm khung bản đồ (map) bên dưới hoặc bên cạnh form, cập nhật realtime
4. Sau khi bấm "Tạo Lịch Trình Tối Ưu" → gọi API backend chạy thuật toán ở mục 4
5. Trang kết quả hiển thị 3 tab: **Tiết kiệm / Cân bằng / Cao cấp**, mỗi tab có lịch trình theo ngày + bản đồ tuyến đường + bảng chi phí

---

Nếu bạn muốn, bước tiếp theo tôi có thể:

- Viết code mẫu thuật toán tối ưu tuyến đường (Nearest Neighbor + 2-opt) bằng JavaScript/Python
- Viết code mẫu tích hợp bản đồ Leaflet
- Thiết kế lại schema database chi tiết hơn
