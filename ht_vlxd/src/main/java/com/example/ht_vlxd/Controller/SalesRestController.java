package com.example.ht_vlxd.Controller;

import com.example.ht_vlxd.Model.*;
import com.example.ht_vlxd.Repository.*;
import com.example.ht_vlxd.Service.DonHangService;
import com.example.ht_vlxd.Service.HangHoaService;
import com.example.ht_vlxd.Service.NguoiDungService;
import com.example.ht_vlxd.Service.HopDongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/sales")
public class SalesRestController {

    private final DonHangRepository donHangRepository;
    private final DonHangChiTietRepository donHangChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final HangHoaRepository hangHoaRepository;
    private final DoiTraHangRepository doiTraHangRepository;
    private final HopDongRepository hopDongRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final CongNoRepository congNoRepository;
    private final TonKhoRepository tonKhoRepository;
    private final PhieuKhoRepository phieuKhoRepository;
    private final KhoRepository khoRepository;

    private final NguoiDungService nguoiDungService;
    private final DonHangService donHangService;
    private final HopDongService hopDongService;
    private final HangHoaService hangHoaService;

    public SalesRestController(DonHangRepository donHangRepository,
                               DonHangChiTietRepository donHangChiTietRepository,
                               KhachHangRepository khachHangRepository,
                               HangHoaRepository hangHoaRepository,
                               DoiTraHangRepository doiTraHangRepository,
                               HopDongRepository hopDongRepository,
                               NguoiDungRepository nguoiDungRepository,
                               CongNoRepository congNoRepository,
                               TonKhoRepository tonKhoRepository,
                               PhieuKhoRepository phieuKhoRepository,
                               KhoRepository khoRepository,
                               NguoiDungService nguoiDungService,
                               DonHangService donHangService,
                               HopDongService hopDongService,
                               HangHoaService hangHoaService) {
        this.donHangRepository = donHangRepository;
        this.donHangChiTietRepository = donHangChiTietRepository;
        this.khachHangRepository = khachHangRepository;
        this.hangHoaRepository = hangHoaRepository;
        this.doiTraHangRepository = doiTraHangRepository;
        this.hopDongRepository = hopDongRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.congNoRepository = congNoRepository;
        this.tonKhoRepository = tonKhoRepository;
        this.phieuKhoRepository = phieuKhoRepository;
        this.khoRepository = khoRepository;
        this.nguoiDungService = nguoiDungService;
        this.donHangService = donHangService;
        this.hopDongService = hopDongService;
        this.hangHoaService = hangHoaService;
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        List<DonHang> donHangs = donHangRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (DonHang dh : donHangs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dh.getId());
            map.put("maDonHang", dh.getMaDonHang());
            map.put("khachHangTen", dh.getKhachHang() != null && dh.getKhachHang().getNguoiDung() != null ? 
                    dh.getKhachHang().getNguoiDung().getHoTen() : "Khách vãng lai");
            map.put("khachHangLoai", dh.getKhachHang() != null ? dh.getKhachHang().getLoaiKhach() : "");
            map.put("ngayDat", dh.getNgayDat() != null ? dh.getNgayDat().format(formatter) : "");
            map.put("tongTien", dh.getTongTien());
            map.put("tienDatCoc", dh.getTienDatCoc());
            map.put("trangThai", dh.getTrangThai());
            map.put("diaChiGiao", dh.getDiaChiGiao());
            map.put("ghiChu", dh.getGhiChu());

            List<DonHangChiTiet> details = donHangChiTietRepository.findByDonHangId(dh.getId());
            List<Map<String, Object>> detailList = new ArrayList<>();
            for (DonHangChiTiet ct : details) {
                Map<String, Object> ctMap = new HashMap<>();
                ctMap.put("id", ct.getId());
                ctMap.put("tenHang", ct.getHangHoa().getTenHang());
                ctMap.put("maHang", ct.getHangHoa().getMaHang());
                ctMap.put("soLuong", ct.getSoLuong());
                ctMap.put("donGia", ct.getDonGia());
                ctMap.put("thanhTien", ct.getThanhTien());
                detailList.add(ctMap);
            }
            map.put("chiTiet", detailList);
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/approve")
    public ResponseEntity<?> approveOrder(@RequestBody Map<String, String> body) {
        String maDonHang = body.get("maDonHang");
        DonHang dh = donHangService.findByMaDonHang(maDonHang);
        if (dh == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng.");
        }
        dh.setTrangThai("DA_XAC_NHAN");
        dh.setNgayCapNhat(LocalDateTime.now());
        donHangRepository.save(dh);
        return ResponseEntity.ok("Đã duyệt đơn hàng thành công!");
    }

    @PostMapping("/orders/cancel")
    public ResponseEntity<?> cancelOrder(@RequestBody Map<String, String> body) {
        String maDonHang = body.get("maDonHang");
        DonHang dh = donHangService.findByMaDonHang(maDonHang);
        if (dh == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng.");
        }
        dh.setTrangThai("DA_HUY");
        dh.setNgayCapNhat(LocalDateTime.now());
        donHangRepository.save(dh);
        return ResponseEntity.ok("Đơn hàng đã được từ chối/hủy bỏ.");
    }

    @PostMapping("/orders/complete")
    public ResponseEntity<?> completeOrder(@RequestBody Map<String, String> body) {
        String maDonHang = body.get("maDonHang");
        DonHang dh = donHangService.findByMaDonHang(maDonHang);
        if (dh == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng.");
        }
        dh.setTrangThai("HOAN_THANH");
        dh.setNgayCapNhat(LocalDateTime.now());
        donHangRepository.save(dh);

        // Record outstanding debt (CongNo) if not exists
        List<CongNo> cnList = congNoRepository.findByKhachHangId(dh.getKhachHang().getId());
        boolean hasCongNo = false;
        for (CongNo cn : cnList) {
            if (cn.getDonHang() != null && cn.getDonHang().getId().equals(dh.getId())) {
                hasCongNo = true;
                break;
            }
        }
        if (!hasCongNo) {
            CongNo cn = new CongNo();
            cn.setKhachHang(dh.getKhachHang());
            cn.setDonHang(dh);
            cn.setNgayPhatSinh(LocalDateTime.now());
            cn.setHanThanhToan(LocalDate.now().plusDays(30));
            BigDecimal debtAmount = dh.getTongTien().subtract(dh.getTienDatCoc() != null ? dh.getTienDatCoc() : BigDecimal.ZERO);
            cn.setSoTienNo(debtAmount);
            cn.setSoTienDaTt(BigDecimal.ZERO);
            cn.setTrangThai("CHUA_THANH_TOAN");
            congNoRepository.save(cn);
        }

        return ResponseEntity.ok("Đơn hàng đã được bàn giao và hoàn thành!");
    }

    @GetMapping("/customers")
    public ResponseEntity<?> getCustomers() {
        List<KhachHang> khachHangs = khachHangRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();
        for (KhachHang kh : khachHangs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", kh.getId());
            map.put("maKhachHang", kh.getMaKhachHang());
            map.put("hoTen", kh.getNguoiDung() != null ? kh.getNguoiDung().getHoTen() : "Không tên");
            map.put("soDienThoai", kh.getNguoiDung() != null ? kh.getNguoiDung().getSoDienThoai() : "");
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/create")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> body) {
        Long customerId = Long.valueOf(body.get("customerId").toString());
        Long productId = Long.valueOf(body.get("productId").toString());
        BigDecimal soLuong = new BigDecimal(body.get("soLuong").toString());
        String diaChiGiao = (String) body.get("diaChiGiao");
        String ghiChu = (String) body.get("ghiChu");
        String username = (String) body.get("username"); // salesperson username

        KhachHang kh = khachHangRepository.findById(customerId).orElse(null);
        if (kh == null) {
            return ResponseEntity.badRequest().body("Khách hàng không hợp lệ.");
        }

        HangHoa hh = hangHoaService.findById(productId);
        if (hh == null) {
            return ResponseEntity.badRequest().body("Sản phẩm không hợp lệ.");
        }

        NguoiDung nvKinhDoanh = nguoiDungService.findByUsername(username);

        BigDecimal donGia = hh.getGiaBanLe();
        BigDecimal tongTien = donGia.multiply(soLuong);
        BigDecimal tienDatCoc = tongTien.multiply(new BigDecimal("0.30"));

        DonHang dh = new DonHang();
        dh.setKhachHang(kh);
        dh.setNvKinhDoanh(nvKinhDoanh);
        dh.setMaDonHang("DH-" + System.currentTimeMillis());
        dh.setDiaChiGiao(diaChiGiao);
        dh.setTongTien(tongTien);
        dh.setTienDatCoc(tienDatCoc);
        dh.setTrangThai("DA_XAC_NHAN"); // salesperson orders are auto-approved
        dh.setGhiChu(ghiChu);
        dh.setNgayDat(LocalDateTime.now());
        
        DonHang savedDh = donHangRepository.save(dh);

        DonHangChiTiet ct = new DonHangChiTiet();
        ct.setDonHang(savedDh);
        ct.setHangHoa(hh);
        ct.setSoLuong(soLuong);
        ct.setDonGia(donGia);
        ct.setThanhTien(tongTien);
        ct.setGhiChu(ghiChu);
        donHangChiTietRepository.save(ct);

        return ResponseEntity.ok("Khởi tạo và phê duyệt đơn hàng thành công! Mã đơn: " + savedDh.getMaDonHang());
    }

    @GetMapping("/returns")
    public ResponseEntity<?> getReturns() {
        List<DoiTraHang> returns = doiTraHangRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (DoiTraHang dth : returns) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dth.getId());
            map.put("maDoiTra", dth.getMaDoiTra());
            map.put("maDonHang", dth.getDonHang() != null ? dth.getDonHang().getMaDonHang() : "");
            map.put("khachHangTen", dth.getKhachHang() != null && dth.getKhachHang().getNguoiDung() != null ? 
                    dth.getKhachHang().getNguoiDung().getHoTen() : "");
            map.put("tenHang", dth.getHangHoa() != null ? dth.getHangHoa().getTenHang() : "");
            map.put("maHang", dth.getHangHoa() != null ? dth.getHangHoa().getMaHang() : "");
            map.put("soLuong", dth.getSoLuong());
            map.put("loai", dth.getLoai());
            map.put("lyDo", dth.getLyDo());
            map.put("trangThai", dth.getTrangThai());
            map.put("ngayYeuCau", dth.getNgayYeuCau() != null ? dth.getNgayYeuCau().format(formatter) : "");
            map.put("ghiChuXuLy", dth.getGhiChuXuLy() != null ? dth.getGhiChuXuLy() : "");
            map.put("ngayXuLy", dth.getNgayXuLy() != null ? dth.getNgayXuLy().format(formatter) : "");
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/returns/approve")
    public ResponseEntity<?> approveReturn(@RequestBody Map<String, String> body) {
        String maDoiTra = body.get("maDoiTra");
        String note = body.get("note");

        DoiTraHang dth = doiTraHangRepository.findByMaDoiTra(maDoiTra);
        if (dth == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy yêu cầu đổi trả.");
        }

        dth.setTrangThai("DA_DUYET");
        dth.setNgayXuLy(LocalDateTime.now());
        dth.setGhiChuXuLy(note != null ? note : "Đã phê duyệt đổi trả.");
        doiTraHangRepository.save(dth);

        // Giảm trừ công nợ nếu hình thức là TRẢ HÀNG
        if ("TRA".equals(dth.getLoai())) {
            List<CongNo> cnList = congNoRepository.findByKhachHangId(dth.getKhachHang().getId());
            for (CongNo cn : cnList) {
                if (cn.getDonHang() != null && cn.getDonHang().getId().equals(dth.getDonHang().getId())) {
                    BigDecimal returnVal = dth.getSoLuong().multiply(dth.getHangHoa().getGiaBanLe());
                    BigDecimal currentNo = cn.getSoTienNo();
                    cn.setSoTienNo(currentNo.subtract(returnVal));
                    if (cn.getSoTienNo().compareTo(cn.getSoTienDaTt()) <= 0) {
                        cn.setTrangThai("DA_THANH_TOAN");
                    }
                    congNoRepository.save(cn);
                }
            }

            // Cộng trả lại kho (khi hoàn trả thì tăng số lượng lên)
            if (dth.getDonHang() != null) {
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

                if (targetKho != null) {
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
        }

        return ResponseEntity.ok("Phê duyệt yêu cầu đổi trả thành công!");
    }

    @PostMapping("/returns/reject")
    public ResponseEntity<?> rejectReturn(@RequestBody Map<String, String> body) {
        String maDoiTra = body.get("maDoiTra");
        String note = body.get("note");

        DoiTraHang dth = doiTraHangRepository.findByMaDoiTra(maDoiTra);
        if (dth == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy yêu cầu đổi trả.");
        }

        dth.setTrangThai("TU_CHOI");
        dth.setNgayXuLy(LocalDateTime.now());
        dth.setGhiChuXuLy(note != null ? note : "Từ chối yêu cầu đổi trả.");
        doiTraHangRepository.save(dth);

        return ResponseEntity.ok("Đã từ chối yêu cầu đổi trả.");
    }

    @GetMapping("/contracts")
    public ResponseEntity<?> getAllContracts() {
        List<HopDong> contracts = hopDongRepository.findAll();
        List<Map<String, Object>> response = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (HopDong hd : contracts) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", hd.getId());
            map.put("maHopDong", hd.getMaHopDong());
            map.put("maDonHang", hd.getDonHang() != null ? hd.getDonHang().getMaDonHang() : "Không có");
            map.put("khachHangTen", hd.getKhachHang() != null && hd.getKhachHang().getNguoiDung() != null ? 
                    hd.getKhachHang().getNguoiDung().getHoTen() : "");
            map.put("ngayKy", hd.getNgayKy() != null ? hd.getNgayKy().format(dateFormatter) : "");
            map.put("ngayHieuLuc", hd.getNgayHieuLuc() != null ? hd.getNgayHieuLuc().format(dateFormatter) : "");
            map.put("giaTri", hd.getGiaTri());
            map.put("tienDatCoc", hd.getTienDatCoc() != null ? hd.getTienDatCoc() : BigDecimal.ZERO);
            map.put("trangThai", hd.getTrangThai());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/approved-no-contract")
    public ResponseEntity<?> getApprovedOrdersNoContract() {
        // Lấy tất cả đơn hàng đã duyệt
        List<DonHang> approvedOrders = donHangRepository.findAll().stream()
                .filter(dh -> "DA_XAC_NHAN".equals(dh.getTrangThai()))
                .toList();
        
        List<Map<String, Object>> response = new ArrayList<>();
        for (DonHang dh : approvedOrders) {
            // Kiểm tra xem đơn hàng đã có hợp đồng chưa
            HopDong hd = hopDongRepository.findByDonHangId(dh.getId());
            if (hd == null) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", dh.getId());
                map.put("maDonHang", dh.getMaDonHang());
                map.put("khachHangId", dh.getKhachHang().getId());
                map.put("khachHangTen", dh.getKhachHang().getNguoiDung().getHoTen());
                map.put("tongTien", dh.getTongTien());
                response.add(map);
            }
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/contracts/create")
    public ResponseEntity<?> createContract(@RequestBody Map<String, Object> body) {
        Long orderId = Long.valueOf(body.get("orderId").toString());
        String ngayKyStr = (String) body.get("ngayKy");
        String ngayHieuLucStr = (String) body.get("ngayHieuLuc");
        BigDecimal chietKhauPercent = new BigDecimal(body.get("chietKhau").toString());
        BigDecimal inputTienCoc = new BigDecimal(body.get("tienDatCoc").toString());
        String dieuKhoan = (String) body.get("dieuKhoan");
        String username = (String) body.get("username"); // salesperson username

        DonHang dh = donHangRepository.findById(orderId).orElse(null);
        if (dh == null) {
            return ResponseEntity.badRequest().body("Đơn hàng liên kết không tồn tại.");
        }

        NguoiDung nvLap = nguoiDungService.findByUsername(username);

        HopDong hd = new HopDong();
        hd.setMaHopDong("HD-" + System.currentTimeMillis());
        hd.setDonHang(dh);
        hd.setKhachHang(dh.getKhachHang());
        hd.setNvLap(nvLap);
        hd.setNgayKy(LocalDate.parse(ngayKyStr));
        hd.setNgayHieuLuc(LocalDate.parse(ngayHieuLucStr));
        
        // Giá trị hợp đồng tính toán bao gồm chiết khấu
        BigDecimal discountFactor = BigDecimal.ONE.subtract(chietKhauPercent.divide(new BigDecimal("100")));
        BigDecimal contractValue = dh.getTongTien().multiply(discountFactor);
        hd.setGiaTri(contractValue);
        
        hd.setTienDatCoc(inputTienCoc);
        hd.setDieuKhoanTt(dieuKhoan);
        hd.setTrangThai("NHAP"); // Initial status draft

        hopDongRepository.save(hd);
        return ResponseEntity.ok("Soạn thảo hợp đồng thành công với mã: " + hd.getMaHopDong());
    }

    @PostMapping("/contracts/activate")
    public ResponseEntity<?> activateContract(@RequestBody Map<String, String> body) {
        String maHopDong = body.get("maHopDong");
        HopDong hd = hopDongRepository.findByMaHopDong(maHopDong);
        if (hd == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy hợp đồng.");
        }
        hd.setTrangThai("HIEU_LUC");
        hopDongRepository.save(hd);
        return ResponseEntity.ok("Kích hoạt hợp đồng thành công!");
    }

    @PostMapping("/contracts/cancel")
    public ResponseEntity<?> cancelContract(@RequestBody Map<String, String> body) {
        String maHopDong = body.get("maHopDong");
        HopDong hd = hopDongRepository.findByMaHopDong(maHopDong);
        if (hd == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy hợp đồng.");
        }
        hd.setTrangThai("DA_HUY");
        hopDongRepository.save(hd);
        return ResponseEntity.ok("Hủy hợp đồng thành công!");
    }
}
