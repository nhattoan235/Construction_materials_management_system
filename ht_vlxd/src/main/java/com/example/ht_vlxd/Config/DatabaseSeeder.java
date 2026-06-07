package com.example.ht_vlxd.Config;

import com.example.ht_vlxd.Model.*;
import com.example.ht_vlxd.Repository.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DatabaseSeeder {

    private final RoleRepository roleRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final KhachHangRepository khachHangRepository;
    private final DanhMucRepository danhMucRepository;
    private final KhoRepository khoRepository;
    private final HangHoaRepository hangHoaRepository;
    private final TonKhoRepository tonKhoRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(RoleRepository roleRepository,
                          NguoiDungRepository nguoiDungRepository,
                          KhachHangRepository khachHangRepository,
                          DanhMucRepository danhMucRepository,
                          KhoRepository khoRepository,
                          HangHoaRepository hangHoaRepository,
                          TonKhoRepository tonKhoRepository,
                          PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.khachHangRepository = khachHangRepository;
        this.danhMucRepository = danhMucRepository;
        this.khoRepository = khoRepository;
        this.hangHoaRepository = hangHoaRepository;
        this.tonKhoRepository = tonKhoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedDatabase() {
        if (roleRepository.count() == 0) {
            seedRoles();
        }
        if (nguoiDungRepository.count() == 0) {
            seedUsers();
        } else {
            resetPasswordsToBCrypt();
        }
        if (danhMucRepository.count() == 0) {
            seedCategories();
        }
        if (khoRepository.count() == 0) {
            seedWarehouses();
        }
        if (hangHoaRepository.count() == 0) {
            seedProducts();
        }
        if (tonKhoRepository.count() == 0) {
            seedInventory();
        }
    }

    private void resetPasswordsToBCrypt() {
        String genericHash = passwordEncoder.encode("Admin@123");
        String[] usernames = {"admin", "giamdoc", "nvkd01", "nvkho01", "nvkt01", "khachhang01"};
        for (String uname : usernames) {
            NguoiDung nd = nguoiDungRepository.findByUsername(uname);
            if (nd != null) {
                nd.setPasswordHash(genericHash);
                nguoiDungRepository.save(nd);
            }
        }
    }

    private void seedRoles() {
        roleRepository.save(new Role(null, "QUAN_TRI_VIEN"));
        roleRepository.save(new Role(null, "BAN_QUAN_LY"));
        roleRepository.save(new Role(null, "NV_KINH_DOANH"));
        roleRepository.save(new Role(null, "NV_KHO"));
        roleRepository.save(new Role(null, "NV_KE_TOAN"));
        roleRepository.save(new Role(null, "NV_GIAO_NHAN"));
        roleRepository.save(new Role(null, "KHACH_HANG"));
        roleRepository.save(new Role(null, "NHA_CUNG_CAP"));
    }

    private void seedUsers() {
        Role adminRole = roleRepository.findByName("QUAN_TRI_VIEN");
        Role managerRole = roleRepository.findByName("BAN_QUAN_LY");
        Role salesRole = roleRepository.findByName("NV_KINH_DOANH");
        Role warehouseRole = roleRepository.findByName("NV_KHO");
        Role accountingRole = roleRepository.findByName("NV_KE_TOAN");
        Role customerRole = roleRepository.findByName("KHACH_HANG");

        String genericHash = passwordEncoder.encode("Admin@123");

        NguoiDung admin = new NguoiDung();
        admin.setUsername("admin");
        admin.setPasswordHash(genericHash);
        admin.setHoTen("Quản trị viên");
        admin.setEmail("admin@vlxd.com");
        admin.setSoDienThoai("0901000001");
        admin.setRole(adminRole);
        admin.setTrangThai("HOAT_DONG");
        nguoiDungRepository.save(admin);

        NguoiDung giamDoc = new NguoiDung();
        giamDoc.setUsername("giamdoc");
        giamDoc.setPasswordHash(genericHash);
        giamDoc.setHoTen("Nguyễn Văn Giám Đốc");
        giamDoc.setEmail("gd@vlxd.com");
        giamDoc.setSoDienThoai("0901000002");
        giamDoc.setRole(managerRole);
        giamDoc.setTrangThai("HOAT_DONG");
        nguoiDungRepository.save(giamDoc);

        NguoiDung nvkd = new NguoiDung();
        nvkd.setUsername("nvkd01");
        nvkd.setPasswordHash(genericHash);
        nvkd.setHoTen("Trần Thị Kinh Doanh");
        nvkd.setEmail("kd01@vlxd.com");
        nvkd.setSoDienThoai("0901000003");
        nvkd.setRole(salesRole);
        nvkd.setTrangThai("HOAT_DONG");
        nguoiDungRepository.save(nvkd);

        NguoiDung nvkho = new NguoiDung();
        nvkho.setUsername("nvkho01");
        nvkho.setPasswordHash(genericHash);
        nvkho.setHoTen("Lê Văn Kho");
        nvkho.setEmail("kho01@vlxd.com");
        nvkho.setSoDienThoai("0901000004");
        nvkho.setRole(warehouseRole);
        nvkho.setTrangThai("HOAT_DONG");
        nguoiDungRepository.save(nvkho);

        NguoiDung nvkt = new NguoiDung();
        nvkt.setUsername("nvkt01");
        nvkt.setPasswordHash(genericHash);
        nvkt.setHoTen("Phạm Thị Kế Toán");
        nvkt.setEmail("kt01@vlxd.com");
        nvkt.setSoDienThoai("0901000005");
        nvkt.setRole(accountingRole);
        nvkt.setTrangThai("HOAT_DONG");
        nguoiDungRepository.save(nvkt);

        NguoiDung khach = new NguoiDung();
        khach.setUsername("khachhang01");
        khach.setPasswordHash(genericHash);
        khach.setHoTen("Nguyễn Văn Khách");
        khach.setEmail("kh01@gmail.com");
        khach.setSoDienThoai("0901000006");
        khach.setRole(customerRole);
        khach.setTrangThai("HOAT_DONG");
        NguoiDung savedKhach = nguoiDungRepository.save(khach);

        // Seed Customer extra profile
        KhachHang khProfile = new KhachHang();
        khProfile.setNguoiDung(savedKhach);
        khProfile.setMaKhachHang("KH-0001");
        khProfile.setLoaiKhach("CA_NHAN");
        khProfile.setHanMucNo(new BigDecimal("50000000.00"));
        khachHangRepository.save(khProfile);
    }

    private void seedCategories() {
        DanhMuc dm1 = new DanhMuc();
        dm1.setMaDanhMuc("DM-001");
        dm1.setTen("Thép xây dựng");
        dm1.setMoTa("Thép hình, thép hộp, thép cây");
        danhMucRepository.save(dm1);

        DanhMuc dm2 = new DanhMuc();
        dm2.setMaDanhMuc("DM-002");
        dm2.setTen("Tôn lợp");
        dm2.setMoTa("Tôn sóng, tôn phẳng, tôn lạnh");
        danhMucRepository.save(dm2);

        DanhMuc dm3 = new DanhMuc();
        dm3.setMaDanhMuc("DM-003");
        dm3.setTen("Xi măng");
        dm3.setMoTa("Xi măng xây dựng các loại");
        danhMucRepository.save(dm3);

        DanhMuc dm4 = new DanhMuc();
        dm4.setMaDanhMuc("DM-004");
        dm4.setTen("Gạch & ngói");
        dm4.setMoTa("Gạch nung, gạch không nung, ngói đất sét nung");
        danhMucRepository.save(dm4);
    }

    private void seedWarehouses() {
        Kho k1 = new Kho();
        k1.setMaKho("KHO-001");
        k1.setTenKho("Kho chính Tân Bình");
        k1.setDiaChi("42A Cống Lỡ, P.15, Q.Tân Bình, TP.HCM");
        k1.setTrangThai("HOAT_DONG");
        khoRepository.save(k1);

        Kho k2 = new Kho();
        k2.setMaKho("KHO-002");
        k2.setTenKho("Kho phụ Bình Dương");
        k2.setDiaChi("KCN Sóng Thần, Bình Dương");
        k2.setTrangThai("HOAT_DONG");
        khoRepository.save(k2);
    }

    private void seedProducts() {
        DanhMuc dm1 = danhMucRepository.findByMaDanhMuc("DM-001");
        DanhMuc dm2 = danhMucRepository.findByMaDanhMuc("DM-002");
        DanhMuc dm3 = danhMucRepository.findByMaDanhMuc("DM-003");
        DanhMuc dm4 = danhMucRepository.findByMaDanhMuc("DM-004");

        HangHoa h1 = new HangHoa();
        h1.setMaHang("HH-001");
        h1.setTenHang("Thép cây D10");
        h1.setDanhMuc(dm1);
        h1.setDonViTinh("cây");
        h1.setQuyCach("D10, dài 11.7m");
        h1.setGiaBanLe(new BigDecimal("185000.00"));
        h1.setGiaBanSi(new BigDecimal("178000.00"));
        h1.setAnhUrl("🔩");
        h1.setTrangThai("KINH_DOANH");
        hangHoaRepository.save(h1);

        HangHoa h2 = new HangHoa();
        h2.setMaHang("HH-002");
        h2.setTenHang("Thép hộp 40x40x2");
        h2.setDanhMuc(dm1);
        h2.setDonViTinh("cây");
        h2.setQuyCach("40x40x2mm, dài 6m");
        h2.setGiaBanLe(new BigDecimal("250000.00"));
        h2.setGiaBanSi(new BigDecimal("240000.00"));
        h2.setAnhUrl("🔩");
        h2.setTrangThai("KINH_DOANH");
        hangHoaRepository.save(h2);

        HangHoa h3 = new HangHoa();
        h3.setMaHang("HH-003");
        h3.setTenHang("Tôn sóng 0.4mm");
        h3.setDanhMuc(dm2);
        h3.setDonViTinh("tấm");
        h3.setQuyCach("0.4mm, 900x2440");
        h3.setGiaBanLe(new BigDecimal("125000.00"));
        h3.setGiaBanSi(new BigDecimal("118000.00"));
        h3.setAnhUrl("🏠");
        h3.setTrangThai("KINH_DOANH");
        hangHoaRepository.save(h3);

        HangHoa h4 = new HangHoa();
        h4.setMaHang("HH-004");
        h4.setTenHang("Xi măng Hà Tiên");
        h4.setDanhMuc(dm3);
        h4.setDonViTinh("bao");
        h4.setQuyCach("Bao 50kg");
        h4.setGiaBanLe(new BigDecimal("95000.00"));
        h4.setGiaBanSi(new BigDecimal("90000.00"));
        h4.setAnhUrl("🏗️");
        h4.setTrangThai("KINH_DOANH");
        hangHoaRepository.save(h4);

        HangHoa h5 = new HangHoa();
        h5.setMaHang("HH-005");
        h5.setTenHang("Gạch nung 6 lỗ");
        h5.setDanhMuc(dm4);
        h5.setDonViTinh("viên");
        h5.setQuyCach("220x105x60mm");
        h5.setGiaBanLe(new BigDecimal("2500.00"));
        h5.setGiaBanSi(new BigDecimal("2200.00"));
        h5.setAnhUrl("🧱");
        h5.setTrangThai("KINH_DOANH");
        hangHoaRepository.save(h5);
    }

    private void seedInventory() {
        Kho k1 = khoRepository.findByMaKho("KHO-001");
        Kho k2 = khoRepository.findByMaKho("KHO-002");

        HangHoa h1 = hangHoaRepository.findByMaHang("HH-001");
        HangHoa h2 = hangHoaRepository.findByMaHang("HH-002");
        HangHoa h3 = hangHoaRepository.findByMaHang("HH-003");
        HangHoa h4 = hangHoaRepository.findByMaHang("HH-004");
        HangHoa h5 = hangHoaRepository.findByMaHang("HH-005");

        tonKhoRepository.save(new TonKho(null, h1, k1, new BigDecimal("500")));
        tonKhoRepository.save(new TonKho(null, h2, k1, new BigDecimal("200")));
        tonKhoRepository.save(new TonKho(null, h3, k1, new BigDecimal("1000")));
        tonKhoRepository.save(new TonKho(null, h4, k1, new BigDecimal("300")));
        tonKhoRepository.save(new TonKho(null, h5, k1, new BigDecimal("5000")));

        tonKhoRepository.save(new TonKho(null, h1, k2, new BigDecimal("800")));
        tonKhoRepository.save(new TonKho(null, h3, k2, new BigDecimal("600")));
    }
}
