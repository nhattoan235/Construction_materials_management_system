package com.example.ht_vlxd.Repository;

import com.example.ht_vlxd.Model.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanhToanRepository extends JpaRepository<ThanhToan, Long> {
    ThanhToan findByMaThanhToan(String maThanhToan);
    List<ThanhToan> findByCongNoId(Long congNoId);
}
