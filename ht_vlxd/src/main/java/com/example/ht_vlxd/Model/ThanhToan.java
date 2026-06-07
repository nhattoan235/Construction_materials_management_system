package com.example.ht_vlxd.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "thanh_toan")
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_thanh_toan", nullable = false, unique = true, length = 30)
    private String maThanhToan;

    @ManyToOne
    @JoinColumn(name = "cong_no_id", nullable = false)
    private CongNo congNo;

    @ManyToOne
    @JoinColumn(name = "nguoi_thu_id", nullable = false)
    private NguoiDung nguoiThu;

    @Column(name = "so_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal soTien;

    @Column(name = "hinh_thuc", nullable = false)
    private String hinhThuc; // TIEN_MAT, CHUYEN_KHOAN, THE

    @Column(name = "ngay_thanh_toan", nullable = false)
    private LocalDateTime ngayThanhToan = LocalDateTime.now();

    @Column(name = "ma_giao_dich", length = 100)
    private String maGiaoDich;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    public ThanhToan() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaThanhToan() { return maThanhToan; }
    public void setMaThanhToan(String maThanhToan) { this.maThanhToan = maThanhToan; }
    public CongNo getCongNo() { return congNo; }
    public void setCongNo(CongNo congNo) { this.congNo = congNo; }
    public NguoiDung getNguoiThu() { return nguoiThu; }
    public void setNguoiThu(NguoiDung nguoiThu) { this.nguoiThu = nguoiThu; }
    public BigDecimal getSoTien() { return soTien; }
    public void setSoTien(BigDecimal soTien) { this.soTien = soTien; }
    public String getHinhThuc() { return hinhThuc; }
    public void setHinhThuc(String hinhThuc) { this.hinhThuc = hinhThuc; }
    public LocalDateTime getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(LocalDateTime ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }
    public String getMaGiaoDich() { return maGiaoDich; }
    public void setMaGiaoDich(String maGiaoDich) { this.maGiaoDich = maGiaoDich; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
