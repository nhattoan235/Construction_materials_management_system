package com.example.ht_vlxd.Repository.product;

import com.example.ht_vlxd.Model.product.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {
    DanhMuc findByMaDanhMuc(String maDanhMuc);
}
