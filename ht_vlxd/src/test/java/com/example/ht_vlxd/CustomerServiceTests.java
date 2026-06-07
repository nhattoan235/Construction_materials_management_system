package com.example.ht_vlxd;

import com.example.ht_vlxd.Model.*;
import com.example.ht_vlxd.Repository.*;
import com.example.ht_vlxd.Service.DonHangService;
import com.example.ht_vlxd.Service.HangHoaService;
import com.example.ht_vlxd.Service.NguoiDungService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CustomerServiceTests {

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private HangHoaService hangHoaService;

    @Autowired
    private DonHangService donHangService;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private DonHangChiTietRepository donHangChiTietRepository;

    @Autowired
    private DoiTraHangRepository doiTraHangRepository;

    @Test
    void testProfileLoadAndUpdate() {
        // 1. Load the seeded customer account
        NguoiDung nd = nguoiDungService.findByUsername("khachhang01");
        assertNotNull(nd, "Seeded customer account 'khachhang01' should exist.");
        assertEquals("Nguyễn Văn Khách", nd.getHoTen());

        KhachHang kh = khachHangRepository.findByNguoiDungUsername("khachhang01");
        assertNotNull(kh, "Customer profile associated with 'khachhang01' should exist.");

        // 2. Perform profile update
        nd.setHoTen("Nguyễn Văn A");
        nd.setEmail("newemail@gmail.com");
        nd.setSoDienThoai("0999888777");
        nd.setDiaChi("123 Đường B, Quận 1, TP. HCM");
        nguoiDungService.save(nd);

        kh.setTenCongTy("Công ty TNHH Thép Việt");
        kh.setMaSoThue("0316487965");
        khachHangRepository.save(kh);

        // 3. Reload and verify updates
        NguoiDung updatedNd = nguoiDungService.findByUsername("khachhang01");
        assertEquals("Nguyễn Văn A", updatedNd.getHoTen());
        assertEquals("newemail@gmail.com", updatedNd.getEmail());
        assertEquals("0999888777", updatedNd.getSoDienThoai());

        KhachHang updatedKh = khachHangRepository.findByNguoiDungUsername("khachhang01");
        assertEquals("Công ty TNHH Thép Việt", updatedKh.getTenCongTy());
        assertEquals("0316487965", updatedKh.getMaSoThue());
    }

    @Test
    void testOrderPlacementAndDepositCalculation() {
        // 1. Load customer and product
        KhachHang kh = khachHangRepository.findByNguoiDungUsername("khachhang01");
        assertNotNull(kh);

        List<HangHoa> products = hangHoaService.getAllProducts();
        assertFalse(products.isEmpty(), "Seeded products list should not be empty.");
        HangHoa hh = products.get(0); // Choose first product (e.g. Thép cây D10)

        // 2. Place order with qty = 10
        BigDecimal qty = new BigDecimal("10");
        BigDecimal donGia = hh.getGiaBanLe();
        BigDecimal expectedTotal = donGia.multiply(qty);
        BigDecimal expectedDeposit = expectedTotal.multiply(new BigDecimal("0.3")); // 30%

        DonHang dh = new DonHang();
        dh.setKhachHang(kh);
        dh.setMaDonHang("TEST-DH-" + System.currentTimeMillis());
        dh.setDiaChiGiao("Công trình Tân Bình");
        dh.setTongTien(expectedTotal);
        dh.setTienDatCoc(expectedDeposit);
        dh.setTrangThai("CHO_XAC_NHAN");
        dh.setNgayDat(LocalDateTime.now());
        DonHang savedDh = donHangService.save(dh);

        assertNotNull(savedDh.getId());
        assertEquals(expectedTotal.compareTo(savedDh.getTongTien()), 0);
        assertEquals(expectedDeposit.compareTo(savedDh.getTienDatCoc()), 0);

        // 3. Create detail items
        DonHangChiTiet ct = new DonHangChiTiet();
        ct.setDonHang(savedDh);
        ct.setHangHoa(hh);
        ct.setSoLuong(qty);
        ct.setDonGia(donGia);
        ct.setThanhTien(expectedTotal);
        DonHangChiTiet savedCt = donHangChiTietRepository.save(ct);

        assertNotNull(savedCt.getId());
        assertEquals(savedCt.getDonHang().getId(), savedDh.getId());
        assertEquals(savedCt.getHangHoa().getId(), hh.getId());

        // Verify repo query
        List<DonHangChiTiet> fetchedDetails = donHangChiTietRepository.findByDonHangId(savedDh.getId());
        assertEquals(1, fetchedDetails.size());
        assertEquals(fetchedDetails.get(0).getHangHoa().getTenHang(), hh.getTenHang());
    }

    @Test
    void testOrderCancellation() {
        // 1. Create a mock order
        KhachHang kh = khachHangRepository.findByNguoiDungUsername("khachhang01");
        DonHang dh = new DonHang();
        dh.setKhachHang(kh);
        dh.setMaDonHang("TEST-CANCEL-" + System.currentTimeMillis());
        dh.setTongTien(BigDecimal.TEN);
        dh.setTienDatCoc(BigDecimal.ONE);
        dh.setTrangThai("CHO_XAC_NHAN");
        dh.setNgayDat(LocalDateTime.now());
        DonHang savedDh = donHangService.save(dh);

        // 2. Cancel order
        savedDh.setTrangThai("DA_HUY");
        DonHang updatedDh = donHangService.save(savedDh);

        assertEquals("DA_HUY", updatedDh.getTrangThai(), "Order status should change to DA_HUY.");
    }

    @Test
    void testExchangeReturnRequest() {
        // 1. Create mock order
        KhachHang kh = khachHangRepository.findByNguoiDungUsername("khachhang01");
        HangHoa hh = hangHoaService.getAllProducts().get(0);

        DonHang dh = new DonHang();
        dh.setKhachHang(kh);
        dh.setMaDonHang("TEST-RETURN-" + System.currentTimeMillis());
        dh.setTongTien(BigDecimal.TEN);
        dh.setTienDatCoc(BigDecimal.ONE);
        dh.setTrangThai("HOAN_THANH"); // Return requests are for completed orders
        dh.setNgayDat(LocalDateTime.now());
        DonHang savedDh = donHangService.save(dh);

        // 2. File return request
        DoiTraHang dth = new DoiTraHang();
        dth.setDonHang(savedDh);
        dth.setKhachHang(kh);
        dth.setHangHoa(hh);
        dth.setSoLuong(BigDecimal.ONE);
        dth.setLyDo("Thép rỉ sét nghiêm trọng");
        dth.setLoai("TRA"); // Return/Refund
        dth.setTrangThai("CHO_DUYET");
        dth.setMaDoiTra("TEST-DT-" + System.currentTimeMillis());
        dth.setNgayYeuCau(LocalDateTime.now());
        DoiTraHang savedDth = doiTraHangRepository.save(dth);

        assertNotNull(savedDth.getId());
        assertEquals("CHO_DUYET", savedDth.getTrangThai());
        assertEquals("TRA", savedDth.getLoai());

        // Verify retrieval
        List<DoiTraHang> returns = doiTraHangRepository.findByKhachHangId(kh.getId());
        assertFalse(returns.isEmpty());
        assertTrue(returns.stream().anyMatch(r -> r.getMaDoiTra().equals(savedDth.getMaDoiTra())));
    }
}
