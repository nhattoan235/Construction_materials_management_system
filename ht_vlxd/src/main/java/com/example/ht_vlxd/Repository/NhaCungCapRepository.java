package com.example.ht_vlxd.Repository;

import com.example.ht_vlxd.Model.NhaCungCap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, Long> {
    NhaCungCap findByMaNcc(String maNcc);
}
