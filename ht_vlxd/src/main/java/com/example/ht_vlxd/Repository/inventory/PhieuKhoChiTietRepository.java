package com.example.ht_vlxd.Repository.inventory;

import com.example.ht_vlxd.Model.inventory.PhieuKhoChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhieuKhoChiTietRepository extends JpaRepository<PhieuKhoChiTiet, Long> {
    List<PhieuKhoChiTiet> findByPhieuKhoId(Long phieuKhoId);
}
