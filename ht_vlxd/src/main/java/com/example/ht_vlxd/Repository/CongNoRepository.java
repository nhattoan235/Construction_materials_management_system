package com.example.ht_vlxd.Repository;

import com.example.ht_vlxd.Model.CongNo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CongNoRepository extends JpaRepository<CongNo, Long> {
    List<CongNo> findByKhachHangId(Long khachHangId);
}
