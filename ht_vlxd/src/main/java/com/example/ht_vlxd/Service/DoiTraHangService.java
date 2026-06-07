package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.DoiTraHang;
import com.example.ht_vlxd.Repository.DoiTraHangRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoiTraHangService {
    private final DoiTraHangRepository doiTraHangRepository;

    public DoiTraHangService(DoiTraHangRepository doiTraHangRepository) {
        this.doiTraHangRepository = doiTraHangRepository;
    }

    public List<DoiTraHang> getAll() {
        return doiTraHangRepository.findAll();
    }

    public List<DoiTraHang> getByKhachHang(Long khachHangId) {
        return doiTraHangRepository.findByKhachHangId(khachHangId);
    }

    public DoiTraHang findByMaDoiTra(String maDoiTra) {
        return doiTraHangRepository.findByMaDoiTra(maDoiTra);
    }

    public DoiTraHang save(DoiTraHang dth) {
        return doiTraHangRepository.save(dth);
    }
}
