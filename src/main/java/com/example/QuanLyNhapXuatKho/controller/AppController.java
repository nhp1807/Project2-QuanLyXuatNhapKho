package com.example.QuanLyNhapXuatKho.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.QuanLyNhapXuatKho.entity.ChiTietNhapKho;
import com.example.QuanLyNhapXuatKho.entity.NhaCungCap;
import com.example.QuanLyNhapXuatKho.entity.NhapKho;
import com.example.QuanLyNhapXuatKho.entity.Role;
import com.example.QuanLyNhapXuatKho.entity.SanPham;
import com.example.QuanLyNhapXuatKho.entity.TaiKhoan;
import com.example.QuanLyNhapXuatKho.repository.ChiTietNhapKhoRepository;
import com.example.QuanLyNhapXuatKho.repository.ChiTietXuatKhoRepository;
import com.example.QuanLyNhapXuatKho.repository.NhaCungCapRepository;
import com.example.QuanLyNhapXuatKho.repository.NhapKhoRepository;
import com.example.QuanLyNhapXuatKho.repository.SanPhamRepository;
import com.example.QuanLyNhapXuatKho.repository.TaiKhoanRepository;
import com.example.QuanLyNhapXuatKho.repository.XuatKhoRepository;
import com.example.QuanLyNhapXuatKho.service.ChiTietNhapKhoService;
import com.example.QuanLyNhapXuatKho.service.ChiTietXuatKhoService;
import com.example.QuanLyNhapXuatKho.service.NhaCungCapService;
import com.example.QuanLyNhapXuatKho.service.NhapKhoService;
import com.example.QuanLyNhapXuatKho.service.SanPhamService;
import com.example.QuanLyNhapXuatKho.service.TaiKhoanService;
import com.example.QuanLyNhapXuatKho.service.XuatKhoService;

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

    public Long idDangNhap;
    public boolean errPassword = false;
    public boolean errRegister = false;
    public Long idNhapKho;

    // -------------------------------Đăng nhập và đăng ký---------------------------------------

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
        if(errPassword == true){
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
        if(taiKhoanRepository.existsByTenTaiKhoan(taiKhoan.getTenTaiKhoan())){
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
    public String showDanhSachTaiKhoanAdmin(Model model) {
        List<TaiKhoan> listTaiKhoan = taiKhoanService.getAllTaiKhoan();
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
        if(errPassword == true){
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
    public String showDanhSachSanPhamAdmin(Model model) {
        List<SanPham> listSanPham = sanPhamService.getAllSanPham();
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
    public String showDanhSachNhapKhoAdmin(Model model){
        List<NhapKho> listNhapKho = nhapKhoService.getAllNhapKho();
        model.addAttribute("listNhapKho", listNhapKho);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "ad_danh_sach_nhap_kho";
    }

    @GetMapping("/admin/them-nhap-kho")
    public String showAddNhapKho(Model model){
        model.addAttribute("nhapkho", new NhapKho());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        
        return "ad_them_nhap_kho";
    }

    @PostMapping("/admin/them-nhap-kho")
    public String addNhapKho(@ModelAttribute("nhapkho") NhapKho nhapKho){
        nhapKho.setMaNhanVien(idDangNhap);
        nhapKho.setMaNhaCungCap(nhapKho.getMaNhaCungCap());
        nhapKho.setNgayNhap(nhapKho.getNgayNhap());
        nhapKho.setTongSoTien(0L);

        nhapKhoService.saveNhapKho(nhapKho);

        return "redirect:/admin/danh-sach-nhap-kho";
    }

    @GetMapping("/admin/danh-sach-nhap-kho/xoa/{maNhapKho}")
    public String deleteNhapKho(@PathVariable Long maNhapKho){
        nhapKhoService.deleteNhapKho(maNhapKho);

        return "redirect:/admin/danh-sach-nhap-kho";
    }

    @GetMapping("/admin/danh-sach-nhap-kho/chi-tiet/{maNhapKho}")
    public String showDetailNhapKhoAdmin(@PathVariable Long maNhapKho, Model model){
        List<ChiTietNhapKho> listChiTiet = chiTietNhapKhoRepository.findByMaNhapKho(maNhapKho);
        model.addAttribute("listChiTiet", listChiTiet);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        
        return "ad_chi_tiet_nhap_kho";
    }

    @GetMapping("/admin/danh-sach-nhap-kho/them-san-pham/{maNhapKho}")
    public String showAddChiTietNhapKhoAdmin(@PathVariable Long maNhapKho, Model model){
        idNhapKho = maNhapKho;
        model.addAttribute("sanpham", new SanPham());
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_them_chi_tiet_nhap_kho";
    }

    @PostMapping("/admin/danh-sach-nhap-kho/them-san-pham")
    public String addSanPham(@ModelAttribute("sanpham") SanPham sanPham){
        sanPhamService.saveSanPham(sanPham);
        ChiTietNhapKho chiTietNhapKho = new ChiTietNhapKho();
        chiTietNhapKho.setMaNhapKho(idNhapKho);
        chiTietNhapKho.setMaSanPham(sanPham.getMaSanPham());

        return "redirect:/admin/danh-sach-nhap-kho";
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
        if(errPassword == true){
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
    public String showUpdateTaiKhoanKH(Model model){
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(idDangNhap));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "kh_chinh_sua_thong_tin.html";
    }

    @PostMapping("/khach-hang/chinh-sua-thong-tin/{maTaiKhoan}")
    public String updateTaiKhoanKH(@PathVariable Long maTaiKhoan, @ModelAttribute("taikhoan") TaiKhoan taiKhoan, Model model){
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
    public String showDanhSachSanPhamKH(Model model){
        List<SanPham> listLop = new ArrayList<>();
        List<SanPham> listDau = new ArrayList<>();
        List<SanPham> listAcQuy = new ArrayList<>();
        List<SanPham> listPhuTung = new ArrayList<>();


        for (SanPham sanPham : sanPhamService.getAllSanPham()) {
            if(sanPham.getLoaiSanPham().equals("Lốp")){
                listLop.add(sanPham);
            } else if(sanPham.getLoaiSanPham().equals("Dầu")){
                listDau.add(sanPham);
            } else if(sanPham.getLoaiSanPham().equals("Ắc quy")){
                listAcQuy.add(sanPham);
            } else {
                listPhuTung.add(sanPham);
            }
        }

        model.addAttribute("listLop", listLop);        model.addAttribute("listLop", listLop);
        model.addAttribute("listDau", listDau);
        model.addAttribute("listAcQuy", listAcQuy);
        model.addAttribute("listPhuTung", listPhuTung);


        return "kh_danh_sach_sp";
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
    public String showDanhSachTaiKhoanKT(Model model){
        List<TaiKhoan> listTaiKhoan = taiKhoanService.getAllTaiKhoan();
        List<TaiKhoan> listKhachHang = new ArrayList<>();
        for (TaiKhoan tk : listTaiKhoan) {
            if(tk.getRole() == Role.KHACHHANG){
                listKhachHang.add(tk);
            }
        }
        model.addAttribute("listKhachHang", listKhachHang);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        
        return "kt_dstk";
    }

    @GetMapping("/ke-toan/them-tai-khoan")
    public String showAddTaiKhoanKT(Model model){
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
    public String showUpdateTaiKhoanKT(Model model){
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(idDangNhap));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        
        return "kt_chinh_sua_thong_tin";
    }

    @PostMapping("/ke-toan/chinh-sua-thong-tin/{maTaiKhoan}")
    public String updateTaiKhoanKT(@PathVariable Long maTaiKhoan, @ModelAttribute("taikhoan") TaiKhoan taiKhoan,
            Model model){
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
        if(errPassword == true){
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

    // ---------------------------Chức năng phụ------------------------------
    public String getLastName(String name) {
        String[] names = name.split(" ");
        return names[names.length - 1];
    }

    public String generateMaTaiKhoan(String chucVu) {
        if (chucVu.equals("Admin")) {
            int max = 0;
            List<TaiKhoan> taiKhoanAdmin = listTaiKhoanByChucVu(chucVu);

            for (TaiKhoan taiKhoan : taiKhoanAdmin) {
                String ma = "AD123";
                int currentMa = Integer.parseInt(ma.substring(2));
                if (max > currentMa) {
                    max = currentMa;
                }

            }

            return "AD" + (max + 1);
        } else if (chucVu.equals("Khách hàng")) {
            int max = 0;
            List<TaiKhoan> taiKhoanAdmin = listTaiKhoanByChucVu(chucVu);

            for (TaiKhoan taiKhoan : taiKhoanAdmin) {
                String ma = "KH123";
                int currentMa = Integer.parseInt(ma.substring(2));
                if (max > currentMa) {
                    max = currentMa;
                }

            }

            return "KH" + (max + 1);
        } else {
            int max = 0;
            List<TaiKhoan> taiKhoanAdmin = listTaiKhoanByChucVu(chucVu);

            for (TaiKhoan taiKhoan : taiKhoanAdmin) {
                String ma = "KT123";
                int currentMa = Integer.parseInt(ma.substring(2));
                if (max > currentMa) {
                    max = currentMa;
                }

            }

            return "KT" + (max + 1);
        }
    }

    public List<TaiKhoan> listTaiKhoanByChucVu(String chucVu) {
        List<TaiKhoan> listTaiKhoan = new ArrayList<>();
        if (chucVu.equals("Admin")) {
            for (TaiKhoan taiKhoan : taiKhoanService.getAllTaiKhoan()) {
                if (taiKhoan.getRole() == Role.ADMIN) {
                    listTaiKhoan.add(taiKhoan);
                }
            }
        } else if (chucVu.equals("Khách hàng")) {
            for (TaiKhoan taiKhoan : taiKhoanService.getAllTaiKhoan()) {
                if (taiKhoan.getRole() == Role.KHACHHANG) {
                    listTaiKhoan.add(taiKhoan);
                }
            }
        } else {
            for (TaiKhoan taiKhoan : taiKhoanService.getAllTaiKhoan()) {
                if (taiKhoan.getRole() == Role.KETOAN) {
                    listTaiKhoan.add(taiKhoan);
                }
            }
        }
        return listTaiKhoan;
    }
}
