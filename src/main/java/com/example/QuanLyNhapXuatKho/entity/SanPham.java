package com.example.QuanLyNhapXuatKho.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "sanphams")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maSanPham;
    @Column
    private String tenSanPham;
    @Column
    private String loaiSanPham;
    @Column
    private String thongTinSanPham;
    @Column
    private Long giaNhap;
    @Column
    private Long giaXuat;
    @Column
    private int soLuongTrongKho;

    public SanPham(String tenSanPham, String loaiSanPham, String thongTinSanPham, Long giaNhap, Long giaXuat,
            int soLuongTrongKho) {
        this.tenSanPham = tenSanPham;
        this.loaiSanPham = loaiSanPham;
        this.thongTinSanPham = thongTinSanPham;
        this.giaNhap = giaNhap;
        this.giaXuat = giaXuat;
        this.soLuongTrongKho = soLuongTrongKho;
    }

    
}
