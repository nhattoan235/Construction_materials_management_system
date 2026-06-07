package com.example.ht_vlxd.Controller;

import com.example.ht_vlxd.Model.*;
import com.example.ht_vlxd.Repository.*;
import com.example.ht_vlxd.Service.NguoiDungService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/accounting")
public class AccountingRestController {

    private final CongNoRepository congNoRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final DonHangRepository donHangRepository;
    private final DonHangChiTietRepository donHangChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final BaoCaoRepository baoCaoRepository;
    private final HopDongRepository hopDongRepository;

    private final NguoiDungService nguoiDungService;

    public AccountingRestController(CongNoRepository congNoRepository,
                                    ThanhToanRepository thanhToanRepository,
                                    DonHangRepository donHangRepository,
                                    DonHangChiTietRepository donHangChiTietRepository,
                                    KhachHangRepository khachHangRepository,
                                    NhaCungCapRepository nhaCungCapRepository,
                                    NguoiDungRepository nguoiDungRepository,
                                    BaoCaoRepository baoCaoRepository,
                                    HopDongRepository hopDongRepository,
                                    NguoiDungService nguoiDungService) {
        this.congNoRepository = congNoRepository;
        this.thanhToanRepository = thanhToanRepository;
        this.donHangRepository = donHangRepository;
        this.donHangChiTietRepository = donHangChiTietRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.baoCaoRepository = baoCaoRepository;
        this.hopDongRepository = hopDongRepository;
        this.nguoiDungService = nguoiDungService;
    }

    // ====================================================
    // CÔNG NỢ KHÁCH HÀNG (Phải Thu)
    // ====================================================

    @GetMapping("/debts/customers")
    public ResponseEntity<?> getCustomerDebts() {
        List<CongNo> congNos = congNoRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (CongNo cn : congNos) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cn.getId());
            map.put("khachHangId", cn.getKhachHang() != null ? cn.getKhachHang().getId() : null);
            map.put("khachHangTen", cn.getKhachHang() != null && cn.getKhachHang().getNguoiDung() != null
                    ? cn.getKhachHang().getNguoiDung().getHoTen() : "Không tên");
            map.put("soDienThoai", cn.getKhachHang() != null && cn.getKhachHang().getNguoiDung() != null
                    ? cn.getKhachHang().getNguoiDung().getSoDienThoai() : "");
            map.put("maDonHang", cn.getDonHang() != null ? cn.getDonHang().getMaDonHang() : "");
            map.put("maHopDong", cn.getHopDong() != null ? cn.getHopDong().getMaHopDong() : "");
            map.put("soTienNo", cn.getSoTienNo());
            map.put("soTienDaTt", cn.getSoTienDaTt());
            BigDecimal conLai = cn.getSoTienNo().subtract(cn.getSoTienDaTt());
            map.put("soTienConLai", conLai.max(BigDecimal.ZERO));
            map.put("hanThanhToan", cn.getHanThanhToan() != null ? cn.getHanThanhToan().format(formatter) : "");
            map.put("trangThai", cn.getTrangThai());
            map.put("ngayPhatSinh", cn.getNgayPhatSinh() != null ? cn.getNgayPhatSinh().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
            map.put("ghiChu", cn.getGhiChu() != null ? cn.getGhiChu() : "");
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    // ====================================================
    // THỐNG KÊ TỔNG HỢP
    // ====================================================

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        List<CongNo> congNos = congNoRepository.findAll();
        BigDecimal tongPhatThu = BigDecimal.ZERO;
        BigDecimal tongDaThu = BigDecimal.ZERO;
        BigDecimal tongConNo = BigDecimal.ZERO;
        int soKhachConNo = 0;

        for (CongNo cn : congNos) {
            tongPhatThu = tongPhatThu.add(cn.getSoTienNo());
            tongDaThu = tongDaThu.add(cn.getSoTienDaTt());
            BigDecimal conLai = cn.getSoTienNo().subtract(cn.getSoTienDaTt()).max(BigDecimal.ZERO);
            tongConNo = tongConNo.add(conLai);
            if (conLai.compareTo(BigDecimal.ZERO) > 0) {
                soKhachConNo++;
            }
        }

        // Tổng doanh thu (từ các đơn hàng hoàn thành)
        List<DonHang> donHangs = donHangRepository.findAll();
        BigDecimal tongDoanhThu = BigDecimal.ZERO;
        long tongDonHang = 0;
        for (DonHang dh : donHangs) {
            if ("HOAN_THANH".equals(dh.getTrangThai()) || "DA_XAC_NHAN".equals(dh.getTrangThai())) {
                tongDoanhThu = tongDoanhThu.add(dh.getTongTien() != null ? dh.getTongTien() : BigDecimal.ZERO);
                tongDonHang++;
            }
        }

        // Tổng phiếu thu
        List<ThanhToan> thanhToans = thanhToanRepository.findAll();
        BigDecimal tongThuThucTe = BigDecimal.ZERO;
        for (ThanhToan tt : thanhToans) {
            tongThuThucTe = tongThuThucTe.add(tt.getSoTien());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tongPhatThu", tongPhatThu);
        result.put("tongDaThu", tongDaThu);
        result.put("tongConNo", tongConNo);
        result.put("soKhachConNo", soKhachConNo);
        result.put("tongDoanhThu", tongDoanhThu);
        result.put("tongDonHang", tongDonHang);
        result.put("tongThuThucTe", tongThuThucTe);
        result.put("soPhieuThu", thanhToans.size());

        return ResponseEntity.ok(result);
    }

    // ====================================================
    // THU TIỀN KHÁCH HÀNG (Ghi nhận thanh toán công nợ)
    // ====================================================

    @PostMapping("/debts/collect")
    public ResponseEntity<?> collectPayment(@RequestBody Map<String, Object> body) {
        Long congNoId = Long.valueOf(body.get("congNoId").toString());
        BigDecimal soTien = new BigDecimal(body.get("soTien").toString());
        String hinhThuc = (String) body.get("hinhThuc");
        String maGiaoDich = (String) body.getOrDefault("maGiaoDich", "");
        String ghiChu = (String) body.getOrDefault("ghiChu", "");
        String username = (String) body.getOrDefault("username", "");

        CongNo cn = congNoRepository.findById(congNoId).orElse(null);
        if (cn == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy công nợ.");
        }

        BigDecimal conLai = cn.getSoTienNo().subtract(cn.getSoTienDaTt()).max(BigDecimal.ZERO);
        if (soTien.compareTo(conLai) > 0) {
            soTien = conLai; // Cap to remaining amount
        }

        // Tạo phiếu thanh toán
        ThanhToan tt = new ThanhToan();
        tt.setMaThanhToan("PT-" + System.currentTimeMillis());
        tt.setCongNo(cn);
        NguoiDung nguoiThu = nguoiDungService.findByUsername(username);
        if (nguoiThu == null && !nguoiDungRepository.findAll().isEmpty()) {
            nguoiThu = nguoiDungRepository.findAll().get(0);
        }
        tt.setNguoiThu(nguoiThu);
        tt.setSoTien(soTien);
        tt.setHinhThuc(hinhThuc != null ? hinhThuc : "CHUYEN_KHOAN");
        tt.setMaGiaoDich(maGiaoDich);
        tt.setGhiChu(ghiChu);
        tt.setNgayThanhToan(LocalDateTime.now());
        thanhToanRepository.save(tt);

        // Cập nhật công nợ
        BigDecimal newDaTt = cn.getSoTienDaTt().add(soTien);
        cn.setSoTienDaTt(newDaTt);
        BigDecimal newConLai = cn.getSoTienNo().subtract(newDaTt).max(BigDecimal.ZERO);
        if (newConLai.compareTo(BigDecimal.ZERO) == 0) {
            cn.setTrangThai("DA_THANH_TOAN");
        } else {
            cn.setTrangThai("CHUA_THANH_TOAN");
        }
        congNoRepository.save(cn);

        return ResponseEntity.ok(Map.of(
            "message", "Thu tiền thành công! Mã phiếu: " + tt.getMaThanhToan(),
            "maThanhToan", tt.getMaThanhToan(),
            "soTienThu", soTien,
            "conLai", newConLai
        ));
    }

    // ====================================================
    // LỊCH SỬ PHIẾU THU
    // ====================================================

    @GetMapping("/payments")
    public ResponseEntity<?> getPayments() {
        List<ThanhToan> thanhToans = thanhToanRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (ThanhToan tt : thanhToans) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", tt.getId());
            map.put("maThanhToan", tt.getMaThanhToan());
            map.put("congNoId", tt.getCongNo() != null ? tt.getCongNo().getId() : null);
            map.put("khachHangTen", tt.getCongNo() != null && tt.getCongNo().getKhachHang() != null &&
                    tt.getCongNo().getKhachHang().getNguoiDung() != null
                    ? tt.getCongNo().getKhachHang().getNguoiDung().getHoTen() : "");
            map.put("maDonHang", tt.getCongNo() != null && tt.getCongNo().getDonHang() != null
                    ? tt.getCongNo().getDonHang().getMaDonHang() : "");
            map.put("soTien", tt.getSoTien());
            map.put("hinhThuc", tt.getHinhThuc());
            map.put("maGiaoDich", tt.getMaGiaoDich() != null ? tt.getMaGiaoDich() : "");
            map.put("nguoiThu", tt.getNguoiThu() != null ? tt.getNguoiThu().getHoTen() : "");
            map.put("ngayThanhToan", tt.getNgayThanhToan() != null ? tt.getNgayThanhToan().format(formatter) : "");
            map.put("ghiChu", tt.getGhiChu() != null ? tt.getGhiChu() : "");
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    // ====================================================
    // NHÀ CUNG CẤP
    // ====================================================

    @GetMapping("/suppliers")
    public ResponseEntity<?> getSuppliers() {
        List<NhaCungCap> suppliers = nhaCungCapRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();

        for (NhaCungCap ncc : suppliers) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", ncc.getId());
            map.put("maNcc", ncc.getMaNcc());
            map.put("tenNcc", ncc.getTenNcc());
            map.put("soDienThoai", ncc.getSoDienThoai() != null ? ncc.getSoDienThoai() : "");
            map.put("email", ncc.getEmail() != null ? ncc.getEmail() : "");
            map.put("diaChi", ncc.getDiaChi() != null ? ncc.getDiaChi() : "");
            map.put("maSoThue", ncc.getMaSoThue() != null ? ncc.getMaSoThue() : "");
            map.put("nguoiLienHe", ncc.getNguoiLienHe() != null ? ncc.getNguoiLienHe() : "");
            map.put("trangThai", ncc.getTrangThai());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    // ====================================================
    // BÁO CÁO TÀI CHÍNH
    // ====================================================

    @GetMapping("/reports/revenue")
    public ResponseEntity<?> getRevenueReport(@RequestParam(required = false) String tuNgay,
                                               @RequestParam(required = false) String denNgay) {
        List<DonHang> donHangs = donHangRepository.findAll();
        List<Map<String, Object>> rows = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate from = tuNgay != null && !tuNgay.isEmpty() ? LocalDate.parse(tuNgay, inputFormatter) : null;
        LocalDate to = denNgay != null && !denNgay.isEmpty() ? LocalDate.parse(denNgay, inputFormatter) : null;

        BigDecimal tongDoanhThu = BigDecimal.ZERO;
        BigDecimal tongDaThu = BigDecimal.ZERO;
        BigDecimal tongConNo = BigDecimal.ZERO;

        for (DonHang dh : donHangs) {
            if (dh.getNgayDat() == null) continue;
            LocalDate ngayDat = dh.getNgayDat().toLocalDate();
            if (from != null && ngayDat.isBefore(from)) continue;
            if (to != null && ngayDat.isAfter(to)) continue;

            Map<String, Object> row = new HashMap<>();
            row.put("maDonHang", dh.getMaDonHang());
            row.put("khachHangTen", dh.getKhachHang() != null && dh.getKhachHang().getNguoiDung() != null
                    ? dh.getKhachHang().getNguoiDung().getHoTen() : "Khách vãng lai");
            row.put("ngayDat", ngayDat.format(dateFormatter));
            row.put("tongTien", dh.getTongTien() != null ? dh.getTongTien() : BigDecimal.ZERO);
            row.put("tienDatCoc", dh.getTienDatCoc() != null ? dh.getTienDatCoc() : BigDecimal.ZERO);
            row.put("trangThai", dh.getTrangThai());

            // Tính công nợ từ db
            BigDecimal daThu = BigDecimal.ZERO;
            BigDecimal conNo = BigDecimal.ZERO;
            List<CongNo> cnList = congNoRepository.findByKhachHangId(
                    dh.getKhachHang() != null ? dh.getKhachHang().getId() : -1L);
            for (CongNo cn : cnList) {
                if (cn.getDonHang() != null && cn.getDonHang().getId().equals(dh.getId())) {
                    daThu = cn.getSoTienDaTt();
                    conNo = cn.getSoTienNo().subtract(cn.getSoTienDaTt()).max(BigDecimal.ZERO);
                    break;
                }
            }
            row.put("daThu", daThu);
            row.put("conNo", conNo);

            // Build product summary
            List<DonHangChiTiet> chiTiets = donHangChiTietRepository.findByDonHangId(dh.getId());
            StringBuilder vatLieu = new StringBuilder();
            for (DonHangChiTiet ct : chiTiets) {
                if (vatLieu.length() > 0) vatLieu.append(", ");
                vatLieu.append(ct.getHangHoa().getTenHang());
            }
            row.put("vatLieu", vatLieu.length() > 0 ? vatLieu.toString() : "—");

            tongDoanhThu = tongDoanhThu.add(dh.getTongTien() != null ? dh.getTongTien() : BigDecimal.ZERO);
            tongDaThu = tongDaThu.add(daThu);
            tongConNo = tongConNo.add(conNo);
            rows.add(row);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rows", rows);
        result.put("tongDoanhThu", tongDoanhThu);
        result.put("tongDaThu", tongDaThu);
        result.put("tongConNo", tongConNo);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reports/cashflow")
    public ResponseEntity<?> getCashflowReport(@RequestParam(required = false) String tuNgay,
                                                @RequestParam(required = false) String denNgay) {
        List<ThanhToan> thanhToans = thanhToanRepository.findAll();
        List<Map<String, Object>> rows = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate from = tuNgay != null && !tuNgay.isEmpty() ? LocalDate.parse(tuNgay, inputFormatter) : null;
        LocalDate to = denNgay != null && !denNgay.isEmpty() ? LocalDate.parse(denNgay, inputFormatter) : null;

        BigDecimal tongThu = BigDecimal.ZERO;
        int soPhieu = 0;

        for (ThanhToan tt : thanhToans) {
            if (tt.getNgayThanhToan() == null) continue;
            LocalDate ngayGd = tt.getNgayThanhToan().toLocalDate();
            if (from != null && ngayGd.isBefore(from)) continue;
            if (to != null && ngayGd.isAfter(to)) continue;

            Map<String, Object> row = new HashMap<>();
            row.put("maThanhToan", tt.getMaThanhToan());
            row.put("loaiPhieu", "THU");
            row.put("nguoiGiaoDich", tt.getCongNo() != null && tt.getCongNo().getKhachHang() != null
                    && tt.getCongNo().getKhachHang().getNguoiDung() != null
                    ? tt.getCongNo().getKhachHang().getNguoiDung().getHoTen() : "");
            row.put("ngayGiaoDich", tt.getNgayThanhToan().format(formatter));
            row.put("hinhThuc", tt.getHinhThuc());
            row.put("soTien", tt.getSoTien());
            row.put("maGiaoDich", tt.getMaGiaoDich() != null ? tt.getMaGiaoDich() : "");
            row.put("ghiChu", tt.getGhiChu() != null ? tt.getGhiChu() : "");
            row.put("nguoiThu", tt.getNguoiThu() != null ? tt.getNguoiThu().getHoTen() : "");
            rows.add(row);
            tongThu = tongThu.add(tt.getSoTien());
            soPhieu++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rows", rows);
        result.put("tongThu", tongThu);
        result.put("tongChi", BigDecimal.ZERO); // supplier payments not tracked in this system yet
        result.put("luuChuyen", tongThu);
        result.put("soPhieuThu", soPhieu);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reports/debt-summary")
    public ResponseEntity<?> getDebtSummaryReport() {
        List<KhachHang> khachHangs = khachHangRepository.findAll();
        List<Map<String, Object>> rows = new ArrayList<>();

        BigDecimal tongDuNo = BigDecimal.ZERO;

        for (KhachHang kh : khachHangs) {
            List<CongNo> cnList = congNoRepository.findByKhachHangId(kh.getId());
            BigDecimal duNo = BigDecimal.ZERO;
            for (CongNo cn : cnList) {
                duNo = duNo.add(cn.getSoTienNo().subtract(cn.getSoTienDaTt()).max(BigDecimal.ZERO));
            }
            if (duNo.compareTo(BigDecimal.ZERO) == 0) continue; // Skip fully paid

            Map<String, Object> row = new HashMap<>();
            row.put("khachHangTen", kh.getNguoiDung() != null ? kh.getNguoiDung().getHoTen() : "Không tên");
            row.put("soDienThoai", kh.getNguoiDung() != null ? kh.getNguoiDung().getSoDienThoai() : "");
            row.put("diaChi", kh.getNguoiDung() != null ? kh.getNguoiDung().getDiaChi() : "");
            row.put("soCongNo", cnList.size());
            row.put("tongDuNo", duNo);
            row.put("trangThai", "CON_NO");
            tongDuNo = tongDuNo.add(duNo);
            rows.add(row);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rows", rows);
        result.put("tongDuNo", tongDuNo);
        result.put("soKhach", rows.size());
        return ResponseEntity.ok(result);
    }

    // ====================================================
    // LƯU BÁO CÁO
    // ====================================================

    @PostMapping("/reports/save")
    public ResponseEntity<?> saveReport(@RequestBody Map<String, Object> body) {
        String loai = (String) body.get("loai");
        String tieuDe = (String) body.get("tieuDe");
        String tuNgayStr = (String) body.get("tuNgay");
        String denNgayStr = (String) body.get("denNgay");
        String username = (String) body.getOrDefault("username", "");
        String ghiChu = (String) body.getOrDefault("ghiChu", "");

        NguoiDung nguoiLap = nguoiDungService.findByUsername(username);
        if (nguoiLap == null && !nguoiDungRepository.findAll().isEmpty()) {
            nguoiLap = nguoiDungRepository.findAll().get(0);
        }

        BaoCao bc = new BaoCao();
        bc.setLoai(loai != null ? loai : "TAI_CHINH");
        bc.setTieuDe(tieuDe != null ? tieuDe : "Báo cáo tài chính");
        bc.setNguoiLap(nguoiLap);
        bc.setNgayLap(LocalDateTime.now());
        bc.setGhiChu(ghiChu);
        bc.setTrangThai("DA_DUYET");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (tuNgayStr != null && !tuNgayStr.isEmpty()) {
            bc.setTuNgay(LocalDate.parse(tuNgayStr, fmt));
        } else {
            bc.setTuNgay(LocalDate.now().minusMonths(1));
        }
        if (denNgayStr != null && !denNgayStr.isEmpty()) {
            bc.setDenNgay(LocalDate.parse(denNgayStr, fmt));
        } else {
            bc.setDenNgay(LocalDate.now());
        }

        BaoCao saved = baoCaoRepository.save(bc);
        return ResponseEntity.ok(Map.of("message", "Lưu báo cáo thành công!", "id", saved.getId()));
    }

    // ====================================================
    // DANH SÁCH HỢP ĐỒNG (kế toán xem)
    // ====================================================

    @PostMapping("/suppliers")
    public ResponseEntity<?> addSupplier(@RequestBody Map<String, Object> body) {
        String maNcc = (String) body.get("maNcc");
        String tenNcc = (String) body.get("tenNcc");
        if (maNcc == null || maNcc.isBlank() || tenNcc == null || tenNcc.isBlank()) {
            return ResponseEntity.badRequest().body("Mã NCC và Tên NCC là bắt buộc.");
        }
        NhaCungCap ncc = new NhaCungCap();
        ncc.setMaNcc(maNcc);
        ncc.setTenNcc(tenNcc);
        ncc.setMaSoThue((String) body.getOrDefault("maSoThue", ""));
        ncc.setNguoiLienHe((String) body.getOrDefault("nguoiLienHe", ""));
        ncc.setSoDienThoai((String) body.getOrDefault("soDienThoai", ""));
        ncc.setEmail((String) body.getOrDefault("email", ""));
        ncc.setDiaChi((String) body.getOrDefault("diaChi", ""));
        ncc.setGhiChu((String) body.getOrDefault("ghiChu", ""));
        ncc.setTrangThai("HOAT_DONG");
        nhaCungCapRepository.save(ncc);
        return ResponseEntity.ok(Map.of("message", "Thêm nhà cung cấp thành công!"));
    }

    // ====================================================
    // HỢP ĐỒNG (kế toán xem)
    // ====================================================

    @GetMapping("/contracts")
    public ResponseEntity<?> getContracts() {
        List<HopDong> contracts = hopDongRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (HopDong hd : contracts) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", hd.getId());
            map.put("maHopDong", hd.getMaHopDong());
            map.put("khachHangTen", hd.getKhachHang() != null && hd.getKhachHang().getNguoiDung() != null
                    ? hd.getKhachHang().getNguoiDung().getHoTen() : "");
            map.put("giaTri", hd.getGiaTri());
            map.put("tienDatCoc", hd.getTienDatCoc() != null ? hd.getTienDatCoc() : BigDecimal.ZERO);
            map.put("ngayKy", hd.getNgayKy() != null ? hd.getNgayKy().format(dateFormatter) : "");
            map.put("trangThai", hd.getTrangThai());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }
}
