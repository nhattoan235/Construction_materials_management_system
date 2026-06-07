package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.HangHoa;
import com.example.ht_vlxd.Repository.HangHoaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HangHoaService {
    private final HangHoaRepository hangHoaRepository;

    public HangHoaService(HangHoaRepository hangHoaRepository) {
        this.hangHoaRepository = hangHoaRepository;
    }

    public List<HangHoa> getAllProducts() {
        return hangHoaRepository.findAll();
    }

    public HangHoa findByMaHang(String maHang) {
        return hangHoaRepository.findByMaHang(maHang);
    }

    public HangHoa findById(Long id) {
        return hangHoaRepository.findById(id).orElse(null);
    }

    public HangHoa save(HangHoa product) {
        return hangHoaRepository.save(product);
    }

    public void deleteProduct(Long id) {
        hangHoaRepository.deleteById(id);
    }
}
