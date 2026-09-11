# CMMS - Hệ Thống Quản Lý Cửa Hàng Vật Liệu Xây Dựng Sài Gòn CMC

**Sài Gòn CMC - Construction Materials Management System (CMMS)** là hệ thống quản lý số hóa toàn diện quy trình vận hành, kinh doanh, kho bãi và tài chính dành riêng cho chuỗi cửa hàng cung ứng vật liệu xây dựng. Được xây dựng trên nền tảng **Spring Boot 4** và **Java 26**, dự án giải quyết triệt để các bài toán nghiệp vụ thực tế phức tạp của ngành VLXD mà các hệ thống bán lẻ thông thường không đáp ứng được.

---

## 🛠️ Công Nghệ Sử Dụng (Technologies Used)

Dự án ứng dụng các công nghệ hiện đại và chuẩn mực trong phát triển phần mềm doanh nghiệp:

*   **Ngôn ngữ lập trình:** Java 26 (tận dụng các tính năng mới nhất của Java nâng cao hiệu suất).
*   **Framework cốt lõi:** Spring Boot 4.0.6 (Spring Web, Spring Data JPA, Spring Security 6).
*   **Cơ sở dữ liệu:** MySQL 8.0+ (sử dụng JPA/Hibernate làm hệ quản trị cơ sở dữ liệu quan hệ, thiết kế chuẩn hóa 3NF).
*   **Bảo mật:** Spring Security 6 (phân quyền đa nhiệm dựa trên vai trò - RBAC, bảo vệ các endpoint nhạy cảm chống tấn công CSRF).
*   **Template Engine:** Thymeleaf 3.0 (tích hợp thẻ an ninh `thymeleaf-extras-springsecurity6` để phân quyền trực tiếp trên giao diện người dùng).
*   **Giao diện & Styling:** CSS thuần chất lượng cao (sử dụng CSS Variables, Flexbox, CSS Grid và hiệu ứng Glassmorphism) đảm bảo Responsive mượt mà trên mọi thiết bị.
*   **Trí tuệ nhân tạo:** Google Gemini API (sử dụng mô hình **Gemini 2.5 Flash** để tư vấn vật tư, tính toán định mức).
*   **Quản lý thư viện & Build:** Maven.

## 🚀 Đặc Tả Nghiệp Vụ Thực Tế (Real-World Business Cases)

Dự án này được thiết kế dựa trên các nghiệp vụ thực tế tại các doanh nghiệp cung ứng vật tư xây dựng:

### 1. Quản lý Hợp đồng & Đơn giá Cố định (Project Contracts)
*   **Vấn đề thực tế:** Giá sắt thép, xi măng biến động theo ngày. Nhà thầu cần đơn giá cố định trong suốt thời gian thi công dự án (6 - 12 tháng).
*   **Giải pháp:** Hệ thống hỗ trợ lập **Hợp đồng dài hạn** (`HopDong`) quy định đơn giá cố định cho từng loại vật tư. Khi xuất hàng lẻ cho công trình (`DonHang`), hệ thống sẽ tự động áp đơn giá ưu đãi trong hợp đồng thay vì giá bán lẻ ngoài thị trường.

### 2. Kiểm soát Hạn mức Công nợ Chặt chẽ (Credit Limit Enforcement)
*   **Vấn đề thực tế:** Các nhà thầu thường mua hàng trước trả tiền sau. Nếu không kiểm soát chặt chẽ, dư nợ sẽ vượt quá khả năng chi trả của nhà thầu.
*   **Giải pháp:** Mỗi khách hàng (`KhachHang`) có một `hanMucNo` riêng. Trước khi duyệt phiếu xuất kho (`PhieuKho`), hệ thống sẽ tính toán: **(Dư nợ hiện tại + Giá trị đơn hàng mới) > Hạn mức nợ**. Nếu vượt quá, hệ thống sẽ tự động khóa đơn hàng và yêu cầu bộ phận Kế toán duyệt thủ công hoặc yêu cầu khách hàng thanh toán bớt nợ trước khi giao hàng.

### 3. Quy trình Xuất Nhập Kho & Đối Soát Hai Chiều (Warehouse Verification)
*   **Vấn đề thực tế:** Số lượng vật liệu thực tế trong kho và trên giấy tờ dễ lệch nhau do hao hụt tự nhiên hoặc sai sót khi bốc dỡ.
*   **Giải pháp:** Tách biệt vai trò **Kinh doanh** (Tạo đơn hàng) và **Thủ kho** (Xác nhận xuất kho). Tồn kho thực tế (`TonKho`) chỉ được cộng/trừ khi Thủ kho bấm duyệt **Phiếu kho** (`PhieuKho`) sau khi đã bốc xếp vật tư lên xe tải thực tế.

### 4. Đổi Trả Hàng & Hoàn Khớp Dòng Tiền (Surplus & Defect Management)
*   **Vấn đề thực tế:** Công trình hoàn thiện thường thừa ra gạch đá hoặc sắt vụn, nhà thầu có nhu cầu trả lại cửa hàng để trừ nợ.
*   **Giải pháp:** Hệ thống xử lý phiếu đổi trả (`DoiTraHang`), tự động tăng lại tồn kho thực tế, đồng thời hạch toán một bút toán giảm trừ công nợ trực tiếp cho khách hàng một cách minh bạch.

### 5. Bộ Tính Toán Định Mức Vật Tư (Material Estimator Calculator)
*   Tự động tính toán khối lượng vật liệu cần thiết dựa trên diện tích xây dựng (m²), loại kết cấu (nhà cấp 4, nhà phố, biệt thự) và số tầng.
*   Áp dụng định mức tiêu chuẩn xây dựng quốc gia (`DinhMucVatLieu`) để quy đổi ra số lượng cát (m³), đá (m³), xi măng (bao), gạch (viên) và sắt (kg) một cách chính xác nhất.

---

## 🛠️ Công Nghệ & Kiến Trúc Mã Nguồn

### 1. Công nghệ sử dụng
*   **Backend:** Spring Boot 4.0.6, Spring Security 6, Spring Data JPA (Hibernate 7)
*   **Database:** MySQL 8.0+
*   **UI/UX:** Thymeleaf Engine, CSS Grid & Flexbox, hiệu ứng Glassmorphic hiện đại, Responsive.
*   **AI Integration:** API Gemini 2.5 Flash thông qua backend Java RestController bảo mật.

### 2. Cấu trúc thư mục chuẩn hóa (Domain Sub-packaging)
Dự án được sắp xếp cực kỳ khoa học, gom nhóm theo các miền nghiệp vụ chuyên biệt dưới các package phân lớp chính:
```
com.example.ht_vlxd
├── Config                  # Cấu hình hệ thống
│   ├── auth                # Bảo mật Spring Security
│   └── common              # Gieo dữ liệu khởi tạo (DatabaseSeeder)
├── Controller              # Lớp tiếp nhận Request
│   ├── auth / customer     # Điều hướng tài khoản, khách hàng
│   ├── inventory / sales   # Quản lý kho, đơn hàng, hợp đồng
│   ├── finance / estimation# Kế toán công nợ, máy tính vật tư
│   └── ai                  # API Trợ lý ảo Gemini
├── DTO                     # Đối tượng chuyển đổi dữ liệu
├── Model                   # Lớp ánh xạ thực thể JPA (20 Entity chính)
├── Repository              # Lớp giao tiếp Database (Spring Data JPA)
└── Service                 # Lớp xử lý Logic nghiệp vụ cốt lõi
```

---

## 🔑 Tài Khoản Kiểm Thử Nghiệp Vụ (Demo Accounts)

Để trải nghiệm toàn bộ luồng nghiệp vụ trên, bạn có thể đăng nhập bằng các tài khoản phân quyền tương ứng dưới đây (mật khẩu mặc định là `Admin@123`):

*   **Quản trị viên (`admin`):** Quản trị hệ thống, cấp quyền tài khoản.
*   **Giám đốc (`giamdoc`):** Xem báo cáo tài chính, tổng dư nợ công nợ toàn hệ thống.
*   **Kinh doanh (`nvkd01`):** Lập hợp đồng dự án, tạo đơn hàng cho khách hàng.
*   **Thủ kho (`nvkho01`):** Xác nhận xuất kho vật liệu, kiểm tra tồn kho thực tế.
*   **Kế toán (`nvkt01`):** Duyệt hạn mức nợ, thu tiền thanh toán từ khách hàng.
*   **Khách hàng (`khachhang01`):** Sử dụng máy tính định mức, chat với Trợ lý AI để tư vấn gạch đá, sắt thép.

---

## ⚙️ Hướng Dẫn Cài Đặt Nhanh

1.  **Cấu hình Database:** Cấu hình thông tin kết nối MySQL và khóa **Gemini API Key** tại [application.properties](file:///D:/Construction_materials_management_system-feature-may-tinh-vat-lieu-1/Construction_materials_management_system-feature-may-tinh-vat-lieu-1/ht_vlxd/src/main/resources/application.properties):
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/vlxd_db?createDatabaseIfNotExist=true
    spring.datasource.username=root
    spring.datasource.password=your_password
    gemini.api.key=your_gemini_api_key
    ```
2.  **Khởi chạy dự án:** Chạy lệnh sau tại thư mục chứa file `pom.xml`:
    ```powershell
    .\mvnw.cmd spring-boot:run
    ```
3.  **Trải nghiệm:** Truy cập đường dẫn `http://localhost:8080` trên trình duyệt.
