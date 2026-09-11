package com.example.ht_vlxd.Repository.sales;

import com.example.ht_vlxd.Model.sales.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {
    DonHang findByMaDonHang(String maDonHang);
    List<DonHang> findByKhachHangId(Long khachHangId);
}
