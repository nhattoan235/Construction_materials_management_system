package com.example.ht_vlxd.Repository;

import com.example.ht_vlxd.Model.TonKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TonKhoRepository extends JpaRepository<TonKho, Long> {
    
}
