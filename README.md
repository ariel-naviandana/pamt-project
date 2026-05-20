# MyKasir - Point of Sales Application

MyKasir adalah aplikasi Point of Sales (POS) berbasis Android yang dirancang untuk membantu pengelolaan operasional bisnis ritel atau UMKM secara digital. Aplikasi ini memungkinkan pengguna untuk mengelola inventaris produk, memantau arus kas, mencatat pengeluaran, serta manajemen data pelanggan dengan antarmuka yang modern dan responsif.

Dengan dukungan sistem backend berbasis cloud (Supabase), MyKasir memastikan sinkronisasi data terjadi secara real-time, memberikan keamanan data transaksi, serta mendukung pembagian peran (Role-Based Access Control) antara Admin dan Kasir untuk menjaga integritas operasional toko.

## Informasi Dasar
*   **Versi Minimum Android:** Android 12 (SDK 31)
*   **Bahasa Pemrograman:** Kotlin
*   **Framework UI:** Jetpack Compose

## Fitur Utama
*   **Sistem Autentikasi:** Login dan Registrasi pengguna dengan pembagian peran Admin dan Kasir.
*   **Dashboard Interaktif:** Ringkasan informasi profil dan akses cepat ke menu utama operasional.
*   **Manajemen Produk:** Pengelolaan daftar produk, harga, satuan, serta pemantauan stok secara otomatis.
*   **Manajemen Kas:** Pengaturan beberapa akun kas, pemantauan saldo real-time, dan fitur penyesuaian saldo (debit/kredit).
*   **Manajemen Pengeluaran:** Pencatatan biaya operasional dengan alur persetujuan (draft/disetujui) dan integrasi saldo kas.
*   **Manajemen Pelanggan:** Data pelanggan untuk mempermudah identifikasi transaksi di masa mendatang.
*   **Laporan Laba Rugi:** Analisis keuangan berkala yang menghitung total pendapatan penjualan dikurangi beban pengeluaran operasional untuk memantau profitabilitas toko.
*   **Role-Based Access Control (RBAC):** Pembatasan hak akses fitur sensitif (seperti edit harga atau kelola kas) hanya untuk akun Admin.

## Tech Stack & Libraries
*   **Jetpack Compose:** Framework modern untuk membangun antarmuka pengguna Android yang reaktif.
*   **Material 3:** Standar desain terbaru dari Google untuk estetika UI yang bersih dan konsisten.
*   **Supabase (Auth & Postgrest):** Solusi backend-as-a-service untuk manajemen user dan database real-time.
*   **Ktor Client:** Library network yang ringan dan efisien untuk komunikasi API.
*   **Navigation Compose:** Navigasi antar layar yang terintegrasi dengan struktur Compose.
*   **ViewModel & StateFlow:** Arsitektur state management untuk memastikan UI selalu sinkron dengan data terbaru.
*   **Kotlin Serialization:** Konversi data JSON yang aman dan cepat.

## Prasyarat & Cara Menjalankan
### Prasyarat
*   Android Studio (Versi terbaru disarankan).
*   Java Development Kit (JDK) 11 atau lebih tinggi.
*   Koneksi internet untuk sinkronisasi library dan database.

### Cara Menjalankan
1.  **Clone Repository:** Unduh project ini ke direktori lokal Anda.
2.  **Buka di Android Studio:** Pilih menu *Open* dan arahkan ke folder project.
3.  **Konfigurasi API:** Pastikan kredensial Supabase (URL & Anon Key) sudah terpasang pada `SupabaseClientProvider.kt`.
4.  **Gradle Sync:** Tunggu proses sinkronisasi library hingga selesai.
5.  **Build & Run:** Klik tombol *Run* untuk menjalankan aplikasi di Emulator atau perangkat fisik Android (SDK 31+).

## Struktur Folder
```text
com.example.pos/
├── data/           # Konfigurasi backend (Supabase)
├── model/          # Definisi skema data (POJO/Data Classes)
├── navigation/     # Pengaturan rute dan navigasi aplikasi
├── repository/     # Logika akses data dan API Client
├── ui/             # Komponen antarmuka pengguna
│   ├── components/ # Komponen UI reusable (Custom Views)
│   └── theme/      # Pengaturan warna, tipografi, dan tema
├── viewmodel/      # State management dan logika bisnis UI
└── MainActivity.kt # Entry point utama aplikasi
```

*   `data/`: Inisialisasi dan konfigurasi klien layanan backend (Supabase).
*   `model/`: Definisi skema data (*Data Classes*) untuk setiap entitas bisnis.
*   `navigation/`: Pengaturan rute layar dan item menu navigasi bawah.
*   `repository/`: Logika akses data dan komunikasi langsung dengan API backend.
*   `ui/`: Berisi layar utama (*Screens*), komponen UI kustom, dan pengaturan tema (Color, Type, Theme).
*   `viewmodel/`: Pengelolaan logika bisnis dan penyimpanan status antarmuka (*UI State*).
