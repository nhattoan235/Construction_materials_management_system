package com.example.ht_vlxd.Controller.estimation;
import com.example.ht_vlxd.Model.sales.DonHang;
import com.example.ht_vlxd.Model.sales.DonHangChiTiet;
import com.example.ht_vlxd.Model.product.HangHoa;
import com.example.ht_vlxd.Model.customer.KhachHang;
import com.example.ht_vlxd.Model.auth.NguoiDung;
import com.example.ht_vlxd.Model.product.TrangThaiHangHoa;

import com.example.ht_vlxd.DTO.estimation.CalculationRequest;
import com.example.ht_vlxd.DTO.estimation.CalculationResponse;
import com.example.ht_vlxd.DTO.common.ErrorResponse;
import com.example.ht_vlxd.Repository.sales.DonHangChiTietRepository;
import com.example.ht_vlxd.Repository.product.HangHoaRepository;
import com.example.ht_vlxd.Repository.customer.KhachHangRepository;
import com.example.ht_vlxd.Repository.auth.NguoiDungRepository;
import com.example.ht_vlxd.Service.sales.DonHangService;
import com.example.ht_vlxd.Service.estimation.MaterialEstimationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vat-lieu")
public class MaterialEstimationRestController {

    @Autowired
    private MaterialEstimationService estimationService;

    @Autowired
    private DonHangService donHangService;

    @Autowired
    private DonHangChiTietRepository donHangChiTietRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private HangHoaRepository hangHoaRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @PostMapping("/tinh-toan")
    public ResponseEntity<?> calculate(@RequestBody CalculationRequest request) {
        if (request.getDienTich() == null || request.getDienTich() <= 0
                || request.getSoTang() == null || request.getSoTang() <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Diện tích và số tầng phải lớn hơn 0"));
        }
        try {
            CalculationResponse response = estimationService.calculateEstimates(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Đã xảy ra lỗi khi tính toán vật liệu: " + e.getMessage()));
        }
    }

    @GetMapping("/danh-sach-san-pham")
    public ResponseEntity<?> getProductsForCategory(@RequestParam String loaiVatLieu,
                                                     @RequestParam(required = false) Long khachHangId) {
        if (loaiVatLieu == null || loaiVatLieu.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Nhóm vật liệu (loaiVatLieu) không được để trống."));
        }
        try {
            List<Map<String, Object>> products = estimationService.getProductsForCategory(loaiVatLieu, khachHangId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Đã xảy ra lỗi khi lấy danh sách sản phẩm: " + e.getMessage()));
        }
    }

    @PostMapping("/dat-hang-nhap")
    public ResponseEntity<?> createDraftOrder(@RequestBody Map<String, Object> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Yêu cầu đăng nhập để lập đơn hàng nháp."));
        }

        String username = auth.getName();
        NguoiDung nvKinhDoanh = nguoiDungRepository.findByUsername(username);

        Long khachHangId = body.get("khachHangId") != null ? Long.valueOf(body.get("khachHangId").toString()) : null;
        String tenKhachVangLai = (String) body.get("tenKhachVangLai");
        String sdtKhachVangLai = (String) body.get("sdtKhachVangLai");
        String diaChiGiao = (String) body.get("diaChiGiao");
        String ghiChu = (String) body.get("ghiChu");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> chiTiet = (List<Map<String, Object>>) body.get("chiTiet");
        if (chiTiet == null || chiTiet.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Danh sách chi tiết vật tư không được để trống."));
        }

        DonHang dh = new DonHang();
        if (khachHangId != null) {
            KhachHang kh = khachHangRepository.findById(khachHangId).orElse(null);
            if (kh == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Khách hàng chỉ định không tồn tại."));
            }
            dh.setKhachHang(kh);
        } else {
            dh.setTenKhachVangLai(tenKhachVangLai);
            dh.setSdtKhachVangLai(sdtKhachVangLai);
        }

        dh.setNvKinhDoanh(nvKinhDoanh);
        dh.setMaDonHang("DHN-" + System.currentTimeMillis());
        dh.setDiaChiGiao(diaChiGiao != null ? diaChiGiao : "");
        dh.setTrangThai("CHO_XAC_NHAN");
        dh.setGhiChu(ghiChu != null ? ghiChu : "");
        dh.setNgayDat(LocalDateTime.now());

        try {
            dh = donHangService.save(dh);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage()));
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Map<String, Object> item : chiTiet) {
            Long hhId = Long.valueOf(item.get("hangHoaId").toString());
            BigDecimal soLuong = new BigDecimal(item.get("soLuong").toString());

            HangHoa hh = hangHoaRepository.findById(hhId).orElse(null);
            if (hh == null || hh.getTrangThai() == TrangThaiHangHoa.NGUNG_KINH_DOANH) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Sản phẩm ID " + hhId + " không hợp lệ hoặc đã ngừng kinh doanh."));
            }

            boolean useGiaSi = false;
            if (dh.getKhachHang() != null) {
                useGiaSi = "DOANH_NGHIEP".equalsIgnoreCase(dh.getKhachHang().getLoaiKhach());
            }
            BigDecimal donGia = (useGiaSi && hh.getGiaBanSi() != null) ? hh.getGiaBanSi() : hh.getGiaBanLe();
            BigDecimal subtotal = donGia.multiply(soLuong);
            total = total.add(subtotal);

            DonHangChiTiet ct = new DonHangChiTiet();
            ct.setDonHang(dh);
            ct.setHangHoa(hh);
            ct.setSoLuong(soLuong);
            ct.setDonGia(donGia);
            ct.setThanhTien(subtotal);
            donHangChiTietRepository.save(ct);
        }

        dh.setTongTien(total);
        dh.setTienDatCoc(total.multiply(new BigDecimal("0.30")));
        donHangService.save(dh);

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("maDonHang", dh.getMaDonHang());
        resp.put("tongTien", total);
        return ResponseEntity.ok(resp);
    }
}
