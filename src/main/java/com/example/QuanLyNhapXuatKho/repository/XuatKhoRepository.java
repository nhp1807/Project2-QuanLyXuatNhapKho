package com.example.QuanLyNhapXuatKho.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.QuanLyNhapXuatKho.entity.XuatKho;

@Repository
public interface XuatKhoRepository extends JpaRepository<XuatKho, Long> {
    
}
