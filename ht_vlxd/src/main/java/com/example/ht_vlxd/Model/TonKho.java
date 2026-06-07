package com.example.ht_vlxd.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ton_kho", uniqueConstraints = {@UniqueConstraint(columnNames = {"hang_hoa_id", "kho_id"})})
public class TonKho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hang_hoa_id", nullable = false)
    private HangHoa hangHoa;

    @ManyToOne
    @JoinColumn(name = "kho_id", nullable = false)
    private Kho kho;

    @Column(name = "so_luong", nullable = false)
    private BigDecimal soLuong = BigDecimal.ZERO;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat = LocalDateTime.now();

    public TonKho() {}

    public TonKho(Long id, HangHoa hangHoa, Kho kho, BigDecimal soLuong) {
        this.id = id;
        this.hangHoa = hangHoa;
        this.kho = kho;
        this.soLuong = soLuong;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public HangHoa getHangHoa() { return hangHoa; }
    public void setHangHoa(HangHoa hangHoa) { this.hangHoa = hangHoa; }
    public Kho getKho() { return kho; }
    public void setKho(Kho kho) { this.kho = kho; }
    public BigDecimal getSoLuong() { return soLuong; }
    public void setSoLuong(BigDecimal soLuong) { this.soLuong = soLuong; }
}
