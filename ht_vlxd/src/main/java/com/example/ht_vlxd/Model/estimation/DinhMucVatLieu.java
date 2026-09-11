package com.example.ht_vlxd.Model.estimation;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "dinh_muc_vat_lieu", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"loai_cong_trinh", "loai_vat_lieu"})
})
public class DinhMucVatLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loai_cong_trinh", nullable = false, length = 100)
    private String loaiCongTrinh;

    @Column(name = "loai_vat_lieu", nullable = false, length = 50)
    private String loaiVatLieu;

    @Column(name = "he_so_m2", nullable = false, precision = 12, scale = 4)
    private BigDecimal heSoM2;

    @Column(name = "don_vi_tinh", nullable = false, length = 30)
    private String donViTinh;

    @Enumerated(EnumType.STRING)
    @Column(name = "cach_tinh", nullable = false)
    private CachTinh cachTinh = CachTinh.THEO_TANG;

    @Enumerated(EnumType.STRING)
    @Column(name = "kieu_lam_tron", nullable = false)
    private KieuLamTron kieuLamTron = KieuLamTron.DEM;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    public enum CachTinh {
        THEO_TANG, MOT_LAN
    }

    public enum KieuLamTron {
        DEM, THE_TICH
    }

    public DinhMucVatLieu() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLoaiCongTrinh() { return loaiCongTrinh; }
    public void setLoaiCongTrinh(String loaiCongTrinh) { this.loaiCongTrinh = loaiCongTrinh; }
    public String getLoaiVatLieu() { return loaiVatLieu; }
    public void setLoaiVatLieu(String loaiVatLieu) { this.loaiVatLieu = loaiVatLieu; }
    public BigDecimal getHeSoM2() { return heSoM2; }
    public void setHeSoM2(BigDecimal heSoM2) { this.heSoM2 = heSoM2; }
    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }
    public CachTinh getCachTinh() { return cachTinh; }
    public void setCachTinh(CachTinh cachTinh) { this.cachTinh = cachTinh; }
    public KieuLamTron getKieuLamTron() { return kieuLamTron; }
    public void setKieuLamTron(KieuLamTron kieuLamTron) { this.kieuLamTron = kieuLamTron; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
