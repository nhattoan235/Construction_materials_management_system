package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.Kho;
import com.example.ht_vlxd.Repository.KhoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhoService {
    private final KhoRepository khoRepository;

    public KhoService(KhoRepository khoRepository) {
        this.khoRepository = khoRepository;
    }

    public List<Kho> getAll() {
        return khoRepository.findAll();
    }

    public Kho findByMaKho(String maKho) {
        return khoRepository.findByMaKho(maKho);
    }

    public Kho save(Kho kho) {
        return khoRepository.save(kho);
    }
}
