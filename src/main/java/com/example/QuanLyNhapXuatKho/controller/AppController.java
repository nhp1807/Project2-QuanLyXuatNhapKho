package com.example.QuanLyNhapXuatKho.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

    //-------------------------------Đăng nhập và đăng ký---------------------------------------

    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model){
        model.addAttribute("taikhoan", new TaiKhoan());

        return "register";
    }

    @PostMapping("/process_register")
    public String processRegister(TaiKhoan taiKhoan){
        taiKhoan.setHoTen(taiKhoan.getHoTen());
        taiKhoan.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());
        taiKhoan.setSoDienThoai(taiKhoan.getSoDienThoai());
        taiKhoan.setMatKhau(taiKhoan.getMatKhau());
        taiKhoan.setRole(Role.KHACHHANG);

        taiKhoanService.saveTaiKhoan(taiKhoan);

        return "redirect:/login";
    }

    //----------------------------------Admin----------------------------------------
    @GetMapping("/admin/trang-chu")
    public String showTrangChuAdmin(){
        return "ad_trang_chu";
    }

    @GetMapping("/admin/danh-sach-tai-khoan")
    public String showDanhSachTaiKhoan(Model model){
        List<TaiKhoan> listTaiKhoan = taiKhoanService.getAllTaiKhoan();
        model.addAttribute("listTaiKhoan", listTaiKhoan);

        return "ad_dstk";
    }

    @GetMapping("/admin/thong-tin-ca-nhan")
    public String showThongTinCaNhanAdmin(){
        return "ad_thong_tin";
    }

    @GetMapping("/admin/doi-mat-khau")
    public String showDoiMatKhauAdmin(){
        return "ad_doi_mat_khau";
    }

    @PostMapping("/admin/doi-mat-khau")
    public String updateMatKhauAdmin(){
        return "TODO: update password";
    }

    @GetMapping("/admin/danh-sach-tai-khoan/xoa/{maTaiKhoan}")
    public String deleteTaiKhoan(@PathVariable Long maTaiKhoan){
        taiKhoanService.deleteTaiKhoan(maTaiKhoan);

        return "redirect:/admin/danh-sach-tai-khoan";
    }

    @GetMapping("/admin/danh-sach-tai-khoan/chinh-sua/{maTaiKhoan}")
    public String showEditThongTinTaiKhoanAdmin(@PathVariable Long maTaiKhoan, Model model){
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(maTaiKhoan));

        return "ad_chinh_sua_thong_tin";
    }

    @PostMapping("/admin/danh-sach-tai-khoan/{maTaiKhoan}")
    public String updateTaiKhoanAdmin(@PathVariable Long maTaiKhoan, @ModelAttribute("taikhoan") TaiKhoan taiKhoan, Model model){
        TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(maTaiKhoan);

        existingTaiKhoan.setMaTaiKhoan(taiKhoan.getMaTaiKhoan());
        existingTaiKhoan.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());
        existingTaiKhoan.setHoTen(taiKhoan.getHoTen());
        existingTaiKhoan.setCccd(taiKhoan.getCccd());
        existingTaiKhoan.setQueQuan(taiKhoan.getQueQuan());
        existingTaiKhoan.setSoDienThoai(taiKhoan.getSoDienThoai());
        existingTaiKhoan.setNgaySinh(taiKhoan.getNgaySinh());
        existingTaiKhoan.setChucVu(taiKhoan.getChucVu());
        existingTaiKhoan.setMatKhau(taiKhoan.getMatKhau());

        taiKhoanService.updateTaiKhoan(existingTaiKhoan);

        return "redirect:/admin/danh-sach-tai-khoan";
    }
}
