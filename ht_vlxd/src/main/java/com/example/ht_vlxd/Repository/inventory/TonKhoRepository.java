package com.example.ht_vlxd.Repository.inventory;

import com.example.ht_vlxd.Model.inventory.TonKho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TonKhoRepository extends JpaRepository<TonKho, Long> {
}
