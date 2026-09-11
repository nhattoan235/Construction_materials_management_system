package com.example.ht_vlxd.Repository.management;

import com.example.ht_vlxd.Model.management.BaoCao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaoCaoRepository extends JpaRepository<BaoCao, Long> {
    List<BaoCao> findByLoai(String loai);
}
