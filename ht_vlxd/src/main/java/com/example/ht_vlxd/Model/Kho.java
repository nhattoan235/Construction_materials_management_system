package com.example.ht_vlxd.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "kho")
public class Kho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_kho", nullable = false, unique = true, length = 20)
    private String maKho;

    @Column(name = "ten_kho", nullable = false, length = 150)
    private String tenKho;

    @Column(name = "dia_chi", columnDefinition = "TEXT")
    private String diaChi;

    @Column(name = "dien_tich")
    private BigDecimal dienTich;

    @Column(name = "trang_thai", nullable = false)
    private String trangThai = "HOAT_DONG";

    public Kho() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaKho() { return maKho; }
    public void setMaKho(String maKho) { this.maKho = maKho; }
    public String getTenKho() { return tenKho; }
    public void setTenKho(String tenKho) { this.tenKho = tenKho; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public BigDecimal getDienTich() { return dienTich; }
    public void setDienTich(BigDecimal dienTich) { this.dienTich = dienTich; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}
