package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.BaoCao;
import com.example.ht_vlxd.Repository.BaoCaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaoCaoService {
    private final BaoCaoRepository baoCaoRepository;

    public BaoCaoService(BaoCaoRepository baoCaoRepository) {
        this.baoCaoRepository = baoCaoRepository;
    }

    public List<BaoCao> getAll() {
        return baoCaoRepository.findAll();
    }

    public List<BaoCao> getByLoai(String loai) {
        return baoCaoRepository.findByLoai(loai);
    }

    public BaoCao save(BaoCao bc) {
        return baoCaoRepository.save(bc);
    }
}
