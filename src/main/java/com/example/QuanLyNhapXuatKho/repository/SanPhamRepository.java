package com.example.QuanLyNhapXuatKho.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.QuanLyNhapXuatKho.entity.SanPham;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {
    
}
