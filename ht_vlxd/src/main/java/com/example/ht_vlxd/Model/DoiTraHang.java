package com.example.ht_vlxd.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "doi_tra_hang")
public class DoiTraHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_doi_tra", nullable = false, unique = true, length = 30)
    private String maDoiTra;

    @ManyToOne
    @JoinColumn(name = "don_hang_id", nullable = false)
    private DonHang donHang;

    @ManyToOne
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private KhachHang khachHang;

    @Column(name = "loai", nullable = false)
    private String loai; // DOI, TRA

    @Column(name = "ly_do", nullable = false, columnDefinition = "TEXT")
    private String lyDo;

    @Column(name = "so_luong", nullable = false, precision = 18, scale = 3)
    private BigDecimal soLuong;

    @ManyToOne
    @JoinColumn(name = "hang_hoa_id", nullable = false)
    private HangHoa hangHoa;

    @Column(name = "ngay_yeu_cau")
    private LocalDateTime ngayYeuCau = LocalDateTime.now();

    @Column(name = "trang_thai", nullable = false)
    private String trangThai = "CHO_DUYET"; // CHO_DUYET, DA_DUYET, TU_CHOI, HOAN_THANH

    @ManyToOne
    @JoinColumn(name = "nguoi_xu_ly_id")
    private NguoiDung nguoiXuLy;

    @Column(name = "ghi_chu_xu_ly", columnDefinition = "TEXT")
    private String ghiChuXuLy;

    @Column(name = "ngay_xu_ly")
    private LocalDateTime ngayXuLy;

    public DoiTraHang() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaDoiTra() { return maDoiTra; }
    public void setMaDoiTra(String maDoiTra) { this.maDoiTra = maDoiTra; }
    public DonHang getDonHang() { return donHang; }
    public void setDonHang(DonHang donHang) { this.donHang = donHang; }
    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }
    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public BigDecimal getSoLuong() { return soLuong; }
    public void setSoLuong(BigDecimal soLuong) { this.soLuong = soLuong; }
    public HangHoa getHangHoa() { return hangHoa; }
    public void setHangHoa(HangHoa hangHoa) { this.hangHoa = hangHoa; }
    public LocalDateTime getNgayYeuCau() { return ngayYeuCau; }
    public void setNgayYeuCau(LocalDateTime ngayYeuCau) { this.ngayYeuCau = ngayYeuCau; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public NguoiDung getNguoiXuLy() { return nguoiXuLy; }
    public void setNguoiXuLy(NguoiDung nguoiXuLy) { this.nguoiXuLy = nguoiXuLy; }
    public String getGhiChuXuLy() { return ghiChuXuLy; }
    public void setGhiChuXuLy(String ghiChuXuLy) { this.ghiChuXuLy = ghiChuXuLy; }
    public LocalDateTime getNgayXuLy() { return ngayXuLy; }
    public void setNgayXuLy(LocalDateTime ngayXuLy) { this.ngayXuLy = ngayXuLy; }
}
