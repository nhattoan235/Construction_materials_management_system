package com.example.ht_vlxd.Service.finance;

import com.example.ht_vlxd.Model.finance.CongNo;
import com.example.ht_vlxd.Repository.finance.CongNoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CongNoService {
    private final CongNoRepository congNoRepository;

    public CongNoService(CongNoRepository congNoRepository) {
        this.congNoRepository = congNoRepository;
    }

    public List<CongNo> getAll() {
        return congNoRepository.findAll();
    }

    public CongNo save(CongNo cn) {
        return congNoRepository.save(cn);
    }
}
