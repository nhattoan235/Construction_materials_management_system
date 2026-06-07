package com.example.ht_vlxd.Repository;

import com.example.ht_vlxd.Model.HangHoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HangHoaRepository extends JpaRepository<HangHoa, Long> {
    HangHoa findByMaHang(String maHang);
}
