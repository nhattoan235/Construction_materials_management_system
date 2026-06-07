package com.example.ht_vlxd.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bao_cao")
public class BaoCao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loai", nullable = false)
    private String loai; // TAI_CHINH, TON_KHO, THONG_KE

    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "tu_ngay", nullable = false)
    private LocalDate tuNgay;

    @Column(name = "den_ngay", nullable = false)
    private LocalDate denNgay;

    @ManyToOne
    @JoinColumn(name = "nguoi_lap_id", nullable = false)
    private NguoiDung nguoiLap;

    @Column(name = "ngay_lap")
    private LocalDateTime ngayLap = LocalDateTime.now();

    @Column(name = "noi_dung_json", columnDefinition = "TEXT")
    private String noiDungJson;

    @Column(name = "trang_thai", nullable = false)
    private String trangThai = "NHAP"; // NHAP, CHO_DUYET, DA_DUYET, TU_CHOI

    @ManyToOne
    @JoinColumn(name = "nguoi_duyet_id")
    private NguoiDung nguoiDuyet;

    @Column(name = "ngay_duyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String ghiChu;

    public BaoCao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }
    public LocalDate getTuNgay() { return tuNgay; }
    public void setTuNgay(LocalDate tuNgay) { this.tuNgay = tuNgay; }
    public LocalDate getDenNgay() { return denNgay; }
    public void setDenNgay(LocalDate denNgay) { this.denNgay = denNgay; }
    public NguoiDung getNguoiLap() { return nguoiLap; }
    public void setNguoiLap(NguoiDung nguoiLap) { this.nguoiLap = nguoiLap; }
    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }
    public String getNoiDungJson() { return noiDungJson; }
    public void setNoiDungJson(String noiDungJson) { this.noiDungJson = noiDungJson; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public NguoiDung getNguoiDuyet() { return nguoiDuyet; }
    public void setNguoiDuyet(NguoiDung nguoiDuyet) { this.nguoiDuyet = nguoiDuyet; }
    public LocalDateTime getNgayDuyet() { return ngayDuyet; }
    public void setNgayDuyet(LocalDateTime ngayDuyet) { this.ngayDuyet = ngayDuyet; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
}
