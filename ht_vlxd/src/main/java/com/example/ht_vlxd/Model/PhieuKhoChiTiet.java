package com.example.ht_vlxd.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "phieu_kho_chi_tiet")
public class PhieuKhoChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "phieu_kho_id", nullable = false)
    private PhieuKho phieuKho;

    @ManyToOne
    @JoinColumn(name = "hang_hoa_id", nullable = false)
    private HangHoa hangHoa;

    @Column(name = "so_luong", nullable = false, precision = 18, scale = 3)
    private BigDecimal soLuong;

    @Column(name = "don_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    public PhieuKhoChiTiet() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PhieuKho getPhieuKho() { return phieuKho; }
    public void setPhieuKho(PhieuKho phieuKho) { this.phieuKho = phieuKho; }
    public HangHoa getHangHoa() { return hangHoa; }
    public void setHangHoa(HangHoa hangHoa) { this.hangHoa = hangHoa; }
    public BigDecimal getSoLuong() { return soLuong; }
    public void setSoLuong(BigDecimal soLuong) { this.soLuong = soLuong; }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
