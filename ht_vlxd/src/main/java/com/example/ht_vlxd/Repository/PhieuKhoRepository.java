package com.example.ht_vlxd.Repository;

import com.example.ht_vlxd.Model.PhieuKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuKhoRepository extends JpaRepository<PhieuKho, Long> {
    PhieuKho findByMaPhieu(String maPhieu);
    List<PhieuKho> findByLoaiPhieu(String loaiPhieu);
}
