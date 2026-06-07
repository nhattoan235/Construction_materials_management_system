package com.example.ht_vlxd.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "giao_nhan")
public class GiaoNhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_giao_nhan", nullable = false, unique = true, length = 30)
    private String maGiaoNhan;

    @ManyToOne
    @JoinColumn(name = "don_hang_id", nullable = false)
    private DonHang donHang;

    @ManyToOne
    @JoinColumn(name = "nv_giao_id")
    private NguoiDung nvGiao;

    @Column(name = "ngay_giao_du_kien")
    private LocalDateTime ngayGiaoDuKien;

    @Column(name = "ngay_giao_thuc")
    private LocalDateTime ngayGiaoThuc;

    @Column(name = "dia_chi_giao", columnDefinition = "TEXT")
    private String diaChiGiao;

    @Column(name = "lo_trinh", columnDefinition = "TEXT")
    private String loTrinh;

    @Column(name = "trang_thai", nullable = false)
    private String trangThai = "CHO_GIAO"; // CHO_GIAO, DANG_GIAO, DA_GIAO, THAT_BAI

    @Column(name = "ghi_chu_giao", columnDefinition = "TEXT")
    private String ghiChuGiao;

    @Column(name = "nguoi_nhan", length = 150)
    private String nguoiNhan;

    @Column(name = "so_dien_thoai_nhan", length = 20)
    private String soDienThoaiNhan;

    @Column(name = "da_ban_giao")
    private Boolean daBanGiao = false;

    @Column(name = "ngay_ban_giao")
    private LocalDateTime ngayBanGiao;

    public GiaoNhan() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaGiaoNhan() { return maGiaoNhan; }
    public void setMaGiaoNhan(String maGiaoNhan) { this.maGiaoNhan = maGiaoNhan; }
    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }
    public NguoiDung getNvGiao() { return nvGiao; }
    public void setNvGiao(NguoiDung nvGiao) { this.nvGiao = nvGiao; }
    public LocalDateTime getNgayGiaoDuKien() { return ngayGiaoDuKien; }
    public void setNgayGiaoDuKien(LocalDateTime ngayGiaoDuKien) { this.ngayGiaoDuKien = ngayGiaoDuKien; }
    public LocalDateTime getNgayGiaoThuc() { return ngayGiaoThuc; }
    public void setNgayGiaoThuc(LocalDateTime ngayGiaoThuc) { this.ngayGiaoThuc = ngayGiaoThuc; }
    public String getDiaChiGiao() { return diaChiGiao; }
    public void setDiaChiGiao(String diaChiGiao) { this.diaChiGiao = diaChiGiao; }
    public String getLoTrinh() { return loTrinh; }
    public void setLoTrinh(String loTrinh) { this.loTrinh = loTrinh; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getGhiChuGiao() { return ghiChuGiao; }
    public void setGhiChuGiao(String ghiChuGiao) { this.ghiChuGiao = ghiChuGiao; }
    public String getNguoiNhan() { return nguoiNhan; }
    public void setNguoiNhan(String nguoiNhan) { this.nguoiNhan = nguoiNhan; }
    public String getSoDienThoaiNhan() { return soDienThoaiNhan; }
    public void setSoDienThoaiNhan(String soDienThoaiNhan) { this.soDienThoaiNhan = soDienThoaiNhan; }
    public Boolean getDaBanGiao() { return daBanGiao; }
    public void setDaBanGiao(Boolean daBanGiao) { this.daBanGiao = daBanGiao; }
    public LocalDateTime getNgayBanGiao() { return ngayBanGiao; }
    public void setNgayBanGiao(LocalDateTime ngayBanGiao) { this.ngayBanGiao = ngayBanGiao; }
}
