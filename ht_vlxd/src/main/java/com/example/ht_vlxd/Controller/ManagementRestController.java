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
@RequestMapping("/api/management")
public class ManagementRestController {

    private final DonHangRepository donHangRepository;
    private final DonHangChiTietRepository donHangChiTietRepository;
    private final CongNoRepository congNoRepository;
    private final TonKhoRepository tonKhoRepository;
    private final HopDongRepository hopDongRepository;
    private final DoiTraHangRepository doiTraHangRepository;
    private final BaoCaoRepository baoCaoRepository;
    private final PhieuKhoRepository phieuKhoRepository;
    private final PhieuKhoChiTietRepository phieuKhoChiTietRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final KhoRepository khoRepository;

    private final NguoiDungService nguoiDungService;

    public ManagementRestController(DonHangRepository donHangRepository,
                                    DonHangChiTietRepository donHangChiTietRepository,
                                    CongNoRepository congNoRepository,
                                    TonKhoRepository tonKhoRepository,
                                    HopDongRepository hopDongRepository,
                                    DoiTraHangRepository doiTraHangRepository,
                                    BaoCaoRepository baoCaoRepository,
                                    PhieuKhoRepository phieuKhoRepository,
                                    PhieuKhoChiTietRepository phieuKhoChiTietRepository,
                                    NguoiDungRepository nguoiDungRepository,
                                    KhoRepository khoRepository,
                                    NguoiDungService nguoiDungService) {
        this.donHangRepository = donHangRepository;
        this.donHangChiTietRepository = donHangChiTietRepository;
        this.congNoRepository = congNoRepository;
        this.tonKhoRepository = tonKhoRepository;
        this.hopDongRepository = hopDongRepository;
        this.doiTraHangRepository = doiTraHangRepository;
        this.baoCaoRepository = baoCaoRepository;
        this.phieuKhoRepository = phieuKhoRepository;
        this.phieuKhoChiTietRepository = phieuKhoChiTietRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.khoRepository = khoRepository;
        this.nguoiDungService = nguoiDungService;
    }

    // ====================================================
    // SUMMARY STATS FOR EXECUTIVE KPIS
    // ====================================================

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        // 1. Tổng doanh thu lũy kế (các đơn hàng HOAN_THANH, DA_XAC_NHAN, DANG_GIAO)
        List<DonHang> donHangs = donHangRepository.findAll();
        BigDecimal tongDoanhThu = BigDecimal.ZERO;
        for (DonHang dh : donHangs) {
            String status = dh.getTrangThai();
            if ("HOAN_THANH".equals(status) || "DA_XAC_NHAN".equals(status) || "DANG_GIAO".equals(status)) {
                tongDoanhThu = tongDoanhThu.add(dh.getTongTien() != null ? dh.getTongTien() : BigDecimal.ZERO);
            }
        }

        // 2. Số dư nợ khách hàng (Phải thu): sum of (soTienNo - soTienDaTt) where status != DA_THANH_TOAN
        List<CongNo> congNos = congNoRepository.findAll();
        BigDecimal tongDuNo = BigDecimal.ZERO;
        for (CongNo cn : congNos) {
            if (!"DA_THANH_TOAN".equals(cn.getTrangThai())) {
                BigDecimal no = cn.getSoTienNo() != null ? cn.getSoTienNo() : BigDecimal.ZERO;
                BigDecimal daTt = cn.getSoTienDaTt() != null ? cn.getSoTienDaTt() : BigDecimal.ZERO;
                tongDuNo = tongDuNo.add(no.subtract(daTt).max(BigDecimal.ZERO));
            }
        }

        // 3. Giá trị tồn kho quy đổi (sum of soLuong * giaBanLe)
        List<TonKho> tonKhos = tonKhoRepository.findAll();
        BigDecimal giaTriTonKho = BigDecimal.ZERO;
        for (TonKho tk : tonKhos) {
            if (tk.getHangHoa() != null && tk.getSoLuong() != null) {
                BigDecimal rate = tk.getHangHoa().getGiaBanLe() != null ? tk.getHangHoa().getGiaBanLe() : BigDecimal.ZERO;
                giaTriTonKho = giaTriTonKho.add(tk.getSoLuong().multiply(rate));
            }
        }

        // 4. Tỉ lệ hoàn thành kế hoạch (mục tiêu tháng: 25,000,000 VND)
        BigDecimal mucTieu = new BigDecimal("25000000");
        BigDecimal phanTramDat = BigDecimal.ZERO;
        if (mucTieu.compareTo(BigDecimal.ZERO) > 0) {
            phanTramDat = tongDoanhThu.multiply(new BigDecimal("100")).divide(mucTieu, 2, BigDecimal.ROUND_HALF_UP);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("tongDoanhThu", tongDoanhThu);
        result.put("tongDuNo", tongDuNo);
        result.put("giaTriTonKho", giaTriTonKho);
        result.put("phanTramDat", phanTramDat);
        result.put("mucTieu", mucTieu);

        return ResponseEntity.ok(result);
    }

    // ====================================================
    // NEW ORDERS AND DELIVERY ROUTINGS
    // ====================================================

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders() {
        List<DonHang> donHangs = donHangRepository.findAll();
        // Sort newest first
        donHangs.sort((o1, o2) -> {
            if (o1.getNgayDat() == null) return 1;
            if (o2.getNgayDat() == null) return -1;
            return o2.getNgayDat().compareTo(o1.getNgayDat());
        });

        List<Map<String, Object>> response = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (DonHang dh : donHangs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dh.getId());
            map.put("maDonHang", dh.getMaDonHang());
            map.put("khachHangTen", dh.getKhachHang() != null && dh.getKhachHang().getNguoiDung() != null ?
                    dh.getKhachHang().getNguoiDung().getHoTen() : "Khách vãng lai");
            map.put("ngayDat", dh.getNgayDat() != null ? dh.getNgayDat().format(formatter) : "");
            map.put("tongTien", dh.getTongTien());
            map.put("trangThai", dh.getTrangThai());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    // ====================================================
    // PENDING APPROVALS LIST
    // ====================================================

    @GetMapping("/pending-approvals")
    public ResponseEntity<?> getPendingApprovals() {
        List<Map<String, Object>> approvals = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // 1. Contracts in NHAP/CHO_DUYET status
        List<HopDong> contracts = hopDongRepository.findAll();
        for (HopDong hd : contracts) {
            if ("NHAP".equals(hd.getTrangThai()) || "CHO_DUYET".equals(hd.getTrangThai())) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", hd.getId());
                item.put("type", "CONTRACT");
                item.put("title", "Duyệt chiết khấu hợp đồng " + hd.getMaHopDong());
                String khTen = (hd.getKhachHang() != null && hd.getKhachHang().getNguoiDung() != null) ?
                        hd.getKhachHang().getNguoiDung().getHoTen() : "Không rõ";
                item.put("description", "Đề xuất ký với KH: " + khTen + " - Trị giá: " + hd.getGiaTri() + "đ");
                item.put("targetKey", hd.getMaHopDong());
                approvals.add(item);
            }
        }

        // 2. Returns/Exchanges in CHO_DUYET status
        List<DoiTraHang> returns = doiTraHangRepository.findAll();
        for (DoiTraHang dth : returns) {
            if ("CHO_DUYET".equals(dth.getTrangThai())) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", dth.getId());
                item.put("type", "RETURN");
                String loai = "DOI".equals(dth.getLoai()) ? "Đổi hàng" : "Trả hàng";
                item.put("title", "Duyệt yêu cầu " + loai + " " + dth.getMaDoiTra());
                String khTen = (dth.getKhachHang() != null && dth.getKhachHang().getNguoiDung() != null) ?
                        dth.getKhachHang().getNguoiDung().getHoTen() : "Không rõ";
                String hangTen = dth.getHangHoa() != null ? dth.getHangHoa().getTenHang() : "hàng hóa";
                item.put("description", "KH: " + khTen + " yêu cầu trả " + dth.getSoLuong() + " " + (dth.getHangHoa() != null ? dth.getHangHoa().getDonViTinh() : "") + " " + hangTen + ". Lý do: " + dth.getLyDo());
                item.put("targetKey", dth.getMaDoiTra());
                approvals.add(item);
            }
        }

        // 3. Reports in CHO_DUYET status
        List<BaoCao> reports = baoCaoRepository.findAll();
        for (BaoCao bc : reports) {
            if ("CHO_DUYET".equals(bc.getTrangThai())) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", bc.getId());
                item.put("type", "REPORT");
                item.put("title", "Phê duyệt báo cáo: " + bc.getTieuDe());
                String nguoiLap = bc.getNguoiLap() != null ? bc.getNguoiLap().getHoTen() : "Nhân viên";
                item.put("description", "Người lập: " + nguoiLap + " - Ngày lập: " + bc.getNgayLap().format(dateFormatter) + ". Ghi chú: " + bc.getGhiChu());
                item.put("targetKey", bc.getId().toString());
                approvals.add(item);
            }
        }

        // 4. Warehouse/Inventory Slips in NHAP status (Draft waiting for approval)
        List<PhieuKho> slips = phieuKhoRepository.findAll();
        for (PhieuKho pk : slips) {
            if ("NHAP".equals(pk.getTrangThai())) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", pk.getId());
                item.put("type", "INVENTORY_SLIP");
                String loai = "NHẬP KHO".equals(pk.getLoaiPhieu()) || "NHAP".equals(pk.getLoaiPhieu()) ? "nhập kho" : "xuất kho";
                item.put("title", "Duyệt phiếu " + loai + " " + pk.getMaPhieu());
                String nguoiLap = pk.getNguoiTao() != null ? pk.getNguoiTao().getHoTen() : "Thủ kho";
                String doiTac = "";
                if ("NHAP".equals(pk.getLoaiPhieu()) && pk.getNhaCungCap() != null) {
                    doiTac = " từ NCC: " + pk.getNhaCungCap().getTenNcc();
                } else if (pk.getDonHang() != null) {
                    doiTac = " cho đơn hàng: " + pk.getDonHang().getMaDonHang();
                }
                item.put("description", "Người lập: " + nguoiLap + doiTac + ". Ghi chú: " + pk.getGhiChu());
                item.put("targetKey", pk.getMaPhieu());
                approvals.add(item);
            }
        }

        return ResponseEntity.ok(approvals);
    }

    // ====================================================
    // DYNAMIC INVENTORY REPORT
    // ====================================================

    @GetMapping("/inventory-report")
    public ResponseEntity<?> getInventoryReport() {
        List<TonKho> tonKhos = tonKhoRepository.findAll();
        List<PhieuKhoChiTiet> slipDetails = phieuKhoChiTietRepository.findAll();

        Map<Long, BigDecimal> currentStocks = new HashMap<>();
        for (TonKho tk : tonKhos) {
            if (tk.getHangHoa() != null) {
                Long hhId = tk.getHangHoa().getId();
                BigDecimal current = currentStocks.getOrDefault(hhId, BigDecimal.ZERO);
                currentStocks.put(hhId, current.add(tk.getSoLuong() != null ? tk.getSoLuong() : BigDecimal.ZERO));
            }
        }

        Map<Long, BigDecimal> importedMap = new HashMap<>();
        Map<Long, BigDecimal> exportedMap = new HashMap<>();

        for (PhieuKhoChiTiet ct : slipDetails) {
            if (ct.getHangHoa() != null && ct.getPhieuKho() != null && "DA_DUYET".equals(ct.getPhieuKho().getTrangThai())) {
                Long hhId = ct.getHangHoa().getId();
                String type = ct.getPhieuKho().getLoaiPhieu();
                BigDecimal qty = ct.getSoLuong() != null ? ct.getSoLuong() : BigDecimal.ZERO;
                if ("NHAP".equals(type)) {
                    BigDecimal current = importedMap.getOrDefault(hhId, BigDecimal.ZERO);
                    importedMap.put(hhId, current.add(qty));
                } else if ("XUAT".equals(type)) {
                    BigDecimal current = exportedMap.getOrDefault(hhId, BigDecimal.ZERO);
                    exportedMap.put(hhId, current.add(qty));
                }
            }
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        Set<Long> addedIds = new HashSet<>();
        List<TonKho> allTks = tonKhoRepository.findAll();
        for (TonKho tk : allTks) {
            HangHoa hh = tk.getHangHoa();
            if (hh == null || addedIds.contains(hh.getId())) continue;
            addedIds.add(hh.getId());

            BigDecimal cur = currentStocks.getOrDefault(hh.getId(), BigDecimal.ZERO);
            BigDecimal imp = importedMap.getOrDefault(hh.getId(), BigDecimal.ZERO);
            BigDecimal exp = exportedMap.getOrDefault(hh.getId(), BigDecimal.ZERO);
            BigDecimal start = cur.subtract(imp).add(exp);

            Map<String, Object> map = new HashMap<>();
            map.put("maHang", hh.getMaHang());
            map.put("tenHang", hh.getTenHang());
            map.put("donViTinh", hh.getDonViTinh());
            map.put("tonDauKy", start);
            map.put("nhapTrongKy", imp);
            map.put("xuatTrongKy", exp);
            map.put("tonCuoiKy", cur);
            rows.add(map);
        }

        return ResponseEntity.ok(rows);
    }

    // ====================================================
    // ACTION ENDPOINTS
    // ====================================================

    @PostMapping("/contracts/approve")
    public ResponseEntity<?> approveContract(@RequestBody Map<String, String> body) {
        String maHopDong = body.get("maHopDong");
        HopDong hd = hopDongRepository.findByMaHopDong(maHopDong);
        if (hd == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy hợp đồng.");
        }
        hd.setTrangThai("HIEU_LUC");
        hopDongRepository.save(hd);
        return ResponseEntity.ok(Map.of("message", "Đã duyệt và kích hoạt hợp đồng thành công!"));
    }

    @PostMapping("/returns/approve")
    public ResponseEntity<?> approveReturn(@RequestBody Map<String, String> body) {
        String maDoiTra = body.get("maDoiTra");
        DoiTraHang dth = doiTraHangRepository.findByMaDoiTra(maDoiTra);
        if (dth == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy yêu cầu đổi trả.");
        }
        dth.setTrangThai("DA_DUYET");
        dth.setNgayXuLy(LocalDateTime.now());
        dth.setGhiChuXuLy("Ban Quản Lý phê duyệt.");
        doiTraHangRepository.save(dth);

        // Giảm trừ công nợ nếu hình thức là TRẢ HÀNG (TRA)
        if ("TRA".equals(dth.getLoai()) && dth.getDonHang() != null) {
            List<CongNo> cnList = congNoRepository.findByKhachHangId(dth.getKhachHang().getId());
            for (CongNo cn : cnList) {
                if (cn.getDonHang() != null && cn.getDonHang().getId().equals(dth.getDonHang().getId())) {
                    BigDecimal rate = dth.getHangHoa() != null ? dth.getHangHoa().getGiaBanLe() : BigDecimal.ZERO;
                    BigDecimal returnVal = dth.getSoLuong().multiply(rate);
                    BigDecimal currentNo = cn.getSoTienNo();
                    cn.setSoTienNo(currentNo.subtract(returnVal).max(BigDecimal.ZERO));
                    if (cn.getSoTienNo().compareTo(cn.getSoTienDaTt()) <= 0) {
                        cn.setTrangThai("DA_THANH_TOAN");
                    }
                    congNoRepository.save(cn);
                }
            }

            // Cộng trả lại kho (khi hoàn trả thì tăng số lượng lên)
            Kho targetKho = null;
            List<PhieuKho> slips = phieuKhoRepository.findAll();
            for (PhieuKho pk : slips) {
                if ("XUAT".equals(pk.getLoaiPhieu()) && pk.getDonHang() != null && pk.getDonHang().getId().equals(dth.getDonHang().getId())) {
                    targetKho = pk.getKho();
                    break;
                }
            }
            if (targetKho == null) {
                List<Kho> khos = khoRepository.findAll();
                if (!khos.isEmpty()) targetKho = khos.get(0);
            }

            if (targetKho != null && dth.getHangHoa() != null) {
                final Kho finalKho = targetKho;
                Optional<TonKho> optTk = tonKhoRepository.findAll().stream()
                        .filter(tk -> tk.getHangHoa().getId().equals(dth.getHangHoa().getId()) && tk.getKho().getId().equals(finalKho.getId()))
                        .findFirst();

                TonKho tk;
                if (optTk.isPresent()) {
                    tk = optTk.get();
                } else {
                    tk = new TonKho();
                    tk.setHangHoa(dth.getHangHoa());
                    tk.setKho(finalKho);
                    tk.setSoLuong(BigDecimal.ZERO);
                }

                BigDecimal currentQty = tk.getSoLuong() != null ? tk.getSoLuong() : BigDecimal.ZERO;
                BigDecimal returnQty = dth.getSoLuong() != null ? dth.getSoLuong() : BigDecimal.ZERO;
                tk.setSoLuong(currentQty.add(returnQty));
                tonKhoRepository.save(tk);
            }
        }
        return ResponseEntity.ok(Map.of("message", "Đã phê duyệt yêu cầu đổi trả thành công!"));
    }

    @PostMapping("/returns/reject")
    public ResponseEntity<?> rejectReturn(@RequestBody Map<String, String> body) {
        String maDoiTra = body.get("maDoiTra");
        DoiTraHang dth = doiTraHangRepository.findByMaDoiTra(maDoiTra);
        if (dth == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy yêu cầu đổi trả.");
        }
        dth.setTrangThai("TU_CHOI");
        dth.setNgayXuLy(LocalDateTime.now());
        dth.setGhiChuXuLy("Ban Quản Lý từ chối phê duyệt.");
        doiTraHangRepository.save(dth);
        return ResponseEntity.ok(Map.of("message", "Đã từ chối yêu cầu đổi trả thành công!"));
    }

    @PostMapping("/reports/approve")
    public ResponseEntity<?> approveReport(@RequestBody Map<String, String> body) {
        Long reportId = Long.valueOf(body.get("reportId"));
        BaoCao bc = baoCaoRepository.findById(reportId).orElse(null);
        if (bc == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy báo cáo.");
        }
        bc.setTrangThai("DA_DUYET");
        bc.setNgayDuyet(LocalDateTime.now());
        baoCaoRepository.save(bc);
        return ResponseEntity.ok(Map.of("message", "Đã phê duyệt báo cáo thành công!"));
    }

    @PostMapping("/inventory-slips/approve")
    public ResponseEntity<?> approveInventorySlip(@RequestBody Map<String, String> body) {
        String maPhieu = body.get("maPhieu");
        PhieuKho pk = phieuKhoRepository.findByMaPhieu(maPhieu);
        if (pk == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy phiếu kho.");
        }
        if (!"NHAP".equals(pk.getTrangThai())) {
            return ResponseEntity.badRequest().body("Phiếu kho đã được xử lý từ trước.");
        }

        // Update TonKho dynamically
        List<PhieuKhoChiTiet> chiTiets = phieuKhoChiTietRepository.findByPhieuKhoId(pk.getId());
        for (PhieuKhoChiTiet ct : chiTiets) {
            HangHoa hh = ct.getHangHoa();
            Kho kho = pk.getKho();
            if (hh != null && kho != null) {
                Optional<TonKho> optTk = tonKhoRepository.findAll().stream()
                        .filter(tk -> tk.getHangHoa().getId().equals(hh.getId()) && tk.getKho().getId().equals(kho.getId()))
                        .findFirst();

                TonKho tk;
                if (optTk.isPresent()) {
                    tk = optTk.get();
                } else {
                    tk = new TonKho();
                    tk.setHangHoa(hh);
                    tk.setKho(kho);
                    tk.setSoLuong(BigDecimal.ZERO);
                }

                BigDecimal currentQty = tk.getSoLuong() != null ? tk.getSoLuong() : BigDecimal.ZERO;
                BigDecimal diff = ct.getSoLuong() != null ? ct.getSoLuong() : BigDecimal.ZERO;

                if ("NHAP".equals(pk.getLoaiPhieu())) {
                    tk.setSoLuong(currentQty.add(diff));
                } else if ("XUAT".equals(pk.getLoaiPhieu())) {
                    tk.setSoLuong(currentQty.subtract(diff));
                }
                tonKhoRepository.save(tk);
            }
        }

        pk.setTrangThai("DA_DUYET");
        phieuKhoRepository.save(pk);

        return ResponseEntity.ok(Map.of("message", "Đã duyệt phiếu kho và cập nhật số lượng tồn kho!"));
    }
}
