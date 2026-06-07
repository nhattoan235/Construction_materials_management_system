package com.example.ht_vlxd.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cong_no")
public class CongNo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "don_hang_id")
    private DonHang donHang;

    @ManyToOne
    @JoinColumn(name = "hop_dong_id")
    private HopDong hopDong;

    @Column(name = "so_tien_no", nullable = false)
    private BigDecimal soTienNo;

    @Column(name = "so_tien_da_tt", nullable = false)
    private BigDecimal soTienDaTt = BigDecimal.ZERO;

    @Column(name = "ngay_phat_sinh", nullable = false)
    private LocalDateTime ngayPhatSinh = LocalDateTime.now();

    @Column(name = "han_thanh_toan")
    private LocalDate hanThanhToan;

    @Column(name = "trang_thai", nullable = false)
    private String trangThai = "CON_NO";

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    public CongNo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }
    public HopDong getHopDong() { return hopDong; }
    public void setHopDong(HopDong hopDong) { this.hopDong = hopDong; }
    public BigDecimal getSoTienNo() { return soTienNo; }
    public void setSoTienNo(BigDecimal soTienNo) { this.soTienNo = soTienNo; }
    public BigDecimal getSoTienDaTt() { return soTienDaTt; }
    public void setSoTienDaTt(BigDecimal soTienDaTt) { this.soTienDaTt = soTienDaTt; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public LocalDateTime getNgayPhatSinh() { return ngayPhatSinh; }
    public void setNgayPhatSinh(LocalDateTime ngayPhatSinh) { this.ngayPhatSinh = ngayPhatSinh; }
    public LocalDate getHanThanhToan() { return hanThanhToan; }
    public void setHanThanhToan(LocalDate hanThanhToan) { this.hanThanhToan = hanThanhToan; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
