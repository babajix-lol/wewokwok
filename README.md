# Eclipse Ball

`Eclipse Ball` adalah game interaktif Android berbasis `Kotlin` dan `Jetpack Compose` yang memanfaatkan sensor perangkat untuk menciptakan pengalaman bermain yang responsif terhadap gerakan dan kondisi cahaya sekitar.

## Deskripsi singkat

Pada game ini, pemain menggerakkan bola dengan memiringkan smartphone. Posisi bola akan berubah mengikuti arah kemiringan perangkat, sehingga pemain dapat mencoba menjaga atau mengarahkan bola ke area target. Selain itu, tampilan game juga akan berubah secara dinamis sesuai kondisi cahaya di sekitar pengguna.

## Sensor yang digunakan

### 1. Accelerometer
- Kategori: `Motion Sensor`
- Fungsi:
  Sensor ini digunakan untuk membaca gerakan atau kemiringan perangkat pada sumbu tertentu.
- Pengaruh pada game:
  Nilai dari accelerometer digunakan untuk menggerakkan bola di dalam arena permainan secara real-time.

### 2. Light Sensor
- Kategori: `Environment Sensor`
- Fungsi:
  Sensor ini mendeteksi intensitas cahaya di sekitar perangkat.
- Pengaruh pada game:
  Data cahaya digunakan untuk mengubah mode tampilan game, misalnya dari mode terang ke mode gelap atau `eclipse mode`, lengkap dengan perubahan warna, kontras, dan efek visual.

## Konsep gameplay

Gameplay utama dari `Eclipse Ball` adalah mengontrol bola menggunakan kemiringan perangkat. Pemain perlu menyesuaikan posisi ponsel agar bola bergerak sesuai arah yang diinginkan. Saat kondisi ruangan berubah menjadi lebih gelap, game akan masuk ke mode visual yang lebih redup dan dramatis, sehingga pengalaman bermain terasa lebih dinamis.

## Fitur utama

- Kontrol bola menggunakan `accelerometer`
- Perubahan tampilan berdasarkan `light sensor`
- Antarmuka dibuat dengan `Jetpack Compose`
- Arena permainan interaktif dengan target lingkaran
- Pengelolaan sensor yang menyesuaikan lifecycle aplikasi
- Penanganan aman jika sensor tertentu tidak tersedia pada perangkat

## Tujuan implementasi

Aplikasi ini dibuat untuk memenuhi tugas pengembangan aplikasi Android dengan ketentuan menggunakan minimal dua sensor dari kategori yang berbeda, serta memastikan bahwa masing-masing sensor memberikan dampak langsung terhadap UI atau pengalaman pengguna.

## Teknologi yang digunakan

- `Kotlin`
- `Jetpack Compose`
- `Android Sensor Framework`

## Catatan

Jika perangkat tidak mendukung salah satu sensor, aplikasi tetap harus menangani kondisi tersebut secara aman tanpa crash, misalnya dengan menampilkan informasi bahwa fitur sensor tertentu tidak tersedia.
