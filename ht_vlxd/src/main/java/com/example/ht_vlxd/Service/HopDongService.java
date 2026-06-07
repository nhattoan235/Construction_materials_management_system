package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.HopDong;
import com.example.ht_vlxd.Repository.HopDongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HopDongService {
    private final HopDongRepository hopDongRepository;

    public HopDongService(HopDongRepository hopDongRepository) {
        this.hopDongRepository = hopDongRepository;
    }

    public List<HopDong> getAllContracts() {
        return hopDongRepository.findAll();
    }

    public HopDong findByMaHopDong(String maHopDong) {
        return hopDongRepository.findByMaHopDong(maHopDong);
    }

    public HopDong findByDonHangId(Long donHangId) {
        return hopDongRepository.findByDonHangId(donHangId);
    }

    public HopDong save(HopDong contract) {
        return hopDongRepository.save(contract);
    }

    public void deleteContract(Long id) {
        hopDongRepository.deleteById(id);
    }
}
