package com.example.ht_vlxd.Repository.product;

import com.example.ht_vlxd.Model.product.HangHoa;
import com.example.ht_vlxd.Model.product.TrangThaiHangHoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HangHoaRepository extends JpaRepository<HangHoa, Long> {
    HangHoa findByMaHang(String maHang);
    List<HangHoa> findByDanhMucMaDanhMucAndTrangThai(String maDanhMuc, TrangThaiHangHoa trangThai);
}
