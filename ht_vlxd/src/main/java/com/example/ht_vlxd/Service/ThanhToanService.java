package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.ThanhToan;
import com.example.ht_vlxd.Repository.ThanhToanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThanhToanService {
    private final ThanhToanRepository thanhToanRepository;

    public ThanhToanService(ThanhToanRepository thanhToanRepository) {
        this.thanhToanRepository = thanhToanRepository;
    }

    public List<ThanhToan> getAll() {
        return thanhToanRepository.findAll();
    }

    public List<ThanhToan> getByCongNo(Long congNoId) {
        return thanhToanRepository.findByCongNoId(congNoId);
    }

    public ThanhToan save(ThanhToan tt) {
        return thanhToanRepository.save(tt);
    }
}
