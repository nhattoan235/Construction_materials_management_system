package com.example.ht_vlxd.Service.product;

import com.example.ht_vlxd.Model.product.DanhMuc;
import com.example.ht_vlxd.Repository.product.DanhMucRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DanhMucService {
    private final DanhMucRepository danhMucRepository;

    public DanhMucService(DanhMucRepository danhMucRepository) {
        this.danhMucRepository = danhMucRepository;
    }

    public List<DanhMuc> getAll() {
        return danhMucRepository.findAll();
    }

    public DanhMuc findByMaDanhMuc(String maDanhMuc) {
        return danhMucRepository.findByMaDanhMuc(maDanhMuc);
    }

    public DanhMuc save(DanhMuc dm) {
        return danhMucRepository.save(dm);
    }

    public void delete(Long id) {
        danhMucRepository.deleteById(id);
    }
}
