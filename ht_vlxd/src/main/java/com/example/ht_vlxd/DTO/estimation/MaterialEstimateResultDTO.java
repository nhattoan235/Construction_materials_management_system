package com.example.ht_vlxd.DTO.estimation;

import java.math.BigDecimal;

public class MaterialEstimateResultDTO {
    private String loaiVatLieu;
    private String tenLoaiVatLieu;
    private Double khoiLuongUocTinh;
    private String donViTinh;
    private Long hangHoaId;
    private String tenHangHoa;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private String canhBao;
    private String ghiChuDinhMuc;

    public MaterialEstimateResultDTO() {}

    public String getLoaiVatLieu() { return loaiVatLieu; }
    public void setLoaiVatLieu(String loaiVatLieu) { this.loaiVatLieu = loaiVatLieu; }
    public String getTenLoaiVatLieu() { return tenLoaiVatLieu; }
    public void setTenLoaiVatLieu(String tenLoaiVatLieu) { this.tenLoaiVatLieu = tenLoaiVatLieu; }
    public Double getKhoiLuongUocTinh() { return khoiLuongUocTinh; }
    public void setKhoiLuongUocTinh(Double khoiLuongUocTinh) { this.khoiLuongUocTinh = khoiLuongUocTinh; }
    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }
    public Long getHangHoaId() { return hangHoaId; }
    public void setHangHoaId(Long hangHoaId) { this.hangHoaId = hangHoaId; }
    public String getTenHangHoa() { return tenHangHoa; }
    public void setTenHangHoa(String tenHangHoa) { this.tenHangHoa = tenHangHoa; }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }
    public String getCanhBao() { return canhBao; }
    public void setCanhBao(String canhBao) { this.canhBao = canhBao; }
    public String getGhiChuDinhMuc() { return ghiChuDinhMuc; }
    public void setGhiChuDinhMuc(String ghiChuDinhMuc) { this.ghiChuDinhMuc = ghiChuDinhMuc; }
}
