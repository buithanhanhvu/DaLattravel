# Báo Cáo Lỗi + Đặc Tả Logic Chi Tiết — Chức năng Lên Lịch Trình Tự Động

## PHẦN A — CÁC LỖI ĐANG GẶP (từ ảnh chụp thực tế)

### Lỗi 1: Lấy vị trí GPS thành công nhưng không điền vào ô input

**Hiện tượng:** Dòng chữ "Đã định vị GPS thành công! (10.8725, 106.7891)" hiển thị đúng, nhưng ô input "Điểm bắt đầu xuất phát" vẫn hiện placeholder "Vị trí GPS hiện tại của bạn" — tức là **giá trị không được set vào state/input**.

**Nguyên nhân khả năng cao:**

- Hàm callback của `navigator.geolocation.getCurrentPosition()` chỉ đang set biến hiển thị text thông báo, mà **quên set giá trị vào state của input** (ví dụ chỉ set `gpsStatus` mà không set `startLocationInput`).
- Hoặc input đang bind vào `value` tĩnh/placeholder thay vì bind vào state động (two-way binding bị thiếu).

**Yêu cầu sửa (logic đúng):**

```
Khi bấm "Lấy vị trí hiện tại":
  1. Gọi navigator.geolocation.getCurrentPosition(success, error)
  2. Trong success callback:
     a. Lấy lat, lng từ position.coords
     b. Gọi reverse-geocoding API (Nominatim/OSM hoặc Google Geocoding)
        để chuyển tọa độ → địa chỉ dễ đọc (VD: "123 Nguyễn Văn Cừ, Q5, TP.HCM")
     c. SET state input.value = địa chỉ vừa lấy được (hoặc hiển thị tọa độ nếu geocode lỗi)
     d. SET state startLat = lat, startLng = lng (để dùng tính toán sau)
     e. Hiển thị dòng thông báo xác nhận (như hiện tại đang làm đúng phần này)
  3. Trong error callback: hiển thị lỗi "Không lấy được vị trí, vui lòng nhập tay"
```

→ Điểm mấu chốt: **input phải là controlled component**, giá trị hiển thị = state, và bước GPS phải update đúng state đó, không phải một biến thông báo riêng.

---

### Lỗi 2: Ô tìm kiếm địa điểm không hoạt động (gõ "th" không lọc ra kết quả đúng)

**Hiện tượng:** Gõ "th" vào ô tìm kiếm nhưng danh sách vẫn hiện nguyên 3 kết quả cũ (Hồ Xuân Hương, Hồ Tuyền Lâm, Thung Lũng Tình Yêu) không lọc theo từ khóa. Tìm nhà hàng/khách sạn cũng không ra gì.

**Nguyên nhân khả năng cao:**

- Ô search **chưa gắn sự kiện `onChange`/`onInput`** để gọi hàm filter — tức là gõ chữ chỉ đổi giá trị input, không trigger logic lọc danh sách.
- Hoặc có gắn nhưng hàm filter đang lọc sai field (so sánh cả object thay vì so sánh `name.toLowerCase().includes(keyword.toLowerCase())`).
- Nút lọc "Khách sạn / Nhà hàng-Quán ăn / Địa điểm du lịch" **không đổi được nguồn dữ liệu** — có thể tất cả đang gọi chung 1 API/mảng dữ liệu chỉ chứa "địa điểm du lịch", chưa nối với bảng khách sạn và nhà hàng thật sự.

**Yêu cầu sửa (logic đúng):**

```
State cần có:
  - allPlaces = [] (toàn bộ dữ liệu: gồm 3 loại — địa điểm / khách sạn / nhà hàng, LOAD MỘT LẦN từ 3 bảng DB)
  - activeCategory = "all" | "hotel" | "restaurant" | "attraction"
  - searchKeyword = ""

Khi người dùng gõ vào ô tìm kiếm (onChange):
  searchKeyword = giá trị input
  → gọi lại hàm filterResults()

Khi người dùng bấm nút category (Tất cả / Khách sạn / Nhà hàng / Địa điểm):
  activeCategory = loại được chọn
  → gọi lại hàm filterResults()

Hàm filterResults():
  result = allPlaces.filter(place =>
      (activeCategory === "all" || place.category === activeCategory)
      AND
      (searchKeyword === "" || place.name.toLowerCase().includes(searchKeyword.toLowerCase()))
  )
  → render danh sách "result" ra UI
```

→ Kiểm tra lại: API backend `/api/places?category=hotel&keyword=th` có thật sự trả đủ dữ liệu khách sạn/nhà hàng không, hay hiện tại endpoint chỉ mới nối bảng `destinations`.

---

### Lỗi 3: Chỉ hiện 3 địa điểm trong khi DB có nhiều hơn

**Nguyên nhân khả năng cao:**

- API đang **hard-code trả về mảng cứng 3 phần tử** trong code (mock data), chưa thật sự query từ database.
- Hoặc query DB có `LIMIT 3` sót lại từ lúc test.
- Hoặc chỉ mới insert 3 dòng dữ liệu mẫu vào bảng `destinations`, chưa có dữ liệu đầy đủ các địa điểm nổi tiếng khác (Đồi chè Cầu Đất, Vườn hoa thành phố, Ga Đà Lạt, Dinh Bảo Đại, Langbiang, Thiền viện Trúc Lâm, Chợ đêm Đà Lạt, Quảng trường Lâm Viên, v.v.)

**Yêu cầu sửa:**

- Kiểm tra lại API endpoint có `LIMIT`/`TOP` hay mock array không.
- Bổ sung đầy đủ dữ liệu địa điểm/khách sạn/nhà hàng thật vào DB (ít nhất 20-30 địa điểm du lịch nổi tiếng của Đà Lạt để hệ thống có đủ lựa chọn tối ưu).

---

## PHẦN B — LOGIC TÍNH TOÁN CHI PHÍ & XẾP LỊCH CHI TIẾT (theo yêu cầu của bạn)

Đây là phần logic "trái tim" của hệ thống — quyết định lịch trình có hợp lý và đúng ngân sách hay không.

### B1. Nguyên tắc tổng quát

```
Ngân sách người dùng nhập = TỔNG TRẦN chi tiêu cho toàn bộ chuyến đi
Hệ thống phải phân bổ ngân sách này vào 5 khoản mục:

  1. Tiền di chuyển (xăng xe / thuê xe)      ~ 20-25%
  2. Tiền khách sạn/lưu trú                   ~ 30-35%
  3. Tiền ăn uống (sáng/trưa/tối)             ~ 20-25%
  4. Tiền vé tham quan các địa điểm           ~ 10-15%
  5. Tiền dự phòng / mua sắm-quà lưu niệm     ~ 10%  (trích cố định 10% tổng ngân sách)

→ Tỷ lệ % này chỉ là khung tham khảo mặc định cho phương án "Cân bằng".
   Với phương án Tiết kiệm / Cao cấp, tỷ lệ dịch chuyển nhưng
   TỔNG vẫn không được vượt ngân sách nhập vào.
```

### B2. Cách chọn "mức giá" cho khách sạn/ăn uống tùy theo ngân sách còn lại

```
Sau khi trừ 10% dự phòng và tiền di chuyển ước tính (theo phương tiện đã chọn),
số tiền còn lại chia cho (khách sạn + ăn uống + vé) trong N ngày.

budget_per_day_remaining = (Ngân sách - dự phòng 10% - tổng tiền xe)
                            / số ngày

Dựa vào budget_per_day_remaining, hệ thống chọn NGƯỠNG giá phù hợp:

  Nếu budget_per_day_remaining >= 1,500,000đ/ngày  → mức Cao cấp
     (khách sạn 4-5 sao/resort, ăn nhà hàng, vé full trải nghiệm)
  Nếu 700,000 - 1,500,000đ/ngày                    → mức Cân bằng
     (khách sạn 3 sao, quán ăn địa phương ngon, vé tiêu chuẩn)
  Nếu < 700,000đ/ngày                               → mức Tiết kiệm
     (homestay/hostel, quán bình dân, ưu tiên điểm miễn phí)

→ Đây chính là lý do vì sao hệ thống PHẢI có bảng giá khách sạn/quán ăn
  theo NHIỀU MỨC (không phải 1 giá cố định) để linh hoạt chọn theo ngân sách.
```

### B3. Chọn khách sạn/quán ăn theo VỊ TRÍ GẦN NHAU (tiết kiệm di chuyển)

```
Với mỗi ngày trong lịch trình:
  1. Xác định cụm địa điểm tham quan sẽ đi trong ngày đó (đã được nhóm ở bước tối ưu tuyến đường)
  2. Tính điểm trung tâm (centroid) của cụm địa điểm đó
  3. Query khách sạn/quán ăn trong bán kính X km quanh centroid đó,
     lọc theo mức giá đã xác định ở B2
  4. Ưu tiên chọn quán ăn/khách sạn có khoảng cách gần nhất
     → giảm thời gian + chi phí xăng xe di chuyển thêm
```

### B4. Xếp lịch ăn uống trong ngày

```
Mỗi ngày mặc định có khung giờ:
  06:30 - 07:30  Ăn sáng (gần nơi ở/gần điểm tham quan đầu tiên trong ngày)
  07:30 - 11:30  Tham quan điểm 1 (gần chỗ ăn sáng)
  11:30 - 13:00  Ăn trưa (gần điểm tham quan vừa xong)
  13:00 - 17:00  Tham quan điểm 2 (gần chỗ ăn trưa)
  17:00 - 18:30  Ăn tối / dạo chợ đêm nếu là ngày cuối cụm
  18:30 - ...    Về khách sạn nghỉ ngơi

→ Nguyên tắc xuyên suốt: SAU KHI ăn xong ở đâu, ưu tiên điểm tham quan
  GẦN quán ăn đó nhất trong danh sách điểm còn lại chưa đi,
  để giảm tối đa quãng đường di chuyển thừa (tiết kiệm xăng + thời gian).
```

### B5. Hiển thị vé tham quan và cộng dồn vào tổng

```
Mỗi địa điểm trong DB phải có trường "ticket_price" (vé vào cổng).
Khi hệ thống xếp địa điểm đó vào lịch trình:
  → hiển thị rõ trong chi tiết lịch trình: "Vé vào cổng: 50,000đ/người"
  → cộng dồn vào biến total_ticket_cost

Cuối cùng hiển thị bảng tổng kết chi phí toàn chuyến:
  - Tiền xe: X đ
  - Tiền khách sạn (N đêm): X đ
  - Tiền ăn uống (N ngày x 3 bữa): X đ
  - Tiền vé tham quan (tổng các điểm): X đ
  - Dự phòng/mua sắm (10%): X đ
  ---------------------------------
  TỔNG CỘNG: X đ   (so với ngân sách: Y đ)
```

### B6. Thông báo lỗi khi ngân sách KHÔNG ĐỦ

```
Trước khi xếp lịch, hệ thống ước tính chi phí TỐI THIỂU (mức tiết kiệm nhất)
cho: số điểm đã chọn + số ngày + phương tiện đã chọn (hoặc phương tiện rẻ nhất
nếu để trống).

min_cost_estimate = (giá xe rẻ nhất x số ngày)
                   + (mức ăn tiết kiệm nhất x số ngày)
                   + (khách sạn rẻ nhất x số đêm)
                   + tổng vé vào cổng các điểm đã chọn

NẾU ngân sách nhập vào < min_cost_estimate:
  → KHÔNG cho tạo lịch trình
  → Hiển thị cảnh báo rõ ràng, ví dụ:
     "⚠️ Ngân sách 1,000,000đ hiện không đủ để đi 10 địa điểm trong 3 ngày.
      Chi phí tối thiểu ước tính là 4,200,000đ.
      Gợi ý: giảm số địa điểm còn 3-4 điểm, hoặc tăng ngân sách,
      hoặc tăng số ngày để giãn chi phí mỗi ngày."
  → Có thể kèm nút "Tự động đề xuất lại số điểm phù hợp với ngân sách"
    (hệ thống tự cắt bớt địa điểm theo thứ tự ưu tiên rating cao nhất
     cho tới khi vừa ngân sách)
```

---

## PHẦN C — TÓM TẮT VIỆC CẦN LÀM

| # | Việc cần sửa/thêm | Mức độ |
|---|---|---|
| 1 | Sửa bug: GPS lấy được nhưng không set vào input state | Bug — ưu tiên cao |
| 2 | Sửa bug: ô tìm kiếm + filter category chưa hoạt động | Bug — ưu tiên cao |
| 3 | Bổ sung đầy đủ dữ liệu DB (địa điểm, khách sạn, nhà hàng — đủ số lượng thật) | Dữ liệu — ưu tiên cao |
| 4 | Thêm trường `ticket_price`, `price_level` (tiết kiệm/cân bằng/cao cấp) cho từng địa điểm/khách sạn/quán ăn | Schema |
| 5 | Viết logic phân bổ ngân sách theo 5 khoản mục (B1) | Logic lõi |
| 6 | Viết logic chọn mức giá khách sạn/ăn uống theo ngân sách còn lại (B2) | Logic lõi |
| 7 | Viết logic chọn khách sạn/quán ăn gần cụm điểm tham quan trong ngày (B3) | Logic lõi |
| 8 | Viết logic xếp khung giờ sáng/trưa/tối ăn ở đâu, đi đâu tiếp theo (B4) | Logic lõi |
| 9 | Hiển thị chi tiết + tổng kết chi phí cuối lịch trình (B5) | UI + Logic |
| 10 | Kiểm tra ngân sách tối thiểu, cảnh báo/từ chối nếu không đủ (B6) | Logic lõi |

Bạn có thể đưa nguyên file này cho AI/dev đang code để họ sửa từng mục một cách rõ ràng, không bị nhầm lẫn logic nữa.Chi phí xe là phần dễ làm sai nhất vì nó **không cố định theo ngày** mà phụ thuộc vào quãng đường di chuyển thực tế mỗi ngày. Tôi làm rõ logic:

## 1. Có 2 loại chi phí xe cần tách riêng

```
Chi phí xe = Chi phí THUÊ XE (cố định theo ngày)
           + Chi phí XĂNG/NHIÊN LIỆU (biến động theo km di chuyển thực tế mỗi ngày)
```

**Chi phí thuê xe** (nếu có thuê + tài xế): tính theo ngày, giống nhau mỗi ngày.
Ví dụ: ô tô 4 chỗ = 900,000đ/ngày × 3 ngày = 2,700,000đ (cố định, không đổi theo địa điểm).

**Chi phí xăng**: mới là phần thay đổi theo từng chặng A→B→C, vì mỗi ngày đi quãng đường khác nhau.

## 2. Cách tính chi phí xăng cho từng chặng

```
Với mỗi chặng di chuyển (VD: Điểm bắt đầu → Địa điểm A):
  1. Lấy khoảng cách thực tế (km) từ Distance Matrix API/OSRM
     distance_km = khoảng_cách(điểm_hiện_tại, điểm_tiếp_theo)

  2. Tính chi phí xăng của chặng đó:
     fuel_cost_segment = distance_km × fuel_cost_per_km (theo loại xe)

     Ví dụ định mức tham khảo:
       - Xe máy:        ~800đ/km  (≈ 2.5L/100km × giá xăng ~24,000đ/L, chia đầu người)
       - Ô tô 4 chỗ:    ~2,500đ/km (≈ 7L/100km)
       - Ô tô 7 chỗ:    ~3,000đ/km (≈ 9L/100km)

  3. Cộng dồn qua tất cả các chặng trong NGÀY đó
     → ra "chi phí xe của riêng ngày hôm đó"
```

## 3. Vì sao ngày 1 khác ngày 2

Vì vị trí xuất phát mỗi ngày khác nhau (ngày 1 xuất phát từ khách sạn/điểm ban đầu, ngày 2 xuất phát từ khách sạn ở gần cụm điểm ngày 2...), và số điểm/khoảng cách giữa các điểm trong mỗi ngày cũng khác. Ví dụ minh họa:

```
Ngày 1: Khách sạn X → Hồ Xuân Hương (3km) → Ăn trưa (1km) → Thung Lũng Tình Yêu (5km) → về KS (5km)
   Tổng km ngày 1 = 3+1+5+5 = 14km
   Chi phí xe ngày 1 = 14km × 2,500đ = 35,000đ

Ngày 2: Khách sạn X → Hồ Tuyền Lâm (12km) → Thiền viện Trúc Lâm (2km) → về KS (13km)
   Tổng km ngày 2 = 12+2+13 = 27km
   Chi phí xe ngày 2 = 27km × 2,500đ = 67,500đ
```

→ Đây chính là lý do **không thể dùng 1 con số chi phí xe cố định cho cả chuyến**, mà phải tính riêng từng ngày dựa trên tổng quãng đường thực tế của ngày đó, rồi cộng dồn lại cuối cùng.

## 4. Công thức tổng chi phí xe toàn chuyến

```
total_transport_cost = (giá thuê xe/ngày × số ngày)          // phần cố định
                      + Σ (km_ngày_i × giá xăng/km)  với i = 1..N   // phần biến động

→ Hiển thị trong lịch trình MỖI NGÀY một dòng riêng:
   "🚗 Chi phí di chuyển ngày 1: 35,000đ (14km)"
   "🚗 Chi phí di chuyển ngày 2: 67,500đ (27km)"
   ...
   "🚗 Tổng chi phí di chuyển toàn chuyến: XXX đ"
```

## 5. Lưu ý quan trọng: thứ tự xếp lịch ảnh hưởng trực tiếp đến chi phí xe

Vì chi phí xăng tỷ lệ thuận với km, nên **bước tối ưu tuyến đường (TSP) ở phần trước không chỉ để "đi cho nhanh"**, mà còn **trực tiếp quyết định chi phí xe rẻ hay đắt**. Nếu xếp lịch cẩu thả (đi lòng vòng, chạy tới chạy lui giữa các cụm), chi phí xăng sẽ đội lên rất nhiều dù cùng số điểm, cùng số ngày.

→ Nên thứ tự đúng là:

1. Tối ưu tuyến đường trước (giảm tổng km toàn chuyến)
2. Nhóm điểm gần nhau vào cùng 1 ngày
3. Rồi mới tính chi phí xe theo km thực tế của từng ngày sau khi đã tối ưu

Bạn có muốn tôi viết luôn công thức/pseudo-code phần này gộp vào file spec cũ, hay để riêng vậy đủ rõ rồi?
