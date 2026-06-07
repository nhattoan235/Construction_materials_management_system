package com.example.ht_vlxd.Service;

import com.example.ht_vlxd.Model.DonHang;
import com.example.ht_vlxd.Repository.DonHangRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonHangService {
    private final DonHangRepository donHangRepository;

    public DonHangService(DonHangRepository donHangRepository) {
        this.donHangRepository = donHangRepository;
    }

    public List<DonHang> getAllOrders() {
        return donHangRepository.findAll();
    }

    public DonHang findByMaDonHang(String maDonHang) {
        return donHangRepository.findByMaDonHang(maDonHang);
    }

    public List<DonHang> getOrdersByCustomer(Long khachHangId) {
        return donHangRepository.findByKhachHangId(khachHangId);
    }

    public DonHang save(DonHang order) {
        return donHangRepository.save(order);
    }

    public void deleteOrder(Long id) {
        donHangRepository.deleteById(id);
    }
}
