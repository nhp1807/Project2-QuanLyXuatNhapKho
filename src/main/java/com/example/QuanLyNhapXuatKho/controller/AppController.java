package com.example.QuanLyNhapXuatKho.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    //-------------------------------Đăng nhập và đăng ký---------------------------------------

    /*
     * Hiển thị giao diện login
     */
    @GetMapping("/login")
    public String showLoginPage(){
        return "login";
    }

    /*
     * Hiển thị giao diện đăng ký
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model){
        model.addAttribute("taikhoan", new TaiKhoan());

        return "register";
    }

    /*
     * Xử lý đăng ký
     */
    @PostMapping("/process_register")
    public String processRegister(TaiKhoan taiKhoan){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        taiKhoan.setHoTen(taiKhoan.getHoTen());
        taiKhoan.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());
        taiKhoan.setSoDienThoai(taiKhoan.getSoDienThoai());
        taiKhoan.setMatKhau(passwordEncoder.encode(taiKhoan.getMatKhau()));
        taiKhoan.setChucVu("Khách hàng");
        taiKhoan.setRole(Role.KHACHHANG);

        taiKhoanService.saveTaiKhoan(taiKhoan);

        return "redirect:/login";
    }

    //----------------------------------Admin----------------------------------------
    /*
     * Hiển thị trang chủ cho admin
     */
    @GetMapping("/admin/trang-chu")
    public String showTrangChuAdmin(Principal principal, Model model){
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
    public String showDanhSachTaiKhoan(Model model){
        List<TaiKhoan> listTaiKhoan = taiKhoanService.getAllTaiKhoan();
        model.addAttribute("listTaiKhoan", listTaiKhoan);
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_dstk";
    }

    /*
     * Hiển thị thông tin cá nhân của admin
     */
    // @GetMapping("admin/thong-tin-ca-nhan/{maTaiKhoan}")
    // public String updateThongTinCaNhanAdmin(@PathVariable Long maTaiKhoan, @ModelAttribute("taikhoan") TaiKhoan taiKhoan, Model model){
    //     TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(maTaiKhoan);

    //     existingTaiKhoan.setMaTaiKhoan(maTaiKhoan);
    //     existingTaiKhoan.setHoTen(taiKhoan.getHoTen());
    //     existingTaiKhoan.setCccd(taiKhoan.getCccd());
    //     existingTaiKhoan.setSoDienThoai(taiKhoan.getSoDienThoai());
    //     existingTaiKhoan.setQueQuan(taiKhoan.getQueQuan());
    //     existingTaiKhoan.setNgaySinh(taiKhoan.getNgaySinh());
    //     existingTaiKhoan.setChucVu(taiKhoan.getChucVu());

    //     taiKhoanService.updateTaiKhoan(existingTaiKhoan);

    //     String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
    //     model.addAttribute("currentAccount", getLastName(currentName));
    //     return "redirect:/admin/danh-sach-tai-khoan";
    // }

    /*
     * Hiển thị giao diện đổi mật khẩu cho admin
     */
    @GetMapping("/admin/doi-mat-khau")
    public String showDoiMatKhauAdmin(Model model){
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(idDangNhap));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        return "ad_doi_mat_khau";
    }

    /*
     * Xử lý đổi mật khẩu
     */
    @PostMapping("/admin/doi-mat-khau")
    public String updateMatKhauAdmin(Model model, Principal principal, @ModelAttribute("taikhoan") TaiKhoan taiKhoan){
        TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(idDangNhap);

        if(!taiKhoan.getMatKhau().equals(taiKhoan.getReMatKhau())){
            return "redirect:/admin/doi-mat-khau";
        }

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
    public String deleteTaiKhoan(@PathVariable Long maTaiKhoan){
        taiKhoanService.deleteTaiKhoan(maTaiKhoan);

        return "redirect:/admin/danh-sach-tai-khoan";
    }

    /*
     * Hiển thị giao diện thêm tài khoản cho admin
     */
    @GetMapping("/admin/them-tai-khoan")
    public String showThemTaiKhoanPage(Model model){
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));
        model.addAttribute("taikhoan", new TaiKhoan());
        return "ad_them_tai_khoan";
    }

    /*
     * Xử lý thêm tài khoản
     */
    @PostMapping("/admin/them-tai-khoan")
    public String saveTaiKhoanAdmin(@Valid @ModelAttribute("taikhoan") TaiKhoan taiKhoan, Model model){
        if(taiKhoan.getChucVu().equals("Admin")){
            taiKhoan.setRole(Role.ADMIN);
        }else if(taiKhoan.getChucVu().equals("Khách hàng")){
            taiKhoan.setRole(Role.KHACHHANG);
        }else{
            taiKhoan.setRole(Role.KETOAN);
        }
        taiKhoanService.saveTaiKhoan(taiKhoan);

        return "redirect:/admin/danh-sach-tai-khoan";
    }

    /*
     * Hiển thị giao diện chỉnh sửa thông tin
     */
    @GetMapping("/admin/chinh-sua-thong-tin")
    public String showEditThongTinTaiKhoanAdmin(Model model){
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(idDangNhap));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_chinh_sua_thong_tin";
    }

    /*
     * Xử lý sửa thông tin
     */
    @PostMapping("/admin/chinh-sua-thong-tin/{maTaiKhoan}")
    public String updateTaiKhoanAdmin(@PathVariable Long maTaiKhoan, @ModelAttribute("taikhoan") TaiKhoan taiKhoan, Model model){
        TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(maTaiKhoan);

        existingTaiKhoan.setMaTaiKhoan(taiKhoan.getMaTaiKhoan());
        existingTaiKhoan.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());
        existingTaiKhoan.setHoTen(taiKhoan.getHoTen());
        existingTaiKhoan.setCccd(taiKhoan.getCccd());
        existingTaiKhoan.setQueQuan(taiKhoan.getQueQuan());
        existingTaiKhoan.setSoDienThoai(taiKhoan.getSoDienThoai());
        existingTaiKhoan.setNgaySinh(taiKhoan.getNgaySinh());
        if(taiKhoan.getChucVu().equals("Admin")){
            existingTaiKhoan.setRole(Role.ADMIN);
        }else if(taiKhoan.getChucVu().equals("Khách hàng")){
            existingTaiKhoan.setRole(Role.KHACHHANG);
        }else{
            existingTaiKhoan.setRole(Role.KETOAN);
        }

        taiKhoanService.updateTaiKhoan(existingTaiKhoan);

        return "redirect:/admin/danh-sach-tai-khoan";
    }

    /*
     * Hiển thị sửa thông tin khách hàng
     */
    @GetMapping("/admin/danh-sach-tai-khoan/{maTaiKhoan}")
    public String showEditThongTinKhachHang(@PathVariable Long maTaiKhoan, Model model){
        model.addAttribute("taikhoan", taiKhoanService.getTaiKhoan(maTaiKhoan));
        String currentName = taiKhoanService.getTaiKhoan(idDangNhap).getHoTen();
        model.addAttribute("currentAccount", getLastName(currentName));

        return "ad_chinh_sua_khach";
    }

    /*
     * Xử lý sửa thông tin khách hàng
     */
    @PostMapping("/admin/danh-sach-tai-khoan/chinh-sua/{maTaiKhoan}")
    public String editTaiKhoanKhachHang(@PathVariable Long maTaiKhoan, @ModelAttribute("taikhoan") TaiKhoan taiKhoan, Model model){
        TaiKhoan existingTaiKhoan = taiKhoanService.getTaiKhoan(maTaiKhoan);

        existingTaiKhoan.setMaTaiKhoan(taiKhoan.getMaTaiKhoan());
        existingTaiKhoan.setTenTaiKhoan(taiKhoan.getTenTaiKhoan());
        existingTaiKhoan.setHoTen(taiKhoan.getHoTen());
        existingTaiKhoan.setCccd(taiKhoan.getCccd());
        existingTaiKhoan.setQueQuan(taiKhoan.getQueQuan());
        existingTaiKhoan.setSoDienThoai(taiKhoan.getSoDienThoai());
        existingTaiKhoan.setNgaySinh(taiKhoan.getNgaySinh());
        existingTaiKhoan.setChucVu(taiKhoan.getChucVu());
        if(taiKhoan.getChucVu().equals("Admin")){
            existingTaiKhoan.setRole(Role.ADMIN);
        }else if(taiKhoan.getChucVu().equals("Khách hàng")){
            existingTaiKhoan.setRole(Role.KHACHHANG);
        }else{
            existingTaiKhoan.setRole(Role.KETOAN);
        }

        taiKhoanService.updateTaiKhoan(existingTaiKhoan);

        return "redirect:/admin/danh-sach-tai-khoan";
    }

    //-----------------------------Khách hàng-------------------------------
    @GetMapping("/khach-hang/trang-chu")
    public String showTrangChuKhachHang(){
        return "kh_trang_chu";
    }

    //------------------------------Kế toán---------------------------------



    //---------------------------Chức năng phụ------------------------------
    public String getLastName(String name){
        String[] names = name.split(" ");
        return names[names.length - 1];
    }
}
