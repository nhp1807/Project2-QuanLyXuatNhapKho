-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Oct 23, 2023 at 04:55 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_quanlyxuatnhapkho`
--

-- --------------------------------------------------------

--
-- Table structure for table `chitietnhapkhos`
--

CREATE TABLE `chitietnhapkhos` (
  `id` bigint(20) NOT NULL,
  `don_gia` bigint(20) DEFAULT NULL,
  `ma_nhap_kho` bigint(20) DEFAULT NULL,
  `ma_san_pham` bigint(20) DEFAULT NULL,
  `so_luong` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `chitietxuatkhos`
--

CREATE TABLE `chitietxuatkhos` (
  `id` bigint(20) NOT NULL,
  `don_gia` bigint(20) DEFAULT NULL,
  `ma_san_pham` bigint(20) DEFAULT NULL,
  `ma_xuat_kho` bigint(20) DEFAULT NULL,
  `so_luong` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `nhacungcaps`
--

CREATE TABLE `nhacungcaps` (
  `ma_nha_cung_cap` bigint(20) NOT NULL,
  `dia_chi` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `so_dien_thoai` varchar(255) DEFAULT NULL,
  `ten_nha_cung_cap` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `nhapkhos`
--

CREATE TABLE `nhapkhos` (
  `ma_nhap_kho` bigint(20) NOT NULL,
  `ma_nha_cung_cap` bigint(20) DEFAULT NULL,
  `ma_nhan_vien` bigint(20) DEFAULT NULL,
  `ngay_nhap` varchar(255) DEFAULT NULL,
  `tong_so_tien` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `sanphams`
--

CREATE TABLE `sanphams` (
  `ma_san_pham` bigint(20) NOT NULL,
  `gia_nhap` bigint(20) DEFAULT NULL,
  `gia_xuat` bigint(20) DEFAULT NULL,
  `loai_san_pham` varchar(255) DEFAULT NULL,
  `so_luong_trong_kho` int(11) DEFAULT NULL,
  `ten_san_pham` varchar(255) DEFAULT NULL,
  `thong_tin_san_pham` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `taikhoans`
--

CREATE TABLE `taikhoans` (
  `ma_tai_khoan` bigint(20) NOT NULL,
  `cccd` varchar(255) DEFAULT NULL,
  `chuc_vu` varchar(255) DEFAULT NULL,
  `ho_ten` varchar(255) DEFAULT NULL,
  `luong` bigint(20) DEFAULT NULL,
  `mat_khau` varchar(255) DEFAULT NULL,
  `ngay_sinh` varchar(255) DEFAULT NULL,
  `ngay_vao_lam` varchar(255) DEFAULT NULL,
  `que_quan` varchar(255) DEFAULT NULL,
  `re_mat_khau` varchar(255) DEFAULT NULL,
  `role` smallint(6) DEFAULT NULL,
  `so_dien_thoai` varchar(255) DEFAULT NULL,
  `ten_tai_khoan` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `taikhoans`
--

INSERT INTO `taikhoans` (`ma_tai_khoan`, `cccd`, `chuc_vu`, `ho_ten`, `luong`, `mat_khau`, `ngay_sinh`, `ngay_vao_lam`, `que_quan`, `re_mat_khau`, `role`, `so_dien_thoai`, `ten_tai_khoan`) VALUES
(1, NULL, 'Admin', 'Nguyễn Hải Phong', NULL, '$2a$10$L6Y4QDDzda8MwcRSr3coAOTW1JvEN9na56Zmh0hLPnX5yU30HPHrC', NULL, NULL, NULL, '$2a$10$L6Y4QDDzda8MwcRSr3coAOTW1JvEN9na56Zmh0hLPnX5yU30HPHrC', 0, NULL, 'admin');

-- --------------------------------------------------------

--
-- Table structure for table `xuatkhos`
--

CREATE TABLE `xuatkhos` (
  `ma_xuat_kho` bigint(20) NOT NULL,
  `ma_khach_hang` bigint(20) DEFAULT NULL,
  `ma_nhan_vien` bigint(20) DEFAULT NULL,
  `ngay_nhap` varchar(255) DEFAULT NULL,
  `tong_so_tien` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chitietnhapkhos`
--
ALTER TABLE `chitietnhapkhos`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `chitietxuatkhos`
--
ALTER TABLE `chitietxuatkhos`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `nhacungcaps`
--
ALTER TABLE `nhacungcaps`
  ADD PRIMARY KEY (`ma_nha_cung_cap`);

--
-- Indexes for table `nhapkhos`
--
ALTER TABLE `nhapkhos`
  ADD PRIMARY KEY (`ma_nhap_kho`);

--
-- Indexes for table `sanphams`
--
ALTER TABLE `sanphams`
  ADD PRIMARY KEY (`ma_san_pham`);

--
-- Indexes for table `taikhoans`
--
ALTER TABLE `taikhoans`
  ADD PRIMARY KEY (`ma_tai_khoan`);

--
-- Indexes for table `xuatkhos`
--
ALTER TABLE `xuatkhos`
  ADD PRIMARY KEY (`ma_xuat_kho`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chitietnhapkhos`
--
ALTER TABLE `chitietnhapkhos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `chitietxuatkhos`
--
ALTER TABLE `chitietxuatkhos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `nhacungcaps`
--
ALTER TABLE `nhacungcaps`
  MODIFY `ma_nha_cung_cap` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `nhapkhos`
--
ALTER TABLE `nhapkhos`
  MODIFY `ma_nhap_kho` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `sanphams`
--
ALTER TABLE `sanphams`
  MODIFY `ma_san_pham` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `taikhoans`
--
ALTER TABLE `taikhoans`
  MODIFY `ma_tai_khoan` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `xuatkhos`
--
ALTER TABLE `xuatkhos`
  MODIFY `ma_xuat_kho` bigint(20) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
