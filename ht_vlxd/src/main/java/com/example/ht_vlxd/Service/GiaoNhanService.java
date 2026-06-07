package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.GiaoNhan;
import com.example.ht_vlxd.Repository.GiaoNhanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiaoNhanService {
    private final GiaoNhanRepository giaoNhanRepository;

    public GiaoNhanService(GiaoNhanRepository giaoNhanRepository) {
        this.giaoNhanRepository = giaoNhanRepository;
    }

    public List<GiaoNhan> getAll() {
        return giaoNhanRepository.findAll();
    }

    public GiaoNhan findByMaGiaoNhan(String maGiaoNhan) {
        return giaoNhanRepository.findByMaGiaoNhan(maGiaoNhan);
    }

    public List<GiaoNhan> getByNvGiao(Long nvGiaoId) {
        return giaoNhanRepository.findByNvGiaoId(nvGiaoId);
    }

    public GiaoNhan save(GiaoNhan gn) {
        return giaoNhanRepository.save(gn);
    }
}
