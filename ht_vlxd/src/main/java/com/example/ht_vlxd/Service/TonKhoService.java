package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.TonKho;
import com.example.ht_vlxd.Repository.TonKhoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TonKhoService {
    private final TonKhoRepository tonKhoRepository;

    public TonKhoService(TonKhoRepository tonKhoRepository) {
        this.tonKhoRepository = tonKhoRepository;
    }

    public List<TonKho> getAll() {
        return tonKhoRepository.findAll();
    }

    public TonKho save(TonKho tk) {
        return tonKhoRepository.save(tk);
    }
}
