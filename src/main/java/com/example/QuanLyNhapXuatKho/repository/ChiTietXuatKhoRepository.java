package com.example.QuanLyNhapXuatKho.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.QuanLyNhapXuatKho.entity.ChiTietXuatKho;

@Repository
public interface ChiTietXuatKhoRepository extends JpaRepository<ChiTietXuatKho, Long> {
    
}
