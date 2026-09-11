package com.example.ht_vlxd.Repository.customer;

import com.example.ht_vlxd.Model.customer.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Long> {
    KhachHang findByMaKhachHang(String maKhachHang);
    KhachHang findByNguoiDungUsername(String username);
}
