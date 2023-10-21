package com.example.QuanLyNhapXuatKho.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.QuanLyNhapXuatKho.entity.ChiTietXuatKho;

@Repository
public interface ChiTietXuatKhoRepository extends JpaRepository<ChiTietXuatKho, Long> {
    @Query("SELECT ct FROM ChiTietXuatKho ct WHERE ct.maXuatKho=?1")
    public ChiTietXuatKho findByMaXuatKho(Long maXuatKho);
}
