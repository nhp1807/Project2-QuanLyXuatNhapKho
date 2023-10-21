package com.example.QuanLyNhapXuatKho.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.QuanLyNhapXuatKho.entity.ChiTietNhapKho;

@Repository
public interface ChiTietNhapKhoRepository extends JpaRepository<ChiTietNhapKho, Long> {
    @Query("SELECT ct FROM ChiTietNhapKho ct WHERE ct.maNhapKho=?1")
    public ChiTietNhapKho findByMaNhapKho(Long maNhapKho);
}
