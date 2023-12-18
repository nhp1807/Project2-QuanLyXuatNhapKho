package com.example.QuanLyNhapXuatKho.repository;

import com.example.QuanLyNhapXuatKho.entity.DatHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatHangRepository extends JpaRepository<DatHang, Long> {
}
