package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.NhaCungCap;
import com.example.ht_vlxd.Repository.NhaCungCapRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NhaCungCapService {
    private final NhaCungCapRepository nhaCungCapRepository;

    public NhaCungCapService(NhaCungCapRepository nhaCungCapRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
    }

    public List<NhaCungCap> getAll() {
        return nhaCungCapRepository.findAll();
    }

    public NhaCungCap findByMaNcc(String maNcc) {
        return nhaCungCapRepository.findByMaNcc(maNcc);
    }

    public NhaCungCap save(NhaCungCap ncc) {
        return nhaCungCapRepository.save(ncc);
    }

    public void delete(Long id) {
        nhaCungCapRepository.deleteById(id);
    }
}
