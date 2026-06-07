package com.example.ht_vlxd.Controller;

import com.example.ht_vlxd.Model.DanhMuc;
import com.example.ht_vlxd.Model.HangHoa;
import com.example.ht_vlxd.Service.DanhMucService;
import com.example.ht_vlxd.Service.HangHoaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class TemplateViewController {

    private final HangHoaService hangHoaService;
    private final DanhMucService danhMucService;

    public TemplateViewController(HangHoaService hangHoaService, DanhMucService danhMucService) {
        this.hangHoaService = hangHoaService;
        this.danhMucService = danhMucService;
    }

    @GetMapping({"/", "/home"})
    public String index() {
        return "index";
    }

    @GetMapping("/san_pham")
    public String publicCatalog(Model model) {
        List<HangHoa> hangHoas = hangHoaService.getAllProducts();
        List<DanhMuc> danhMucs = danhMucService.getAll();
        model.addAttribute("hangHoas", hangHoas);
        model.addAttribute("danhMucs", danhMucs);
        return "san_pham";
    }

    @GetMapping("/san_pham/chi_tiet")
    public String productDetail(@RequestParam Long id, Model model) {
        HangHoa hangHoa = hangHoaService.findById(id);
        model.addAttribute("prod", hangHoa);
        return "san_pham_chi_tiet";
    }

    @GetMapping({"/login", "/register", "/auth"})
    public String loginRegister() {
        return "xac_thuc/dang_nhap_dang_ky";
    }

    @GetMapping("/dung_chung/thong_tin_ca_nhan")
    public String profile() {
        return "dung_chung/thong_tin_ca_nhan";
    }

    @GetMapping("/khach_hang/don_hang_cua_toi")
    public String customerOrders() {
        return "khach_hang/don_hang_cua_toi";
    }

    @GetMapping("/khach_hang/gio_hang")
    public String customerCart() {
        return "khach_hang/gio_hang";
    }

    @GetMapping("/kinh_doanh/quan_ly_don_hang")
    public String salesOrders() {
        return "kinh_doanh/quan_ly_don_hang";
    }

    @GetMapping("/kinh_doanh/quan_ly_hop_dong")
    public String salesContracts() {
        return "kinh_doanh/quan_ly_hop_dong";
    }

    @GetMapping("/kinh_doanh/danh_muc_hang_hoa")
    public String salesCatalog(Model model) {
        List<HangHoa> hangHoas = hangHoaService.getAllProducts();
        List<DanhMuc> danhMucs = danhMucService.getAll();
        model.addAttribute("hangHoas", hangHoas);
        model.addAttribute("danhMucs", danhMucs);
        return "kinh_doanh/danh_muc_hang_hoa";
    }

    @PostMapping("/kinh_doanh/danh_muc_hang_hoa/add")
    public String addProduct(
            @RequestParam String maHang,
            @RequestParam String tenHang,
            @RequestParam Long danhMucId,
            @RequestParam String donViTinh,
            @RequestParam String quyCach,
            @RequestParam Double giaBanLe,
            @RequestParam(required = false) Double giaBanSi,
            @RequestParam(required = false) String anhUrl,
            @RequestParam(value = "anhFile", required = false) MultipartFile anhFile) {

        String finalAnhUrl = anhUrl;
        if (anhFile != null && !anhFile.isEmpty()) {
            try {
                String fileName = java.util.UUID.randomUUID().toString() + "_" + anhFile.getOriginalFilename();
                String userDir = System.getProperty("user.dir");
                
                // Paths: src/main/resources/static/images/uploads and target/classes/static/images/uploads
                java.nio.file.Path srcDir = java.nio.file.Paths.get(userDir, "src", "main", "resources", "static", "images", "uploads");
                java.nio.file.Path targetDir = java.nio.file.Paths.get(userDir, "target", "classes", "static", "images", "uploads");
                
                java.nio.file.Files.createDirectories(srcDir);
                java.nio.file.Files.createDirectories(targetDir);
                
                java.nio.file.Path srcFile = srcDir.resolve(fileName);
                java.nio.file.Path targetFile = targetDir.resolve(fileName);
                
                // Copy stream to both locations
                try (java.io.InputStream in1 = anhFile.getInputStream()) {
                    java.nio.file.Files.copy(in1, srcFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                try (java.io.InputStream in2 = anhFile.getInputStream()) {
                    java.nio.file.Files.copy(in2, targetFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                
                finalAnhUrl = "/images/uploads/" + fileName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        HangHoa hangHoa = new HangHoa();
        hangHoa.setMaHang(maHang);
        hangHoa.setTenHang(tenHang);
        if (danhMucId != null) {
            DanhMuc dm = new DanhMuc();
            dm.setId(danhMucId);
            hangHoa.setDanhMuc(dm);
        }
        hangHoa.setDonViTinh(donViTinh);
        hangHoa.setQuyCach(quyCach);
        hangHoa.setGiaBanLe(BigDecimal.valueOf(giaBanLe));
        if (giaBanSi != null) {
            hangHoa.setGiaBanSi(BigDecimal.valueOf(giaBanSi));
        } else {
            hangHoa.setGiaBanSi(BigDecimal.valueOf(giaBanLe));
        }
        if (finalAnhUrl != null && !finalAnhUrl.trim().isEmpty()) {
            hangHoa.setAnhUrl(finalAnhUrl);
        } else {
            hangHoa.setAnhUrl("📦");
        }
        hangHoa.setTrangThai("KINH_DOANH");
        hangHoaService.save(hangHoa);

        return "redirect:/kinh_doanh/danh_muc_hang_hoa";
    }

    @PostMapping("/kinh_doanh/danh_muc_hang_hoa/delete")
    public String deleteProduct(@RequestParam Long id) {
        hangHoaService.deleteProduct(id);
        return "redirect:/kinh_doanh/danh_muc_hang_hoa";
    }

    @PostMapping("/kinh_doanh/danh_muc/add")
    public String addCategory(
            @RequestParam String maDanhMuc,
            @RequestParam String ten,
            @RequestParam(required = false) String moTa) {
        DanhMuc dm = new DanhMuc();
        dm.setMaDanhMuc(maDanhMuc);
        dm.setTen(ten);
        dm.setMoTa(moTa);
        danhMucService.save(dm);
        return "redirect:/kinh_doanh/danh_muc_hang_hoa";
    }

    @PostMapping("/kinh_doanh/danh_muc/delete")
    public String deleteCategory(@RequestParam Long id) {
        danhMucService.delete(id);
        return "redirect:/kinh_doanh/danh_muc_hang_hoa";
    }

    @GetMapping("/ke_toan/quan_ly_tai_chinh_va_cong_no")
    public String accountingDebt() {
        return "ke_toan/quan_ly_tai_chinh_va_cong_no";
    }

    @GetMapping("/ke_toan/bao_cao")
    public String accountingReports() {
        return "ke_toan/bao_cao";
    }

    @GetMapping("/ke_toan/quan_ly_nha_cung_cap")
    public String accountingSuppliers() {
        return "ke_toan/quan_ly_nha_cung_cap";
    }

    @GetMapping("/ban_quan_ly/dashboard_tong_quan")
    public String managementDashboard() {
        return "ban_quan_ly/dashboard_tong_quan";
    }

    @GetMapping("/ban_quan_ly/bao_cao")
    public String managementReports() {
        return "ban_quan_ly/bao_cao";
    }

    @GetMapping("/quan_ly_kho/quan_ly_kho")
    public String warehouseInventory() {
        return "quan_ly_kho/quan_ly_kho";
    }

    @GetMapping("/quan_ly_kho/giao_nhan_va_ban_giao")
    public String warehouseDelivery() {
        return "quan_ly_kho/giao_nhan_va_ban_giao";
    }

    @GetMapping("/quan_ly_kho/quan_ly_don_hang")
    public String warehouseOrders() {
        return "quan_ly_kho/quan_ly_don_hang";
    }

    @GetMapping("/quan_tri_vien/quan_ly_tai_khoan")
    public String adminAccounts() {
        return "quan_tri_vien/quan_ly_tai_khoan";
    }
}
