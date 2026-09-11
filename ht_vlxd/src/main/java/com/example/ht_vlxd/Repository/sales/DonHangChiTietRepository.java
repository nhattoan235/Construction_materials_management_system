package com.example.ht_vlxd.Repository.sales;

import com.example.ht_vlxd.Model.sales.DonHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonHangChiTietRepository extends JpaRepository<DonHangChiTiet, Long> {
    List<DonHangChiTiet> findByDonHangId(Long donHangId);
}
