package com.example.ht_vlxd.Model.sales;
import com.example.ht_vlxd.Model.customer.KhachHang;
import com.example.ht_vlxd.Model.auth.NguoiDung;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "don_hang")
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_don_hang", nullable = false, unique = true, length = 30)
    private String maDonHang;

    @ManyToOne
    @JoinColumn(name = "khach_hang_id", nullable = true)
    private KhachHang khachHang;

    @Column(name = "ten_khach_vang_lai", length = 150)
    private String tenKhachVangLai;

    @Column(name = "sdt_khach_vang_lai", length = 20)
    private String sdtKhachVangLai;

    @ManyToOne
    @JoinColumn(name = "nv_kinh_doanh_id")
    private NguoiDung nvKinhDoanh;

    @Column(name = "ngay_dat", nullable = false)
    private LocalDateTime ngayDat = LocalDateTime.now();

    @Column(name = "ngay_giao_du_kien")
    private LocalDateTime ngayGiaoDuKien;

    @Column(name = "dia_chi_giao", columnDefinition = "TEXT")
    private String diaChiGiao;

    @Column(name = "tong_tien", nullable = false)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(name = "tien_dat_coc")
    private BigDecimal tienDatCoc = BigDecimal.ZERO;

    @Column(name = "trang_thai", nullable = false)
    private String trangThai = "CHO_XAC_NHAN";

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat = LocalDateTime.now();

    public DonHang() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public String getTenKhachVangLai() { return tenKhachVangLai; }
    public void setTenKhachVangLai(String tenKhachVangLai) { this.tenKhachVangLai = tenKhachVangLai; }
    public String getSdtKhachVangLai() { return sdtKhachVangLai; }
    public void setSdtKhachVangLai(String sdtKhachVangLai) { this.sdtKhachVangLai = sdtKhachVangLai; }
    public NguoiDung getNvKinhDoanh() { return nvKinhDoanh; }
    public void setNvKinhDoanh(NguoiDung nvKinhDoanh) { this.nvKinhDoanh = nvKinhDoanh; }
    public LocalDateTime getNgayDat() { return ngayDat; }
    public void setNgayDat(LocalDateTime ngayDat) { this.ngayDat = ngayDat; }
    public String getDiaChiGiao() { return diaChiGiao; }
    public void setDiaChiGiao(String diaChiGiao) { this.diaChiGiao = diaChiGiao; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public BigDecimal getTienDatCoc() { return tienDatCoc; }
    public void setTienDatCoc(BigDecimal tienDatCoc) { this.tienDatCoc = tienDatCoc; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public LocalDateTime getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(LocalDateTime ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
}
