# Web Du Lịch Đà Lạt

Nền tảng du lịch trực tuyến giúp người dùng khám phá địa điểm, lên kế hoạch chuyến đi thông minh và đặt xe ghép đến Đà Lạt.

**Tech Stack:** ASP.NET Core 8 · Entity Framework Core 8 · SQL Server · ASP.NET Identity · OSRM API · Google Maps API

---

## Tính năng chính

### Khám phá địa điểm
- Danh sách địa điểm tham quan, khách sạn, nhà hàng theo khu vực và danh mục
- Xem chi tiết với ảnh, mô tả, tọa độ GPS và đánh giá sao
- Lưu địa điểm yêu thích
- Tìm kiếm và lọc theo danh mục / vùng

### Lập kế hoạch chuyến đi (Trip Planner)
- Gợi ý lịch trình thông minh theo ngân sách: Tiết kiệm / Cân bằng / Cao cấp
- Tự động phân cụm địa điểm gần nhau để tối ưu lộ trình theo ngày
- Gợi ý khách sạn và nhà hàng phù hợp cho từng ngày
- Tính chi phí vận chuyển từ điểm xuất phát đến Đà Lạt

### Đặt xe ghép (Carpooling)
- Ghép khách tự động với xe phù hợp dựa trên vị trí đón/trả và khung giờ
- Hỗ trợ xe 4, 7, 9 chỗ và đặt xe riêng cho nhóm
- Tính giá theo số ghế và khoảng cách thực tế (OSRM API)
- Xem lộ trình trực quan trên bản đồ

### Tính giá vận chuyển
- Tính chi phí di chuyển từ bất kỳ tỉnh thành nào đến Đà Lạt theo GPS
- Hỗ trợ xe cá nhân (tính theo nhiên liệu) và xe khách (giá cố định / ước tính)
- Xử lý dữ liệu tỉnh thành sáp nhập (legacy locations)

### Blog & Tin tức
- Bài viết hướng dẫn du lịch, kinh nghiệm khám phá Đà Lạt

### Lễ hội & Sự kiện
- Danh sách lễ hội, sự kiện sắp diễn ra tại Đà Lạt

### Xác thực người dùng
- Đăng ký / Đăng nhập với xác nhận email
- Xác thực 2 bước (2FA)
- Quản lý hồ sơ cá nhân, đổi mật khẩu

### Trang quản trị (Admin)
- Quản lý địa điểm du lịch (thêm, sửa, xóa, phân loại)
- Quản lý danh mục, khu vực, bài viết blog
- Xem và xử lý tin nhắn liên hệ

---

## Thuật toán

### Lập kế hoạch chuyến đi

Quy trình xử lý khi người dùng nhập ngân sách, số ngày và địa điểm muốn đến:

**Bước 1 — Phân cụm địa điểm (Clustering)**

Các địa điểm được nhóm lại theo khoảng cách địa lý bằng công thức Haversine. Những địa điểm gần nhau được xếp vào cùng một cụm để tối ưu lộ trình di chuyển trong ngày, tránh đi lại lòng vòng.

**Bước 2 — Phân bổ cụm vào ngày**

Mỗi cụm địa điểm được phân bổ vào một ngày cụ thể trong lịch trình. Số lượng địa điểm mỗi ngày được cân bằng dựa trên số ngày du lịch.

**Bước 3 — Chọn địa điểm theo ngân sách (Knapsack)**

Áp dụng tư duy bài toán Knapsack để chọn tập hợp địa điểm tối ưu trong giới hạn ngân sách. Mỗi mức ngân sách (Tiết kiệm / Cân bằng / Cao cấp) sẽ lọc khách sạn và nhà hàng phù hợp.

**Bước 4 — Tính chi phí vận chuyển**

- Xe cá nhân (IsSelfDrive): Tính theo mức tiêu hao nhiên liệu × giá xăng + 20% bảo dưỡng
- Xe công cộng: Tra cứu giá cố định trong DB theo tỉnh thành xuất phát, nếu không có thì ước tính theo khoảng cách Haversine (2.500–4.000 đ/km, giảm dần theo quãng xa)
- Xử lý tỉnh thành sáp nhập: Tìm location gần nhất trong bán kính 50km, ánh xạ sang tên hiện tại

---

### Ghép xe đi chung (Carpooling)

Hệ thống ghép xe sử dụng pipeline 3 thuật toán kết hợp:

#### Bước 1 — K-Means Clustering (Phân cụm hành khách)

Mục tiêu: Nhóm hành khách theo khu vực địa lý để giảm số lượng xe cần thiết.

```
Input:  Danh sách hành khách với tọa độ điểm đón
Output: k cụm hành khách theo vị trí gần nhau

Quy trình:
1. Khởi tạo k centroid ngẫu nhiên từ danh sách hành khách
2. Gán mỗi hành khách vào centroid gần nhất (Haversine distance)
3. Cập nhật centroid = trung bình tọa độ các điểm trong cụm
4. Lặp lại bước 2-3 cho đến khi centroid không thay đổi (ngưỡng 0.0001°)
   hoặc đạt tối đa 100 vòng lặp
```

Khoảng cách được tính bằng công thức Haversine (bán kính Trái Đất 6371 km) để đảm bảo độ chính xác trên bề mặt cầu.

#### Bước 2 — Min-Cost Max-Flow (Phân công hành khách vào xe)

Mục tiêu: Phân công hành khách vào xe sao cho tổng chi phí di chuyển là nhỏ nhất, đồng thời tối đa hóa số hành khách được ghép.

```
Mô hình đồ thị:
  Source → Xe (capacity = số ghế trống, cost = 0)
  Xe → Hành khách (capacity = 1, cost = khoảng cách xe-hành khách)
  Hành khách → Sink (capacity = 1, cost = 0)

Ràng buộc:
  - Nhóm bắt buộc đi chung (PrivateGroup) được xử lý như một node đặc biệt
  - Tổng ghế sử dụng không vượt quá sức chứa xe
  - Khung giờ khởi hành ±1 giờ
```

Thuật toán tìm đường tăng luồng (augmenting path) với chi phí tối thiểu, đảm bảo mỗi hành khách chỉ được ghép vào một xe.

#### Bước 3 — PDPTW (Tối ưu thứ tự đón/trả)

Mục tiêu: Sau khi biết xe nào chở hành khách nào, tìm thứ tự đón/trả tối ưu nhất.

```
Bài toán: Pickup and Delivery Problem with Time Windows (PDPTW)

Ràng buộc cứng:
  - Hành khách phải được đón (pickup) trước khi trả (dropoff)
  - Thời gian đến mỗi điểm phải nằm trong khung giờ [earliest, latest]

Thuật toán: Greedy Nearest Neighbor + Time Window Feasibility Check
  1. Bắt đầu từ vị trí xe
  2. Tại mỗi bước, chọn điểm dừng tiếp theo gần nhất thỏa mãn:
     - Nếu là dropoff: hành khách đã được đón
     - Thời gian đến ≤ latest time của điểm đó
  3. Nếu không có điểm hợp lệ theo thời gian: chọn điểm gần nhất còn lại
  4. Tính tổng khoảng cách và chi phí = distance × CostPerKm

Vận tốc giả định: 50 km/h
Thời gian dừng mỗi điểm: 5 phút
```

Kết quả trả về lộ trình đầy đủ với thứ tự đón/trả, thời gian ước tính và chi phí từng hành khách.

---

## Kiến trúc dự án

```
WebDuLichDaLat/
├── Areas/
│   ├── Admin/              # Quản trị (Controllers, Views, Repositories)
│   └── Identity/           # Xác thực (Login, Register, 2FA)
├── Controllers/            # TripPlannerController, CarpoolController, ...
├── Models/                 # Entity models + ApplicationDbContext
├── Services/
│   ├── KMeansClusteringService.cs
│   ├── MinCostMaxFlowService.cs
│   ├── PDPTWService.cs
│   ├── CarpoolMatchingService.cs
│   ├── OsrmRouteService.cs
│   ├── RouteMatchingService.cs
│   └── TransportPriceCalculator.cs
├── Views/                  # Razor Views
└── wwwroot/                # CSS, JS, Images
```

**Patterns:** MVC · Repository Pattern · Dependency Injection · Async/Await

---

## Cài đặt & Chạy dự án

### Yêu cầu
- .NET 8 SDK
- SQL Server hoặc SQL Server Express

### Các bước

1. Clone repo
```bash
git clone <repo-url>
cd DoAnCoSo
```

2. Cấu hình connection string trong `appsettings.json`
```json
"ConnectionStrings": {
  "DefaultConnection": "Server=.;Database=WTF;Trusted_Connection=True;TrustServerCertificate=True"
}
```

3. Cấu hình email (Gmail SMTP) trong `appsettings.json`
```json
"EmailSettings": {
  "SenderEmail": "your-email@gmail.com",
  "SenderPassword": "your-app-password"
}
```

4. Chạy migration hoặc import file SQL
```bash
cd WebDuLichDaLat
dotnet ef database update
```
Hoặc chạy file `Scripts/database_fixed.sql` trực tiếp trong SSMS.

5. Chạy ứng dụng
```bash
dotnet run
```

---

## Tóm tắt thuật toán

| Thuật toán | Áp dụng cho |
|---|---|
| K-Means Clustering | Nhóm hành khách theo khu vực địa lý |
| Min-Cost Max-Flow | Phân công hành khách vào xe tối ưu chi phí |
| PDPTW (Greedy + Nearest Neighbor) | Tối ưu thứ tự đón/trả theo khung giờ |
| Haversine Formula | Tính khoảng cách thực tế giữa 2 tọa độ GPS |
| Knapsack (heuristic) | Chọn địa điểm tối ưu theo ngân sách |
| OSRM Routing API | Lấy tuyến đường và khoảng cách thực tế trên bản đồ |
