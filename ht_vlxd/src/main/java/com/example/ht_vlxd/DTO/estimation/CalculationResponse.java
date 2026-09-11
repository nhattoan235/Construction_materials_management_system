package com.example.ht_vlxd.DTO.estimation;

import java.math.BigDecimal;
import java.util.List;

public class CalculationResponse {
    private Boolean tinhDayDu;
    private Integer soDongChuaHoanTat;
    private BigDecimal tongTien;
    private List<MaterialEstimateResultDTO> chiTietVatLieu;

    public CalculationResponse() {}

    public Boolean getTinhDayDu() { return tinhDayDu; }
    public void setTinhDayDu(Boolean tinhDayDu) { this.tinhDayDu = tinhDayDu; }
    public Integer getSoDongChuaHoanTat() { return soDongChuaHoanTat; }
    public void setSoDongChuaHoanTat(Integer soDongChuaHoanTat) { this.soDongChuaHoanTat = soDongChuaHoanTat; }
    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }
    public List<MaterialEstimateResultDTO> getChiTietVatLieu() { return chiTietVatLieu; }
    public void setChiTietVatLieu(List<MaterialEstimateResultDTO> chiTietVatLieu) { this.chiTietVatLieu = chiTietVatLieu; }
}
