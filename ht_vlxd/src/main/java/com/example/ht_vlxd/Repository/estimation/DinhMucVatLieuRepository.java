package com.example.ht_vlxd.Repository.estimation;

import com.example.ht_vlxd.Model.estimation.DinhMucVatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DinhMucVatLieuRepository extends JpaRepository<DinhMucVatLieu, Long> {
    List<DinhMucVatLieu> findByLoaiCongTrinh(String loaiCongTrinh);
}
