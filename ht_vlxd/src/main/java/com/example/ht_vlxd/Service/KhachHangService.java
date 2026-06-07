package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.KhachHang;
import com.example.ht_vlxd.Repository.KhachHangRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhachHangService {
    private final KhachHangRepository khachHangRepository;

    public KhachHangService(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    public List<KhachHang> getAll() {
        return khachHangRepository.findAll();
    }

    public KhachHang findByMaKhachHang(String maKhachHang) {
        return khachHangRepository.findByMaKhachHang(maKhachHang);
    }

    public KhachHang save(KhachHang kh) {
        return khachHangRepository.save(kh);
    }

    public void delete(Long id) {
        khachHangRepository.deleteById(id);
    }
}
