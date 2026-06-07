package com.example.ht_vlxd.Repository;

import com.example.ht_vlxd.Model.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {
    NguoiDung findByUsername(String username);
    NguoiDung findByEmail(String email);
    NguoiDung findBySoDienThoai(String soDienThoai);
    java.util.List<NguoiDung> findByHoTen(String hoTen);
    java.util.List<NguoiDung> findByRoleName(String roleName);
}
