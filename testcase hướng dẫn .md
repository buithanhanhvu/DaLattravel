KẾ HOẠCH KIỂM THỬ TỔNG THỂ (MASTER TEST PLAN)
Dự án: AstraShop - Hệ thống Thương mại Điện tử Mini (Mini E-Commerce System)
Phiên bản: 1.0
Tác giả: Tester / QA Engineer
Ngày lập: 23/07/2026

1. GIỚI THIỆU & MỤC TIÊU (INTRODUCTION & OBJECTIVES)
Tài liệu Kế hoạch Kiểm thử này xác định chiến lược, phạm vi, môi trường, nguồn lực và lịch trình kiểm thử cho hệ thống thương mại điện tử AstraShop (Fullstack Java Spring Boot + React TypeScript).

Mục tiêu chất lượng (Quality Goals):
Tính đúng đắn về chức năng (Functional Correctness): Đảm bảo 100% các luồng nghiệp vụ mua hàng, giỏ hàng, thanh toán VNPAY/COD, áp mã giảm giá và quản trị vận hành chính xác theo yêu cầu.
Tính toàn vẹn dữ liệu (Data Integrity): Ngăn chặn lỗi tranh chấp hàng tồn kho (Race condition) khi nhiều khách hàng cùng mua một mặt hàng bằng cơ chế Database Row Locking (SELECT ... FOR UPDATE).
Tính an toàn & bảo mật (Security): Kiểm tra cơ chế phân quyền (RBAC - Customer vs Admin), bảo mật API bằng JWT Bearer Token, băm mật khẩu BCrypt.
Độ tin cậy của API (API Reliability): Đảm bảo tất cả các endpoint API trả về đúng HTTP Status Code và định dạng JSON chuẩn.
2. PHẠM VI KIỂM THỬ (TEST SCOPE)
2.1 Trong phạm vi (In-Scope)
Phân hệ (Module) Các tính năng chi tiết cần kiểm thử
Xác thực người dùng (Auth) Đăng ký tài khoản, Đăng nhập (JWT), Google OAuth2 Login, Quên mật khẩu qua OTP 6 số, Đổi mật khẩu.
Danh mục & Sản phẩm Tìm kiếm sản phẩm, Lọc đa tiêu chí (danh mục, khoảng giá, rating), Phân trang, Xem chi tiết sản phẩm & album ảnh gallery.
Yêu thích (Wishlist) Thêm/Xóa wishlist, Nhận thông báo tự động khi sản phẩm wishlist giảm giá trong 7 ngày.
Giỏ hàng & Coupon Thêm/Xóa/Sửa số lượng trong giỏ hàng, Thu thập voucher từ trang /vouchers, Áp mã giảm giá tự động hoặc chọn từ ví.
Thanh toán & Đơn hàng Checkout đơn hàng (COD và VNPAY Sandbox), Kiểm tra trừ tồn kho an toàn, Theo dõi trạng thái đơn hàng Stepper (PENDING -> DELIVERED), Tự hủy đơn PENDING.
Đánh giá & Review Viết đánh giá 1-5 sao và bình luận (chỉ cho phép khách hàng đã mua sản phẩm đó và đơn hàng DELIVERED).
Quản trị viên (Admin) Dashboard thống kê doanh thu/tồn kho, CRUD Sản phẩm & Upload ảnh, CRUD Danh mục, CRUD Coupon, Quản lý trạng thái Đơn hàng, Thùng rác (Recycle Bin - Restore / Hard Delete).
2.2 Ngoài phạm vi (Out-of-Scope)
Kiểm thử hiệu năng tải cực lớn (Stress/Load Testing trên 100,000 CCU).
Cổng thanh toán quốc tế thực tế (Stripe, Paypal) - chỉ sử dụng VNPAY Sandbox thử nghiệm.
3. CHIẾN LƯỢC KIỂM THỬ (TEST STRATEGY & APPROACH)
Hệ thống áp dụng phương pháp kiểm thử toàn diện 3 cấp độ:

3.1 Kiểm thử Hộp trắng (White-box Testing / Unit Testing)
Công cụ: JUnit 5, Mockito.
Đối tượng: Các phương thức xử lý logic nghiệp vụ tại tầng Service (AuthService, ShopService).
Tiêu chí: Đạt code coverage cao đối với các hàm tính toán tiền, áp mã giảm giá, kiểm tra tồn kho.
3.2 Kiểm thử Hộp xám (Gray-box Testing / API Testing)
Công cụ: Postman, REST Client (api-test-cases.http), Spring Boot MockMvc.
Nội dung: Kiểm tra response status code (200 OK, 400 Bad Request, 401 Unauthorized, 403 Forbidden), cấu trúc body JSON, và thời gian phản hồi API.
3.3 Kiểm thử Hộp đen (Black-box Testing / System & UI Testing)
Công cụ: Selenium WebDriver, Chạy kiểm thử thủ công (Manual Testing).
Kỹ thuật thiết kế Test Case:
Phân vùng tương đương (Equivalence Partitioning): Phân chia dữ liệu đầu vào hợp lệ và không hợp lệ (ví dụ: Tồn kho <= 0, giá tiền < 0, số điện thoại đúng/sai định dạng).
Phân tích giá trị biên (Boundary Value Analysis): Kiểm thử tại các mốc biên (ví dụ: Số lượng đặt mua = 1, = Tồn kho tối đa, = Tồn kho + 1).
4. MÔI TRƯỜNG KIỂM THỬ (TEST ENVIRONMENT)
Thành phần Cấu hình / Thông số
Hệ điều hành Windows 11 / Linux
Backend Runtime Java OpenJDK 21, Spring Boot 4.0.6
Database MySQL Server 8.0 (Database: webbanhang, Port: 3306)
Frontend Runtime Node.js v18+, React 19, Vite Dev Server (Port: 3000)
Cổng thanh toán thử nghiệm VNPAY Sandbox Environment (Thẻ NCB Test)
Trình duyệt kiểm thử Google Chrome Version 120+
5. TIÊU CHÍ DỪNG & CHUYỂN GIAO (ENTRY / EXIT CRITERIA)
5.1 Tiêu chí bắt đầu (Entry Criteria)
Mã nguồn Backend và Frontend đã được biên dịch thành công không có lỗi syntax (BUILD SUCCESS).
Cơ sở dữ liệu MySQL đã được nạp dữ liệu mẫu (Seed Data) thông qua các migration script của Flyway (V1__init_schema.sql đến V12).
5.2 Tiêu chí hoàn thành (Exit Criteria)
Unit Test Backend: 100% các test case trong JUnit/Mockito vượt qua (Tests run: PASSED).
API Automation Test: 100% các request trong Postman Collection trả về phản hồi hợp lệ với các assertions passed.
UI Automation Test: Các kịch bản Selenium kết thúc thành công không bị gián đoạn hoặc gặp lỗi uncaught exception.
Không còn bất kỳ lỗi mức độ Critical hoặc High nào còn tồn đọng chưa được khắc phục.
6. QUẢN LÝ LỖI (DEFECT MANAGEMENT PROCESS)
Các lỗi phát hiện trong quá trình kiểm thử được ghi nhận vào Defect Log theo mẫu quy chuẩn MantisBT / Jira bao gồm các trường:

Bug ID & Title: Mã định danh và tiêu đề ngắn gọn về lỗi.
Module: Phân hệ phát sinh lỗi.
Severity (Mức độ nghiêm trọng): Critical / High / Medium / Low.
Priority (Độ ưu tiên xử lý): P1 (Khẩn cấp) -> P4 (Thấp).
Steps to Reproduce: Các bước chi tiết để tái hiện lỗi.
Expected Result vs Actual Result: Kết quả mong đợi đối chiếu với kết quả thực tế.

# 📊 MA TRẬN KỊCH BẢN KIỂM THỬ CHI TIẾT (TEST CASES MATRIX)

**Dự án:** AstraShop - Hệ thống Thương mại Điện tử Mini  
**Áp dụng kỹ thuật:** Phân vùng tương đương (Equivalence Partitioning), Phân tích giá trị biên (Boundary Value Analysis), Bảng quyết định (Decision Table).

---

## 👨‍💻 PHẦN 1: PHÂN HỆ XÁC THỰC & NGƯỜI DÙNG (AUTHENTICATION & USER)

| Test Case ID | Feature | Mô tả Kịch bản Kiểm thử | Điều kiện tiền đề (Pre-conditions) | Các bước thực hiện (Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Output) | Kỹ thuật áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_AUTH_001** | Đăng ký | Đăng ký tài khoản mới thành công với dữ liệu hợp lệ | Chưa đăng nhập, Username chưa tồn tại | 1. Mở trang Đăng ký<br>2. Nhập thông tin hợp lệ<br>3. Nhấn nút "Đăng ký" | Username: `testuser01`<br>Email: `test01@gmail.com`<br>Pass: `Password123`<br>Phone: `0912345678` | Đăng ký thành công, nhận JWT Token, chuyển hướng về trang chủ. | Hộp đen (Normal Flow) |
| **TC_AUTH_002** | Đăng ký | Đăng ký thất bại khi trùng tên đăng nhập (Username) | Username `customer` đã tồn tại trong DB | 1. Mở trang Đăng ký<br>2. Nhập Username `customer`<br>3. Nhấn nút "Đăng ký" | Username: `customer`<br>Pass: `Password123` | Hệ thống báo lỗi "Username đã tồn tại", mã lỗi 400 Bad Request. | Phân vùng tương đương (Invalid) |
| **TC_AUTH_003** | Đăng ký | Đăng ký thất bại khi mật khẩu quá yếu (< 6 ký tự) | Form đăng ký rỗng | 1. Nhập mật khẩu 3 ký tự<br>2. Nhấn "Đăng ký" | Password: `123` | Báo lỗi validation "Mật khẩu phải từ 6 ký tự trở lên". | Giá trị biên (Boundary Analysis) |
| **TC_AUTH_004** | Đăng nhập | Đăng nhập thành công với tài khoản Khách hàng | Tài khoản đã đăng ký | 1. Mở trang Login<br>2. Nhập username/pass hợp lệ<br>3. Nhấn "Đăng nhập" | User: `customer`<br>Pass: `customer123` | Đăng nhập thành công, trả về JWT Access Token & Refresh Token. | Normal Flow |
| **TC_AUTH_005** | Đăng nhập | Đăng nhập thất bại khi sai mật khẩu | Tài khoản `customer` tồn tại | 1. Nhập password sai<br>2. Nhấn "Đăng nhập" | User: `customer`<br>Pass: `wrongpass` | Báo lỗi "Tên đăng nhập hoặc mật khẩu không chính xác" (HTTP 401). | Negative Testing |
| **TC_AUTH_006** | Đăng nhập | Đăng nhập thất bại khi tài khoản bị khóa (`BANNED`) | Tài khoản `banned_user` bị khóa | 1. Nhập thông tin tài khoản bị khóa<br>2. Nhấn "Đăng nhập" | User: `banned_user`<br>Pass: `pass123` | Báo lỗi "Tài khoản của bạn đã bị khóa", không cấp JWT token. | Decision Table |

---

## 🛒 PHẦN 2: PHÂN HỆ GIỎ HÀNG, COUPON & THANH TOÁN (CART, VOUCHER & CHECKOUT)

| Test Case ID | Feature | Mô tả Kịch bản Kiểm thử | Điều kiện tiền đề | Các bước thực hiện | Dữ liệu kiểm thử | Kết quả mong đợi | Kỹ thuật áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_CART_001** | Giỏ hàng | Thêm sản phẩm vào giỏ hàng thành công | Đã đăng nhập tài khoản Khách hàng | 1. Vào trang Chi tiết sản phẩm ID 1<br>2. Chọn số lượng 2<br>3. Bấm "Thêm vào giỏ" | Product ID: `1`<br>Quantity: `2` | Giỏ hàng được cập nhật, tổng số lượng tăng thêm 2. HTTP 200 OK. | Normal Flow |
| **TC_CART_002** | Giỏ hàng | Thêm sản phẩm vượt quá số lượng tồn kho | Sản phẩm A chỉ còn tồn kho 5 sản phẩm | 1. Chọn số lượng 10<br>2. Bấm "Thêm vào giỏ" | Product ID: `A`<br>Quantity: `10` | Báo lỗi "Số lượng yêu cầu vượt quá tồn kho hiện có". | Giá trị biên (Boundary Value) |
| **TC_COUPON_001**| Voucher | Thu thập mã giảm giá thành công vào ví | Mã coupon `WELCOME10` đang hoạt động | 1. Truy cập trang `/vouchers`<br>2. Bấm "Thu thập" tại voucher WELCOME10 | Coupon Code: `WELCOME10` | Mã voucher được thêm vào ví cá nhân của người dùng. | State Transition |
| **TC_COUPON_002**| Voucher | Áp dụng mã giảm giá hợp lệ thành công | Đã thu thập mã `WELCOME10`, giỏ hàng có sản phẩm | 1. Vào trang Checkout<br>2. Chọn mã `WELCOME10` từ danh sách | Coupon Code: `WELCOME10` | Số tiền giảm giá được tính toán chính xác (-10%), tổng tiền thanh toán giảm tương ứng. | Normal Flow |
| **TC_CHECKOUT_001**| Checkout | Đặt hàng thành công bằng phương thức COD | Giỏ hàng có sản phẩm | 1. Nhập thông tin người nhận<br>2. Chọn phương thức COD<br>3. Bấm "Đặt hàng" | Receiver: "Nguyen Van A"<br>Phone: "0987654321"<br>Address: "Hanoi" | Đơn hàng tạo thành công với trạng thái `PENDING`, tồn kho sản phẩm tự động trừ đi. | Database Row Locking Test |
| **TC_CHECKOUT_002**| Checkout | Đặt hàng & Thanh toán qua VNPAY Sandbox | Giỏ hàng có sản phẩm | 1. Chọn phương thức VNPAY<br>2. Bấm "Thanh toán"<br>3. Nhập thẻ test NCB (`9704198526191432198`, OTP `123456`) | Card: `9704198526191432198`<br>OTP: `123456` | Điều hướng thành công sang VNPAY, thanh toán thành công và trả về trang xác nhận đơn hàng `PAID`. | Integration & E2E Testing |

---

## 👑 PHẦN 3: PHÂN HỆ QUẢN TRỊ VIÊN & THÙNG RÁC (ADMIN & RECYCLE BIN)

| Test Case ID | Feature | Mô tả Kịch bản Kiểm thử | Điều kiện tiền đề | Các bước thực hiện | Dữ liệu kiểm thử | Kết quả mong đợi | Kỹ thuật áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_ADMIN_001** | Admin CRUD | Thêm mới sản phẩm thành công | Đăng nhập tài khoản `admin` | 1. Truy cập Admin Dashboard $\rightarrow$ Sản phẩm<br>2. Bấm "Thêm sản phẩm mới"<br>3. Điền thông tin & Lưu | Name: "Test Phone X"<br>Price: 15000000<br>Stock: 50 | Sản phẩm hiển thị trong danh sách Admin và trang khách hàng. | CRUD Normal Flow |
| **TC_ADMIN_002** | Admin Order| Cập nhật trạng thái đơn hàng từ `PENDING` $\rightarrow$ `CONFIRMED` | Có đơn hàng đang ở trạng thái `PENDING` | 1. Vào trang Quản lý đơn hàng<br>2. Chọn đơn hàng ID 1<br>3. Đổi trạng thái sang `CONFIRMED` | Order ID: `1`<br>Status: `CONFIRMED` | Trạng thái đơn hàng cập nhật thành công, phát sóng sự kiện qua WebSocket. | State Transition |
| **TC_ADMIN_003** | Admin Order| Hủy đơn hàng và kiểm tra tự động hoàn tồn kho | Đơn hàng đang ở trạng thái `PENDING` | 1. Chọn đơn hàng ID 1<br>2. Đổi trạng thái sang `CANCELLED` | Order ID: `1`<br>Status: `CANCELLED` | Đơn hàng đổi thành `CANCELLED`, số lượng tồn kho của các sản phẩm trong đơn được cộng hoàn lại tự động. | Business Rule Testing |
| **TC_ADMIN_004** | Recycle Bin | Xóa mềm sản phẩm và Khôi phục dữ liệu từ Thùng rác | Sản phẩm A tồn tại trong DB | 1. Xóa sản phẩm A<br>2. Vào trang Thùng rác (Recycle Bin)<br>3. Bấm "Khôi phục" (Restore) | Entity: `Product`<br>Item: `Product A` | Sản phẩm A được xóa mềm chuyển vào bảng `recycle_bin`, sau khi khôi phục dữ liệu khôi phục nguyên trạng. | Soft Delete & Recovery Test |

---

## 💻 PHẦN 4: CÁC KỊCH BẢN KIỂM THỬ GIAO DIỆN & TRẢI NGHIỆM (UI/UX AUTOMATION TEST CASES)

| Test Case ID | Phân hệ / Màn hình | Kịch bản kiểm thử (Test Scenario) | Điều kiện tiền đề (Prerequisites) | Các bước thực hiện (Test Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Result) | Kỹ thuật áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_UI_001** | Đăng ký | Hiển thị thông báo lỗi validate form khi để trống trường | Đang ở trang Đăng ký | 1. Không nhập gì<br>2. Bấm nút "Đăng ký" | Không có | Hiển thị viền đỏ quanh các ô nhập liệu. Hiển thị lỗi dưới từng ô: "Tên đăng nhập không được trống", "Mật khẩu là bắt buộc". | Client Validation |
| **TC_UI_002** | Đăng ký | Kiểm tra độ mạnh của mật khẩu (Password Strength) | Đang ở trang Đăng ký | 1. Nhập username, email đúng<br>2. Nhập mật khẩu quá đơn giản ('123')<br>3. Bấm Đăng ký | Password: `123` | Hiển thị cảnh báo mật khẩu yếu: "Mật khẩu phải từ 6 ký tự trở lên". Nút Đăng ký bị vô hiệu hóa. | Boundary Analysis |
| **TC_UI_003** | Đăng nhập | Kiểm tra hiển thị lỗi khi đăng nhập sai thông tin | Đang ở trang Đăng nhập | 1. Nhập username đúng<br>2. Nhập password sai<br>3. Bấm nút "Đăng nhập" | User: `customer`<br>Pass: `wrong_pass` | Hiển thị Toast màu đỏ: "Tên đăng nhập hoặc mật khẩu không chính xác". Trạng thái loading kết thúc mượt mà. | UX Notification |
| **TC_UI_004** | Đăng nhập | Đăng nhập thành công và chuyển hướng thông minh | Đang ở trang Login sau khi click Giỏ hàng | 1. Điền thông tin đăng nhập đúng<br>2. Bấm "Đăng nhập" | User: `customer`<br>Pass: `customer123` | Đăng nhập thành công. Tự động chuyển hướng quay lại màn hình giỏ hàng/thanh toán trước đó. | Smart Redirect |
| **TC_UI_005** | Đăng nhập | Duy trì trạng thái đăng nhập sau khi Refresh trang (Reload Page) | Đã đăng nhập thành công | 1. Nhấn nút F5 (Refresh) trình duyệt | F5 Refresh | Trạng thái đăng nhập được duy trì. Header hiển thị avatar và tên user. Giỏ hàng phục hồi từ localStorage/API. | State Persistence |
| **TC_UI_006** | Đăng xuất | Đăng xuất khỏi hệ thống và xóa sạch phiên làm việc | User đã đăng nhập | 1. Bấm nút "Đăng xuất" trên Header<br>2. Nhấn nút quay lại (Back) của trình duyệt | Nhấn Back | Đăng xuất thành công, chuyển về trang chủ. Nhấn Back không xem lại được trang cá nhân (bắt đăng nhập lại). | Session Cleanup |
| **TC_UI_007** | Danh sách SP | Lọc sản phẩm theo nhiều danh mục cùng lúc (Multi-select) | Đang ở trang danh sách SP | 1. Tích chọn đồng thời 2 danh mục trên Sidebar Filter | Select: Điện thoại & Phụ kiện | Danh sách hiển thị sản phẩm thuộc cả 2 nhóm này. Số lượng sản phẩm tìm thấy cập nhật đúng. | FilterSidebar Test |
| **TC_UI_008** | Danh sách SP | Lọc sản phẩm bằng thanh kéo Khoảng giá (Price Slider Boundary) | Đang ở trang danh sách SP | 1. Kéo mốc Min về 2Mđ và Max về 15Mđ<br>2. Nhấn Áp dụng | Khoảng giá: 2Mđ - 15Mđ | Tất cả sản phẩm hiển thị có giá nằm trong đoạn [2 triệu, 15 triệu]. Sản phẩm ngoài khoảng bị lọc bỏ. | Price Slider Boundary |
| **TC_UI_009** | Tìm kiếm | Tìm kiếm với ký tự đặc biệt hoặc câu lệnh SQL (Sanitization) | Đang ở Trang chủ hoặc danh sách | 1. Nhập vào ô search các ký tự: `%`, `_`, `'`, `"`, `<script>`<br>2. Nhấn tìm kiếm | Query: `%` hoặc `<script>` | Hệ thống xử lý bình thường, hiển thị "Không tìm thấy sản phẩm" hoặc escape chuỗi. Không crash UI/cháy trang. | Search Sanitization |
| **TC_UI_010** | Chi tiết SP | Đổi ảnh lớn khi click ảnh thu nhỏ và zoom ảnh chính | Đang ở trang chi tiết SP | 1. Click lần lượt vào các ảnh thumbnail<br>2. Rê chuột lên ảnh chính | Click & Hover | Ảnh chính thay đổi mượt mà tương ứng với thumbnail được click. Khung zoom hiển thị ảnh phóng to mượt mà. | Gallery Component |
| **TC_UI_011** | Điều hướng | Quay lại trang danh sách SP và giữ nguyên bộ lọc (State Preservation) | Đã lọc Laptop & sắp xếp Giá tăng | 1. Click xem chi tiết 1 sản phẩm<br>2. Nhấn nút Back trên trình duyệt | Click Back | Quay lại trang danh sách sản phẩm. Bộ lọc 'Laptop' và sắp xếp 'Giá tăng' vẫn giữ nguyên trạng thái cũ. | State Preservation |
| **TC_UI_012** | Giỏ hàng | Đóng/Mở CartDrawer từ cạnh phải màn hình mượt mà | Đang ở trang chủ | 1. Bấm icon Giỏ hàng trên Header để mở<br>2. Click nút 'X' hoặc overlay để đóng | Click mở/đóng | CartDrawer trượt mở từ lề phải ra với animation mượt mà. Khi đóng, overlay tối màu biến mất. | UI Animation Test |
| **TC_UI_013** | Giỏ hàng | Cập nhật Badge số lượng sản phẩm trên Icon giỏ hàng lập tức | Đang xem danh sách SP | 1. Nhấn nút "Thêm vào giỏ" tại 1 thẻ sản phẩm<br>2. Quan sát icon giỏ hàng | Click thêm | Số badge đỏ trên icon giỏ hàng lập tức tăng lên. Hiệu ứng rung nhẹ micro-animation thu hút chú ý. | Zustand Reactivity |
| **TC_UI_014** | Giỏ hàng | Chặn thêm sản phẩm vượt quá số lượng tồn kho hiển thị | SP A chỉ còn tồn kho 2 cái | 1. Nhập số lượng 3<br>2. Nhấn "Thêm vào giỏ" hoặc nút '+' | Quantity = 3 | Nút '+' hoặc 'Thêm vào giỏ' bị vô hiệu hóa hoặc hiển thị toast: "Số lượng trong kho không đủ". Chặn vượt quá 2. | Boundary Check |
| **TC_UI_015** | Giỏ hàng | Nhấp chuột liên tục nút tăng số lượng (Spam click nút +/-) | Đang mở giỏ hàng | 1. Spam click liên tục nút '+' tăng số lượng 10 lần cực nhanh | Spam click 10 lần | Hệ thống xử lý debounce/throttle mượt mà. Đội hình spinner loading nhỏ xuất hiện. Tổng tiền cập nhật chính xác. | Async Throttle |
| **TC_UI_016** | Giỏ hàng | Modal xác nhận khi xóa sản phẩm khỏi giỏ hàng | Đang mở giỏ hàng | 1. Bấm nút 'Xóa' (thùng rác) bên cạnh sản phẩm | Click xóa | Hiển thị Modal xác nhận: "Bạn có chắc muốn xóa sản phẩm này?". Nếu chọn Có, sản phẩm xóa & tổng tiền giảm. | UX Modal Warning |
| **TC_UI_017** | Giỏ hàng | Phản hồi trực quan của ô nhập mã giảm giá (Coupon Input Feedback) | Đang ở trang giỏ hàng | 1. Nhập mã voucher sai 'XYZ' $\rightarrow$ Áp dụng<br>2. Nhập mã đúng 'WELCOME10' $\rightarrow$ Áp dụng | Code: 'XYZ' & 'WELCOME10' | Nhập sai: Ô nhập viền đỏ, hiển thị lỗi "Mã không hợp lệ". Nhập đúng: Viền xanh lá, báo "Áp dụng thành công (-10%)". | CouponInput Feedback |
| **TC_UI_018** | Thanh toán | Nhớ thông tin đã điền khi tải lại trang Checkout (Page Reload) | Đang điền form Checkout | 1. Điền Họ tên và SĐT nhận hàng<br>2. Nhấn F5 tải lại trang | Điền form + F5 | Họ tên và SĐT đã điền tự động phục hồi lại vào form sau khi trang tải xong, không bắt nhập lại từ đầu. | Form Persistence |
| **TC_UI_019** | Thanh toán | Tự động cuộn màn hình tới vị trí lỗi đầu tiên khi validate thất bại | Trang Checkout dài nhiều ô nhập | 1. Bỏ trống SĐT phía trên, điền đủ bên dưới<br>2. Cuộn xuống dưới bấm "Đặt hàng" | Click Đặt hàng | Màn hình tự động cuộn mượt (Smooth Scroll) lên ô SĐT đang lỗi và highlight viền đỏ giúp nhận diện ngay. | Auto Scroll to Error |
| **TC_UI_020** | Thanh toán | Mất kết nối mạng đột ngột khi bấm Đặt hàng (Network Offline Check) | Chuẩn bị bấm Đặt hàng | 1. Ngắt kết nối mạng (DevTools Offline)<br>2. Nhấn nút "Đặt hàng" | Offline Mode | Nút chuyển hiển thị lỗi hoặc xuất hiện Toast đỏ: "Không có kết nối internet. Vui lòng kiểm tra lại mạng". | Offline Resilient |
| **TC_UI_021** | Thanh toán | Vô hiệu hóa nút Đặt hàng sau khi click lần đầu (Double Click Protection) | Đang ở trang Checkout | 1. Nhấn nút "Đặt hàng"<br>2. Quan sát nút đặt hàng | Click Đặt hàng | Nút "Đặt hàng" ngay lập tức bị disabled, hiển thị text "Đang xử lý..." kèm spinner. Chặn click trùng lặp tạo 2 đơn. | Double Click Protection |
| **TC_UI_022** | Thanh toán | Trang thông báo Đặt hàng thành công hiển thị thông tin rõ ràng | Vừa đặt hàng thành công | 1. Quan sát màn hình xác nhận đơn thành công | Đặt hàng thành công | Hiển thị icon thành công, Mã đơn hàng rõ ràng, nút "Tiếp tục mua sắm" và "Xem đơn hàng của tôi". | Order Confirmation UX |
| **TC_UI_023** | Đơn hàng | Hiển thị đúng trạng thái đơn hàng trên Stepper trực quan | Đơn hàng ở trạng thái CONFIRMED | 1. Truy cập trang đơn hàng cá nhân<br>2. Quan sát Stepper | Order: CONFIRMED | OrderStatusStepper hiển thị mượt mà: Chờ xác nhận $\rightarrow$ Đã xác nhận $\rightarrow$ Đang giao $\rightarrow$ Đã giao. Bước 1, 2 sáng xanh. | Stepper Component |
| **TC_UI_024** | Admin | Chặn truy cập trang Admin bằng URL trực tiếp đối với khách thường | Đăng nhập tài khoản Customer | 1. Nhập đường dẫn `/admin` hoặc `/admin/products`<br>2. Nhấn Enter | URL: `/admin` | Hệ thống chặn truy cập, hiển thị 403 Forbidden hoặc redirect về trang chủ kèm Toast: "Không có quyền truy cập". | Protected Routes |
| **TC_UI_025** | Admin | Admin thêm sản phẩm - Tải lên ảnh quá dung lượng cho phép | Trang thêm sản phẩm Admin | 1. Upload file ảnh > 5MB hoặc file .zip<br>2. Quan sát thông báo | File > 5MB hoặc .zip | Hệ thống chặn tải lên. Báo lỗi: "Dung lượng ảnh tối đa là 5MB" hoặc "Chỉ hỗ trợ file ảnh JPG, PNG". Nút lưu bị khóa. | Upload File Validation |
| **TC_UI_026** | Admin | Admin thêm sản phẩm - Xem trước hình ảnh đã chọn (Image Preview) | Trang thêm sản phẩm Admin | 1. Nhấn chọn file ảnh từ máy tính<br>2. Chọn file hợp lệ `iphone.jpg` | File `iphone.jpg` | Giao diện hiển thị ngay ảnh thu nhỏ (preview) để Admin xác nhận trước khi lưu lên database. | Image Preview Test |
| **TC_UI_027** | Admin | Admin sửa sản phẩm - Hiển thị đầy đủ thông tin cũ trên Form sửa | Sản phẩm "Sofa Da" tồn tại | 1. Bấm nút "Sửa" sản phẩm "Sofa Da"<br>2. Quan sát form sửa | Edit Sofa Da | Form sửa hiển thị tự động pre-fill đầy đủ thông tin cũ: Tên, giá bán, mô tả, ảnh preview cũ. Không phải gõ lại. | Form Pre-fill Test |
| **TC_UI_028** | Admin | Admin xóa sản phẩm - Cảnh báo ràng buộc đơn hàng active | SP A đang nằm trong đơn hàng active | 1. Chọn xóa sản phẩm A<br>2. Quan sát Modal cảnh báo | Delete Product A | Modal cảnh báo hiển thị: "Sản phẩm đang nằm trong các đơn hàng chưa hoàn thành. Bạn có chắc muốn xóa?". | Delete Modal Warning |
| **TC_UI_029** | Admin | Admin cập nhật trạng thái đơn hàng và phản hồi phía khách hàng | Đơn hàng trạng thái PENDING | 1. Admin đổi trạng thái sang CONFIRMED $\rightarrow$ Lưu<br>2. Khách hàng xem lịch sử đơn | PENDING $\rightarrow$ CONFIRMED | Admin đổi trạng thái thành công. Phía khách hàng lập tức thấy Stepper chuyển sang "Đã xác nhận" thời gian thực. | Sync Admin-Customer |
| **TC_UI_030** | Giỏ hàng | Đồng bộ giỏ hàng thời gian thực giữa các Tabs trình duyệt | Mở 2 Tab A và B cùng trang web | 1. Tại Tab A, bấm thêm 1 sản phẩm vào giỏ<br>2. Chuyển sang Tab B (không reload) | Thêm SP ở Tab A | Tab B tự động cập nhật số badge icon giỏ hàng và danh sách trong CartDrawer mà không cần F5. | Multi-tab Storage Sync |
| **TC_UI_031** | Giỏ hàng | Xử lý khi sản phẩm trong giỏ hàng bị hết hàng đột ngột trước khi mua | SP A đã nằm trong giỏ khách | 1. Admin cập nhật kho SP A về 0<br>2. Khách mở trang Giỏ hàng/Checkout | Stock SP A = 0 | Hàng SP A hiển thị nhãn đỏ "Hết hàng". Nút Đặt hàng bị disabled và yêu cầu xóa SP A khỏi giỏ trước khi thanh toán. | Realtime Stock Check |

---

## 🛡️ PHẦN 5: CÁC KỊCH BẢN KIỂM THỬ API, PHÂN QUYỀN & BẢO MẬT (API AUTOMATION & SECURITY TEST CASES)

| Test Case ID | Phân hệ / Chức năng | Kịch bản kiểm thử (Test Scenario) | Điều kiện tiền đề | Các bước thực hiện (Test Steps) | Dữ liệu kiểm thử (Test Data) | Kết quả mong đợi (Expected Result) | Kỹ thuật áp dụng |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC_API_001** | Auth - Đăng ký | Đăng ký tài khoản khách hàng mới thành công | Username & Email chưa tồn tại | 1. Request POST `/api/auth/register`<br>2. Body chứa thông tin hợp lệ | User: `tester_qa_01`<br>Email: `tester_qa_01@gmail.com` | HTTP 200 OK. Response trả về thông tin user mới tạo (bảo mật không lộ password). | API Normal Flow |
| **TC_API_002** | Auth - Đăng nhập | Đăng nhập bằng tài khoản vừa tạo để lấy Token JWT | User `tester_qa_01` đã đăng ký | 1. Request POST `/api/auth/login`<br>2. Body chứa thông tin đúng | User: `tester_qa_01`<br>Pass: `Password123!` | HTTP 200 OK. Response trả về Access Token JWT và Refresh Token. Token chứa claims đúng. | Token Generation |
| **TC_API_003** | Auth - Đăng ký | Đăng ký tài khoản khi thiếu các trường bắt buộc | Không có | 1. Request POST `/api/auth/register`<br>2. Body rỗng các trường bắt buộc | Username: "", Email: "", Pass: "" | HTTP 400 Bad Request. Response chứa chi tiết lỗi validation cụ thể từng trường. | DTO Bean Validation |
| **TC_API_004** | Auth - Đăng ký | Đăng ký với username đã tồn tại (Duplicate Username) | User `tester_qa_01` đã tồn tại | 1. Request POST `/api/auth/register`<br>2. Dùng trùng Username, Email mới | User: `tester_qa_01`<br>Email: `new_email@gmail.com` | HTTP 400 Bad Request. Response thông báo: "Username đã tồn tại". CSDL không bị ghi đè. | Duplicate Constraint |
| **TC_API_005** | Auth - Đăng ký | Đăng ký với email đã tồn tại (Duplicate Email) | Email `tester_qa_01@gmail.com` đã dùng | 1. Request POST `/api/auth/register`<br>2. Dùng trùng Email, Username mới | User: `new_username`<br>Email: `tester_qa_01@gmail.com` | HTTP 400 Bad Request. Response báo: "Email đã được sử dụng". | Unique Email Check |
| **TC_API_006** | Auth - Đăng ký | Đăng ký với định dạng Email sai quy chuẩn (Regex Validation) | Không có | 1. Request POST `/api/auth/register`<br>2. Email thiếu ký tự `@` hoặc sai định dạng | Email: `invalid_email.com` | HTTP 400 Bad Request. Response báo lỗi: "Email không hợp lệ". | Regex Pattern Test |
| **TC_API_007** | Auth - Đăng ký | Đăng ký với SĐT sai quy chuẩn (Regex Validation) | Không có | 1. Request POST `/api/auth/register`<br>2. SĐT ít hơn 9 số hoặc chứa chữ | Phone: `098123abc` | HTTP 400 Bad Request. Response báo lỗi: "Số điện thoại không hợp lệ (phải gồm 9-11 chữ số)". | Phone Regex Check |
| **TC_API_008** | Auth - Đăng nhập | Đăng nhập với mật khẩu sai | User `tester_qa_01` đã tồn tại | 1. Request POST `/api/auth/login`<br>2. Username đúng, Password sai | User: `tester_qa_01`<br>Pass: `WrongPass` | HTTP 400 Bad Request hoặc 401 Unauthorized. Báo lỗi đăng nhập sai thông tin (không tiết lộ sai user hay pass). | Login Security |
| **TC_API_009** | Auth - Đăng nhập | Đăng nhập với tài khoản không tồn tại | User `non_existent` chưa tạo | 1. Request POST `/api/auth/login`<br>2. Username không tồn tại | User: `non_existent`<br>Pass: `Pass123` | HTTP 400 Bad Request hoặc 401 Unauthorized. Báo lỗi thông tin đăng nhập không đúng. | User Enumeration Prev |
| **TC_API_010** | Security | Truy cập tài nguyên bảo mật không gửi kèm Token | Không có | 1. Request GET `/api/cart`<br>2. Không đính kèm Header Authorization | No Authorization Header | HTTP 401 Unauthorized hoặc 403 Forbidden. Từ chối truy cập, trả về JSON error chuẩn. | Security Filter Check |
| **TC_API_011** | Security | Truy cập với Token JWT đã hết hạn hoặc bị giả mạo | Không có | 1. Request GET `/api/cart`<br>2. Header chứa Token giả hoặc expired | Bearer `invalid_token_str` | HTTP 401 Unauthorized hoặc 403 Forbidden. Chữ ký số JWT thất bại, từ chối request. | JWT Signature Check |
| **TC_API_012** | Security - RBAC | Tài khoản CUSTOMER cố gắng truy cập API ADMIN | Token vai trò CUSTOMER | 1. Request GET `/api/admin/dashboard`<br>2. Header Authorization token Customer | Bearer {customer_token} | HTTP 403 Forbidden. Từ chối truy cập do thiếu quyền ROLE_ADMIN. | RBAC Access Control |
| **TC_API_013** | Catalog | Lấy danh sách sản phẩm có Phân trang và Sắp xếp | CSDL đã seed sản phẩm | 1. Request GET `/api/products`<br>2. Params: `page=0&size=5&sortBy=price&direction=desc` | `?page=0&size=5` | HTTP 200 OK. Trả về 5 sản phẩm đầu tiên giá giảm dần. JSON chứa metadata `totalElements`, `totalPages`. | Pagination Test |
| **TC_API_014** | Catalog | Lấy danh sách sản phẩm theo Danh mục | Danh mục ID = 1 có sản phẩm | 1. Request GET `/api/products`<br>2. Query param: `categoryId=1` | `?categoryId=1` | HTTP 200 OK. Trả về danh sách sản phẩm thuộc danh mục ID = 1. | Category Filter API |
| **TC_API_015** | Catalog | Xem chi tiết sản phẩm bằng ID hợp lệ | Sản phẩm ID = 1 tồn tại | 1. Request GET `/api/products/1` | ID: `1` | HTTP 200 OK. Trả về thông tin sản phẩm: tên, giá, tồn kho, danh mục, list gallery ảnh phụ. | Detail API Test |
| **TC_API_016** | Catalog | Xem chi tiết sản phẩm với ID không tồn tại | SP ID = 99999 không có | 1. Request GET `/api/products/99999` | ID: `99999` | HTTP 404 Not Found. Response chứa message: "Resource not found". | ResourceNotFound Exception |
| **TC_API_017** | Catalog | Xem chi tiết sản phẩm với ID sai định dạng | Không có | 1. Request GET `/api/products/abc` | ID: `abc` | HTTP 400 Bad Request. Báo lỗi ép kiểu dữ liệu URL (TypeMismatch). | TypeMismatch Test |
| **TC_API_018** | Giỏ hàng | Thêm sản phẩm vào giỏ hàng thành công | Đã đăng nhập, SP ID 1 còn hàng | 1. Request POST `/api/cart/add`<br>2. Body `productId=1`, `quantity=2` | `productId: 1`, `quantity: 2` | HTTP 200 OK. Giỏ hàng cập nhật, trả về tổng số lượng và subtotal tính đúng. | Add Cart API |
| **TC_API_019** | Giỏ hàng | Thêm vào giỏ hàng với số lượng âm | Đã đăng nhập | 1. Request POST `/api/cart/add`<br>2. Body `quantity = -3` | `quantity: -3` | HTTP 400 Bad Request. Thông báo validation: "Số lượng phải lớn hơn 0". | Quantity Min Validation |
| **TC_API_020** | Giỏ hàng | Thêm vào giỏ hàng vượt quá số lượng tồn kho | SP ID 1 tồn kho chỉ có 5 | 1. Request POST `/api/cart/add`<br>2. Body `quantity = 10` | `quantity: 10` | HTTP 400 Bad Request. Response báo: "Quantity exceeds stock". | Stock Limit Check |
| **TC_API_021** | Voucher | Áp dụng mã giảm giá hợp lệ vào giỏ hàng | Giỏ hàng có SP, code WELCOME10 | 1. Request POST `/api/coupons/apply`<br>2. Body `code: "WELCOME10"` | `code: "WELCOME10"` | HTTP 200 OK. Trả về giỏ hàng mới giảm giá 10%, tiền giảm trừ chính xác vào tổng hóa đơn. | Apply Coupon Success |
| **TC_API_022** | Voucher | Áp dụng mã giảm giá đã hết hạn sử dụng | Code EXPIRED50 đã hết hạn | 1. Request POST `/api/coupons/apply`<br>2. Body `code: "EXPIRED50"` | `code: "EXPIRED50"` | HTTP 400 Bad Request. Báo lỗi: "Mã giảm giá đã hết hạn sử dụng". Tổng tiền không đổi. | Coupon Expiry Test |
| **TC_API_023** | Voucher | Áp dụng mã giảm giá không tồn tại | Không có | 1. Request POST `/api/coupons/apply`<br>2. Body `code: "FAKECODE"` | `code: "FAKECODE"` | HTTP 400 Bad Request. Báo lỗi: "Mã giảm giá không tồn tại". | Coupon Not Found |
| **TC_API_024** | Voucher | Áp dụng mã giảm giá khi giỏ hàng trống | Giỏ hàng trống rỗng | 1. Request POST `/api/coupons/apply`<br>2. Body `code: "WELCOME10"` | `code: "WELCOME10"` | HTTP 400 Bad Request. Response báo lỗi: "Giỏ hàng trống, không thể áp dụng mã". | Empty Cart Coupon |
| **TC_API_025** | Checkout | Đặt hàng thành công với thông tin người nhận hợp lệ | Giỏ hàng có sản phẩm | 1. Request POST `/api/orders`<br>2. Body chứa đủ tên, địa chỉ, SĐT, COD | Shipping details valid | HTTP 200 OK. Đơn hàng tạo trạng thái PENDING. Giỏ hàng dọn sạch. Tồn kho SP trừ đi. | Checkout COD Flow |
| **TC_API_026** | Checkout | Đặt hàng khi thiếu thông tin giao hàng bắt buộc | Giỏ hàng có sản phẩm | 1. Request POST `/api/orders`<br>2. Body thiếu địa chỉ và SĐT | Missing address & phone | HTTP 400 Bad Request. Validation error: "Shipping address is required" & "Shipping phone is required". | Checkout Validation |
| **TC_API_027** | Checkout | Đặt hàng khi giỏ hàng trống | Giỏ hàng trống rỗng | 1. Request POST `/api/orders`<br>2. Body điền đủ thông tin giao hàng | Shipping details valid | HTTP 400 Bad Request. Response báo: "Không thể đặt hàng do giỏ hàng trống". | Empty Cart Checkout |
| **TC_API_028** | Checkout | Đặt hàng liên tiếp nhiều lần (Idempotency Check) | Giỏ hàng có sản phẩm | 1. Gửi đồng thời 2 request Checkout trong thời gian < 100ms | 2 request cùng lúc | Chỉ 1 request đầu tạo đơn thành công. Request 2 bị từ chối do giỏ hàng đã dọn rỗng sau đơn 1. | Idempotency Test |
| **TC_API_029** | Review | Gửi đánh giá sản phẩm đã mua thành công | Đã mua SP 1 và đơn DELIVERED | 1. Request POST `/api/reviews`<br>2. Body `productId=1`, `rating=5` | `productId: 1`, `rating: 5` | HTTP 200 OK. Đánh giá được lưu vào CSDL. Rating trung bình của sản phẩm được cập nhật. | Review Success |
| **TC_API_030** | Review | Gửi đánh giá sản phẩm chưa từng mua | Chưa từng mua SP ID 2 | 1. Request POST `/api/reviews`<br>2. Body `productId=2`, `rating=4` | `productId: 2`, `rating: 4` | HTTP 400 Bad Request. Response báo lỗi: "Bạn chỉ được đánh giá sản phẩm đã mua và giao thành công". | Review Permission Check |
| **TC_API_031** | Review | Gửi đánh giá với số sao (rating) ngoài khoảng 1-5 | Đã mua SP ID 1 | 1. Request POST `/api/reviews`<br>2. Body `rating = 6` hoặc `0` | `rating: 6` | HTTP 400 Bad Request. Báo lỗi validation: "Điểm đánh giá phải nằm trong khoảng từ 1 đến 5". | Rating Boundary Test |
| **TC_API_032** | Security | SQL Injection tại ô tìm kiếm hoặc đăng nhập (Attack Check) | Không có | 1. Search query: `?search=abc' UNION SELECT...`<br>2. Login: `admin' OR '1'='1` | SQL Payload | Hệ thống không bị sập (500 Error). Không lộ lỗi SQL CSDL. Không bypass được đăng nhập. | SQL Injection Prev |
| **TC_API_033** | Security | Cross-Site Scripting (XSS) payload trong comment đánh giá | Đủ điều kiện đánh giá SP 1 | 1. Request POST `/api/reviews`<br>2. Comment: `<script>alert('xss');</script>` | XSS Script Payload | Mã HTML/JS được sanitize hoặc escape entities. Khi tải dữ liệu ra hiển thị văn bản thường, không chạy alert. | XSS Sanitization Test |
| **TC_API_034** | Security | IDOR Xem đơn hàng của người khác bằng cách đổi ID (IDOR Check) | Token tài khoản B | 1. Request GET `/api/orders/10` (Đơn hàng thuộc sở hữu của User A) | Order ID của User A | HTTP 403 Forbidden hoặc 404 Not Found. Tuyệt đối không trả về thông tin đơn hàng người khác. | IDOR Vulnerability Test |

# 🐛 NHẬT KÝ THEO DÕI VÀ QUẢN LÝ LỖI (DEFECT LOG / BUG REPORT)

**Dự án:** AstraShop - Hệ thống Thương mại Điện tử Mini  
**Mô phỏng quy trình quản lý lỗi:** MantisBT / Jira Bug Tracking  

---

## 📌 QUY TRÌNH PHÂN LOẠI MỨC ĐỘ NGIÊM TRỌNG (SEVERITY) & ƯU TIÊN (PRIORITY)

- **Severity (Mức độ nghiêm trọng):**
  - 🔴 **Critical:** Hệ thống sụp đổ, gián đoạn hoàn toàn luồng thanh toán hoặc gây mất mát dữ liệu.
  - 🟠 **High:** Lỗi chức năng chính (ví dụ: Áp voucher sai số tiền, sai tính toán tồn kho).
  - 🟡 **Medium:** Lỗi giao diện, lỗi hiển thị thông báo, mất định dạng UI.
  - 🟢 **Low:** Lỗi chính tả, canh lề khoảng cách UI.

- **Status (Trạng thái vòng đời lỗi):** `NEW` $\rightarrow$ `OPEN` $\rightarrow$ `IN_PROGRESS` $\rightarrow$ `RESOLVED` $\rightarrow$ `CLOSED`.

---

## 📋 MẪU BÁO CÁO LỖI NỔI BẬT TRONG DỰ ÁN (SAMPLE DEFECT REPORTS)

### 🔴 BUG-001: Tranh chấp hàng tồn kho (Race Condition) khi 2 khách hàng cùng checkout sản phẩm cuối cùng

- **Bug ID:** `BUG-001`
- **Tiêu đề:** Tồn kho sản phẩm bị âm khi 2 luồng đồng thời gọi API Checkout đặt mua mặt hàng còn 1 sản phẩm.
- **Phân hệ:** Order / Checkout Service.
- **Severity:** 🔴 Critical | **Priority:** P1 (Highest).
- **Trạng thái:** `CLOSED` (Đã khắc phục).
- **Môi trường:** Local Server / MySQL 8.0.
- **Các bước tái hiện (Steps to Reproduce):**
  1. Sản phẩm A có `stock_quantity = 1`.
  2. Tạo 2 tài khoản khách hàng `user1` và `user2` cùng thêm sản phẩm A vào giỏ.
  3. Gửi đồng thời 2 HTTP Request POST `/api/orders` tại cùng 1 thời điểm millisecond.
- **Kết quả thực tế (Actual Result trước khi sửa):** Cả 2 đơn hàng đều tạo thành công, `stock_quantity` bị tụt xuống `-1`.
- **Kết quả mong đợi (Expected Result):** Chỉ 1 đơn hàng thành công, đơn hàng thứ 2 nhận được thông báo lỗi "Sản phẩm đã hết hàng" (HTTP 400).
- **Giải pháp khắc phục (Fix Solution):** Áp dụng Database Row Locking (`SELECT ... FOR UPDATE`) trong phương thức `@Transactional checkout()` tại [ShopService.java](file:///e:/WEBBANHANG/src/main/java/com/example/Webbanhang/service/ShopService.java).

---

### 🟠 BUG-002: Áp dụng mã giảm giá hết hạn hoặc đã vượt quá lượt sử dụng tối đa

- **Bug ID:** `BUG-002`
- **Tiêu đề:** Mã coupon vẫn có thể áp dụng thành công khi `max_uses` đã đạt giới hạn tối đa.
- **Phân hệ:** Coupon Service.
- **Severity:** 🟠 High | **Priority:** P2 (High).
- **Trạng thái:** `CLOSED` (Đã khắc phục).
- **Các bước tái hiện:**
  1. Tạo mã Coupon `DISCOUNT50` với `max_uses = 1`.
  2. Khách hàng 1 áp dụng mã thành công và hoàn tất đơn hàng (`current_uses` tăng thành 1).
  3. Khách hàng 2 tiếp tục nhập mã `DISCOUNT50` tại trang Checkout.
- **Kết quả thực tế (trước khi sửa):** Khách hàng 2 vẫn được chiết khấu 50%.
- **Kết quả mong đợi:** Báo lỗi "Mã giảm giá đã hết lượt sử dụng hoặc không hợp lệ".
- **Giải pháp khắc phục:** Thêm kiểm tra điều kiện `coupon.getCurrentUses() >= coupon.getMaxUses()` và `expiration_date` trong phương thức `applyCoupon()`.

---

### 🟡 BUG-003: Đánh giá sản phẩm hiển thị sai thời gian và không kiểm tra điều kiện mua hàng

- **Bug ID:** `BUG-003`
- **Tiêu đề:** Người dùng chưa từng mua sản phẩm vẫn gửi được đánh giá 5 sao.
- **Phân hệ:** Product Review Module.
- **Severity:** 🟡 Medium | **Priority:** P3 (Medium).
- **Trạng thái:** `CLOSED` (Đã khắc phục).
- **Các bước tái hiện:**
  1. Tạo tài khoản mới chưa phát sinh bất kỳ đơn hàng nào.
  2. Gọi API POST `/api/products/1/reviews` kèm rating 5 sao.
- **Kết quả thực tế (trước khi sửa):** Đánh giá được lưu thành công vào CSDL.
- **Kết quả mong đợi:** Báo lỗi "Bạn chỉ có thể đánh giá sản phẩm sau khi đã mua và nhận hàng thành công (Đơn hàng DELIVERED)".
- **Giải pháp khắc phục:** Bổ sung truy vấn kiểm tra sự tồn tại của bảng `orders` chứa `order_items` với `status = DELIVERED` của `userId` hiện tại trước khi tạo Review.
