package com.example.ht_vlxd.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hang_hoa")
public class HangHoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_hang", nullable = false, unique = true, length = 30)
    private String maHang;

    @Column(name = "ten_hang", nullable = false, length = 200)
    private String tenHang;

    @ManyToOne
    @JoinColumn(name = "danh_muc_id")
    private DanhMuc danhMuc;

    @Column(name = "don_vi_tinh", nullable = false, length = 30)
    private String donViTinh;

    @Column(name = "quy_cach", length = 200)
    private String quyCach;

    @Column(name = "gia_ban_le", nullable = false)
    private BigDecimal giaBanLe = BigDecimal.ZERO;

    @Column(name = "gia_ban_si")
    private BigDecimal giaBanSi;

    @Column(name = "trong_luong_kg")
    private BigDecimal trongLuongKg;

    @Column(name = "anh_url", length = 500)
    private String anhUrl;

    @Column(name = "trang_thai", nullable = false)
    private String trangThai = "KINH_DOANH";

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    public HangHoa() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaHang() { return maHang; }
    public void setMaHang(String maHang) { this.maHang = maHang; }
    public String getTenHang() { return tenHang; }
    public void setTenHang(String tenHang) { this.tenHang = tenHang; }
    public DanhMuc getDanhMuc() { return danhMuc; }
    public void setDanhMuc(DanhMuc danhMuc) { this.danhMuc = danhMuc; }
    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }
    public String getQuyCach() { return quyCach; }
    public void setQuyCach(String quyCach) { this.quyCach = quyCach; }
    public BigDecimal getGiaBanLe() { return giaBanLe; }
    public void setGiaBanLe(BigDecimal giaBanLe) { this.giaBanLe = giaBanLe; }
    public BigDecimal getGiaBanSi() { return giaBanSi; }
    public void setGiaBanSi(BigDecimal giaBanSi) { this.giaBanSi = giaBanSi; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getAnhUrl() { return anhUrl; }
    public void setAnhUrl(String anhUrl) { this.anhUrl = anhUrl; }
}
