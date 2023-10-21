package com.example.QuanLyNhapXuatKho.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.QuanLyNhapXuatKho.entity.TaiKhoan;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Long> {
    @Query("SELECT tk FROM TaiKhoan tk WHERE tk.tenTaiKhoan=?1")
    public TaiKhoan findByTenTaiKhoan(String tenTaiKhoan);
}
