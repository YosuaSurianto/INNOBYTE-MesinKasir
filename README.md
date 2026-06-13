# 🏪 INNOBYTE - Desktop Point of Sale (POS) System

## 📖 Deskripsi Proyek
INNOBYTE adalah sistem *Point of Sale* (POS) berbasis desktop yang dirancang khusus untuk memenuhi kebutuhan operasional harian restoran dan kafe modern. Dibangun menggunakan arsitektur *Role-Based Access Control* (RBAC), aplikasi ini memisahkan fungsionalitas sistem ke dalam tiga pilar utama: **Manager**, **Kasir**, dan **Dapur (Kitchen)**. Setiap divisi memiliki ruang lingkup pekerjaan spesifik yang beroperasi secara mandiri, namun tersinkronisasi secara *real-time* melalui satu *database* terpusat, memastikan alur kerja dari pemesanan oleh pelanggan hingga penyajian makanan berjalan mulus, cepat, dan transparan.

Proyek ini tidak hanya mendemonstrasikan antarmuka transaksi (POS) yang responsif, namun juga implementasi manajemen sesi pengguna, operasi CRUD (Create, Read, Update, Delete) tingkat lanjut, serta pelaporan analitik berbasis data dengan pola perancangan MVC pada Java Swing dan MySQL.

## 🚀 Pembagian Tugas (*Role Duties*) & Fitur Utama

Sistem beroperasi berdasarkan otentikasi. Setelah pengguna (karyawan) melakukan login dengan *username* dan *password*, aplikasi akan memuat kapabilitas dan antarmuka (*dashboard*) sesuai dengan jabatan mereka.

### 1. 📊 Tugas Manager (Administrator & Pengendali Bisnis)
Manager adalah peran tingkat eksekutif (*Superuser*) yang tidak melayani transaksi langsung, melainkan mengendalikan aspek operasional data dan menganalisis performa restoran.
- **Tugas Utama:**
  - **Kelola Karyawan & Hak Akses:** Bertanggung jawab melakukan perekrutan di sistem (CRUD data karyawan), menonaktifkan karyawan, dan menentukan kredensial *login* (User ID) masing-masing staf.
  - **Manajemen Inventaris/Katalog Menu:** Secara aktif menambahkan menu baru, menetapkan dan menyesuaikan harga jual, atau menghapus produk yang sudah tidak relevan dari aplikasi.
  - **Audit Keuangan & Performa:** Memantau tren penjualan untuk keperluan strategis.
- **Fitur pada Dashboard Manager:**
  - Panel **Kelola Karyawan**: Modul CRUD lengkap.
  - Panel **Kelola Menu**: Tabel manajemen data menu dengan filter angka/teks presisi.
  - Panel **Laporan Penjualan (Reporting)**:
    - *Executive Summary*: Ringkasan instan Total Pendapatan, Jumlah Transaksi, dan Menu Terlaris (*Best Seller*).
    - *Filter Periode*: Opsi penarikan data secara Harian, Bulanan, atau Semua Waktu.
    - *Riwayat Transaksi & Rincian*: Tabel rekap penjualan tiap individu menu dan jejak audit (*audit trail*) transaksi secara mendetail dengan dukungan fitur pencarian.
  - Panel **Profil**: Mengelola pengaturan akun pribadi.

### 2. 💳 Tugas Kasir (Front-End & Transaksi)
Kasir merupakan ujung tombak (*frontliner*) yang berinteraksi langsung dengan pelanggan. Tugas utama difokuskan pada kecepatan pemrosesan pesanan dan akurasi pembayaran.
- **Tugas Utama:**
  - **Memproses Pesanan:** Mengonversi permintaan pelanggan menjadi keranjang pesanan digital dengan cepat.
  - **Manajemen Modifikasi:** Melakukan penyesuaian instan jika pelanggan menambah/mengurangi pesanan atau membatalkan produk tertentu sebelum *checkout*.
  - **Penyelesaian Pembayaran:** Mencatat nama pelanggan dan menyelesaikan transaksi agar ter-input ke dalam basis data operasional.
  - **Monitoring Status Pesanan:** Memantau status pada riwayat transaksi untuk melihat pesanan mana yang sedang dalam antrean atau sudah selesai dari dapur.
- **Fitur pada Dashboard Kasir:**
  - **Point of Sale (POS) Grid:** Antarmuka interaktif menampilkan katalog dalam wujud tombol-tombol *grid* visual yang responsif terhadap interaksi klik.
  - **Interactive Order Cart:** Keranjang pesanan dengan kalkulasi *subtotal* waktu nyata dan dukungan fungsi *inline-edit* (tambah/kurangi kuantitas item).
  - **Proses Transaksi (*Checkout*):** Modul penyelesaian yang menyimpan rekap pesanan ke tabel `transaksi` dan membagi detailnya ke `detail_transaksi`.
  - Panel **Daftar Pesanan (*List Pesanan*)**: Visibilitas penuh akan riwayat faktur.
  - Panel **Profil**: Pemeliharaan kredensial keamanan kasir.

### 3. 🍳 Tugas Dapur (Kitchen)
Dapur adalah divisi produksi layar belakang (*Back-Office Production*). Staf dapur berfokus pada penyiapan makanan tanpa mengelola harga atau arus kas.
- **Tugas Utama:**
  - **Menerima & Merespons Pesanan:** Memantau papan antrean dan segera meracik hidangan (*First In, First Out*).
  - **Mengonfirmasi Ketersediaan:** Saat pesanan selesai, Dapur bertanggung jawab menekan status "Selesai" agar pramusaji/kasir dapat memanggil pelanggan.
- **Fitur pada Dashboard Dapur:**
  - **Live Kanban Board (*Papan Pesanan Masuk*):** Papan digital yang menyajikan pesanan masuk (Status "Baru Masuk") dalam wujud *Order Cards* yang bersih.
  - **Manajemen Siklus Cepat:** Transisi status hanya dengan sekali klik. Data di-*update* ke *database*, menyinkronkan status tersebut sehingga pesanan langsung lenyap dari antrean layar Dapur dan berstatus "Selesai" di layar Kasir.

## 🛠️ Tech Stack & Alat yang Digunakan (Dependencies)

Proyek ini dibangun dari dasar menggunakan fungsionalitas murni OOP tanpa memuat *framework heavyweight*, menonjolkan kemampuan Java standar yang kuat.

- **Bahasa Pemrograman Utama:** Java (Standar JDK 8+)
- **Graphical User Interface (GUI):** Java Swing & AWT (Abstract Window Toolkit)
- **Database Management System:** MySQL (Relational Database)
- **Database Connectivity/Driver:** JDBC API terintegrasi dinamis via `mysql-connector-j-9.3.0.jar`
- **Lingkungan Server Lokal:** XAMPP (Apache HTTP, MariaDB/MySQL Server)
- **Desain Tipografi Kustom:** Autour One Font (`AutourOne-Regular.ttf`)
- **Integrated Development Environment (IDE):** Visual Studio Code (VS Code) dengan *Extension Pack for Java*
- **Version Control System:** Git & GitHub

## 📂 Struktur Arsitektur Proyek

Sistem dibentuk menggunakan metodologi desain pola **MVC (Model-View-Controller/Service)** untuk memisahkan domain logika bisnis, lapisan tampilan, dan skema basis data.

```text
INNOBYTE-MesinKasir/
│
├── Database/
│   └── innobyte.sql                # Skema Database (DDL, Tabel, Primary/Foreign Keys)
│
├── INNOBYTE/                       # Root Direktori Source Code Java
│   ├── lib/
│   │   └── mysql-connector-j-9.3.0.jar  # Driver eksternal JDBC MySQL
│   ├── src/
│   │   ├── login/                  # Core System Package
│   │   │   ├── App.java                # Main entry point eksekusi program (psvm)
│   │   │   ├── AppService.java         # Router & Scene Manager untuk penyesuaian Panel
│   │   │   ├── AuthenticationService.java # Lapisan logika validasi dan otorisasi
│   │   │   ├── DatabaseConnection.java # Konfigurasi Singleton koneksi *database*
│   │   │   ├── LoginPage.java          # View UI Autentikasi Login
│   │   │   ├── Role.java               # Enum standardisasi akses (Manager, Kasir, Kitchen)
│   │   │   ├── User.java               # POJO User Session Context
│   │   │   │
│   │   │   ├── model/                  # Data Entitas Tabel (Karyawan, Menu, Transaksi)
│   │   │   ├── manager/                # Views & Services Khusus Modul Administrator
│   │   │   ├── kasir/                  # Views & Services Khusus Modul Kasir & POS
│   │   │   └── kitchen/                # Views & Services Khusus Modul Papan Dapur
│   │   └── fonts/                      # Aset Tipografi GUI Aplikasi
│   │
├── .gitignore                      # Git Exclusion Configuration
└── README.md                       # Master Documentation (File Ini)
```

## ⚙️ Cara Instalasi & Menjalankan Proyek di Lingkungan Lokal

Untuk men-*deploy* dan mencoba sistem kasir ini secara mandiri pada Windows/Linux/Mac, perhatikan instruksi berikut:

### Prasyarat (Prerequisites)
Pastikan OS Anda sudah dikonfigurasi dengan hal berikut:
1. **Java Development Kit (JDK):** Memerlukan JDK 8 atau versi terbaru. (Periksa di terminal dengan mengetik `java -version`).
2. **XAMPP Server:** Digunakan untuk menjalankan *host database* MySQL lokal secara mudah.
3. Editor Kode Sumber: Direkomendasikan **Visual Studio Code** yang telah dilampiri ekstensi *Java*.
4. **Git** Client.

### Tahapan Instalasi

1. **Kloning Repositori Git**
   Jalankan program terminal atau *command prompt*, lalu kloning proyek:
   ```bash
   git clone https://github.com/YosuaSurianto/INNOBYTE-MesinKasir.git
   cd INNOBYTE-MesinKasir
   ```

2. **Migrasi Database (MySQL)**
   - Jalankan perangkat **XAMPP Control Panel**.
   - Nyalakan layanan dasar MySQL dengan mengeklik **Start**.
   - Buka peramban (browser) dan ketik: `http://localhost/phpmyadmin/`.
   - Buat sebuah *database* baru dengan nama: `innobyte` (Wajib menggunakan nama pasti ini).
   - Masuk ke basis data `innobyte`, tekan tab **Import**, klik *Choose File*, kemudian arahkan lokasi file ke `Database/innobyte.sql` di dalam folder hasil *clone* tadi.
   - Klik tombol **Import** (atau **Go**) untuk menginisialisasi skema tabel.

3. **Registrasi Classpath JDBC (VS Code)**
   - Masukkan folder repositori ini ke *Workspace* Visual Studio Code Anda.
   - Agar *driver* JDBC terbaca saat eksekusi: Buka panel bagian **Java Projects** di sisi terbawah dari bilah kiri *Explorer*.
   - Temukan dan bentangkan bagian **Referenced Libraries**, lalu klik logo `+` (Add library).
   - Arahkan ke file pustaka `INNOBYTE/lib/mysql-connector-j-9.3.0.jar` lalu *Add*.

4. **Kompilasi & Eksekusi**
   - Cari kelas peluncuran utama dari bilah *explorer*: `INNOBYTE/src/login/App.java`
   - Klik teks tombol **Run** yang muncul melayang atau tekan tombol **F5**.
   - Jendela Aplikasi Kasir (INNOBYTE) akan terbuka!

*(Catatan Penggunaan: Skema saat diimpor belum tentu berisi akun lengkap. Anda mungkin perlu menyuntikkan minimal satu rekaman baru untuk di tabel `users` lewat antarmuka phpMyAdmin dan mendefinisikan nilainya sebagai 'Manager', 'Kasir', atau 'Kitchen' agar dapat melewati otorisasi layar Login).*

## 💡 Analisis Sistem & Rencana Pengembangan (*Future Enhancements*)

Meskipun sistem telah memenuhi standar mendasar siklus operasi aplikasi POS (pemesanan, konfirmasi, analitik), berdasarkan analisis arsitektur tahap *Enterprise*, terdapat beberapa fitur fungsional yang kurang dan bisa ditambahkan pada iterasi selanjutnya:

1. **Fungsionalitas Integrasi Cetak Struk (*Thermal Receipt Printing*)**
   - Aplikasi saat ini bekerja secara penuh di dalam layar (sistem visual murni). Mengintegrasikan *Java Print Service API* untuk mencetak tanda terima kertas (*receipt*) via *Thermal Printer* adalah esensi krusial untuk standar mesin kasir retail sesungguhnya.
2. **Sistem Pengeluaran (*Export*) Laporan Berkas**
   - Pada Dashboard Manager, rangkuman finansial hanya dapat divisualisasikan dari antarmuka GUI. Penambahan tombol untuk meng-*export* tabel menjadi bentuk fail `.xlsx` (menggunakan *Apache POI*) atau dokumen `.pdf` (menggunakan *iText*) amat krusial bagi divisi pembukuan akuntansi dunia nyata.
3. **Penerapan Kriptografi Kata Sandi Otentikasi**
   - Saat ditelisik secara saksama, sandi dalam *database* mungkin masih polos. Diimplementasikannya modul kriptografi `Bcrypt` atau `SHA-256` pada komponen layanan validasi akan mendorong standar keamanan sistem ke fase sangat tinggi (*Production Grade*).

## 👥 Pengembang Utama & Kontributor

- **Yosua Surianto** – *Fullstack Developer*
  *(Tanggung Jawab: Merancang infrastruktur kerangka MySQL, mengonsep logika alur GUI berbasis Swing secara intuitif, merumuskan lapisan controller/service MVC, serta menangani kelancaran laju relasi antardata dari hulu Kasir menuju muara Dapur).*
- **Link Repositori:** [https://github.com/YosuaSurianto/INNOBYTE-MesinKasir](https://github.com/YosuaSurianto/INNOBYTE-MesinKasir)

---
Didesain secara profesional dan solid sebagai Solusi Sistem Informasi Manajemen Transaksi Point of Sale (POS).
