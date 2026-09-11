package com.example.ht_vlxd.Repository.inventory;

import com.example.ht_vlxd.Model.inventory.Kho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhoRepository extends JpaRepository<Kho, Long> {
    Kho findByMaKho(String maKho);
}
