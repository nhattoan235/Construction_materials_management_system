package com.example.ht_vlxd.Service.estimation;

import com.example.ht_vlxd.DTO.estimation.CalculationRequest;
import com.example.ht_vlxd.DTO.estimation.CalculationResponse;
import com.example.ht_vlxd.DTO.estimation.MaterialEstimateResultDTO;
import com.example.ht_vlxd.Model.estimation.DinhMucVatLieu;
import com.example.ht_vlxd.Model.product.HangHoa;
import com.example.ht_vlxd.Model.customer.KhachHang;
import com.example.ht_vlxd.Model.auth.NguoiDung;
import com.example.ht_vlxd.Model.product.TrangThaiHangHoa;
import com.example.ht_vlxd.Repository.estimation.DinhMucVatLieuRepository;
import com.example.ht_vlxd.Repository.product.HangHoaRepository;
import com.example.ht_vlxd.Repository.customer.KhachHangRepository;
import com.example.ht_vlxd.Repository.auth.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class MaterialEstimationService {

    @Autowired
    private DinhMucVatLieuRepository dinhMucRepository;

    @Autowired
    private HangHoaRepository hangHoaRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    public CalculationResponse calculateEstimates(CalculationRequest request) {
        List<DinhMucVatLieu> dinhMucs = dinhMucRepository.findByLoaiCongTrinh(request.getLoaiCongTrinh());
        List<MaterialEstimateResultDTO> chiTietList = new ArrayList<>();
        BigDecimal tongTien = BigDecimal.ZERO;
        int soDongChuaHoanTat = 0;

        // 1. Check logged in status via Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLogged = auth != null && auth.isAuthenticated() 
                && !(auth instanceof AnonymousAuthenticationToken);

        // 2. Resolve pricing category (wholesale or retail) safely to prevent IDOR
        boolean useGiaSi = false;
        if (isLogged) {
            useGiaSi = determinePriceCategory(auth, request.getKhachHangId());
        }

        // Null check for product map to avoid NPE
        Map<String, Long> sanPhamMap = request.getSanPhamLinhHoat() != null 
                ? request.getSanPhamLinhHoat() : Collections.emptyMap();

        for (DinhMucVatLieu dm : dinhMucs) {
            MaterialEstimateResultDTO result = new MaterialEstimateResultDTO();
            result.setLoaiVatLieu(dm.getLoaiVatLieu());
            result.setTenLoaiVatLieu(getFriendlyMaterialName(dm.getLoaiVatLieu()));
            result.setDonViTinh(dm.getDonViTinh());
            result.setGhiChuDinhMuc(dm.getGhiChu());

            // 3. Compute material quantity
            double khoiLuong = request.getDienTich() * dm.getHeSoM2().doubleValue();
            if (dm.getCachTinh() == DinhMucVatLieu.CachTinh.THEO_TANG) {
                khoiLuong *= request.getSoTang();
            }

            // 4. Round up (CEILING) based on rounding rules
            if (dm.getKieuLamTron() == DinhMucVatLieu.KieuLamTron.DEM) {
                khoiLuong = Math.ceil(khoiLuong);
            } else if (dm.getKieuLamTron() == DinhMucVatLieu.KieuLamTron.THE_TICH) {
                khoiLuong = Math.ceil(khoiLuong * 10) / 10.0;
            }
            result.setKhoiLuongUocTinh(khoiLuong);

            // 5. Query prices & details only if user is logged in
            if (isLogged) {
                Long customProductId = sanPhamMap.get(dm.getLoaiVatLieu());
                if (customProductId != null) {
                    validateAndAssignProduct(result, customProductId, dm, useGiaSi);
                }

                // If no brand selected OR validation failed (produced a warning), row is incomplete
                if (customProductId == null || result.getCanhBao() != null) {
                    soDongChuaHoanTat++;
                }

                if (result.getDonGia() != null) {
                    BigDecimal thanhTien = result.getDonGia().multiply(BigDecimal.valueOf(khoiLuong));
                    result.setThanhTien(thanhTien);
                    tongTien = tongTien.add(thanhTien);
                }
            } else {
                // Guests don't get prices/brands, increment incomplete count
                soDongChuaHoanTat++;
            }
            chiTietList.add(result);
        }

        CalculationResponse response = new CalculationResponse();
        response.setChiTietVatLieu(chiTietList);
        response.setTongTien(isLogged ? tongTien : null);
        response.setTinhDayDu(soDongChuaHoanTat == 0);
        response.setSoDongChuaHoanTat(soDongChuaHoanTat);
        return response;
    }

    private boolean determinePriceCategory(Authentication auth, Long targetKhachHangId) {
        // Retrieve roles from security authentication token
        boolean isStaffOrAdmin = auth.getAuthorities().stream().anyMatch(a -> 
            a.getAuthority().equals("ROLE_NV_KINH_DOANH") || 
            a.getAuthority().equals("ROLE_BAN_QUAN_LY") || 
            a.getAuthority().equals("ROLE_QUAN_TRI_VIEN")
        );

        // Sales / Admins can calculate using specific client pricing if specified
        if (isStaffOrAdmin && targetKhachHangId != null) {
            return khachHangRepository.findById(targetKhachHangId)
                .map(kh -> "DOANH_NGHIEP".equalsIgnoreCase(kh.getLoaiKhach()))
                .orElse(false);
        }

        // Standard clients must default to their own category, preventing IDOR
        String username = auth.getName();
        KhachHang kh = khachHangRepository.findByNguoiDungUsername(username);
        return kh != null && "DOANH_NGHIEP".equalsIgnoreCase(kh.getLoaiKhach());
    }

    private void validateAndAssignProduct(MaterialEstimateResultDTO result, Long productId, DinhMucVatLieu dm, boolean useGiaSi) {
        Optional<HangHoa> opt = hangHoaRepository.findById(productId);
        if (opt.isEmpty() || opt.get().getTrangThai() == TrangThaiHangHoa.NGUNG_KINH_DOANH) {
            result.setCanhBao("Sản phẩm đã chọn không tồn tại hoặc đã ngừng kinh doanh");
            return;
        }
        HangHoa hh = opt.get();
        if (!isCategoryMatch(hh.getDanhMuc().getMaDanhMuc(), dm.getLoaiVatLieu())) {
            result.setCanhBao("Sản phẩm không thuộc nhóm vật liệu phù hợp");
            return;
        }
        result.setHangHoaId(hh.getId());
        result.setTenHangHoa(hh.getTenHang());
        
        // Select wholesale or retail pricing
        BigDecimal price = (useGiaSi && hh.getGiaBanSi() != null) ? hh.getGiaBanSi() : hh.getGiaBanLe();
        result.setDonGia(price);
    }

    public List<Map<String, Object>> getProductsForCategory(String loaiVatLieu, Long targetKhachHangId) {
        String maDanhMuc = "";
        if ("XI_MANG".equals(loaiVatLieu)) maDanhMuc = "DM-003";
        else if ("THEP".equals(loaiVatLieu)) maDanhMuc = "DM-001";
        else if ("GACH".equals(loaiVatLieu)) maDanhMuc = "DM-004";
        else if ("CAT".equals(loaiVatLieu)) maDanhMuc = "DM-005";
        else if ("DA".equals(loaiVatLieu)) maDanhMuc = "DM-006";

        List<HangHoa> list = hangHoaRepository.findByDanhMucMaDanhMucAndTrangThai(maDanhMuc, TrangThaiHangHoa.KINH_DOANH);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isLogged = auth != null && auth.isAuthenticated() 
                && !(auth instanceof AnonymousAuthenticationToken);

        boolean useGiaSi = false;
        if (isLogged) {
            useGiaSi = determinePriceCategory(auth, targetKhachHangId);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (HangHoa hh : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", hh.getId());
            map.put("tenHang", hh.getTenHang());
            map.put("donViTinh", hh.getDonViTinh());
            if (isLogged) {
                BigDecimal price = (useGiaSi && hh.getGiaBanSi() != null) ? hh.getGiaBanSi() : hh.getGiaBanLe();
                map.put("donGia", price);
            } else {
                map.put("donGia", null);
            }
            result.add(map);
        }
        return result;
    }

    private boolean isCategoryMatch(String maDanhMuc, String loaiVatLieu) {
        if ("XI_MANG".equals(loaiVatLieu)) return "DM-003".equals(maDanhMuc);
        if ("THEP".equals(loaiVatLieu)) return "DM-001".equals(maDanhMuc);
        if ("GACH".equals(loaiVatLieu)) return "DM-004".equals(maDanhMuc);
        // Generics for Sand (Cát) & Gravel (Đá) if categories exist
        if ("CAT".equals(loaiVatLieu)) return maDanhMuc != null && maDanhMuc.contains("CAT") || "DM-005".equals(maDanhMuc);
        if ("DA".equals(loaiVatLieu)) return maDanhMuc != null && maDanhMuc.contains("DA") || "DM-006".equals(maDanhMuc);
        return false;
    }

    private String getFriendlyMaterialName(String loai) {
        if (loai == null) return "";
        switch (loai) {
            case "XI_MANG": return "Xi măng";
            case "CAT": return "Cát";
            case "DA": return "Đá";
            case "THEP": return "Thép";
            case "GACH": return "Gạch";
            default: return loai;
        }
    }
}
