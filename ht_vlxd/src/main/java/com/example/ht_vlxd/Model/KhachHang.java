package com.example.ht_vlxd.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "khach_hang")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "nguoi_dung_id", nullable = false, unique = true)
    private NguoiDung nguoiDung;

    @Column(name = "ma_khach_hang", nullable = false, unique = true, length = 20)
    private String maKhachHang;

    @Column(name = "ten_cong_ty", length = 200)
    private String tenCongTy;

    @Column(name = "ma_so_thue", length = 20)
    private String maSoThue;

    @Column(name = "nguoi_dai_dien", length = 150)
    private String nguoiDaiDien;

    @Column(name = "loai_khach", nullable = false)
    private String loaiKhach = "CA_NHAN";

    @Column(name = "han_muc_no")
    private BigDecimal hanMucNo = BigDecimal.ZERO;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    public KhachHang() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public NguoiDung getNguoiDung() { return nguoiDung; }
    public void setNguoiDung(NguoiDung nguoiDung) { this.nguoiDung = nguoiDung; }
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String maKhachHang) { this.maKhachHang = maKhachHang; }
    public String getTenCongTy() { return tenCongTy; }
    public void setTenCongTy(String tenCongTy) { this.tenCongTy = tenCongTy; }
    public String getMaSoThue() { return maSoThue; }
    public void setMaSoThue(String maSoThue) { this.maSoThue = maSoThue; }
    public String getNguoiDaiDien() { return nguoiDaiDien; }
    public void setNguoiDaiDien(String nguoiDaiDien) { this.nguoiDaiDien = nguoiDaiDien; }
    public String getLoaiKhach() { return loaiKhach; }
    public void setLoaiKhach(String loaiKhach) { this.loaiKhach = loaiKhach; }
    public BigDecimal getHanMucNo() { return hanMucNo; }
    public void setHanMucNo(BigDecimal hanMucNo) { this.hanMucNo = hanMucNo; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
