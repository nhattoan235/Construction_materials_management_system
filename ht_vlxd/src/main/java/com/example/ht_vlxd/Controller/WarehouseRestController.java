package com.example.ht_vlxd.Controller;

import com.example.ht_vlxd.Model.*;
import com.example.ht_vlxd.Repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseRestController {

    private final TonKhoRepository tonKhoRepository;
    private final PhieuKhoRepository phieuKhoRepository;
    private final PhieuKhoChiTietRepository phieuKhoChiTietRepository;
    private final HangHoaRepository hangHoaRepository;
    private final KhoRepository khoRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final DonHangRepository donHangRepository;
    private final DonHangChiTietRepository donHangChiTietRepository;
    private final GiaoNhanRepository giaoNhanRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public WarehouseRestController(TonKhoRepository tonKhoRepository,
                                   PhieuKhoRepository phieuKhoRepository,
                                   PhieuKhoChiTietRepository phieuKhoChiTietRepository,
                                   HangHoaRepository hangHoaRepository,
                                   KhoRepository khoRepository,
                                   NhaCungCapRepository nhaCungCapRepository,
                                   DonHangRepository donHangRepository,
                                   DonHangChiTietRepository donHangChiTietRepository,
                                   GiaoNhanRepository giaoNhanRepository,
                                   NguoiDungRepository nguoiDungRepository) {
        this.tonKhoRepository = tonKhoRepository;
        this.phieuKhoRepository = phieuKhoRepository;
        this.phieuKhoChiTietRepository = phieuKhoChiTietRepository;
        this.hangHoaRepository = hangHoaRepository;
        this.khoRepository = khoRepository;
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.donHangRepository = donHangRepository;
        this.donHangChiTietRepository = donHangChiTietRepository;
        this.giaoNhanRepository = giaoNhanRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    // ====================================================
    // SUMMARY STATS
    // ====================================================

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        List<TonKho> tonKhos = tonKhoRepository.findAll();
        long totalProducts = tonKhos.stream()
                .map(tk -> tk.getHangHoa() != null ? tk.getHangHoa().getId() : -1L)
                .distinct().filter(id -> id >= 0).count();

        long lowStock = tonKhos.stream()
                .filter(tk -> tk.getSoLuong() != null && tk.getSoLuong().compareTo(new BigDecimal("10")) < 0)
                .count();

        // Orders needing warehouse action: DA_XAC_NHAN status
        List<DonHang> donHangs = donHangRepository.findAll();
        long needExport = donHangs.stream()
                .filter(dh -> "DA_XAC_NHAN".equals(dh.getTrangThai()))
                .count();

        long inDelivery = giaoNhanRepository.findAll().stream()
                .filter(gn -> "DANG_GIAO".equals(gn.getTrangThai()))
                .count();

        // Pending slips (NHAP = draft waiting approval from management)
        long pendingSlips = phieuKhoRepository.findAll().stream()
                .filter(pk -> "NHAP".equals(pk.getTrangThai()))
                .count();

        Map<String, Object> result = new HashMap<>();
        result.put("totalProducts", totalProducts);
        result.put("lowStock", lowStock);
        result.put("needExport", needExport);
        result.put("inDelivery", inDelivery);
        result.put("pendingSlips", pendingSlips);
        return ResponseEntity.ok(result);
    }

    // ====================================================
    // TỒN KHO THỰC TẾ
    // ====================================================

    @GetMapping("/stock")
    public ResponseEntity<?> getStock() {
        List<TonKho> tonKhos = tonKhoRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        // Deduplicate by hangHoa id
        Map<Long, TonKho> seen = new LinkedHashMap<>();
        for (TonKho tk : tonKhos) {
            if (tk.getHangHoa() != null) {
                seen.put(tk.getHangHoa().getId(), tk);
            }
        }

        for (TonKho tk : seen.values()) {
            HangHoa hh = tk.getHangHoa();
            Map<String, Object> map = new HashMap<>();
            map.put("id", tk.getId());
            map.put("hangHoaId", hh.getId());
            map.put("maHang", hh.getMaHang());
            map.put("tenHang", hh.getTenHang());
            map.put("donViTinh", hh.getDonViTinh());
            map.put("soLuong", tk.getSoLuong());
            map.put("khoTen", tk.getKho() != null ? tk.getKho().getTenKho() : "Kho chính");
            // Low-stock threshold: 10 units
            boolean isLow = tk.getSoLuong() != null && tk.getSoLuong().compareTo(new BigDecimal("10")) < 0;
            map.put("isLow", isLow);
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    // ====================================================
    // LỊCH SỬ PHIẾU KHO (Nhập / Xuất)
    // ====================================================

    @GetMapping("/slips")
    public ResponseEntity<?> getSlips() {
        List<PhieuKho> slips = phieuKhoRepository.findAll();
        // Sort newest first
        slips.sort((a, b) -> {
            if (a.getNgayLap() == null) return 1;
            if (b.getNgayLap() == null) return -1;
            return b.getNgayLap().compareTo(a.getNgayLap());
        });

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<Map<String, Object>> result = new ArrayList<>();

        for (PhieuKho pk : slips) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", pk.getId());
            map.put("maPhieu", pk.getMaPhieu());
            map.put("loaiPhieu", pk.getLoaiPhieu());
            map.put("ngayLap", pk.getNgayLap() != null ? pk.getNgayLap().format(fmt) : "");
            map.put("nguoiTao", pk.getNguoiTao() != null ? pk.getNguoiTao().getHoTen() : "Thủ kho");
            map.put("khoTen", pk.getKho() != null ? pk.getKho().getTenKho() : "Kho chính");
            map.put("trangThai", pk.getTrangThai());
            map.put("ghiChu", pk.getGhiChu() != null ? pk.getGhiChu() : "");

            // Source: NCC or DonHang
            String nguon = "";
            if (pk.getNhaCungCap() != null) {
                nguon = "NCC: " + pk.getNhaCungCap().getTenNcc();
            } else if (pk.getDonHang() != null) {
                nguon = "Đơn hàng: " + pk.getDonHang().getMaDonHang();
            }
            map.put("nguon", nguon);

            // Chi tiết hàng hóa
            List<PhieuKhoChiTiet> chiTiets = phieuKhoChiTietRepository.findByPhieuKhoId(pk.getId());
            StringBuilder sanPham = new StringBuilder();
            BigDecimal tongSL = BigDecimal.ZERO;
            for (PhieuKhoChiTiet ct : chiTiets) {
                if (sanPham.length() > 0) sanPham.append(", ");
                sanPham.append(ct.getHangHoa() != null ? ct.getHangHoa().getTenHang() : "?");
                tongSL = tongSL.add(ct.getSoLuong() != null ? ct.getSoLuong() : BigDecimal.ZERO);
            }
            map.put("sanPham", sanPham.length() > 0 ? sanPham.toString() : "—");
            map.put("tongSoLuong", tongSL);
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    // ====================================================
    // LẤY DỮ LIỆU CHO FORM TẠO PHIẾU
    // ====================================================

    @GetMapping("/form-data")
    public ResponseEntity<?> getFormData() {
        List<HangHoa> hangHoas = hangHoaRepository.findAll();
        List<NhaCungCap> nccs = nhaCungCapRepository.findAll();
        List<Kho> khos = khoRepository.findAll();
        List<DonHang> donHangs = donHangRepository.findAll().stream()
                .filter(dh -> "DA_XAC_NHAN".equals(dh.getTrangThai()))
                .collect(java.util.stream.Collectors.toList());

        List<Map<String, Object>> hangHoaList = new ArrayList<>();
        for (HangHoa hh : hangHoas) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", hh.getId());
            m.put("maHang", hh.getMaHang());
            m.put("tenHang", hh.getTenHang());
            m.put("donViTinh", hh.getDonViTinh());
            hangHoaList.add(m);
        }

        List<Map<String, Object>> nccList = new ArrayList<>();
        for (NhaCungCap ncc : nccs) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", ncc.getId());
            m.put("tenNcc", ncc.getTenNcc());
            nccList.add(m);
        }

        List<Map<String, Object>> khoList = new ArrayList<>();
        for (Kho k : khos) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", k.getId());
            m.put("tenKho", k.getTenKho());
            khoList.add(m);
        }

        List<Map<String, Object>> donHangList = new ArrayList<>();
        for (DonHang dh : donHangs) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", dh.getId());
            m.put("maDonHang", dh.getMaDonHang());
            String khTen = dh.getKhachHang() != null && dh.getKhachHang().getNguoiDung() != null
                    ? dh.getKhachHang().getNguoiDung().getHoTen() : "KH";
            m.put("label", dh.getMaDonHang() + " - " + khTen);
            donHangList.add(m);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("hangHoas", hangHoaList);
        result.put("nccs", nccList);
        result.put("khos", khoList);
        result.put("donHangs", donHangList);

        // Generate next slip code
        List<PhieuKho> existingSlips = phieuKhoRepository.findAll();
        int nextNum = existingSlips.size() + 1;
        String year = String.valueOf(LocalDateTime.now().getYear());
        result.put("nextNhapCode", String.format("PN-%s-%04d", year, nextNum));
        result.put("nextXuatCode", String.format("PX-%s-%04d", year, nextNum));

        return ResponseEntity.ok(result);
    }

    // ====================================================
    // TẠO PHIẾU KHO MỚI (Nhân viên kho tạo, chờ BQL duyệt)
    // ====================================================

    @PostMapping("/slips/create")
    public ResponseEntity<?> createSlip(@RequestBody Map<String, Object> body) {
        try {
            String loaiPhieu = (String) body.get("loaiPhieu"); // NHAP or XUAT
            String ghiChu = (String) body.get("ghiChu");
            Long khoId = Long.valueOf(body.get("khoId").toString());

            // Generate unique slip code
            List<PhieuKho> existing = phieuKhoRepository.findAll();
            int nextNum = existing.size() + 1;
            String year = String.valueOf(LocalDateTime.now().getYear());
            String prefix = "NHAP".equals(loaiPhieu) ? "PN" : "PX";
            String maPhieu = String.format("%s-%s-%04d", prefix, year, nextNum);

            // Ensure unique
            while (phieuKhoRepository.findByMaPhieu(maPhieu) != null) {
                nextNum++;
                maPhieu = String.format("%s-%s-%04d", prefix, year, nextNum);
            }

            PhieuKho pk = new PhieuKho();
            pk.setMaPhieu(maPhieu);
            pk.setLoaiPhieu(loaiPhieu);
            pk.setNgayLap(LocalDateTime.now());
            pk.setGhiChu(ghiChu);
            pk.setTrangThai("NHAP"); // Draft, waiting management approval

            Kho kho = khoRepository.findById(khoId).orElse(null);
            if (kho == null) return ResponseEntity.badRequest().body("Không tìm thấy kho.");
            pk.setKho(kho);

            // Link to NCC or DonHang
            if ("NHAP".equals(loaiPhieu) && body.get("nhaCungCapId") != null) {
                Long nccId = Long.valueOf(body.get("nhaCungCapId").toString());
                nhaCungCapRepository.findById(nccId).ifPresent(pk::setNhaCungCap);
            } else if ("XUAT".equals(loaiPhieu) && body.get("donHangId") != null) {
                Long dhId = Long.valueOf(body.get("donHangId").toString());
                donHangRepository.findById(dhId).ifPresent(pk::setDonHang);
            }

            // nguoiTao: get first admin/warehouse user as placeholder (real auth not implemented)
            List<NguoiDung> users = nguoiDungRepository.findAll();
            if (!users.isEmpty()) pk.setNguoiTao(users.get(0));

            pk = phieuKhoRepository.save(pk);

            // Save chi tiet lines
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> lines = (List<Map<String, Object>>) body.get("chiTiet");
            if (lines != null) {
                for (Map<String, Object> line : lines) {
                    Long hhId = Long.valueOf(line.get("hangHoaId").toString());
                    BigDecimal soLuong = new BigDecimal(line.get("soLuong").toString());
                    String ghiChuCt = line.containsKey("ghiChu") ? (String) line.get("ghiChu") : "";

                    HangHoa hh = hangHoaRepository.findById(hhId).orElse(null);
                    if (hh == null) continue;

                    PhieuKhoChiTiet ct = new PhieuKhoChiTiet();
                    ct.setPhieuKho(pk);
                    ct.setHangHoa(hh);
                    ct.setSoLuong(soLuong);
                    ct.setDonGia(BigDecimal.ZERO);
                    ct.setGhiChu(ghiChuCt);
                    phieuKhoChiTietRepository.save(ct);
                }
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Đã tạo phiếu " + maPhieu + " thành công! Phiếu đang chờ Ban Quản Lý phê duyệt.");
            resp.put("maPhieu", maPhieu);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi tạo phiếu: " + e.getMessage());
        }
    }

    // ====================================================
    // KIỂM KÊ KHO (Điều chỉnh số lượng thực tế)
    // ====================================================

    @PostMapping("/stock/adjust")
    public ResponseEntity<?> adjustStock(@RequestBody Map<String, Object> body) {
        try {
            Long tonKhoId = Long.valueOf(body.get("tonKhoId").toString());
            BigDecimal soLuongThuc = new BigDecimal(body.get("soLuongThuc").toString());
            String lyDo = (String) body.get("lyDo");

            TonKho tk = tonKhoRepository.findById(tonKhoId).orElse(null);
            if (tk == null) return ResponseEntity.badRequest().body("Không tìm thấy bản ghi tồn kho.");

            BigDecimal soLuongSoSach = tk.getSoLuong() != null ? tk.getSoLuong() : BigDecimal.ZERO;
            BigDecimal chenh = soLuongThuc.subtract(soLuongSoSach);

            tk.setSoLuong(soLuongThuc);
            tonKhoRepository.save(tk);

            // Create an adjustment slip to record the change
            List<PhieuKho> existing = phieuKhoRepository.findAll();
            int nextNum = existing.size() + 1;
            String year = String.valueOf(LocalDateTime.now().getYear());
            String maPhieu = String.format("KK-%s-%04d", year, nextNum);
            while (phieuKhoRepository.findByMaPhieu(maPhieu) != null) {
                nextNum++;
                maPhieu = String.format("KK-%s-%04d", year, nextNum);
            }

            PhieuKho adjSlip = new PhieuKho();
            adjSlip.setMaPhieu(maPhieu);
            adjSlip.setLoaiPhieu(chenh.compareTo(BigDecimal.ZERO) >= 0 ? "NHAP" : "XUAT");
            adjSlip.setKho(tk.getKho());
            adjSlip.setNgayLap(LocalDateTime.now());
            adjSlip.setGhiChu("Kiểm kê thực tế. Chênh lệch: " + chenh + ". Lý do: " + lyDo);
            adjSlip.setTrangThai("DA_DUYET"); // Adjustment slips auto-approved

            List<NguoiDung> users = nguoiDungRepository.findAll();
            if (!users.isEmpty()) adjSlip.setNguoiTao(users.get(0));

            adjSlip = phieuKhoRepository.save(adjSlip);

            PhieuKhoChiTiet ct = new PhieuKhoChiTiet();
            ct.setPhieuKho(adjSlip);
            ct.setHangHoa(tk.getHangHoa());
            ct.setSoLuong(chenh.abs());
            ct.setDonGia(BigDecimal.ZERO);
            ct.setGhiChu("Điều chỉnh kiểm kê");
            phieuKhoChiTietRepository.save(ct);

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", String.format("Đã cập nhật tồn kho! Số lượng điều chỉnh: %s%s %s. Biên bản kiểm kê %s đã được ghi nhận.",
                    chenh.compareTo(BigDecimal.ZERO) > 0 ? "+" : "",
                    chenh.toPlainString(),
                    tk.getHangHoa() != null ? tk.getHangHoa().getDonViTinh() : "",
                    maPhieu));
            resp.put("chenh", chenh);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi điều chỉnh kho: " + e.getMessage());
        }
    }

    // ====================================================
    // ĐƠN HÀNG CẦN XUẤT KHO
    // ====================================================

    @GetMapping("/orders-to-export")
    public ResponseEntity<?> getOrdersToExport() {
        List<DonHang> donHangs = donHangRepository.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Map<String, Object>> result = new ArrayList<>();

        for (DonHang dh : donHangs) {
            // Only show orders that are confirmed and not yet delivered
            String status = dh.getTrangThai();
            if (!"DA_XAC_NHAN".equals(status) && !"DANG_GIAO".equals(status)) continue;

            Map<String, Object> map = new HashMap<>();
            map.put("id", dh.getId());
            map.put("maDonHang", dh.getMaDonHang());
            String khTen = dh.getKhachHang() != null && dh.getKhachHang().getNguoiDung() != null
                    ? dh.getKhachHang().getNguoiDung().getHoTen() : "Khách";
            String diaChi = dh.getKhachHang() != null && dh.getKhachHang().getNguoiDung() != null
                    ? dh.getKhachHang().getNguoiDung().getDiaChi() : "";
            String sdt = dh.getKhachHang() != null && dh.getKhachHang().getNguoiDung() != null
                    ? dh.getKhachHang().getNguoiDung().getSoDienThoai() : "";
            map.put("khachHangTen", khTen);
            map.put("diaChiGiao", diaChi);
            map.put("soDienThoai", sdt);
            map.put("ngayDat", dh.getNgayDat() != null ? dh.getNgayDat().format(fmt) : "");
            map.put("tongTien", dh.getTongTien());
            map.put("trangThai", status);

            // Chi tiet hang hoa
            List<DonHangChiTiet> chiTiets = donHangChiTietRepository.findByDonHangId(dh.getId());
            List<Map<String, Object>> items = new ArrayList<>();
            for (DonHangChiTiet ct : chiTiets) {
                Map<String, Object> item = new HashMap<>();
                item.put("tenHang", ct.getHangHoa() != null ? ct.getHangHoa().getTenHang() : "?");
                item.put("soLuong", ct.getSoLuong());
                item.put("donViTinh", ct.getHangHoa() != null ? ct.getHangHoa().getDonViTinh() : "");
                items.add(item);
            }
            map.put("chiTiet", items);

            // Check if delivery record exists
            List<GiaoNhan> gns = giaoNhanRepository.findByDonHangId(dh.getId());
            map.put("daLapLoTrinh", !gns.isEmpty());
            if (!gns.isEmpty()) {
                GiaoNhan gn = gns.get(0);
                map.put("maGiaoNhan", gn.getMaGiaoNhan());
                map.put("trangThaiGiao", gn.getTrangThai());
            }

            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    // ====================================================
    // LẬP LỘ TRÌNH GIAO HÀNG (Tạo GiaoNhan)
    // ====================================================

    @PostMapping("/delivery/create")
    public ResponseEntity<?> createDelivery(@RequestBody Map<String, Object> body) {
        try {
            Long donHangId = Long.valueOf(body.get("donHangId").toString());
            String loTrinh = (String) body.get("loTrinh");
            String taiXe = (String) body.get("taiXe");
            String ghiChu = body.containsKey("ghiChu") ? (String) body.get("ghiChu") : "";

            DonHang dh = donHangRepository.findById(donHangId).orElse(null);
            if (dh == null) return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng.");

            // Check if delivery already created
            List<GiaoNhan> existing = giaoNhanRepository.findByDonHangId(donHangId);
            if (!existing.isEmpty() && "DANG_GIAO".equals(existing.get(0).getTrangThai())) {
                return ResponseEntity.badRequest().body("Đơn hàng này đã có lộ trình đang hoạt động.");
            }

            // Generate delivery code
            List<GiaoNhan> allGns = giaoNhanRepository.findAll();
            int nextNum = allGns.size() + 1;
            String year = String.valueOf(LocalDateTime.now().getYear());
            String maGiaoNhan = String.format("GN-%s-%04d", year, nextNum);
            while (giaoNhanRepository.findByMaGiaoNhan(maGiaoNhan) != null) {
                nextNum++;
                maGiaoNhan = String.format("GN-%s-%04d", year, nextNum);
            }

            GiaoNhan gn = new GiaoNhan();
            gn.setMaGiaoNhan(maGiaoNhan);
            gn.setDonHang(dh);
            gn.setLoTrinh(loTrinh);
            gn.setGhiChuGiao(taiXe + (ghiChu.isEmpty() ? "" : " | " + ghiChu));
            gn.setNgayGiaoDuKien(LocalDateTime.now().plusHours(4));
            gn.setTrangThai("DANG_GIAO");
            gn.setDaBanGiao(false);

            // Set recipient info from customer
            if (dh.getKhachHang() != null && dh.getKhachHang().getNguoiDung() != null) {
                gn.setNguoiNhan(dh.getKhachHang().getNguoiDung().getHoTen());
                gn.setSoDienThoaiNhan(dh.getKhachHang().getNguoiDung().getSoDienThoai());
                gn.setDiaChiGiao(dh.getKhachHang().getNguoiDung().getDiaChi());
            }

            giaoNhanRepository.save(gn);

            // Update order status to DANG_GIAO
            dh.setTrangThai("DANG_GIAO");
            donHangRepository.save(dh);

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Lập lộ trình " + maGiaoNhan + " thành công! Đơn hàng chuyển sang trạng thái \"Đang Giao Hàng\".");
            resp.put("maGiaoNhan", maGiaoNhan);
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi lập lộ trình: " + e.getMessage());
        }
    }

    // ====================================================
    // DANH SÁCH LỘ TRÌNH ĐANG GIAO
    // ====================================================

    @GetMapping("/deliveries")
    public ResponseEntity<?> getDeliveries() {
        List<GiaoNhan> gns = giaoNhanRepository.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<Map<String, Object>> result = new ArrayList<>();

        for (GiaoNhan gn : gns) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", gn.getId());
            map.put("maGiaoNhan", gn.getMaGiaoNhan());
            map.put("maDonHang", gn.getDonHang() != null ? gn.getDonHang().getMaDonHang() : "");
            map.put("donHangId", gn.getDonHang() != null ? gn.getDonHang().getId() : null);
            map.put("nguoiNhan", gn.getNguoiNhan() != null ? gn.getNguoiNhan() : "");
            map.put("soDienThoaiNhan", gn.getSoDienThoaiNhan() != null ? gn.getSoDienThoaiNhan() : "");
            map.put("diaChiGiao", gn.getDiaChiGiao() != null ? gn.getDiaChiGiao() : "");
            map.put("loTrinh", gn.getLoTrinh() != null ? gn.getLoTrinh() : "");
            map.put("ghiChuGiao", gn.getGhiChuGiao() != null ? gn.getGhiChuGiao() : "");
            map.put("trangThai", gn.getTrangThai());
            map.put("daBanGiao", gn.getDaBanGiao());
            map.put("ngayGiaoDuKien", gn.getNgayGiaoDuKien() != null ? gn.getNgayGiaoDuKien().format(fmt) : "");
            map.put("ngayBanGiao", gn.getNgayBanGiao() != null ? gn.getNgayBanGiao().format(fmt) : "");
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    // ====================================================
    // XÁC NHẬN BÀN GIAO THÀNH CÔNG
    // ====================================================

    @PostMapping("/delivery/confirm-handover")
    public ResponseEntity<?> confirmHandover(@RequestBody Map<String, Object> body) {
        try {
            String maGiaoNhan = (String) body.get("maGiaoNhan");
            String tinhTrang = (String) body.get("tinhTrang"); // OK or LOI
            String ghiChu = body.containsKey("ghiChu") ? (String) body.get("ghiChu") : "";

            GiaoNhan gn = giaoNhanRepository.findByMaGiaoNhan(maGiaoNhan);
            if (gn == null) return ResponseEntity.badRequest().body("Không tìm thấy lộ trình giao hàng.");

            gn.setDaBanGiao(true);
            gn.setNgayBanGiao(LocalDateTime.now());
            gn.setNgayGiaoThuc(LocalDateTime.now());
            gn.setGhiChuGiao(gn.getGhiChuGiao() + " | Bàn giao: " + tinhTrang + (ghiChu.isEmpty() ? "" : " - " + ghiChu));

            if ("OK".equals(tinhTrang)) {
                gn.setTrangThai("DA_GIAO");
                giaoNhanRepository.save(gn);

                // Update order to HOAN_THANH
                if (gn.getDonHang() != null) {
                    DonHang dh = gn.getDonHang();
                    dh.setTrangThai("HOAN_THANH");
                    donHangRepository.save(dh);

                    // Deduct inventory stock (giảm trừ số lượng khi giao thành công)
                    List<DonHangChiTiet> details = donHangChiTietRepository.findByDonHangId(dh.getId());
                    // Find target Kho from XUAT slip of this DonHang
                    Kho targetKho = null;
                    List<PhieuKho> slips = phieuKhoRepository.findAll();
                    for (PhieuKho pk : slips) {
                        if ("XUAT".equals(pk.getLoaiPhieu()) && pk.getDonHang() != null && pk.getDonHang().getId().equals(dh.getId())) {
                            targetKho = pk.getKho();
                            break;
                        }
                    }
                    if (targetKho == null) {
                        List<Kho> khos = khoRepository.findAll();
                        if (!khos.isEmpty()) {
                            targetKho = khos.get(0);
                        }
                    }

                    if (targetKho != null) {
                        final Kho finalKho = targetKho;
                        for (DonHangChiTiet ct : details) {
                            HangHoa hh = ct.getHangHoa();
                            if (hh == null) continue;

                            Optional<TonKho> optTk = tonKhoRepository.findAll().stream()
                                    .filter(tk -> tk.getHangHoa().getId().equals(hh.getId()) && tk.getKho().getId().equals(finalKho.getId()))
                                    .findFirst();

                            TonKho tk;
                            if (optTk.isPresent()) {
                                tk = optTk.get();
                            } else {
                                tk = new TonKho();
                                tk.setHangHoa(hh);
                                tk.setKho(finalKho);
                                tk.setSoLuong(BigDecimal.ZERO);
                            }

                            BigDecimal currentQty = tk.getSoLuong() != null ? tk.getSoLuong() : BigDecimal.ZERO;
                            BigDecimal orderQty = ct.getSoLuong() != null ? ct.getSoLuong() : BigDecimal.ZERO;
                            tk.setSoLuong(currentQty.subtract(orderQty));
                            tonKhoRepository.save(tk);
                        }
                    }
                }
                return ResponseEntity.ok(Map.of("message",
                        "Bàn giao đơn hàng thành công! Đơn chuyển trạng thái \"Hoàn Thành\". Kế toán sẽ được thông báo quyết toán."));
            } else {
                // Delivery failed/issue
                gn.setTrangThai("THAT_BAI");
                giaoNhanRepository.save(gn);

                // Keep order in DANG_GIAO, let sales handle return
                return ResponseEntity.ok(Map.of("message",
                        "Đã ghi nhận sự vụ lỗi bàn giao! Biên bản sự cố đã lưu. Phòng Kinh Doanh sẽ liên hệ khách hàng xử lý."));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi xác nhận bàn giao: " + e.getMessage());
        }
    }

    // ====================================================
    // BÁO SỰ CỐ LỘ TRÌNH
    // ====================================================

    @PostMapping("/delivery/report-issue")
    public ResponseEntity<?> reportIssue(@RequestBody Map<String, Object> body) {
        try {
            String maGiaoNhan = (String) body.get("maGiaoNhan");
            String moTa = (String) body.get("moTa");

            GiaoNhan gn = giaoNhanRepository.findByMaGiaoNhan(maGiaoNhan);
            if (gn == null) return ResponseEntity.badRequest().body("Không tìm thấy lộ trình.");

            gn.setGhiChuGiao((gn.getGhiChuGiao() != null ? gn.getGhiChuGiao() : "") + " | Sự cố: " + moTa);
            giaoNhanRepository.save(gn);

            return ResponseEntity.ok(Map.of("message",
                    "Đã ghi nhận sự cố lộ trình " + maGiaoNhan + ". Quản lý và khách hàng sẽ được thông báo."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi ghi nhận sự cố: " + e.getMessage());
        }
    }
}
