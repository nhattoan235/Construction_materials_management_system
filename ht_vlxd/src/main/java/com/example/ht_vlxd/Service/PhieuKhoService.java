package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.PhieuKho;
import com.example.ht_vlxd.Model.PhieuKhoChiTiet;
import com.example.ht_vlxd.Repository.PhieuKhoChiTietRepository;
import com.example.ht_vlxd.Repository.PhieuKhoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhieuKhoService {
    private final PhieuKhoRepository phieuKhoRepository;
    private final PhieuKhoChiTietRepository phieuKhoChiTietRepository;

    public PhieuKhoService(PhieuKhoRepository phieuKhoRepository, PhieuKhoChiTietRepository phieuKhoChiTietRepository) {
        this.phieuKhoRepository = phieuKhoRepository;
        this.phieuKhoChiTietRepository = phieuKhoChiTietRepository;
    }

    public List<PhieuKho> getAll() {
        return phieuKhoRepository.findAll();
    }

    public List<PhieuKho> getByLoai(String loaiPhieu) {
        return phieuKhoRepository.findByLoaiPhieu(loaiPhieu);
    }

    public PhieuKho findByMaPhieu(String maPhieu) {
        return phieuKhoRepository.findByMaPhieu(maPhieu);
    }

    public PhieuKho save(PhieuKho pk) {
        return phieuKhoRepository.save(pk);
    }

    public PhieuKhoChiTiet saveChiTiet(PhieuKhoChiTiet pkct) {
        return phieuKhoChiTietRepository.save(pkct);
    }

    public List<PhieuKhoChiTiet> getChiTietByPhieuKhoId(Long phieuKhoId) {
        return phieuKhoChiTietRepository.findByPhieuKhoId(phieuKhoId);
    }
}
