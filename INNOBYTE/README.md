# Aplikasi Mesin Kasir "Innobyte" (Desktop)

Aplikasi ini adalah sistem Point of Sale (POS) atau mesin kasir desktop yang dibangun menggunakan Java Swing. Proyek ini merupakan simulasi sistem kasir untuk sebuah restoran atau kafe, yang dikembangkan sebagai bagian dari tugas kuliah.

## Deskripsi

Aplikasi "Innobyte" dirancang untuk mengelola operasi harian sebuah restoran dengan membagi fungsionalitas berdasarkan peran pengguna. Sistem ini memiliki tiga peran utama yang saling terhubung: **Manager**, **Kasir**, dan **Dapur (Kitchen)**, yang semuanya berinteraksi dengan **database MySQL** terpusat.

## Fitur Utama

### 1. Sistem Login Berbasis Peran (Role-Based)
- Pengguna harus login untuk masuk ke dalam sistem.
- Tampilan dashboard akan disesuaikan secara otomatis berdasarkan peran pengguna yang berhasil login (Manager, Kasir, atau Dapur).

### 2. Dashboard Manager
- **Kelola Karyawan:** Fungsionalitas CRUD (Create, Read, Update, Delete) penuh untuk mengelola data karyawan dan akun login mereka.
- **Kelola Menu:** Fungsionalitas CRUD penuh untuk mengelola daftar menu beserta harganya, dilengkapi dengan fitur pencarian.
- **Laporan Penjualan:** Halaman laporan canggih yang menampilkan:
    - Ringkasan penjualan (Total Pendapatan, Jumlah Transaksi, Menu Terlaris).
    - Filter laporan berdasarkan jangka waktu (Harian, Bulanan, Semua Waktu).
    - Dua jenis tabel rincian: Ringkasan per Menu dan Riwayat Transaksi.
    - Fitur pencarian di dalam laporan.

### 3. Dashboard Kasir
- **Tampilan Point of Sale (POS):** Antarmuka utama yang dirancang untuk kecepatan, menampilkan semua menu dalam bentuk grid yang bisa diklik.
- **Keranjang Pesanan Interaktif:** Daftar pesanan *real-time* yang menampilkan item, jumlah, dan subtotal.
    - Fitur untuk menambah/mengurangi jumlah atau menghapus item langsung dari keranjang.
- **Proses Transaksi:** Alur untuk menyelesaikan pesanan, termasuk input nama customer dan konfirmasi, yang kemudian dicatat ke dalam database.
- **List Pesanan:** Halaman untuk melihat riwayat semua pesanan yang telah diproses, lengkap dengan statusnya.

### 4. Dashboard Dapur (Kitchen)
- **Papan Pesanan Masuk:** Menampilkan semua pesanan yang aktif (berstatus "Baru Masuk") dalam bentuk kartu-kartu pesanan yang mudah dibaca.
- **Manajemen Status Pesanan:** Dapur dapat mengubah status pesanan dari "Baru Masuk" menjadi "Selesai", yang akan otomatis menghilangkan pesanan dari layar dan memperbarui datanya untuk role lain.

## Teknologi yang Digunakan
- Bahasa: Java
- GUI: Java Swing
- Database: MySQL (dijalankan via XAMPP)
- Koneksi Database: JDBC (MySQL Connector/J)
- IDE: Visual Studio Code

## Struktur Proyek
Proyek ini dibangun dengan arsitektur yang memisahkan antara *view* (tampilan), *model* (data), dan *service* (logika bisnis) untuk setiap fiturnya. Struktur folder yang digunakan adalah sebagai berikut:
- `src/login/` (Kelas-kelas inti seperti Login, App, User, Role)
- `src/login/model/` (Kelas-kelas "cetakan" data seperti Karyawan, Menu, Transaksi)
- `src/login/manager/` (Semua panel dan service khusus untuk Manager)
- `src/login/kasir/` (Semua panel khusus untuk Kasir)
- `src/login/kitchen/` (Semua panel khusus untuk Dapur)

---
*Proyek ini dibuat oleh: Yosua Surianto © 2025*