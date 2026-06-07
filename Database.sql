-- ============================================================
--  SCHEMA: Quản lý Cửa hàng Vật liệu Xây dựng (VLXD)
--  Tương thích: MySQL 8.0+  |  Spring Boot + JPA/Hibernate
--  Gồm 16 bảng phục vụ 13 màn hình
-- ============================================================

CREATE DATABASE IF NOT EXISTS vlxd_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE vlxd_db;

-- ============================================================
-- 1. ROLE - Phân quyền hệ thống
--    Dùng cho: Màn Quản lý tài khoản, Đăng nhập
-- ============================================================
CREATE TABLE role (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(50) NOT NULL UNIQUE  -- KHACH_HANG, NV_KINH_DOANH, NV_KHO, NV_KE_TOAN, BAN_QUAN_LY, QUAN_TRI_VIEN, NHA_CUNG_CAP, NV_GIAO_NHAN
);

-- ============================================================
-- 2. NGUOI_DUNG - Tài khoản hệ thống
--    Dùng cho: Đăng nhập, Đăng ký, Thông tin cá nhân, Quản lý tài khoản
-- ============================================================
CREATE TABLE nguoi_dung (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    ho_ten          VARCHAR(150) NOT NULL,
    email           VARCHAR(150) UNIQUE,
    so_dien_thoai   VARCHAR(20),
    dia_chi         TEXT,
    avatar_url      VARCHAR(500),
    role_id         BIGINT NOT NULL,
    trang_thai      ENUM('HOAT_DONG', 'BI_KHOA', 'CHO_DUYET') NOT NULL DEFAULT 'HOAT_DONG',
    ngay_tao        DATETIME DEFAULT CURRENT_TIMESTAMP,
    ngay_cap_nhat   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_nguoidung_role FOREIGN KEY (role_id) REFERENCES role(id)
);

-- ============================================================
-- 3. KHACH_HANG - Thông tin mở rộng của khách hàng
--    Dùng cho: Thông tin cá nhân, Quản lý đơn hàng, Hợp đồng
-- ============================================================
CREATE TABLE khach_hang (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nguoi_dung_id   BIGINT NOT NULL UNIQUE,
    ma_khach_hang   VARCHAR(20) NOT NULL UNIQUE,   -- KH-0001
    ten_cong_ty     VARCHAR(200),
    ma_so_thue      VARCHAR(20),
    nguoi_dai_dien  VARCHAR(150),
    loai_khach      ENUM('CA_NHAN', 'DOANH_NGHIEP') NOT NULL DEFAULT 'CA_NHAN',
    han_muc_no      DECIMAL(18, 2) DEFAULT 0,       -- hạn mức công nợ cho phép
    ghi_chu         TEXT,
    CONSTRAINT fk_khachhang_nguoidung FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);

-- ============================================================
-- 4. NHA_CUNG_CAP - Nhà cung cấp hàng hóa
--    Dùng cho: Quản lý kho, Quản lý nhập hàng
-- ============================================================
CREATE TABLE nha_cung_cap (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nguoi_dung_id   BIGINT UNIQUE,                 -- NULL nếu NCC chưa có tài khoản
    ma_ncc          VARCHAR(20) NOT NULL UNIQUE,   -- NCC-0001
    ten_ncc         VARCHAR(200) NOT NULL,
    ma_so_thue      VARCHAR(20),
    dia_chi         TEXT,
    email           VARCHAR(150),
    so_dien_thoai   VARCHAR(20),
    nguoi_lien_he   VARCHAR(150),
    trang_thai      ENUM('HOAT_DONG', 'NGUNG') NOT NULL DEFAULT 'HOAT_DONG',
    ghi_chu         TEXT,
    ngay_tao        DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ncc_nguoidung FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);

-- ============================================================
-- 5. DANH_MUC - Phân loại hàng hóa
--    Dùng cho: Danh mục hàng hóa
-- ============================================================
CREATE TABLE danh_muc (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_danh_muc VARCHAR(20) NOT NULL UNIQUE,
    ten         VARCHAR(150) NOT NULL,
    mo_ta       TEXT,
    parent_id   BIGINT,                             -- hỗ trợ danh mục cha-con
    CONSTRAINT fk_danhmuc_parent FOREIGN KEY (parent_id) REFERENCES danh_muc(id)
);

-- ============================================================
-- 6. HANG_HOA - Sản phẩm/Mặt hàng vật liệu xây dựng
--    Dùng cho: Danh mục hàng hóa, Quản lý kho, Quản lý đơn hàng
-- ============================================================
CREATE TABLE hang_hoa (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_hang         VARCHAR(30) NOT NULL UNIQUE,
    ten_hang        VARCHAR(200) NOT NULL,
    danh_muc_id     BIGINT,
    don_vi_tinh     VARCHAR(30) NOT NULL,           -- khối, bao, cây, viên, tấm...
    quy_cach        VARCHAR(200),                   -- kích thước, thông số kỹ thuật
    gia_ban_le      DECIMAL(18, 2) NOT NULL DEFAULT 0,
    gia_ban_si      DECIMAL(18, 2),
    trong_luong_kg  DECIMAL(10, 3),
    anh_url         VARCHAR(500),
    trang_thai      ENUM('KINH_DOANH', 'NGUNG_KINH_DOANH') NOT NULL DEFAULT 'KINH_DOANH',
    ghi_chu         TEXT,
    ngay_tao        DATETIME DEFAULT CURRENT_TIMESTAMP,
    ngay_cap_nhat   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_hanghoa_danhmuc FOREIGN KEY (danh_muc_id) REFERENCES danh_muc(id)
);

-- ============================================================
-- 7. KHO - Kho bãi
--    Dùng cho: Quản lý kho
-- ============================================================
CREATE TABLE kho (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_kho      VARCHAR(20) NOT NULL UNIQUE,
    ten_kho     VARCHAR(150) NOT NULL,
    dia_chi     TEXT,
    dien_tich   DECIMAL(10, 2),                    -- m²
    trang_thai  ENUM('HOAT_DONG', 'NGUNG') NOT NULL DEFAULT 'HOAT_DONG'
);

-- ============================================================
-- 8. TON_KHO - Số lượng tồn kho theo từng mặt hàng / kho
--    Dùng cho: Quản lý kho, Dashboard báo cáo
-- ============================================================
CREATE TABLE ton_kho (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    hang_hoa_id BIGINT NOT NULL,
    kho_id      BIGINT NOT NULL,
    so_luong    DECIMAL(18, 3) NOT NULL DEFAULT 0,
    ngay_cap_nhat DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_tonkho (hang_hoa_id, kho_id),
    CONSTRAINT fk_tonkho_hanghoa FOREIGN KEY (hang_hoa_id) REFERENCES hang_hoa(id),
    CONSTRAINT fk_tonkho_kho     FOREIGN KEY (kho_id)      REFERENCES kho(id)
);

-- ============================================================
-- 9. PHIEU_KHO - Phiếu nhập / xuất kho
--    Dùng cho: Quản lý kho
-- ============================================================
CREATE TABLE phieu_kho (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_phieu        VARCHAR(30) NOT NULL UNIQUE,    -- PN-2024-0001 / PX-2024-0001
    loai_phieu      ENUM('NHAP', 'XUAT') NOT NULL,
    kho_id          BIGINT NOT NULL,
    nguoi_tao_id    BIGINT NOT NULL,
    don_hang_id     BIGINT,                         -- liên kết đơn hàng (nullable)
    nha_cung_cap_id BIGINT,                         -- khi nhập từ NCC
    ngay_lap        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ghi_chu         TEXT,
    trang_thai      ENUM('NHAP', 'DA_DUYET', 'HUY') NOT NULL DEFAULT 'NHAP',
    CONSTRAINT fk_phieukho_kho      FOREIGN KEY (kho_id)          REFERENCES kho(id),
    CONSTRAINT fk_phieukho_nguoitao FOREIGN KEY (nguoi_tao_id)    REFERENCES nguoi_dung(id),
    CONSTRAINT fk_phieukho_ncc      FOREIGN KEY (nha_cung_cap_id) REFERENCES nha_cung_cap(id)
);

CREATE TABLE phieu_kho_chi_tiet (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    phieu_kho_id    BIGINT NOT NULL,
    hang_hoa_id     BIGINT NOT NULL,
    so_luong        DECIMAL(18, 3) NOT NULL,
    don_gia         DECIMAL(18, 2) NOT NULL,
    ghi_chu         TEXT,
    CONSTRAINT fk_pkct_phieukho FOREIGN KEY (phieu_kho_id) REFERENCES phieu_kho(id),
    CONSTRAINT fk_pkct_hanghoa  FOREIGN KEY (hang_hoa_id)  REFERENCES hang_hoa(id)
);

-- ============================================================
-- 10. DON_HANG - Đơn đặt hàng của khách
--     Dùng cho: Quản lý đơn hàng, Đơn hàng của tôi (KH)
-- ============================================================
CREATE TABLE don_hang (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_don_hang     VARCHAR(30) NOT NULL UNIQUE,    -- DH-2024-0001
    khach_hang_id   BIGINT NOT NULL,
    nv_kinh_doanh_id BIGINT,                        -- nhân viên phụ trách
    ngay_dat        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ngay_giao_du_kien DATETIME,
    dia_chi_giao    TEXT,
    tong_tien       DECIMAL(18, 2) NOT NULL DEFAULT 0,
    tien_dat_coc    DECIMAL(18, 2) DEFAULT 0,
    trang_thai      ENUM(
                        'CHO_XAC_NHAN',
                        'DA_XAC_NHAN',
                        'DANG_CHUAN_BI',
                        'DANG_GIAO',
                        'DA_GIAO',
                        'HOAN_THANH',
                        'HUY',
                        'DOI_TRA'
                    ) NOT NULL DEFAULT 'CHO_XAC_NHAN',
    ghi_chu         TEXT,
    ngay_cap_nhat   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_donhang_khachhang  FOREIGN KEY (khach_hang_id)    REFERENCES khach_hang(id),
    CONSTRAINT fk_donhang_nvkd       FOREIGN KEY (nv_kinh_doanh_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE don_hang_chi_tiet (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    don_hang_id     BIGINT NOT NULL,
    hang_hoa_id     BIGINT NOT NULL,
    so_luong        DECIMAL(18, 3) NOT NULL,
    don_gia         DECIMAL(18, 2) NOT NULL,        -- giá tại thời điểm đặt
    thanh_tien      DECIMAL(18, 2) NOT NULL,
    ghi_chu         TEXT,
    CONSTRAINT fk_dhct_donhang  FOREIGN KEY (don_hang_id) REFERENCES don_hang(id),
    CONSTRAINT fk_dhct_hanghoa  FOREIGN KEY (hang_hoa_id) REFERENCES hang_hoa(id)
);

-- ============================================================
-- 11. HOP_DONG - Hợp đồng cung ứng
--     Dùng cho: Quản lý hợp đồng
-- ============================================================
CREATE TABLE hop_dong (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_hop_dong     VARCHAR(30) NOT NULL UNIQUE,    -- HD-2024-0001
    don_hang_id     BIGINT,
    khach_hang_id   BIGINT NOT NULL,
    nv_lap_id       BIGINT NOT NULL,
    ngay_ky         DATE NOT NULL,
    ngay_hieu_luc   DATE NOT NULL,
    ngay_het_han    DATE,
    gia_tri         DECIMAL(18, 2) NOT NULL,
    tien_dat_coc    DECIMAL(18, 2) DEFAULT 0,
    dieu_khoan_tt   TEXT,                           -- điều khoản thanh toán
    noi_dung        TEXT,
    trang_thai      ENUM('NHAP', 'HIEU_LUC', 'HET_HAN', 'HUY', 'HOAN_THANH') NOT NULL DEFAULT 'NHAP',
    file_url        VARCHAR(500),                   -- file hợp đồng scan
    ngay_tao        DATETIME DEFAULT CURRENT_TIMESTAMP,
    ngay_cap_nhat   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_hopdong_donhang   FOREIGN KEY (don_hang_id)  REFERENCES don_hang(id),
    CONSTRAINT fk_hopdong_khachhang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id),
    CONSTRAINT fk_hopdong_nvlap     FOREIGN KEY (nv_lap_id)     REFERENCES nguoi_dung(id)
);

-- ============================================================
-- 12. GIAO_NHAN - Phiếu giao nhận hàng hóa
--     Dùng cho: Giao nhận & bàn giao
-- ============================================================
CREATE TABLE giao_nhan (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_giao_nhan    VARCHAR(30) NOT NULL UNIQUE,    -- GN-2024-0001
    don_hang_id     BIGINT NOT NULL,
    nv_giao_id      BIGINT,                         -- nhân viên giao hàng
    ngay_giao_du_kien DATETIME,
    ngay_giao_thuc  DATETIME,
    dia_chi_giao    TEXT,
    lo_trinh        TEXT,                           -- mô tả lộ trình
    trang_thai      ENUM('CHO_GIAO', 'DANG_GIAO', 'DA_GIAO', 'THAT_BAI') NOT NULL DEFAULT 'CHO_GIAO',
    ghi_chu_giao    TEXT,
    nguoi_nhan      VARCHAR(150),
    so_dien_thoai_nhan VARCHAR(20),
    da_ban_giao     TINYINT(1) DEFAULT 0,
    ngay_ban_giao   DATETIME,
    CONSTRAINT fk_giaonhan_donhang FOREIGN KEY (don_hang_id) REFERENCES don_hang(id),
    CONSTRAINT fk_giaonhan_nvgiao  FOREIGN KEY (nv_giao_id)  REFERENCES nguoi_dung(id)
);

-- ============================================================
-- 13. DOI_TRA_HANG - Yêu cầu đổi/trả hàng
--     Dùng cho: Tab đổi trả trong Quản lý đơn hàng
-- ============================================================
CREATE TABLE doi_tra_hang (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_doi_tra      VARCHAR(30) NOT NULL UNIQUE,    -- DTH-2024-0001
    don_hang_id     BIGINT NOT NULL,
    khach_hang_id   BIGINT NOT NULL,
    loai            ENUM('DOI', 'TRA') NOT NULL,
    ly_do           TEXT NOT NULL,
    so_luong        DECIMAL(18, 3) NOT NULL,
    hang_hoa_id     BIGINT NOT NULL,
    ngay_yeu_cau    DATETIME DEFAULT CURRENT_TIMESTAMP,
    trang_thai      ENUM('CHO_DUYET', 'DA_DUYET', 'TU_CHOI', 'HOAN_THANH') NOT NULL DEFAULT 'CHO_DUYET',
    nguoi_xu_ly_id  BIGINT,
    ghi_chu_xu_ly   TEXT,
    ngay_xu_ly      DATETIME,
    CONSTRAINT fk_doitra_donhang   FOREIGN KEY (don_hang_id)    REFERENCES don_hang(id),
    CONSTRAINT fk_doitra_khachhang FOREIGN KEY (khach_hang_id)  REFERENCES khach_hang(id),
    CONSTRAINT fk_doitra_hanghoa   FOREIGN KEY (hang_hoa_id)    REFERENCES hang_hoa(id),
    CONSTRAINT fk_doitra_nguoixuly FOREIGN KEY (nguoi_xu_ly_id) REFERENCES nguoi_dung(id)
);

-- ============================================================
-- 14. CONG_NO - Quản lý công nợ khách hàng
--     Dùng cho: Tài chính & công nợ
-- ============================================================
CREATE TABLE cong_no (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    khach_hang_id   BIGINT NOT NULL,
    don_hang_id     BIGINT,
    hop_dong_id     BIGINT,
    so_tien_no      DECIMAL(18, 2) NOT NULL,
    so_tien_da_tt   DECIMAL(18, 2) NOT NULL DEFAULT 0,
    so_tien_con_lai DECIMAL(18, 2) AS (so_tien_no - so_tien_da_tt) STORED,
    ngay_phat_sinh  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    han_thanh_toan  DATE,
    trang_thai      ENUM('CON_NO', 'DA_THANH_TOAN', 'QUA_HAN') NOT NULL DEFAULT 'CON_NO',
    ghi_chu         TEXT,
    CONSTRAINT fk_congno_khachhang FOREIGN KEY (khach_hang_id) REFERENCES khach_hang(id),
    CONSTRAINT fk_congno_donhang   FOREIGN KEY (don_hang_id)   REFERENCES don_hang(id),
    CONSTRAINT fk_congno_hopdong   FOREIGN KEY (hop_dong_id)   REFERENCES hop_dong(id)
);

-- ============================================================
-- 15. THANH_TOAN - Lịch sử thanh toán
--     Dùng cho: Tài chính & công nợ
-- ============================================================
CREATE TABLE thanh_toan (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    ma_thanh_toan   VARCHAR(30) NOT NULL UNIQUE,    -- TT-2024-0001
    cong_no_id      BIGINT NOT NULL,
    nguoi_thu_id    BIGINT NOT NULL,
    so_tien         DECIMAL(18, 2) NOT NULL,
    hinh_thuc       ENUM('TIEN_MAT', 'CHUYEN_KHOAN', 'THE') NOT NULL,
    ngay_thanh_toan DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ma_giao_dich    VARCHAR(100),                   -- mã GD ngân hàng
    ghi_chu         TEXT,
    CONSTRAINT fk_thanhtoan_congno   FOREIGN KEY (cong_no_id)  REFERENCES cong_no(id),
    CONSTRAINT fk_thanhtoan_nguoithu FOREIGN KEY (nguoi_thu_id) REFERENCES nguoi_dung(id)
);

-- ============================================================
-- 16. BAO_CAO - Báo cáo thống kê
--     Dùng cho: Màn Báo cáo, Dashboard
-- ============================================================
CREATE TABLE bao_cao (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    loai            ENUM('TAI_CHINH', 'TON_KHO', 'THONG_KE') NOT NULL,
    tieu_de         VARCHAR(200) NOT NULL,
    tu_ngay         DATE NOT NULL,
    den_ngay        DATE NOT NULL,
    nguoi_lap_id    BIGINT NOT NULL,
    ngay_lap        DATETIME DEFAULT CURRENT_TIMESTAMP,
    noi_dung_json   JSON,                           -- dữ liệu báo cáo dạng JSON
    trang_thai      ENUM('NHAP', 'CHO_DUYET', 'DA_DUYET', 'TU_CHOI') NOT NULL DEFAULT 'NHAP',
    nguoi_duyet_id  BIGINT,
    ngay_duyet      DATETIME,
    ghi_chu         TEXT,
    CONSTRAINT fk_baocao_nguoilap   FOREIGN KEY (nguoi_lap_id)   REFERENCES nguoi_dung(id),
    CONSTRAINT fk_baocao_nguoiduyet FOREIGN KEY (nguoi_duyet_id) REFERENCES nguoi_dung(id)
);

-- ============================================================
-- DỮ LIỆU MẪU (SEED DATA)
-- ============================================================

-- Roles
INSERT INTO role (name) VALUES
    ('QUAN_TRI_VIEN'),
    ('BAN_QUAN_LY'),
    ('NV_KINH_DOANH'),
    ('NV_KHO'),
    ('NV_KE_TOAN'),
    ('NV_GIAO_NHAN'),
    ('KHACH_HANG'),
    ('NHA_CUNG_CAP');

-- Tài khoản mẫu (password: Admin@123 -> bcrypt)
INSERT INTO nguoi_dung (username, password_hash, ho_ten, email, so_dien_thoai, role_id, trang_thai) VALUES
    ('admin',       '$2a$10$examplehashAdmin',    'Quản trị viên',       'admin@vlxd.com',   '0901000001', 1, 'HOAT_DONG'),
    ('giamdoc',     '$2a$10$examplehashGD',       'Nguyễn Văn Giám Đốc', 'gd@vlxd.com',      '0901000002', 2, 'HOAT_DONG'),
    ('nvkd01',      '$2a$10$examplehashKD',       'Trần Thị Kinh Doanh', 'kd01@vlxd.com',    '0901000003', 3, 'HOAT_DONG'),
    ('nvkho01',     '$2a$10$examplehashKho',      'Lê Văn Kho',          'kho01@vlxd.com',   '0901000004', 4, 'HOAT_DONG'),
    ('nvkt01',      '$2a$10$examplehashKT',       'Phạm Thị Kế Toán',   'kt01@vlxd.com',    '0901000005', 5, 'HOAT_DONG'),
    ('khachhang01', '$2a$10$examplehashKH',       'Nguyễn Văn Khách',    'kh01@gmail.com',   '0901000006', 7, 'HOAT_DONG');

-- Khách hàng mẫu
INSERT INTO khach_hang (nguoi_dung_id, ma_khach_hang, ten_cong_ty, loai_khach, han_muc_no) VALUES
    (6, 'KH-0001', NULL, 'CA_NHAN', 50000000.00);

-- Danh mục mẫu
INSERT INTO danh_muc (ma_danh_muc, ten, mo_ta) VALUES
    ('DM-001', 'Thép xây dựng', 'Thép hình, thép hộp, thép cây'),
    ('DM-002', 'Tôn lợp',       'Tôn sóng, tôn phẳng, tôn màu'),
    ('DM-003', 'Xi măng',       'Xi măng các loại'),
    ('DM-004', 'Gạch & ngói',   'Gạch nung, gạch không nung, ngói');

-- Kho mẫu
INSERT INTO kho (ma_kho, ten_kho, dia_chi, trang_thai) VALUES
    ('KHO-001', 'Kho chính Tân Bình', '42A Cống Lỡ, P.15, Q.Tân Bình, TP.HCM', 'HOAT_DONG'),
    ('KHO-002', 'Kho phụ Bình Dương', 'KCN Sóng Thần, Bình Dương',              'HOAT_DONG');

-- Hàng hóa mẫu
INSERT INTO hang_hoa (ma_hang, ten_hang, danh_muc_id, don_vi_tinh, quy_cach, gia_ban_le, gia_ban_si, trang_thai) VALUES
    ('HH-001', 'Thép cây D10',      1, 'cây',  'D10, dài 11.7m', 185000.00,  178000.00, 'KINH_DOANH'),
    ('HH-002', 'Thép hộp 40x40x2', 1, 'cây',  '40x40x2mm, dài 6m', 250000.00, 240000.00, 'KINH_DOANH'),
    ('HH-003', 'Tôn sóng 0.4mm',   2, 'tấm',  '0.4mm, 900x2440', 125000.00,  118000.00, 'KINH_DOANH'),
    ('HH-004', 'Xi măng Hà Tiên',  3, 'bao',  'Bao 50kg',          95000.00,   90000.00, 'KINH_DOANH'),
    ('HH-005', 'Gạch nung 6 lỗ',   4, 'viên', '220x105x60mm',       2500.00,    2200.00, 'KINH_DOANH');

-- Tồn kho mẫu
INSERT INTO ton_kho (hang_hoa_id, kho_id, so_luong) VALUES
    (1, 1, 500), (2, 1, 200), (3, 1, 1000),
    (4, 1, 300), (5, 1, 5000),
    (1, 2, 800), (3, 2, 600);

-- ============================================================
-- INDEX để tăng tốc query phổ biến
-- ============================================================
CREATE INDEX idx_nguoidung_role       ON nguoi_dung (role_id);
CREATE INDEX idx_donhang_khachhang    ON don_hang   (khach_hang_id);
CREATE INDEX idx_donhang_trangthai    ON don_hang   (trang_thai);
CREATE INDEX idx_donhang_ngaydat      ON don_hang   (ngay_dat);
CREATE INDEX idx_hopdong_khachhang    ON hop_dong   (khach_hang_id);
CREATE INDEX idx_hopdong_tranghai     ON hop_dong   (trang_thai);
CREATE INDEX idx_congno_khachhang     ON cong_no    (khach_hang_id);
CREATE INDEX idx_congno_tranghai      ON cong_no    (trang_thai);
CREATE INDEX idx_tonkho_hanghoa       ON ton_kho    (hang_hoa_id);
CREATE INDEX idx_phieukho_loai        ON phieu_kho  (loai_phieu, ngay_lap);
CREATE INDEX idx_giaonhan_donhang     ON giao_nhan  (don_hang_id);
CREATE INDEX idx_baocao_loai_ngay     ON bao_cao    (loai, tu_ngay, den_ngay);