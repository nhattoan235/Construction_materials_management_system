package com.example.ht_vlxd.Service.sales;

import com.example.ht_vlxd.Model.sales.DonHang;
import com.example.ht_vlxd.Repository.sales.DonHangRepository;
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
        if (order.getKhachHang() == null && 
            (order.getTenKhachVangLai() == null || order.getTenKhachVangLai().trim().isEmpty() ||
             order.getSdtKhachVangLai() == null || order.getSdtKhachVangLai().trim().isEmpty())) {
            throw new IllegalArgumentException("Đơn hàng phải được gán cho một Khách hàng hoặc có đầy đủ thông tin Khách vãng lai (Tên + Số điện thoại).");
        }
        return donHangRepository.save(order);
    }

    public void deleteOrder(Long id) {
        donHangRepository.deleteById(id);
    }
}
