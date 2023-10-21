package com.example.QuanLyNhapXuatKho.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
