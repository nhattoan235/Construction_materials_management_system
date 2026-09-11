-- Migration Script for Material Estimation Calculator

-- 1. Create table dinh_muc_vat_lieu
CREATE TABLE IF NOT EXISTS dinh_muc_vat_lieu (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    loai_cong_trinh     VARCHAR(100) NOT NULL,
    loai_vat_lieu       VARCHAR(50) NOT NULL,
    he_so_m2            DECIMAL(12, 4) NOT NULL,
    don_vi_tinh         VARCHAR(30) NOT NULL,
    cach_tinh           ENUM('THEO_TANG', 'MOT_LAN') NOT NULL DEFAULT 'THEO_TANG',
    kieu_lam_tron       ENUM('DEM', 'THE_TICH') NOT NULL DEFAULT 'DEM',
    ghi_chu             TEXT,
    UNIQUE KEY uq_loai_ct_vl (loai_cong_trinh, loai_vat_lieu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Modify don_hang table
ALTER TABLE don_hang MODIFY COLUMN khach_hang_id BIGINT NULL;
ALTER TABLE don_hang ADD COLUMN IF NOT EXISTS ten_khach_vang_lai VARCHAR(150) NULL;
ALTER TABLE don_hang ADD COLUMN IF NOT EXISTS sdt_khach_vang_lai VARCHAR(20) NULL;

-- 3. Add CHECK constraint (for MySQL 8.0.16+)
-- Note: MySQL does not support "DROP CONSTRAINT IF EXISTS". If you need to drop it, use standard DROP CONSTRAINT, but it will error if it does not exist.
-- ALTER TABLE don_hang DROP CONSTRAINT chk_khachhang_or_vanglai;
ALTER TABLE don_hang ADD CONSTRAINT chk_khachhang_or_vanglai CHECK (
    (khach_hang_id IS NOT NULL) OR 
    (ten_khach_vang_lai IS NOT NULL AND sdt_khach_vang_lai IS NOT NULL)
);

-- 4. Seed default estimation coefficients
INSERT INTO dinh_muc_vat_lieu (loai_cong_trinh, loai_vat_lieu, he_so_m2, don_vi_tinh, cach_tinh, kieu_lam_tron, ghi_chu) VALUES
    ('NHA_CAP_4', 'XI_MANG', 1.2500, 'bao', 'MOT_LAN', 'DEM', 'Xi măng đổ móng và sàn trệt'),
    ('NHA_CAP_4', 'CAT', 0.0800, 'm3', 'MOT_LAN', 'THE_TICH', 'Cát xây tô'),
    ('NHA_CAP_4', 'DA', 0.0600, 'm3', 'MOT_LAN', 'THE_TICH', 'Đá dăm đổ móng'),
    ('NHA_CAP_4', 'THEP', 15.0000, 'kg', 'MOT_LAN', 'DEM', 'Thép xây dựng cho nhà cấp 4'),
    ('NHA_CAP_4', 'GACH', 68.0000, 'viên', 'MOT_LAN', 'DEM', 'Gạch xây tường bao'),
    
    ('NHA_PHO_BTCT', 'XI_MANG', 1.5000, 'bao', 'THEO_TANG', 'DEM', 'Xi măng xây tô dầm cột theo tầng'),
    ('NHA_PHO_BTCT', 'CAT', 0.0900, 'm3', 'THEO_TANG', 'THE_TICH', 'Cát xây tô theo tầng'),
    ('NHA_PHO_BTCT', 'DA', 0.0500, 'm3', 'MOT_LAN', 'THE_TICH', 'Đá dăm đổ móng cốt nền (chỉ tính 1 lần)'),
    ('NHA_PHO_BTCT', 'THEP', 25.0000, 'kg', 'THEO_TANG', 'DEM', 'Thép dầm sàn cột theo tầng'),
    ('NHA_PHO_BTCT', 'GACH', 75.0000, 'viên', 'THEO_TANG', 'DEM', 'Gạch xây tường ngăn và bao theo tầng'),
    
    ('BIET_THU', 'XI_MANG', 1.8000, 'bao', 'THEO_TANG', 'DEM', 'Xi măng biệt thự theo tầng'),
    ('BIET_THU', 'CAT', 0.1100, 'm3', 'THEO_TANG', 'THE_TICH', 'Cát xây biệt thự theo tầng'),
    ('BIET_THU', 'DA', 0.0800, 'm3', 'MOT_LAN', 'THE_TICH', 'Đá đổ móng (chỉ tính 1 lần)'),
    ('BIET_THU', 'THEP', 35.0000, 'kg', 'THEO_TANG', 'DEM', 'Thép chịu lực biệt thự'),
    ('BIET_THU', 'GACH', 90.0000, 'viên', 'THEO_TANG', 'DEM', 'Gạch xây tường biệt thự')
ON DUPLICATE KEY UPDATE 
    he_so_m2 = VALUES(he_so_m2),
    don_vi_tinh = VALUES(don_vi_tinh),
    cach_tinh = VALUES(cach_tinh),
    kieu_lam_tron = VALUES(kieu_lam_tron);
