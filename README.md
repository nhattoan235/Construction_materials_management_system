# Hệ Thống Quản Lý Cửa Hàng Vật Liệu Xây Dựng (Construction Materials Management System)

Dự án Hệ thống Quản lý Cửa hàng Vật liệu Xây dựng (VLXD) được xây dựng trên nền tảng **Spring Boot 4** và **Java 26**, kết hợp với công nghệ giao diện **Thymeleaf**, bảo mật bằng **Spring Security** và cơ sở dữ liệu **MySQL**.

---

## 🛠️ Công Nghệ Sử Dụng (Tech Stack)

* **Backend Framework:** Spring Boot 4.0.6
* **Language:** Java 26
* **Database:** MySQL 8.0+
* **ORM & JPA:** Spring Data JPA + Hibernate 7
* **Security:** Spring Security 6+
* **Template Engine:** Thymeleaf (hỗ trợ phân quyền Security trên UI)
* **Build System:** Maven

---

## 📂 Cấu Trúc Dự Án (Project Structure)

```text
Construction_materials_management_system/
├── .agents/                        # Cấu hình rule và workflow dành cho AI Agent
│   └── AGENTS.md                   # Các quy chuẩn code và quy tắc làm việc cho Agent
├── ht_vlxd/                         # Source code ứng dụng Spring Boot
│   ├── .mvn/                       # Cấu hình Maven Wrapper
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/ht_vlxd/
│   │   │   │   ├── Config/          # SecurityConfig, DatabaseSeeder...
│   │   │   │   ├── Controller/      # Web MVC Controllers & RestControllers
│   │   │   │   ├── Model/           # Các JPA Entities (HangHoa, NguoiDung, DonHang...)
│   │   │   │   ├── Repository/      # Spring Data JPA Repositories
│   │   │   │   └── Service/         # Các dịch vụ xử lý logic nghiệp vụ
│   │   │   └── resources/
│   │   │       ├── templates/       # Giao diện HTML (Thymeleaf)
│   │   │       ├── static/          # CSS, JS, Images tĩnh
│   │   │       └── application.properties # File cấu hình kết nối DB và hệ thống
│   │   └── test/                    # Các kịch bản Unit Test & Integration Test
│   ├── mvnw                         # Maven Wrapper script cho Linux/macOS
│   ├── mvnw.cmd                     # Maven Wrapper script cho Windows
│   └── pom.xml                      # Cấu hình dependencies & plugins Maven
└── Database.sql                     # File SQL chứa schema thô và lệnh chèn dữ liệu mẫu
```

---

## 🚀 Hướng Dẫn Khởi Chạy (Getting Started)

### 1. Chuẩn bị môi trường
* Đảm bảo máy tính đã cài đặt **Java JDK 26+** (Ví dụ: Eclipse Temurin).
* Cài đặt và kích hoạt dịch vụ **MySQL Server** chạy trên cổng mặc định `3306`.

### 2. Cấu hình kết nối Cơ sở dữ liệu
Mở file [application.properties](file:///d:/Learning/Projects/Construction_materials_management_system/ht_vlxd/src/main/resources/application.properties) và điều chỉnh các cấu hình tương ứng với tài khoản MySQL của bạn:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vlxd_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD # Bỏ trống nếu không dùng mật khẩu
```

### 3. Chạy ứng dụng
Mở Terminal tại thư mục `ht_vlxd` và thực hiện lệnh chạy:
```powershell
# Trên Windows
.\mvnw.cmd spring-boot:run

# Trên Linux/macOS
./mvnw spring-boot:run
```

* **Khởi tạo dữ liệu tự động:** 
  * Khi ứng dụng khởi chạy lần đầu, Hibernate sẽ tự động quét các Model và sinh cấu trúc bảng vào database `vlxd_db`.
  * Lớp [DatabaseSeeder.java](file:///d:/Learning/Projects/Construction_materials_management_system/ht_vlxd/src/main/java/com/example/ht_vlxd/Config/DatabaseSeeder.java) sẽ tự động nạp toàn bộ danh mục, hàng hóa, tài khoản phân quyền mẫu nếu phát hiện cơ sở dữ liệu trống.

---

## 🔑 Tài Khoản Thử Nghiệm (Demo Accounts)

Tất cả tài khoản thử nghiệm dưới đây sử dụng mật khẩu mặc định là: **`Admin@123`**

| Username | Vai Trò (Role) | Mô tả phân quyền |
| :--- | :--- | :--- |
| `admin` | **QUAN_TRI_VIEN** | Quản lý toàn bộ hệ thống, phân quyền người dùng. |
| `giamdoc` | **BAN_QUAN_LY** | Xem các báo cáo thống kê doanh thu, tồn kho, công nợ. |
| `nvkd01` | **NV_KINH_DOANH** | Tạo đơn hàng, quản lý khách hàng. |
| `nvkho01` | **NV_KHO** | Tạo phiếu nhập/xuất kho, cập nhật số lượng tồn kho. |
| `nvkt01` | **NV_KE_TOAN** | Xử lý thanh toán, hóa đơn, quản lý công nợ. |
| `khachhang01` | **KHACH_HANG** | Xem danh mục hàng hóa, đặt hàng online, xem lịch sử mua hàng. |

---

## 📈 Sơ đồ các chức năng chính (Core Features)
* **Quản lý Hàng hóa & Danh mục:** Quản lý thông tin chi tiết, quy cách, đơn vị tính, giá bán sỉ/lẻ.
* **Quản lý Kho bãi & Tồn kho:** Quản lý kho, cập nhật số lượng tồn, phiếu nhập/xuất kho.
* **Quản lý Đơn hàng & Hợp đồng:** Tạo đơn hàng bán hàng, quản lý vòng đời đơn hàng, hợp đồng vật liệu.
* **Báo cáo & Thống kê:** Doanh thu bán hàng, tồn kho, biến động công nợ của khách hàng theo thời gian.
