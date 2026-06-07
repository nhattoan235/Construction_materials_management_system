package com.example.ht_vlxd.Controller;

import com.example.ht_vlxd.Model.*;
import com.example.ht_vlxd.Repository.*;
import com.example.ht_vlxd.Service.NguoiDungService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final NguoiDungService nguoiDungService;
    private final NguoiDungRepository nguoiDungRepository;
    private final RoleRepository roleRepository;
    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "Admin@123";

    public AdminRestController(NguoiDungService nguoiDungService,
                               NguoiDungRepository nguoiDungRepository,
                               RoleRepository roleRepository,
                               KhachHangRepository khachHangRepository,
                               PasswordEncoder passwordEncoder) {
        this.nguoiDungService = nguoiDungService;
        this.nguoiDungRepository = nguoiDungRepository;
        this.roleRepository = roleRepository;
        this.khachHangRepository = khachHangRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/admin/users — Lấy danh sách toàn bộ tài khoản
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<NguoiDung> users = nguoiDungRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (NguoiDung u : users) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", u.getId());
            item.put("username", u.getUsername());
            item.put("hoTen", u.getHoTen());
            item.put("email", u.getEmail() != null ? u.getEmail() : "");
            item.put("soDienThoai", u.getSoDienThoai() != null ? u.getSoDienThoai() : "");
            item.put("diaChi", u.getDiaChi() != null ? u.getDiaChi() : "");
            item.put("role", u.getRole() != null ? u.getRole().getName() : "KHACH_HANG");
            item.put("trangThai", u.getTrangThai());
            result.add(item);
        }

        return ResponseEntity.ok(result);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/admin/summary — Thống kê tổng quan tài khoản
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        List<NguoiDung> all = nguoiDungRepository.findAll();
        long total = all.size();
        long active = all.stream().filter(u -> "HOAT_DONG".equals(u.getTrangThai())).count();
        long locked = all.stream().filter(u -> "BI_KHOA".equals(u.getTrangThai())).count();
        long pending = all.stream().filter(u -> "CHO_DUYET".equals(u.getTrangThai())).count();

        // Đếm theo từng vai trò
        Map<String, Long> byRole = new LinkedHashMap<>();
        for (NguoiDung u : all) {
            String roleName = u.getRole() != null ? u.getRole().getName() : "KHONG_RO";
            byRole.merge(roleName, 1L, Long::sum);
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("total", total);
        summary.put("active", active);
        summary.put("locked", locked);
        summary.put("pending", pending);
        summary.put("byRole", byRole);

        return ResponseEntity.ok(summary);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/admin/roles — Lấy danh sách vai trò
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/admin/users/create — Tạo tài khoản nhân viên mới
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/users/create")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> body) {
        String username = body.getOrDefault("username", "").trim();
        String hoTen = body.getOrDefault("hoTen", "").trim();
        String email = body.getOrDefault("email", "").trim();
        String soDienThoai = body.getOrDefault("soDienThoai", "").trim();
        String roleName = body.getOrDefault("role", "KHACH_HANG").trim();
        String diaChi = body.getOrDefault("diaChi", "").trim();

        if (username.isEmpty() || hoTen.isEmpty()) {
            return ResponseEntity.badRequest().body("Tên đăng nhập và họ tên không được để trống.");
        }

        // Kiểm tra tên đăng nhập trùng
        if (nguoiDungRepository.findByUsername(username) != null) {
            return ResponseEntity.badRequest().body("Tên đăng nhập '" + username + "' đã tồn tại trong hệ thống.");
        }

        // Kiểm tra email trùng (nếu có)
        if (!email.isEmpty() && nguoiDungRepository.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body("Email '" + email + "' đã được sử dụng bởi tài khoản khác.");
        }

        // Lấy vai trò
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            return ResponseEntity.badRequest().body("Vai trò '" + roleName + "' không tồn tại trong hệ thống.");
        }

        // Tạo tài khoản
        NguoiDung nd = new NguoiDung();
        nd.setUsername(username);
        nd.setHoTen(hoTen);
        nd.setEmail(email.isEmpty() ? null : email);
        nd.setSoDienThoai(soDienThoai.isEmpty() ? null : soDienThoai);
        nd.setDiaChi(diaChi.isEmpty() ? null : diaChi);
        nd.setRole(role);
        nd.setTrangThai("HOAT_DONG");
        nd.setPasswordHash(passwordEncoder.encode(DEFAULT_PASSWORD));

        NguoiDung saved = nguoiDungRepository.save(nd);

        // Nếu là KHACH_HANG, tự động tạo bản ghi khach_hang
        if ("KHACH_HANG".equals(roleName)) {
            KhachHang kh = new KhachHang();
            kh.setNguoiDung(saved);
            kh.setMaKhachHang("KH-" + String.format("%04d", saved.getId()));
            kh.setLoaiKhach("CA_NHAN");
            kh.setHanMucNo(java.math.BigDecimal.ZERO);
            khachHangRepository.save(kh);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Tạo tài khoản thành công! Mật khẩu mặc định: " + DEFAULT_PASSWORD);
        res.put("userId", saved.getId());
        res.put("username", saved.getUsername());
        return ResponseEntity.ok(res);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/admin/users/toggle-lock — Khóa / Mở khóa tài khoản
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/users/toggle-lock")
    public ResponseEntity<?> toggleLock(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Optional<NguoiDung> opt = nguoiDungRepository.findById(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy tài khoản ID: " + userId);
        }

        NguoiDung nd = opt.get();

        // Bảo vệ tài khoản QUAN_TRI_VIEN
        if (nd.getRole() != null && "QUAN_TRI_VIEN".equals(nd.getRole().getName())) {
            return ResponseEntity.badRequest().body("Không thể khóa tài khoản Quản trị viên.");
        }

        String newStatus;
        String message;
        if ("HOAT_DONG".equals(nd.getTrangThai())) {
            nd.setTrangThai("BI_KHOA");
            newStatus = "BI_KHOA";
            message = "Đã khóa tài khoản " + nd.getUsername() + ".";
        } else {
            nd.setTrangThai("HOAT_DONG");
            newStatus = "HOAT_DONG";
            message = "Đã mở khóa tài khoản " + nd.getUsername() + ".";
        }

        nguoiDungRepository.save(nd);

        Map<String, Object> res = new HashMap<>();
        res.put("message", message);
        res.put("newStatus", newStatus);
        return ResponseEntity.ok(res);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/admin/users/reset-password — Đặt lại mật khẩu về Admin@123
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/users/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Optional<NguoiDung> opt = nguoiDungRepository.findById(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy tài khoản ID: " + userId);
        }

        NguoiDung nd = opt.get();
        nd.setPasswordHash(passwordEncoder.encode(DEFAULT_PASSWORD));
        nguoiDungRepository.save(nd);

        return ResponseEntity.ok("Đã đặt lại mật khẩu của tài khoản '" + nd.getUsername() + "' về: " + DEFAULT_PASSWORD);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/admin/users/change-role — Thay đổi vai trò người dùng
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/users/change-role")
    public ResponseEntity<?> changeRole(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String newRoleName = body.get("role").toString();

        Optional<NguoiDung> opt = nguoiDungRepository.findById(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy tài khoản ID: " + userId);
        }

        Role role = roleRepository.findByName(newRoleName);
        if (role == null) {
            return ResponseEntity.badRequest().body("Vai trò '" + newRoleName + "' không tồn tại.");
        }

        NguoiDung nd = opt.get();
        String oldRole = nd.getRole() != null ? nd.getRole().getName() : "N/A";

        // Bảo vệ không cho đổi vai trò của tài khoản admin cuối
        if ("QUAN_TRI_VIEN".equals(oldRole) && !newRoleName.equals("QUAN_TRI_VIEN")) {
            long adminCount = nguoiDungRepository.findAll().stream()
                    .filter(u -> u.getRole() != null && "QUAN_TRI_VIEN".equals(u.getRole().getName()))
                    .count();
            if (adminCount <= 1) {
                return ResponseEntity.badRequest().body("Không thể đổi vai trò của Quản trị viên duy nhất còn lại trong hệ thống.");
            }
        }

        nd.setRole(role);
        nguoiDungRepository.save(nd);

        // Tự động tạo bản ghi khach_hang nếu đổi thành KHACH_HANG
        if ("KHACH_HANG".equals(newRoleName)) {
            KhachHang existing = khachHangRepository.findByNguoiDungUsername(nd.getUsername());
            if (existing == null) {
                KhachHang kh = new KhachHang();
                kh.setNguoiDung(nd);
                kh.setMaKhachHang("KH-" + String.format("%04d", nd.getId()));
                kh.setLoaiKhach("CA_NHAN");
                kh.setHanMucNo(java.math.BigDecimal.ZERO);
                khachHangRepository.save(kh);
            }
        }

        return ResponseEntity.ok("Đã thay đổi vai trò của '" + nd.getUsername() + "' từ " + oldRole + " sang " + newRoleName + ".");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/admin/users/update — Cập nhật thông tin cá nhân tài khoản
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/users/update")
    public ResponseEntity<?> updateUser(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Optional<NguoiDung> opt = nguoiDungRepository.findById(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy tài khoản ID: " + userId);
        }

        NguoiDung nd = opt.get();

        String hoTen = body.getOrDefault("hoTen", nd.getHoTen()).toString().trim();
        String email = body.getOrDefault("email", "").toString().trim();
        String soDienThoai = body.getOrDefault("soDienThoai", "").toString().trim();
        String diaChi = body.getOrDefault("diaChi", "").toString().trim();

        nd.setHoTen(hoTen.isEmpty() ? nd.getHoTen() : hoTen);
        nd.setEmail(email.isEmpty() ? nd.getEmail() : email);
        nd.setSoDienThoai(soDienThoai.isEmpty() ? nd.getSoDienThoai() : soDienThoai);
        nd.setDiaChi(diaChi.isEmpty() ? nd.getDiaChi() : diaChi);

        nguoiDungRepository.save(nd);
        return ResponseEntity.ok("Cập nhật thông tin tài khoản '" + nd.getUsername() + "' thành công.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/admin/users/delete — Xóa tài khoản (chỉ khi không có giao dịch)
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/users/delete")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Optional<NguoiDung> opt = nguoiDungRepository.findById(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy tài khoản ID: " + userId);
        }

        NguoiDung nd = opt.get();

        // Bảo vệ QUAN_TRI_VIEN
        if (nd.getRole() != null && "QUAN_TRI_VIEN".equals(nd.getRole().getName())) {
            long adminCount = nguoiDungRepository.findAll().stream()
                    .filter(u -> u.getRole() != null && "QUAN_TRI_VIEN".equals(u.getRole().getName()))
                    .count();
            if (adminCount <= 1) {
                return ResponseEntity.badRequest().body("Không thể xóa Quản trị viên duy nhất của hệ thống.");
            }
        }

        try {
            nguoiDungRepository.deleteById(userId);
            return ResponseEntity.ok("Đã xóa tài khoản '" + nd.getUsername() + "' khỏi hệ thống.");
        } catch (Exception e) {
            // Có ràng buộc khóa ngoại (có giao dịch liên quan)
            return ResponseEntity.badRequest().body("Không thể xóa tài khoản này vì có dữ liệu giao dịch liên quan. Hãy khóa tài khoản thay vì xóa.");
        }
    }
}
