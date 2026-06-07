package com.example.ht_vlxd.Repository;

import com.example.ht_vlxd.Model.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {
    DanhMuc findByMaDanhMuc(String maDanhMuc);
}
