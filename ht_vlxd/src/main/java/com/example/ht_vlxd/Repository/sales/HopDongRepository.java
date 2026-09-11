package com.example.ht_vlxd.Repository.sales;

import com.example.ht_vlxd.Model.sales.HopDong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HopDongRepository extends JpaRepository<HopDong, Long> {
    HopDong findByMaHopDong(String maHopDong);
    HopDong findByDonHangId(Long donHangId);
}
