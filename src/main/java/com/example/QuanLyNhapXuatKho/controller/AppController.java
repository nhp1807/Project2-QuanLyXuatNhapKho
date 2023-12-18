package com.example.QuanLyNhapXuatKho.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.example.QuanLyNhapXuatKho.entity.*;
import com.example.QuanLyNhapXuatKho.repository.*;
import com.example.QuanLyNhapXuatKho.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
public class AppController {
    @Autowired
    private ChiTietNhapKhoRepository chiTietNhapKhoRepository;
    @Autowired
    private ChiTietNhapKhoService chiTietNhapKhoService;
    @Autowired
    private NhapKhoRepository nhapKhoRepository;
    @Autowired
    private NhapKhoService nhapKhoService;
    @Autowired
    private ChiTietXuatKhoRepository chiTietXuatKhoRepository;
    @Autowired
    private ChiTietXuatKhoService chiTietXuatKhoService;
    @Autowired
    private XuatKhoRepository xuatKhoRepository;
    @Autowired
    private XuatKhoService xuatKhoService;
    @Autowired
    private NhaCungCapRepository nhaCungCapRepository;
    @Autowired
    private NhaCungCapService nhaCungCapService;
    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Autowired
    private SanPhamService sanPhamService;
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    @Autowired
    private TaiKhoanService taiKhoanService;
    @Autowired
    private DatHangRepository datHangRepository;
    @Autowired
    private DatHangService datHangService;

    public Long idDangNhap, idSanPhamSelect;
    public boolean errPassword = false;
    public boolean errRegister = false;
    public Long idNhapKho, idXuatKho;

    // ---------Đăng nhập và đăng ký-----------------

    /*
     * Hiển thị giao diện login
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /*
     * Hiển thị giao diện đăng ký
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (errPassword == true) {
            model.addAttribute("errRegister", "Tên đăng nhập đã tồn tại");
        }
        model.addAttribute("taikhoan", new TaiKhoan());

        return "register";
    }

    /*
     * Xử lý đăng ký
     */
    @PostMapping("/process_register")
    public String processRegister(TaiKhoan taiKhoan) {
        if (taiKhoanRepository.existsByTenTaiKhoan(taiKhoan.getTenTaiKhoan())) {
            errRegister = true;
            return "redirect:/register";
        }

        errRegister = false;
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        taiKhoan.setHoTen(taiKhoan.getHoTen());
        taiKhoan.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());
        taiKhoan.setSoDienThoai(taiKhoan.getSoDienThoai());
        taiKhoan.setMatKhau(passwordEncoder.encode(taiKhoan.getMatKhau()));
        taiKhoan.setDiemThuong(0);
        taiKhoan.setChucVu("Khách hàng");
        taiKhoan.setRole(Role.KHACHHANG);

        taiKhoanService.saveTaiKhoan(taiKhoan);

        return "redirect:/login";
    }

    // ----------------------------------Admin----------------------------------------
    /*
     * Hiển thị trang chủ cho admin
     */
    @GetMapping("/admin/trang-chu")
    public String showTrangChuAdmin(Principal principal, Model model) {
        String tenDangNhap = principal.getName();
        idDangNhap = taiKhoanRepository.findByTenTaiKhoan(tenDangNhap).getMaTaiKhoan();
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();

        model.addAttribute("currentAccount", getLastName(currentName));
        return "ad_trang_chu";
    }

    /*
     * Hiển thị danh sách tài khoản cho admin
     */
    @GetMapping("/admin/danh-sach-tai-khoan")
    public String showDanhSachTaiKhoanAdmin(Model model, @Param("keyword") String keyword) {
        List<TaiKhoan> listTaiKhoan;
        if (keyword != null) {
            listTaiKhoan = taiKhoanRepository.findByHoTenContaining(keyword);
        } else {
            listTaiKhoan = taiKhoanService.getAllTaiKhoan();
        }
        model.addAttribute("listTaiKhoan", listTaiKhoan);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_dstk";
    }

    /*
     * Hiển thị giao diện đổi mật khẩu cho admin
     */
    @GetMapping("/admin/doi-mat-khau")
    public String showDoiMatKhauAdmin(Model model) {
        if (errPassword == true) {
            model.addAttribute("errPassword", "Mật khẩu không khớp");
        }
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(idDangNhap));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "ad_doi_mat_khau";
    }

    /*
     * Xử lý đổi mật khẩu
     */
    @PostMapping("/admin/doi-mat-khau")
    public String updateMatKhauAdmin(Model model, Principal principal, @ModelAttribute("taikhoan") TaiKhoan taiKhoan) {
        TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(idDangNhap);

        if (!taiKhoan.getMatKhau().equals(taiKhoan.getReMatKhau())) {
            errPassword = true;
            return "redirect:/admin/doi-mat-khau";
        }

        errPassword = false;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        existingTaiKhoan.setMatKhau(passwordEncoder.encode(taiKhoan.getMatKhau()));
        existingTaiKhoan.setReMatKhau(existingTaiKhoan.getMatKhau());

        taiKhoanService.updateTaiKhoan(existingTaiKhoan);

        return "redirect:/admin/danh-sach-tai-khoan";
    }

    /*
     * Xóa tài khoản cho admin
     */
    @GetMapping("/admin/danh-sach-tai-khoan/xoa/{maTaiKhoan}")
    public String deleteTaiKhoanAdmin(@PathVariable Long maTaiKhoan) {
        taiKhoanService.deleteTaiKhoan(maTaiKhoan);

        return "redirect:/admin/danh-sach-tai-khoan";
    }

    /*
     * Hiển thị giao diện thêm tài khoản cho admin
     */
    @GetMapping("/admin/them-tai-khoan")
    public String showAddTaiKhoanAdmin(Model model) {
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        model.addAttribute("taikhoan", new TaiKhoan());
        return "ad_them_tai_khoan";
    }

    /*
     * Xử lý thêm tài khoản
     */
    @PostMapping("/admin/them-tai-khoan")
    public String addTaiKhoanAdmin(@Valid @ModelAttribute("taikhoan") TaiKhoan taiKhoan) {
        if (taiKhoan.getChucVu().equals("Admin")) {
            taiKhoan.setRole(Role.ADMIN);
        } else if (taiKhoan.getChucVu().equals("Khách hàng")) {
            taiKhoan.setRole(Role.KHACHHANG);
        } else {
            taiKhoan.setRole(Role.KETOAN);
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        taiKhoan.setMatKhau(passwordEncoder.encode(taiKhoan.getMatKhau()));
        taiKhoan.setReMatKhau(taiKhoan.getMatKhau());
        taiKhoanService.saveTaiKhoan(taiKhoan);

        return "redirect:/admin/danh-sach-tai-khoan";
    }

    /*
     * Hiển thị giao diện chỉnh sửa thông tin
     */
    @GetMapping("/admin/chinh-sua-thong-tin")
    public String showUpdateTaiKhoanAdmin(Model model) {
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(idDangNhap));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_chinh_sua_thong_tin";
    }

    /*
     * Xử lý sửa thông tin
     */
    @PostMapping("/admin/chinh-sua-thong-tin/{maTaiKhoan}")
    public String updateTaiKhoanAdmin(@PathVariable Long maTaiKhoan, @ModelAttribute("taikhoan") TaiKhoan taiKhoan,
                                      Model model) {
        TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(maTaiKhoan);

        existingTaiKhoan.setMaTaiKhoan(taiKhoan.getMaTaiKhoan());
        existingTaiKhoan.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());
        existingTaiKhoan.setHoTen(taiKhoan.getHoTen());
        existingTaiKhoan.setCccd(taiKhoan.getCccd());
        existingTaiKhoan.setQueQuan(taiKhoan.getQueQuan());
        existingTaiKhoan.setSoDienThoai(taiKhoan.getSoDienThoai());
        existingTaiKhoan.setNgaySinh(taiKhoan.getNgaySinh());
        if (taiKhoan.getChucVu().equals("Admin")) {
            existingTaiKhoan.setRole(Role.ADMIN);
        } else if (taiKhoan.getChucVu().equals("Khách hàng")) {
            existingTaiKhoan.setRole(Role.KHACHHANG);
        } else {
            existingTaiKhoan.setRole(Role.KETOAN);
        }

        taiKhoanService.updateTaiKhoan(existingTaiKhoan);

        return "redirect:/admin/danh-sach-tai-khoan";
    }

    /*
     * Hiển thị sửa thông tin khách hàng
     */
    @GetMapping("/admin/danh-sach-tai-khoan/{maTaiKhoan}")
    public String showUpdateTaiKhoanKhachHangAdmin(@PathVariable Long maTaiKhoan, Model model) {
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(maTaiKhoan));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_chinh_sua_khach";
    }

    /*
     * Xử lý sửa thông tin khách hàng
     */
    @PostMapping("/admin/danh-sach-tai-khoan/chinh-sua/{maTaiKhoan}")
    public String updateTaiKhoanKhachHangAdmin(@PathVariable Long maTaiKhoan,
                                               @ModelAttribute("taikhoan") TaiKhoan taiKhoan,
                                               Model model) {
        TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(maTaiKhoan);

        existingTaiKhoan.setMaTaiKhoan(taiKhoan.getMaTaiKhoan());
        existingTaiKhoan.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());
        existingTaiKhoan.setHoTen(taiKhoan.getHoTen());
        existingTaiKhoan.setCccd(taiKhoan.getCccd());
        existingTaiKhoan.setQueQuan(taiKhoan.getQueQuan());
        existingTaiKhoan.setSoDienThoai(taiKhoan.getSoDienThoai());
        existingTaiKhoan.setNgaySinh(taiKhoan.getNgaySinh());
        existingTaiKhoan.setChucVu(taiKhoan.getChucVu());
        if (taiKhoan.getChucVu().equals("Admin")) {
            existingTaiKhoan.setRole(Role.ADMIN);
        } else if (taiKhoan.getChucVu().equals("Khách hàng")) {
            existingTaiKhoan.setRole(Role.KHACHHANG);
        } else {
            existingTaiKhoan.setRole(Role.KETOAN);
        }

        taiKhoanService.updateTaiKhoan(existingTaiKhoan);

        return "redirect:/admin/danh-sach-tai-khoan";
    }

    /*
     * Hiển thị danh sách các nhà cung cấp cho admin
     */
    @GetMapping("/admin/danh-sach-nha-cung-cap")
    public String showDanhSachNhaCungCapAdmin(Model model) {
        model.addAttribute("listNhaCungCap", nhaCungCapService.getAllNhaCungCap());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "ad_nha_cung_cap";
    }

    /*
     * Hiển thị giao diện thêm nhà cung cấp
     */
    @GetMapping("/admin/them-nha-cung-cap")
    public String showAddNhaCungCapAdmin(Model model) {
        model.addAttribute("nhacungcap", new NhaCungCap());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "ad_them_nha_cung_cap";
    }

    /*
     * Xử lý thêm nhà cung cấp
     */
    @PostMapping("/admin/them-nha-cung-cap")
    public String addNhaCungCapAdmin(@ModelAttribute("nhacungcap") NhaCungCap nhaCungCap) {
        nhaCungCapService.saveNhaCungCap(nhaCungCap);
        return "redirect:/admin/danh-sach-nha-cung-cap";
    }

    /*
     * Xóa nhà cung cấp
     */
    @GetMapping("/admin/danh-sach-nha-cung-cap/xoa/{maNhaCungCap}")
    public String deleteNhaCungCapAdmin(@PathVariable Long maNhaCungCap) {
        nhaCungCapService.deleteNhaCungCap(maNhaCungCap);
        return "redirect:/admin/danh-sach-nha-cung-cap";
    }

    /*
     * Hiển thị sửa nhà cung cấp cho admin
     */
    @GetMapping("/admin/danh-sach-nha-cung-cap/{maNhaCungCap}")
    public String showUpdateNhaCungCapAdmin(Model model, @PathVariable Long maNhaCungCap) {
        model.addAttribute("nhacungcap", nhaCungCapService.getNhaCungCap(maNhaCungCap));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "ad_sua_nha_cung_cap";
    }

    /*
     * Xử lý chính chỉnh sửa nhà cung cấp
     */
    @PostMapping("/admin/danh-sach-nha-cung-cap/chinh-sua/{maNhaCungCap}")
    public String updateNhaCungCapAdmin(@PathVariable Long maNhaCungCap,
                                        @ModelAttribute("nhacungcap") NhaCungCap nhaCungCap) {
        NhaCungCap existingNhaCungCap = nhaCungCapService.getNhaCungCap(maNhaCungCap);
        existingNhaCungCap.setMaNhaCungCap(nhaCungCap.getMaNhaCungCap());
        existingNhaCungCap.setTenNhaCungCap(nhaCungCap.getTenNhaCungCap());
        existingNhaCungCap.setDiaChi(nhaCungCap.getDiaChi());
        existingNhaCungCap.setEmail(nhaCungCap.getEmail());
        existingNhaCungCap.setSoDienThoai(nhaCungCap.getSoDienThoai());

        nhaCungCapService.updateNhaCungCap(existingNhaCungCap);

        return "redirect:/admin/danh-sach-nha-cung-cap";
    }

    /*
     * Hiển thị danh sách sản phẩm cho admin
     */
    @GetMapping("/admin/danh-sach-san-pham")
    public String showDanhSachSanPhamAdmin(Model model, @Param("keyword") String keyword) {
        List<SanPham> listSanPham;
        if (keyword != null) {
            listSanPham = sanPhamRepository.findByTenSanPhamContaining(keyword);
        } else {
            listSanPham = sanPhamService.getAllSanPham();
        }

        Collections.sort(listSanPham, Comparator.comparing(SanPham::getTenSanPham));
        model.addAttribute("listSanPham", listSanPham);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_danh_sach_sp";
    }

    /*
     * Hiển thị thêm sản phẩm
     */
    @GetMapping("/admin/them-san-pham")
    public String showAddSanPhamAdmin(Model model) {
        model.addAttribute("sanpham", new SanPham());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "ad_them_sp";
    }

    /*
     * Xử lý thêm sản phẩm
     */
    @PostMapping("/admin/them-san-pham")
    public String addSanPhamAdmin(@ModelAttribute("sanpham") SanPham sanPham) {
        sanPham.setThongTinSanPham("");
        sanPham.setGiaNhap(0L);
        sanPham.setGiaXuat(0L);
        sanPham.setSoLuongTrongKho(0);
        sanPham.setSoLuong(0);
        sanPhamService.saveSanPham(sanPham);

        return "redirect:/admin/danh-sach-san-pham";
    }

    /*
     * Xử lý xóa sản phẩm
     */
    @GetMapping("/admin/danh-sach-san-pham/xoa/{maSanPham}")
    public String deleteSanPhamAdmin(@PathVariable Long maSanPham) {
        sanPhamService.deleteSanPham(maSanPham);

        return "redirect:/admin/danh-sach-san-pham";
    }

    /*
     * Hiển thị chỉnh sửa sản phẩm
     */
    @GetMapping("/admin/danh-sach-san-pham/{maSanPham}")
    public String showUpdateSanPhamAdmin(Model model, @PathVariable Long maSanPham) {
        model.addAttribute("sanpham", sanPhamService.getSanPham(maSanPham));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_chinh_sua_sp";
    }

    /*
     * Xử lý chỉnh sửa sản phẩm
     */
    @PostMapping("/admin/danh-sach-san-pham/chinh-sua/{maSanPham}")
    public String updateSanPhamAdmin(@PathVariable Long maSanPham, @ModelAttribute("sanpham") SanPham sanPham) {
        SanPham existingSanPham = sanPhamService.getSanPham(maSanPham);
        existingSanPham.setMaSanPham(maSanPham);
        existingSanPham.setTenSanPham(sanPham.getTenSanPham());
        existingSanPham.setLoaiSanPham(sanPham.getLoaiSanPham());
        existingSanPham.setThongTinSanPham(sanPham.getThongTinSanPham());
        existingSanPham.setGiaNhap(sanPham.getGiaNhap());
        existingSanPham.setGiaXuat(sanPham.getGiaXuat());

        sanPhamService.updateSanPham(existingSanPham);

        return "redirect:/admin/danh-sach-san-pham";
    }

    @GetMapping("/admin/danh-sach-nhap-kho")
    public String showDanhSachNhapKhoAdmin(Model model) {
        List<NhapKho> listNhapKho = nhapKhoService.getAllNhapKho();
        model.addAttribute("listNhapKho", listNhapKho);
//        model.addAttribute("lítNhaCungCap", nhaCungCapService.getAllNhaCungCap());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "ad_danh_sach_nhap_kho";
    }

    @GetMapping("/admin/them-nhap-kho")
    public String showAddNhapKho(Model model) {
        model.addAttribute("nhapkho", new NhapKho());
        model.addAttribute("listNhaCungCap", nhaCungCapService.getAllNhaCungCap());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_them_nhap_kho";
    }

    @PostMapping("/admin/them-nhap-kho")
    public String addNhapKho(@ModelAttribute("nhapkho") NhapKho nhapKho, @RequestParam("selectedObject") Long maNhaCungCap) {
        nhapKho.setMaNhanVien(idDangNhap);
        nhapKho.setMaNhaCungCap(maNhaCungCap);
        nhapKho.setMaNhaCungCap(nhapKho.getMaNhaCungCap());
        nhapKho.setNgayNhap(nhapKho.getNgayNhap());
        nhapKho.setTongSoTien(0L);

        nhapKhoService.saveNhapKho(nhapKho);

        return "redirect:/admin/danh-sach-nhap-kho";
    }

    @GetMapping("/admin/danh-sach-nhap-kho/xoa/{maNhapKho}")
    public String deleteNhapKho(@PathVariable Long maNhapKho) {
        TaiKhoan tkAdmin = taiKhoanService.getTaiKhoan(idDangNhap);
        tkAdmin.setTienXuat(tkAdmin.getTienXuat() - nhapKhoService.getNhapKho(maNhapKho).getTongSoTien());
        nhapKhoService.deleteNhapKho(maNhapKho);
        List<ChiTietNhapKho> chiTietNhapKhos = chiTietNhapKhoRepository.findByMaNhapKho(maNhapKho);
        for (ChiTietNhapKho c : chiTietNhapKhos) {
            SanPham sp = sanPhamService.getSanPham(c.getMaSanPham());
            sp.setSoLuongTrongKho(sp.getSoLuongTrongKho() - c.getSoLuong());
            sanPhamService.saveSanPham(sp);
            chiTietNhapKhoRepository.delete(c);
        }

        return "redirect:/admin/danh-sach-nhap-kho";
    }

    @GetMapping("/admin/danh-sach-nhap-kho/chi-tiet/{maNhapKho}")
    public String showDetailNhapKhoAdmin(@PathVariable Long maNhapKho, Model model) {
        List<ChiTietNhapKho> listChiTiet = chiTietNhapKhoRepository.findByMaNhapKho(maNhapKho);
        model.addAttribute("listChiTiet", listChiTiet);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_chi_tiet_nhap_kho";
    }

    @GetMapping("/admin/danh-sach-nhap-kho/chi-tiet/xoa/{id}")
    public String deleteChiTietNhapKhoAdmin(@PathVariable Long id) {
        ChiTietNhapKho chiTietNhapKho = chiTietNhapKhoService.getChiTietNhapKho(id);
        SanPham sanPham = sanPhamService.getSanPham(chiTietNhapKho.getMaSanPham());
        sanPham.setSoLuongTrongKho(sanPham.getSoLuongTrongKho() - chiTietNhapKho.getSoLuong());
        sanPhamService.saveSanPham(sanPham);

        NhapKho nhapKho = nhapKhoService.getNhapKho(chiTietNhapKho.getMaNhapKho());
        nhapKho.setTongSoTien(nhapKho.getTongSoTien() - chiTietNhapKho.getDonGia());
        nhapKhoService.saveNhapKho(nhapKho);

        TaiKhoan tkAdmin = taiKhoanService.getTaiKhoan(idDangNhap);
        tkAdmin.setTienXuat(tkAdmin.getTienXuat() - chiTietNhapKho.getSoLuong() * chiTietNhapKho.getDonGia());

        chiTietNhapKhoService.deleteChiTietNhapKho(id);

        return "redirect:/admin/danh-sach-nhap-kho";
    }

    @GetMapping("/admin/danh-sach-nhap-kho/them-san-pham/{maNhapKho}")
    public String showAddChiTietNhapKhoAdmin(@PathVariable Long maNhapKho, Model model) {
        idNhapKho = maNhapKho;
        List<SanPham> listSanPham = sanPhamService.getAllSanPham();
        Collections.sort(listSanPham, Comparator.comparing(SanPham::getTenSanPham));
        model.addAttribute("listSanPham", listSanPham);
        model.addAttribute("sanpham", new SanPham());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_them_chi_tiet_nhap_kho";
    }

    @PostMapping("/admin/danh-sach-nhap-kho/them-san-pham")
    public String addChiTietNhapKhoAdmin(@ModelAttribute("sanpham") SanPham sanPham, @RequestParam("selectedObject") String tenSanPham) {
        System.out.println(tenSanPham);
        SanPham existedSanPham = sanPhamRepository.ifSanPhamExisted(tenSanPham, sanPham.getHangSanPham());
        if (existedSanPham == null) {
            sanPham.setSoLuongTrongKho(sanPham.getSoLuong());
            sanPhamService.saveSanPham(sanPham);

            ChiTietNhapKho newchiTietNhapKho = new ChiTietNhapKho();
            newchiTietNhapKho.setMaNhapKho(idNhapKho);
            newchiTietNhapKho.setMaSanPham(sanPham.getMaSanPham());
            newchiTietNhapKho.setSoLuong(sanPham.getSoLuong());
            newchiTietNhapKho.setDonGia(sanPham.getGiaNhap());

            chiTietNhapKhoService.saveChiTietNhapKho(newchiTietNhapKho);
        } else {
            existedSanPham.setSoLuongTrongKho(existedSanPham.getSoLuongTrongKho() + sanPham.getSoLuong());
            // Nếu giá nhập và giá xuất lớn hơn sản phẩm đã tồn tại thì mới thay đổi
            if (sanPham.getGiaNhap() > existedSanPham.getGiaNhap()) {
                existedSanPham.setGiaNhap(sanPham.getGiaNhap());
            } else {
                existedSanPham.setGiaNhap(existedSanPham.getGiaNhap());
            }
            if (sanPham.getGiaXuat() > existedSanPham.getGiaXuat()) {
                existedSanPham.setGiaXuat(sanPham.getGiaXuat());
            } else {
                existedSanPham.setGiaXuat(existedSanPham.getGiaXuat());
            }

            sanPhamService.updateSanPham(existedSanPham);
            ChiTietNhapKho chiTietNhapKho = chiTietNhapKhoRepository.ifChiTietExistedSanPham(sanPham.getMaSanPham());
            if (chiTietNhapKho == null) {
                ChiTietNhapKho newchiTietNhapKho = new ChiTietNhapKho();
                newchiTietNhapKho.setMaNhapKho(idNhapKho);
                newchiTietNhapKho.setMaSanPham(existedSanPham.getMaSanPham());
                newchiTietNhapKho.setSoLuong(sanPham.getSoLuong());
                newchiTietNhapKho.setDonGia(sanPham.getGiaNhap());

                chiTietNhapKhoService.saveChiTietNhapKho(newchiTietNhapKho);
            } else {
                chiTietNhapKho.setSoLuong(chiTietNhapKho.getSoLuong() + sanPham.getSoLuong());
                chiTietNhapKho.setDonGia(sanPham.getGiaNhap());

                chiTietNhapKhoService.updateChiTietNhapKho(chiTietNhapKho);
            }
        }

        NhapKho nhapKho = nhapKhoService.getNhapKho(idNhapKho);
        List<ChiTietNhapKho> chiTietNhapKhos = chiTietNhapKhoRepository.findByMaNhapKho(idNhapKho);
        Long sum = 0L;
        for (ChiTietNhapKho c : chiTietNhapKhos) {
            sum += c.getDonGia() * c.getSoLuong();
        }
        nhapKho.setTongSoTien(sum);
        TaiKhoan tkAdmin = taiKhoanService.getTaiKhoan(idDangNhap);
        tkAdmin.setTienXuat(tkAdmin.getTienXuat() + sanPham.getGiaNhap() * sanPham.getSoLuong());
        taiKhoanService.saveTaiKhoan(tkAdmin);
        nhapKhoService.saveNhapKho(nhapKho);

        return "redirect:/admin/danh-sach-nhap-kho";
    }

    @GetMapping("/admin/danh-sach-xuat-kho")
    public String showDanhSachXuatKhoAdmin(Model model) {
        List<XuatKho> listXuatKho = xuatKhoService.getAllXuatKho();
        model.addAttribute("listXuatKho", listXuatKho);
//        model.addAttribute("lítNhaCungCap", nhaCungCapService.getAllNhaCungCap());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "ad_danh_sach_xuat_kho";
    }

    @GetMapping("/admin/them-xuat-kho")
    public String showAddXuatKhoAdmin(Model model) {
        model.addAttribute("xuatkho", new XuatKho());
        model.addAttribute("listNhaCungCap", nhaCungCapService.getAllNhaCungCap());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_them_xuat_kho";
    }

    @PostMapping("/admin/them-xuat-kho")
    public String addXuatKhoAdmin(@ModelAttribute("nhapkho") XuatKho xuatKho, @RequestParam("sdt") String sdt) {
        xuatKho.setMaNhanVien(idDangNhap);
        xuatKho.setMaKhachHang(taiKhoanRepository.findBySoDienThoai(sdt).getMaTaiKhoan());
        xuatKho.setNgayNhap(xuatKho.getNgayNhap());
        xuatKho.setTongSoTien(0L);

        xuatKhoService.saveXuatKho(xuatKho);

        return "redirect:/admin/danh-sach-xuat-kho";
    }

    @GetMapping("/admin/danh-sach-xuat-kho/xoa/{maXuatKho}")
    public String deleteXuatKho(@PathVariable Long maXuatKho) {
        TaiKhoan tkAdmin = taiKhoanService.getTaiKhoan(idDangNhap);
        tkAdmin.setTienNhap(tkAdmin.getTienNhap() - xuatKhoService.getXuatKho(maXuatKho).getTongSoTien());
        xuatKhoService.deleteXuatKho(maXuatKho);
        List<ChiTietXuatKho> chiTietXuatKhos = chiTietXuatKhoRepository.findByMaXuatKho(maXuatKho);
        for (ChiTietXuatKho c : chiTietXuatKhos) {
            SanPham sp = sanPhamService.getSanPham(c.getMaSanPham());
            sp.setSoLuongTrongKho(sp.getSoLuongTrongKho() + c.getSoLuong());
            sanPhamService.saveSanPham(sp);
            chiTietXuatKhoRepository.delete(c);
        }

        return "redirect:/admin/danh-sach-xuat-kho";
    }

    @GetMapping("/admin/danh-sach-xuat-kho/chi-tiet/{maXuatKho}")
    public String showDetailXuatKhoAdmin(@PathVariable Long maXuatKho, Model model) {
        List<ChiTietXuatKho> listChiTiet = chiTietXuatKhoRepository.findByMaXuatKho(maXuatKho);
        model.addAttribute("listChiTiet", listChiTiet);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_chi_tiet_xuat_kho";
    }

    @GetMapping("/admin/danh-sach-xuat-kho/chi-tiet/xoa/{id}")
    public String deleteChiTietXuatKhoAdmin(@PathVariable Long id) {
        ChiTietXuatKho chiTietXuatKho = chiTietXuatKhoService.getChiTietXuatKho(id);
        SanPham sanPham = sanPhamService.getSanPham(chiTietXuatKho.getMaSanPham());
        sanPham.setSoLuongTrongKho(sanPham.getSoLuongTrongKho() + chiTietXuatKho.getSoLuong());
        sanPhamService.saveSanPham(sanPham);

        XuatKho xuatKho = xuatKhoService.getXuatKho(chiTietXuatKho.getMaXuatKho());
        xuatKho.setTongSoTien(xuatKho.getTongSoTien() - chiTietXuatKho.getDonGia());
        xuatKhoService.saveXuatKho(xuatKho);

        TaiKhoan tkAdmin = taiKhoanService.getTaiKhoan(idDangNhap);
        tkAdmin.setTienNhap(tkAdmin.getTienNhap() + chiTietXuatKho.getSoLuong() * chiTietXuatKho.getDonGia());

        chiTietXuatKhoService.deleteChiTietXuatKho(id);

        return "redirect:/admin/danh-sach-xuat-kho";
    }

    @GetMapping("/admin/danh-sach-xuat-kho/them-san-pham/{maXuatKho}")
    public String showAddChiTietXuatKhoAdmin(@PathVariable Long maXuatKho, Model model) {
        idXuatKho = maXuatKho;
        List<SanPham> listSanPham = sanPhamService.getAllSanPham();
        List<SanPham> listSanPhamConLai = new ArrayList<>();
        for(SanPham sanPham : listSanPham){
            if(sanPham.getSoLuongTrongKho() > 0){
                listSanPhamConLai.add(sanPham);
            }
        }
        Collections.sort(listSanPham, Comparator.comparing(SanPham::getTenSanPham));
        model.addAttribute("listSanPham", listSanPhamConLai);
        model.addAttribute("sanpham", new SanPham());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        List<String> listHangDau = new ArrayList<>();
        List<String> listHangAcQuy = new ArrayList<>();
        List<String> listHangLop = new ArrayList<>();
        List<String> listHangPhuTung = new ArrayList<>();

        for (SanPham sp : listSanPham) {
            if (sp.getLoaiSanPham().equals("Lốp")) {
                if (!listHangLop.contains(sp.getHangSanPham())) {
                    listHangLop.add(sp.getHangSanPham());
                }
            } else if (sp.getLoaiSanPham().equals("Dầu")) {
                if (!listHangDau.contains(sp.getHangSanPham())) {
                    listHangDau.add(sp.getHangSanPham());
                }
            } else if (sp.getLoaiSanPham().equals("Ắc quy")) {
                if (!listHangAcQuy.contains(sp.getHangSanPham())) {
                    listHangAcQuy.add(sp.getHangSanPham());
                }
            } else if (sp.getLoaiSanPham().equals("Phụ tùng")) {
                if (!listHangPhuTung.contains(sp.getHangSanPham())) {
                    listHangPhuTung.add(sp.getHangSanPham());
                }
            }
        }

        listHangDau.forEach(sp -> System.out.println(sp));

        model.addAttribute("listLop", listHangLop);
        model.addAttribute("listDau", listHangDau);
        model.addAttribute("listAcQuy", listHangAcQuy);
        model.addAttribute("listPhuTung", listHangPhuTung);


        return "ad_them_chi_tiet_xuat_kho";
    }

    @PostMapping("/admin/danh-sach-xuat-kho/them-san-pham")
    public String addChiTietXuatKhoAdmin(@ModelAttribute("sanpham") SanPham sanPham, @RequestParam("selectedObject") String tenSanPham) {
//        System.out.println(tenSanPham);
        SanPham existedSanPham = sanPhamRepository.ifSanPhamExisted(tenSanPham, sanPham.getHangSanPham());
        existedSanPham.setSoLuongTrongKho(existedSanPham.getSoLuongTrongKho() - sanPham.getSoLuong());
        sanPhamService.updateSanPham(existedSanPham);

        ChiTietXuatKho chiTietXuatKho = chiTietXuatKhoRepository.ifChiTietExistedSanPham(sanPham.getMaSanPham());

        if (chiTietXuatKho == null) {
            ChiTietXuatKho newchiTietXuatKho = new ChiTietXuatKho();
            newchiTietXuatKho.setMaXuatKho(idXuatKho);
            newchiTietXuatKho.setMaSanPham(existedSanPham.getMaSanPham());
            newchiTietXuatKho.setSoLuong(sanPham.getSoLuong());
            newchiTietXuatKho.setDonGia(existedSanPham.getGiaXuat());
            System.out.println(newchiTietXuatKho.getDonGia());
            chiTietXuatKhoService.saveChiTietXuatKho(newchiTietXuatKho);
        } else {
            chiTietXuatKho.setSoLuong(chiTietXuatKho.getSoLuong() + sanPham.getSoLuong());
            chiTietXuatKho.setDonGia(sanPham.getGiaXuat());

            chiTietXuatKhoService.updateChiTietXuatKho(chiTietXuatKho);
        }

        XuatKho xuatKho = xuatKhoService.getXuatKho(idXuatKho);
        List<ChiTietXuatKho> chiTietXuatKhos = chiTietXuatKhoRepository.findByMaXuatKho(idXuatKho);
        Long sum = 0L;
        for (ChiTietXuatKho c : chiTietXuatKhos) {
            sum += c.getDonGia() * c.getSoLuong();
        }
        xuatKho.setTongSoTien(sum);
        TaiKhoan tkAdmin = taiKhoanService.getTaiKhoan(idDangNhap);
        tkAdmin.setTienNhap(tkAdmin.getTienNhap() + existedSanPham.getGiaXuat() * sanPham.getSoLuong());
        taiKhoanService.saveTaiKhoan(tkAdmin);
        xuatKhoService.saveXuatKho(xuatKho);

        return "redirect:/admin/danh-sach-xuat-kho";
    }

    @GetMapping("/admin/thong-ke")
    public String showThongKeAdmin(Model model) {
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "ad_thong_ke";
    }

    @GetMapping("/admin/thong-ke/xuat-kho")
    public String showThongKeXuatKhoAdmin(Model model) {
        model.addAttribute("listXuatKho", xuatKhoService.getAllXuatKho());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        // Tìm khách mua nhiều nhất
        List<XuatKho> listXuatKho = xuatKhoService.getAllXuatKho();
        Map<Long, Long> customerSpent = new HashMap<>();
        for (XuatKho xk : listXuatKho) {
            customerSpent.put(xk.getMaKhachHang(), customerSpent.getOrDefault(xk.getMaKhachHang(), 0L) + xk.getTongSoTien());
        }

        Long maxSpent = 0L;
        for (Map.Entry<Long, Long> map : customerSpent.entrySet()) {
            if (map.getValue() > maxSpent) {
                maxSpent = map.getValue();
            }
        }

        model.addAttribute("maxCustomer", maxSpent);

        // Tìm hàng bán được nhiều nhất
        int maxProductCount = 0;
        List<ChiTietXuatKho> listChiTietXuatKho = chiTietXuatKhoService.getAllChiTietXuatKho();
        Map<Long, Integer> productSellCount = new HashMap<>();
        for (ChiTietXuatKho ctxk : listChiTietXuatKho) {
            productSellCount.put(ctxk.getMaSanPham(), productSellCount.getOrDefault(ctxk.getMaSanPham(), 0) + ctxk.getSoLuong());
        }

        for (Map.Entry<Long, Integer> map : productSellCount.entrySet()) {
            if (map.getValue() > maxProductCount) {
                maxProductCount = map.getValue();
            }
        }

        model.addAttribute("maxProductCount", maxProductCount);

        // TODO: Tìm nhà cung cấp cung cấp nhiều sản phẩm nhất
        // TODO: Tìm nhân viên bán được nhiều hàng nhất

        // Tìm tiền nhập, xuất, lãi
        int numberOfHoaDonXuat = xuatKhoService.getAllXuatKho().size();
        int numberOfHoaDonNhap = nhapKhoService.getAllNhapKho().size();

        model.addAttribute("numberOfHoaDonXuat", numberOfHoaDonXuat);
        model.addAttribute("numberOfHoaDonNhap", numberOfHoaDonNhap);
        Long tongTienChi = taiKhoanService.getTaiKhoan(1L).getTienXuat();
        Long tongTienThu = taiKhoanService.getTaiKhoan(1L).getTienNhap();
        Long tongTienLai= tongTienThu - tongTienChi;

        model.addAttribute("tongTienChi", tongTienChi);
        model.addAttribute("tongTienThu", tongTienThu);
        model.addAttribute("tongTienLai", tongTienLai);

        return "ad_thong_ke_xuat_kho";
    }

    // TODO:
    @GetMapping("/admin/thong-ke/san-pham")
    public String showThongKeSanPhamAdmin(){
        return "";
    }

    @GetMapping("/admin/dat-hang")
    public String showDanhSachDatHangAdmin(Model model){
        model.addAttribute("listDatHang", datHangService.getAllDatHang());


        return "ad_danh_sach_dat_hang";
    }

    @GetMapping("/admin/dat-hang/xoa/{maDatHang}")
    public String deleteDatHangAdmin(@PathVariable Long maDatHang){
        DatHang datHang = datHangService.getDatHang(maDatHang);
        SanPham sanPham = sanPhamService.getSanPham(datHang.getMaSanPham());
        sanPham.setSoLuongTrongKho(sanPham.getSoLuongTrongKho() + datHang.getSoLuong());

        datHangService.deleteDatHang(maDatHang);

        return "redirect:/admin/dat-hang";
    }

    @GetMapping("/admin/dat-hang/thanh-toan/{maDatHang}")
    public String thanhToanDatHangAdmin(@PathVariable Long maDatHang){
        DatHang datHang = datHangService.getDatHang(maDatHang);
        SanPham sanPham = sanPhamService.getSanPham(datHang.getMaSanPham());
        XuatKho xuatKho = new XuatKho();
        xuatKho.setMaKhachHang(datHang.getMaKhachHang());
        xuatKho.setTongSoTien(datHang.getTongTien());
        xuatKho.setMaNhanVien(idDangNhap);
        xuatKho.setNgayNhap(datHang.getNgayDat());
        xuatKhoService.saveXuatKho(xuatKho);
        ChiTietXuatKho chiTietXuatKho = new ChiTietXuatKho();
        chiTietXuatKho.setDonGia(sanPham.getGiaXuat());
        chiTietXuatKho.setSoLuong(datHang.getSoLuong());
        chiTietXuatKho.setMaXuatKho(xuatKho.getMaXuatKho());
        chiTietXuatKho.setMaSanPham(datHang.getMaSanPham());
        chiTietXuatKhoService.saveChiTietXuatKho(chiTietXuatKho);

        datHangService.deleteDatHang(maDatHang);


        return "redirect:/admin/dat-hang";
    }

    // -----------------------------Khách hàng-------------------------------
    /*
     * Hiển thị trang chủ khách hàng
     */
    @GetMapping("/khach-hang/trang-chu")
    public String showTrangChuKhachHang(Principal principal, Model model) {
        String tenDangNhap = principal.getName();
        idDangNhap = taiKhoanRepository.findByTenTaiKhoan(tenDangNhap).getMaTaiKhoan();
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();

        model.addAttribute("currentAccount", getLastName(currentName));
        return "kh_trang_chu";
    }

    /*
     * Hiển thị giao diện đổi mật khẩu cho khách hàng
     */
    @GetMapping("/khach-hang/doi-mat-khau")
    public String showDoiMatKhauKH(Model model) {
        if (errPassword == true) {
            model.addAttribute("errPassword", "Mật khẩu không khớp");
        }
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(idDangNhap));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "kh_doi_mat_khau";
    }

    /*
     * Xử lý đổi mật khẩu
     */
    @PostMapping("/khach-hang/doi-mat-khau")
    public String updateMatKhauKH(Model model, Principal principal, @ModelAttribute("taikhoan") TaiKhoan taiKhoan) {
        TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(idDangNhap);

        if (!taiKhoan.getMatKhau().equals(taiKhoan.getReMatKhau())) {
            errPassword = true;
            return "redirect:/khach-hang/doi-mat-khau";
        }

        errPassword = false;
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        existingTaiKhoan.setMatKhau(passwordEncoder.encode(taiKhoan.getMatKhau()));
        existingTaiKhoan.setReMatKhau(existingTaiKhoan.getMatKhau());

        taiKhoanService.updateTaiKhoan(existingTaiKhoan);

        return "redirect:/khach-hang/trang-chu";
    }

    @GetMapping("/khach-hang/chinh-sua-thong-tin")
    public String showUpdateTaiKhoanKH(Model model) {
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(idDangNhap));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "kh_chinh_sua_thong_tin.html";
    }

    @PostMapping("/khach-hang/chinh-sua-thong-tin/{maTaiKhoan}")
    public String updateTaiKhoanKH(@PathVariable Long maTaiKhoan, @ModelAttribute("taikhoan") TaiKhoan taiKhoan,
                                   Model model) {
        TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(maTaiKhoan);

        existingTaiKhoan.setMaTaiKhoan(taiKhoan.getMaTaiKhoan());
        existingTaiKhoan.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());
        existingTaiKhoan.setHoTen(taiKhoan.getHoTen());
        existingTaiKhoan.setCccd(taiKhoan.getCccd());
        existingTaiKhoan.setQueQuan(taiKhoan.getQueQuan());
        existingTaiKhoan.setSoDienThoai(taiKhoan.getSoDienThoai());
        existingTaiKhoan.setNgaySinh(taiKhoan.getNgaySinh());

        taiKhoanService.updateTaiKhoan(existingTaiKhoan);

        return "redirect:/khach-hang/trang-chu";
    }

    @GetMapping("/khach-hang/danh-sach-san-pham")
    public String showDanhSachSanPhamKH(Model model) {
        List<SanPham> listLop = new ArrayList<>();
        List<SanPham> listDau = new ArrayList<>();
        List<SanPham> listAcQuy = new ArrayList<>();
        List<SanPham> listPhuTung = new ArrayList<>();

        for (SanPham sanPham : sanPhamService.getAllSanPham()) {
            if (sanPham.getLoaiSanPham().equals("Lốp")) {
                listLop.add(sanPham);
            } else if (sanPham.getLoaiSanPham().equals("Dầu")) {
                listDau.add(sanPham);
            } else if (sanPham.getLoaiSanPham().equals("Ắc quy")) {
                listAcQuy.add(sanPham);
            } else {
                listPhuTung.add(sanPham);
            }
        }

        model.addAttribute("listLop", listLop);
        model.addAttribute("listLop", listLop);
        model.addAttribute("listDau", listDau);
        model.addAttribute("listAcQuy", listAcQuy);
        model.addAttribute("listPhuTung", listPhuTung);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "kh_danh_sach_sp";
    }

    @GetMapping("/khach-hang/lich-su")
    public String showLichSu(Model model){
        List<XuatKho> listXuatKho = xuatKhoService.getAllXuatKho();
        List<XuatKho> listXuatKhoKH = new ArrayList<>();

        for(XuatKho xk : listXuatKho){
            if(xk.getMaKhachHang() == idDangNhap){
                listXuatKhoKH.add(xk);
            }
        }

        model.addAttribute("listXuatKho", listXuatKhoKH);
        return "kh_lich_su";
    }

    @GetMapping("/khach-hang/danh-sach-san-pham/dat-hang/{maSanPham}")
    public String showDatHangKH(Model model, @PathVariable Long maSanPham){
        idSanPhamSelect = maSanPham;
        model.addAttribute("dathang", new DatHang());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "kh_them_dat_hang";
    }

    @PostMapping("/khach-hang/dat-hang")
    public String addDatHangKH(@ModelAttribute("dathang") DatHang datHang){
        Long currentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formatedDate = format.format(new Date(currentTime));
        datHang.setNgayDat(formatedDate);
        datHang.setMaKhachHang(idDangNhap);
        datHang.setMaSanPham(idSanPhamSelect);
        datHang.setTongTien(sanPhamService.getSanPham(idSanPhamSelect).getGiaXuat() * datHang.getSoLuong());
        datHangService.saveDatHang(datHang);

        // Giảm số lượng sản phẩm đó trong kho
        SanPham sanPham = sanPhamService.getSanPham(idSanPhamSelect);
        sanPham.setSoLuongTrongKho(sanPham.getSoLuongTrongKho() - datHang.getSoLuong());
        sanPhamService.saveSanPham(sanPham);
        return "redirect:/khach-hang/danh-sach-dat-hang";
    }

    @GetMapping("/khach-hang/danh-sach-dat-hang")
    public String showDanhSachDatHangKH(Model model){
        List<DatHang> listDatHang = datHangService.getAllDatHang();
        List<DatHang> listDathangKH = new ArrayList<>();

        for (DatHang datHang : listDatHang){
            if(datHang.getMaKhachHang() == idDangNhap){
                listDathangKH.add(datHang);
            }
        }

        model.addAttribute("listDatHang", listDathangKH);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "kh_danh_sach_dat_hang";
    }

    @GetMapping("/khach-hang/danh-sach-dat-hang/xoa/{maDatHang}")
    public String deleteDatHangKH(@PathVariable Long maDatHang){
        // Tăng số lượng sản phẩm đó trong kho
        SanPham sanPham = sanPhamService.getSanPham(datHangService.getDatHang(maDatHang).getMaSanPham());
        sanPham.setSoLuongTrongKho(sanPham.getSoLuongTrongKho() + datHangService.getDatHang(maDatHang).getSoLuong());
        sanPhamService.saveSanPham(sanPham);
        datHangService.deleteDatHang(maDatHang);
        return "redirect:/khach-hang/danh-sach-dat-hang";
    }

    // ------------------------------Kế toán---------------------------------

    @GetMapping("/ke-toan/trang-chu")
    public String showTrangChuKeToan(Principal principal, Model model) {
        String tenDangNhap = principal.getName();
        idDangNhap = taiKhoanRepository.findByTenTaiKhoan(tenDangNhap).getMaTaiKhoan();
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();

        model.addAttribute("currentAccount", getLastName(currentName));
        return "kt_trang_chu";
    }

    @GetMapping("/ke-toan/danh-sach-tai-khoan")
    public String showDanhSachTaiKhoanKT(Model model) {
        List<TaiKhoan> listTaiKhoan = taiKhoanService.getAllTaiKhoan();
        List<TaiKhoan> listKhachHang = new ArrayList<>();
        for (TaiKhoan tk : listTaiKhoan) {
            if (tk.getRole() == Role.KHACHHANG) {
                listKhachHang.add(tk);
            }
        }
        model.addAttribute("listKhachHang", listKhachHang);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "kt_dstk";
    }

    @GetMapping("/ke-toan/them-tai-khoan")
    public String showAddTaiKhoanKT(Model model) {
        model.addAttribute("taikhoan", new TaiKhoan());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "kt_them_tai_khoan";
    }

    @PostMapping("/ke-toan/them-tai-khoan")
    public String addTaiKhoanKT(@Valid @ModelAttribute("taikhoan") TaiKhoan taiKhoan) {
        taiKhoan.setChucVu("Khách hàng");
        taiKhoan.setRole(Role.KHACHHANG);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        taiKhoan.setMatKhau(passwordEncoder.encode("admin"));
        taiKhoan.setReMatKhau(taiKhoan.getMatKhau());
        taiKhoanService.saveTaiKhoan(taiKhoan);

        return "redirect:/ke-toan/danh-sach-tai-khoan";
    }

    @GetMapping("/ke-toan/danh-sach-tai-khoan/xoa/{maTaiKhoan}")
    public String deleteTaiKhoanKT(@PathVariable Long maTaiKhoan) {
        taiKhoanService.deleteTaiKhoan(maTaiKhoan);

        return "redirect:/ke-toan/danh-sach-tai-khoan";
    }

    @GetMapping("/ke-toan/chinh-sua-thong-tin")
    public String showUpdateTaiKhoanKT(Model model) {
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(idDangNhap));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "kt_chinh_sua_thong_tin";
    }

    @PostMapping("/ke-toan/chinh-sua-thong-tin/{maTaiKhoan}")
    public String updateTaiKhoanKT(@PathVariable Long maTaiKhoan, @ModelAttribute("taikhoan") TaiKhoan taiKhoan,
                                   Model model) {
        TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(maTaiKhoan);

        existingTaiKhoan.setMaTaiKhoan(taiKhoan.getMaTaiKhoan());
        existingTaiKhoan.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());
        existingTaiKhoan.setHoTen(taiKhoan.getHoTen());
        existingTaiKhoan.setCccd(taiKhoan.getCccd());
        existingTaiKhoan.setQueQuan(taiKhoan.getQueQuan());
        existingTaiKhoan.setSoDienThoai(taiKhoan.getSoDienThoai());
        existingTaiKhoan.setNgaySinh(taiKhoan.getNgaySinh());
        if (taiKhoan.getChucVu().equals("Admin")) {
            existingTaiKhoan.setRole(Role.ADMIN);
        } else if (taiKhoan.getChucVu().equals("Khách hàng")) {
            existingTaiKhoan.setRole(Role.KHACHHANG);
        } else {
            existingTaiKhoan.setRole(Role.KETOAN);
        }

        taiKhoanService.updateTaiKhoan(existingTaiKhoan);

        return "redirect:/ke-toan/danh-sach-tai-khoan";
    }

    @GetMapping("/ke-toan/doi-mat-khau")
    public String showDoiMatKhauKT(Model model) {
        if (errPassword == true) {
            model.addAttribute("errPassword", "Mật khẩu không khớp");
        }
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(idDangNhap));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "kt_doi_mat_khau";
    }

    @PostMapping("/ke-toan/doi-mat-khau")
    public String updateMatKhauKT(Model model, Principal principal, @ModelAttribute("taikhoan") TaiKhoan taiKhoan) {
        TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(idDangNhap);

        if (!taiKhoan.getMatKhau().equals(taiKhoan.getReMatKhau())) {
            errPassword = true;
            return "redirect:/ke-toan/doi-mat-khau";
        }

        errPassword = false;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        existingTaiKhoan.setMatKhau(passwordEncoder.encode(taiKhoan.getMatKhau()));
        existingTaiKhoan.setReMatKhau(existingTaiKhoan.getMatKhau());

        taiKhoanService.updateTaiKhoan(existingTaiKhoan);

        return "redirect:/ke-toan/danh-sach-tai-khoan";
    }

    /*
     * Hiển thị danh sách sản phẩm cho admin
     */
    @GetMapping("/ke-toan/danh-sach-san-pham")
    public String showDanhSachSanPhamKT(Model model) {
        List<SanPham> listSanPham = sanPhamService.getAllSanPham();
        Collections.sort(listSanPham, Comparator.comparing(SanPham::getTenSanPham));
        model.addAttribute("listSanPham", listSanPham);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "kt_danh_sach_sp";
    }

    /*
     * Hiển thị thêm sản phẩm
     */
    @GetMapping("/ke-toan/them-san-pham")
    public String showAddSanPhamKT(Model model) {
        model.addAttribute("sanpham", new SanPham());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "kt_them_sp";
    }

    /*
     * Xử lý thêm sản phẩm
     */
    @PostMapping("/ke-toan/them-san-pham")
    public String addSanPhamKT(@ModelAttribute("sanpham") SanPham sanPham) {
        sanPham.setThongTinSanPham("");
        sanPham.setGiaNhap(0L);
        sanPham.setGiaXuat(0L);
        sanPham.setSoLuongTrongKho(0);
        sanPham.setSoLuong(0);
        sanPhamService.saveSanPham(sanPham);

        return "redirect:/ke-toan/danh-sach-san-pham";
    }

    /*
     * Xử lý xóa sản phẩm
     */
    @GetMapping("/ke-toan/danh-sach-san-pham/xoa/{maSanPham}")
    public String deleteSanPhamKT(@PathVariable Long maSanPham) {
        sanPhamService.deleteSanPham(maSanPham);

        return "redirect:/ke-toan/danh-sach-san-pham";
    }

    /*
     * Hiển thị chỉnh sửa sản phẩm
     */
    @GetMapping("/ke-toan/danh-sach-san-pham/{maSanPham}")
    public String showUpdateSanPhamKT(Model model, @PathVariable Long maSanPham) {
        model.addAttribute("sanpham", sanPhamService.getSanPham(maSanPham));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "kt_chinh_sua_sp";
    }

    /*
     * Xử lý chỉnh sửa sản phẩm
     */
    @PostMapping("/ke-toan/danh-sach-san-pham/chinh-sua/{maSanPham}")
    public String updateSanPhamKT(@PathVariable Long maSanPham, @ModelAttribute("sanpham") SanPham sanPham) {
        SanPham existingSanPham = sanPhamService.getSanPham(maSanPham);
        existingSanPham.setMaSanPham(maSanPham);
        existingSanPham.setTenSanPham(sanPham.getTenSanPham());
        existingSanPham.setLoaiSanPham(sanPham.getLoaiSanPham());
        existingSanPham.setThongTinSanPham(sanPham.getThongTinSanPham());
        existingSanPham.setGiaNhap(sanPham.getGiaNhap());
        existingSanPham.setGiaXuat(sanPham.getGiaXuat());

        sanPhamService.updateSanPham(existingSanPham);

        return "redirect:/ke-toan/danh-sach-san-pham";
    }

    @GetMapping("/ke-toan/danh-sach-nhap-kho")
    public String showDanhSachNhapKhoKT(Model model) {
        List<NhapKho> listNhapKho = nhapKhoService.getAllNhapKho();
        model.addAttribute("listNhapKho", listNhapKho);
//        model.addAttribute("lítNhaCungCap", nhaCungCapService.getAllNhaCungCap());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "kt_danh_sach_nhap_kho";
    }

    @GetMapping("/ke-toan/them-nhap-kho")
    public String showAddNhapKhoKT(Model model) {
        model.addAttribute("nhapkho", new NhapKho());
        model.addAttribute("listNhaCungCap", nhaCungCapService.getAllNhaCungCap());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "kt_them_nhap_kho";
    }

    @PostMapping("/ke-toan/them-nhap-kho")
    public String addNhapKhoKT(@ModelAttribute("nhapkho") NhapKho nhapKho, @RequestParam("selectedObject") Long maNhaCungCap) {
        nhapKho.setMaNhanVien(idDangNhap);
        nhapKho.setMaNhaCungCap(maNhaCungCap);
        nhapKho.setMaNhaCungCap(nhapKho.getMaNhaCungCap());
        nhapKho.setNgayNhap(nhapKho.getNgayNhap());
        nhapKho.setTongSoTien(0L);

        nhapKhoService.saveNhapKho(nhapKho);

        return "redirect:/ke-toan/danh-sach-nhap-kho";
    }

    @GetMapping("/ke-toan/danh-sach-nhap-kho/xoa/{maNhapKho}")
    public String deleteNhapKhoKT(@PathVariable Long maNhapKho) {
        TaiKhoan tkAdmin = taiKhoanService.getTaiKhoan(1L);
        tkAdmin.setTienXuat(tkAdmin.getTienXuat() - nhapKhoService.getNhapKho(maNhapKho).getTongSoTien());
        nhapKhoService.deleteNhapKho(maNhapKho);
        List<ChiTietNhapKho> chiTietNhapKhos = chiTietNhapKhoRepository.findByMaNhapKho(maNhapKho);
        for (ChiTietNhapKho c : chiTietNhapKhos) {
            SanPham sp = sanPhamService.getSanPham(c.getMaSanPham());
            sp.setSoLuongTrongKho(sp.getSoLuongTrongKho() - c.getSoLuong());
            sanPhamService.saveSanPham(sp);
            chiTietNhapKhoRepository.delete(c);
        }

        return "redirect:/ke-toan/danh-sach-nhap-kho";
    }

    @GetMapping("/ke-toan/danh-sach-nhap-kho/chi-tiet/{maNhapKho}")
    public String showDetailNhapKhoKT(@PathVariable Long maNhapKho, Model model) {
        List<ChiTietNhapKho> listChiTiet = chiTietNhapKhoRepository.findByMaNhapKho(maNhapKho);
        model.addAttribute("listChiTiet", listChiTiet);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "kt_chi_tiet_nhap_kho";
    }

    @GetMapping("/ke-toan/danh-sach-nhap-kho/chi-tiet/xoa/{id}")
    public String deleteChiTietNhapKhoKT(@PathVariable Long id) {
        ChiTietNhapKho chiTietNhapKho = chiTietNhapKhoService.getChiTietNhapKho(id);
        SanPham sanPham = sanPhamService.getSanPham(chiTietNhapKho.getMaSanPham());
        sanPham.setSoLuongTrongKho(sanPham.getSoLuongTrongKho() - chiTietNhapKho.getSoLuong());
        sanPhamService.saveSanPham(sanPham);

        NhapKho nhapKho = nhapKhoService.getNhapKho(chiTietNhapKho.getMaNhapKho());
        nhapKho.setTongSoTien(nhapKho.getTongSoTien() - chiTietNhapKho.getDonGia());
        nhapKhoService.saveNhapKho(nhapKho);

        TaiKhoan tkAdmin = taiKhoanService.getTaiKhoan(1L);
        tkAdmin.setTienXuat(tkAdmin.getTienXuat() - chiTietNhapKho.getSoLuong() * chiTietNhapKho.getDonGia());

        chiTietNhapKhoService.deleteChiTietNhapKho(id);

        return "redirect:/ke-toan/danh-sach-nhap-kho";
    }

    @GetMapping("/ke-toan/danh-sach-nhap-kho/them-san-pham/{maNhapKho}")
    public String showAddChiTietNhapKhoKT(@PathVariable Long maNhapKho, Model model) {
        idNhapKho = maNhapKho;
        List<SanPham> listSanPham = sanPhamService.getAllSanPham();
        Collections.sort(listSanPham, Comparator.comparing(SanPham::getTenSanPham));
        model.addAttribute("listSanPham", listSanPham);
        model.addAttribute("sanpham", new SanPham());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "kt_them_chi_tiet_nhap_kho";
    }

    @PostMapping("/ke-toan/danh-sach-nhap-kho/them-san-pham")
    public String addChiTietNhapKhoKT(@ModelAttribute("sanpham") SanPham sanPham, @RequestParam("selectedObject") String tenSanPham) {
        System.out.println(tenSanPham);
        SanPham existedSanPham = sanPhamRepository.ifSanPhamExisted(tenSanPham, sanPham.getHangSanPham());
        if (existedSanPham == null) {
            sanPham.setSoLuongTrongKho(sanPham.getSoLuong());
            sanPhamService.saveSanPham(sanPham);

            ChiTietNhapKho newchiTietNhapKho = new ChiTietNhapKho();
            newchiTietNhapKho.setMaNhapKho(idNhapKho);
            newchiTietNhapKho.setMaSanPham(sanPham.getMaSanPham());
            newchiTietNhapKho.setSoLuong(sanPham.getSoLuong());
            newchiTietNhapKho.setDonGia(sanPham.getGiaNhap());

            chiTietNhapKhoService.saveChiTietNhapKho(newchiTietNhapKho);
        } else {
            existedSanPham.setSoLuongTrongKho(existedSanPham.getSoLuongTrongKho() + sanPham.getSoLuong());
            // Nếu giá nhập và giá xuất lớn hơn sản phẩm đã tồn tại thì mới thay đổi
            if (sanPham.getGiaNhap() > existedSanPham.getGiaNhap()) {
                existedSanPham.setGiaNhap(sanPham.getGiaNhap());
            } else {
                existedSanPham.setGiaNhap(existedSanPham.getGiaNhap());
            }
            if (sanPham.getGiaXuat() > existedSanPham.getGiaXuat()) {
                existedSanPham.setGiaXuat(sanPham.getGiaXuat());
            } else {
                existedSanPham.setGiaXuat(existedSanPham.getGiaXuat());
            }

            sanPhamService.updateSanPham(existedSanPham);
            ChiTietNhapKho chiTietNhapKho = chiTietNhapKhoRepository.ifChiTietExistedSanPham(sanPham.getMaSanPham());
            if (chiTietNhapKho == null) {
                ChiTietNhapKho newchiTietNhapKho = new ChiTietNhapKho();
                newchiTietNhapKho.setMaNhapKho(idNhapKho);
                newchiTietNhapKho.setMaSanPham(existedSanPham.getMaSanPham());
                newchiTietNhapKho.setSoLuong(sanPham.getSoLuong());
                newchiTietNhapKho.setDonGia(sanPham.getGiaNhap());

                chiTietNhapKhoService.saveChiTietNhapKho(newchiTietNhapKho);
            } else {
                chiTietNhapKho.setSoLuong(chiTietNhapKho.getSoLuong() + sanPham.getSoLuong());
                chiTietNhapKho.setDonGia(sanPham.getGiaNhap());

                chiTietNhapKhoService.updateChiTietNhapKho(chiTietNhapKho);
            }
        }

        NhapKho nhapKho = nhapKhoService.getNhapKho(idNhapKho);
        List<ChiTietNhapKho> chiTietNhapKhos = chiTietNhapKhoRepository.findByMaNhapKho(idNhapKho);
        Long sum = 0L;
        for (ChiTietNhapKho c : chiTietNhapKhos) {
            sum += c.getDonGia() * c.getSoLuong();
        }
        nhapKho.setTongSoTien(sum);
        TaiKhoan tkAdmin = taiKhoanService.getTaiKhoan(1L);
        tkAdmin.setTienXuat(tkAdmin.getTienXuat() + sanPham.getGiaNhap() * sanPham.getSoLuong());
        taiKhoanService.saveTaiKhoan(tkAdmin);
        nhapKhoService.saveNhapKho(nhapKho);

        return "redirect:/ke-toan/danh-sach-nhap-kho";
    }

    // ---------------------------Chức năng phụ------------------------------
    public String getLastName(String name) {
        String[] names = name.split(" ");
        return names[names.length - 1];
    }
}