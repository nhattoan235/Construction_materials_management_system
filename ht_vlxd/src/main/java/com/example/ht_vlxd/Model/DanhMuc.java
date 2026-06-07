package com.example.ht_vlxd.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "danh_muc")
public class DanhMuc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_danh_muc", nullable = false, unique = true, length = 20)
    private String maDanhMuc;

    @Column(name = "ten", nullable = false, length = 150)
    private String ten;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private DanhMuc parent;

    public DanhMuc() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaDanhMuc() { return maDanhMuc; }
    public void setMaDanhMuc(String maDanhMuc) { this.maDanhMuc = maDanhMuc; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public DanhMuc getParent() { return parent; }
    public void setParent(DanhMuc parent) { this.parent = parent; }
}
