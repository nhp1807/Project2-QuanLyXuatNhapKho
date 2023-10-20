package com.example.QuanLyNhapXuatKho.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.QuanLyNhapXuatKho.entity.ChiTietNhapKho;

@Repository
public interface ChiTietNhapKhoRepository extends JpaRepository<ChiTietNhapKho, Long> {
    
}
