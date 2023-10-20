package com.example.QuanLyNhapXuatKho.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.QuanLyNhapXuatKho.entity.NhapKho;

@Repository
public interface NhapKhoRepository extends JpaRepository<NhapKho, Long> {
    
}
