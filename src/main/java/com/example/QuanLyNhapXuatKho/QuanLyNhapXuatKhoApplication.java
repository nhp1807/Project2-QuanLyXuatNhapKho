package com.example.QuanLyNhapXuatKho;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.QuanLyNhapXuatKho.entity.Role;
import com.example.QuanLyNhapXuatKho.entity.TaiKhoan;
import com.example.QuanLyNhapXuatKho.service.TaiKhoanService;

@SpringBootApplication
public class QuanLyNhapXuatKhoApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuanLyNhapXuatKhoApplication.class, args);
		// BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		// TaiKhoan admin = new TaiKhoan();
		// admin.setChucVu("Admin");
		// admin.setHoTen("Nguyễn Hải Phong");
		// admin.setTenTaiKhoan("admin");
		// admin.setMatKhau(passwordEncoder.encode("admin"));
		// admin.setReMatKhau(admin.getMatKhau());
		// admin.setRole(Role.ADMIN);
	}

}
