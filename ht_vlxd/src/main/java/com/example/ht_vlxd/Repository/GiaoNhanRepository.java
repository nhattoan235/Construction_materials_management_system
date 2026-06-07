package com.example.ht_vlxd.Repository;

import com.example.ht_vlxd.Model.GiaoNhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiaoNhanRepository extends JpaRepository<GiaoNhan, Long> {
    GiaoNhan findByMaGiaoNhan(String maGiaoNhan);
    List<GiaoNhan> findByDonHangId(Long donHangId);
    List<GiaoNhan> findByNvGiaoId(Long nvGiaoId);
}
