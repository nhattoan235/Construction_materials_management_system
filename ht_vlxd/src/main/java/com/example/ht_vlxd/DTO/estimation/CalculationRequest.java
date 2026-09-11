package com.example.ht_vlxd.DTO.estimation;

import java.util.Map;

public class CalculationRequest {
    private Double dienTich;
    private String loaiCongTrinh;
    private Integer soTang;
    private Map<String, Long> sanPhamLinhHoat;
    private Long khachHangId;

    public CalculationRequest() {}

    public Double getDienTich() { return dienTich; }
    public void setDienTich(Double dienTich) { this.dienTich = dienTich; }
    public String getLoaiCongTrinh() { return loaiCongTrinh; }
    public void setLoaiCongTrinh(String loaiCongTrinh) { this.loaiCongTrinh = loaiCongTrinh; }
    public Integer getSoTang() { return soTang; }
    public void setSoTang(Integer soTang) { this.soTang = soTang; }
    public Map<String, Long> getSanPhamLinhHoat() { return sanPhamLinhHoat; }
    public void setSanPhamLinhHoat(Map<String, Long> sanPhamLinhHoat) { this.sanPhamLinhHoat = sanPhamLinhHoat; }
    public Long getKhachHangId() { return khachHangId; }
    public void setKhachHangId(Long khachHangId) { this.khachHangId = khachHangId; }
}
