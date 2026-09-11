package com.example.ht_vlxd;

import com.example.ht_vlxd.DTO.estimation.CalculationRequest;
import com.example.ht_vlxd.DTO.estimation.CalculationResponse;
import com.example.ht_vlxd.Repository.estimation.DinhMucVatLieuRepository;
import com.example.ht_vlxd.Repository.product.HangHoaRepository;
import com.example.ht_vlxd.Repository.customer.KhachHangRepository;
import com.example.ht_vlxd.Service.estimation.MaterialEstimationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MaterialEstimationServiceTests {

    @InjectMocks
    private MaterialEstimationService estimationService;

    @Mock
    private DinhMucVatLieuRepository dinhMucRepository;

    @Mock
    private HangHoaRepository hangHoaRepository;

    @Mock
    private KhachHangRepository khachHangRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testGuestCalculationOmitPrices() {
        // Setup coefficients
        DinhMucVatLieu dm1 = new DinhMucVatLieu();
        dm1.setLoaiVatLieu("XI_MANG");
        dm1.setHeSoM2(new BigDecimal("1.5000"));
        dm1.setDonViTinh("bao");
        dm1.setCachTinh(DinhMucVatLieu.CachTinh.THEO_TANG);
        dm1.setKieuLamTron(DinhMucVatLieu.KieuLamTron.DEM);

        when(dinhMucRepository.findByLoaiCongTrinh("NHA_PHO_BTCT")).thenReturn(Collections.singletonList(dm1));

        CalculationRequest request = new CalculationRequest();
        request.setDienTich(100.0);
        request.setLoaiCongTrinh("NHA_PHO_BTCT");
        request.setSoTang(3);
        request.setSanPhamLinhHoat(new HashMap<>());

        // Perform guest calculation (unauthenticated context)
        CalculationResponse response = estimationService.calculateEstimates(request);

        assertNotNull(response);
        assertNull(response.getTongTien());
        assertFalse(response.getTinhDayDu());
        assertEquals(1, response.getSoDongChuaHoanTat());
        assertEquals(450.0, response.getChiTietVatLieu().get(0).getKhoiLuongUocTinh()); // 100 * 1.5 * 3 = 450
        assertNull(response.getChiTietVatLieu().get(0).getDonGia());
        assertNull(response.getChiTietVatLieu().get(0).getThanhTien());
    }

    @Test
    public void testAuthenticatedCalculationWithPricing() {
        // Setup authentication
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "khachhang01", null, AuthorityUtils.createAuthorityList("ROLE_KHACH_HANG")
        );
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Setup coefficients
        DinhMucVatLieu dm1 = new DinhMucVatLieu();
        dm1.setLoaiVatLieu("XI_MANG");
        dm1.setHeSoM2(new BigDecimal("1.5000"));
        dm1.setDonViTinh("bao");
        dm1.setCachTinh(DinhMucVatLieu.CachTinh.THEO_TANG);
        dm1.setKieuLamTron(DinhMucVatLieu.KieuLamTron.DEM);

        // Setup product
        DanhMuc cat = new DanhMuc();
        cat.setMaDanhMuc("DM-003"); // Cement Category
        
        HangHoa hh = new HangHoa();
        hh.setId(4L);
        hh.setTenHang("Xi măng Hà Tiên");
        hh.setDanhMuc(cat);
        hh.setGiaBanLe(new BigDecimal("95000.00"));
        hh.setTrangThai(TrangThaiHangHoa.KINH_DOANH);

        // Setup customer profile (retail type)
        KhachHang kh = new KhachHang();
        kh.setLoaiKhach("CA_NHAN");

        when(dinhMucRepository.findByLoaiCongTrinh("NHA_PHO_BTCT")).thenReturn(Collections.singletonList(dm1));
        when(hangHoaRepository.findById(4L)).thenReturn(Optional.of(hh));
        when(khachHangRepository.findByNguoiDungUsername("khachhang01")).thenReturn(kh);

        CalculationRequest request = new CalculationRequest();
        request.setDienTich(100.0);
        request.setLoaiCongTrinh("NHA_PHO_BTCT");
        request.setSoTang(2);
        
        Map<String, Long> brands = new HashMap<>();
        brands.put("XI_MANG", 4L);
        request.setSanPhamLinhHoat(brands);

        // Perform calculation
        CalculationResponse response = estimationService.calculateEstimates(request);

        assertNotNull(response);
        assertTrue(response.getTinhDayDu());
        assertEquals(0, response.getSoDongChuaHoanTat());
        assertEquals(0, response.getTongTien().compareTo(new BigDecimal("28500000.00"))); // 300 bao * 95000 = 28500000
        assertEquals(0, response.getChiTietVatLieu().get(0).getDonGia().compareTo(new BigDecimal("95000.00")));
    }

    @Test
    public void testRoundingRules() {
        // Rounding type DEM: Ceiling to integer
        DinhMucVatLieu dmDem = new DinhMucVatLieu();
        dmDem.setLoaiVatLieu("GACH");
        dmDem.setHeSoM2(new BigDecimal("0.7523"));
        dmDem.setCachTinh(DinhMucVatLieu.CachTinh.MOT_LAN);
        dmDem.setKieuLamTron(DinhMucVatLieu.KieuLamTron.DEM);

        // Rounding type THE_TICH: Ceiling to 1 decimal place
        DinhMucVatLieu dmTheTich = new DinhMucVatLieu();
        dmTheTich.setLoaiVatLieu("CAT");
        dmTheTich.setHeSoM2(new BigDecimal("0.0823"));
        dmTheTich.setCachTinh(DinhMucVatLieu.CachTinh.MOT_LAN);
        dmTheTich.setKieuLamTron(DinhMucVatLieu.KieuLamTron.THE_TICH);

        when(dinhMucRepository.findByLoaiCongTrinh("NHA_CAP_4")).thenReturn(Arrays.asList(dmDem, dmTheTich));

        CalculationRequest request = new CalculationRequest();
        request.setDienTich(100.0); // 100 * 0.7523 = 75.23 -> 76; 100 * 0.0823 = 8.23 -> 8.3
        request.setLoaiCongTrinh("NHA_CAP_4");
        request.setSoTang(1);

        CalculationResponse response = estimationService.calculateEstimates(request);

        assertNotNull(response);
        assertEquals(76.0, response.getChiTietVatLieu().get(0).getKhoiLuongUocTinh());
        assertEquals(8.3, response.getChiTietVatLieu().get(1).getKhoiLuongUocTinh());
    }
}
