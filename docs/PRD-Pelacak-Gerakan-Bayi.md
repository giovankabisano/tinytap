# PRD — Aplikasi Pelacak Gerakan Bayi (Kick & Movement Tracker)

> **Untuk:** Implementasi via Claude Code
> **Bahasa aplikasi:** Bahasa Indonesia (default), struktur siap i18n
> **Status:** v1 — siap dikembangkan

---

## 1. Ringkasan Singkat

Aplikasi bantu-ingat untuk ibu hamil agar bisa mencatat **kapan bayi bergerak, berapa lama, dan kapan terakhir kali bergerak** — cukup dengan satu ketukan dari **widget di layar utama HP**.

Aplikasi mendukung dua cara pencatatan:
1. **Catat Gerakan (start–stop)** — ketuk saat bayi mulai bergerak, ketuk lagi saat berhenti. Menghasilkan durasi episode + info "terakhir bergerak".
2. **Sesi Hitung Tendangan (kick count)** — metode klinis "hitung sampai 10 gerakan", aplikasi menghitung waktunya.

> ⚠️ **Bukan alat medis.** Aplikasi ini hanya alat pencatat/pengingat, tidak melakukan diagnosa dan tidak menggantikan pemeriksaan bidan/dokter. Lihat **Bagian 12 — Keselamatan & Framing Medis** (WAJIB diimplementasikan sesuai spesifikasi).

---

## 2. Masalah & Tujuan

**Masalah:** Ibu hamil sering lupa kapan terakhir kali merasakan bayinya bergerak, padahal mengenali pola gerakan dan menyadari perubahannya penting untuk kesehatan janin. Mencatat manual (kertas/notes) merepotkan dan mudah lupa.

**Tujuan produk:**
- Ibu bisa mencatat gerakan dalam **1 ketukan** tanpa membuka aplikasi (widget layar utama).
- Ibu selalu bisa melihat **"terakhir bergerak berapa lama lalu"** secara sekilas.
- Ibu bisa mengenali **pola gerakan harian** bayinya sendiri dari waktu ke waktu.
- Aplikasi mendorong ibu **menghubungi tenaga medis** ketika gerakan berkurang/berubah — tanpa memberi rasa aman palsu.

**Metrik keberhasilan (kualitatif untuk v1):**
- Waktu dari "niat mencatat" ke "tercatat" ≤ 2 detik lewat widget.
- Ibu dapat menjawab "kapan terakhir bayi bergerak?" hanya dengan melihat layar utama.

---

## 3. Pengguna Sasaran

- **Persona utama:** Ibu hamil (khususnya trimester 2–3), berbahasa Indonesia, mayoritas pengguna Android.
- **Konteks pemakaian:** sering satu tangan, kadang malam hari, kadang lelah — UI harus besar, sederhana, jelas.

---

## 4. Non-Goals (Yang TIDAK Dilakukan)

- ❌ Tidak melakukan diagnosa, penilaian risiko, atau memberi keputusan medis.
- ❌ Tidak mengukur detak jantung janin / bukan CTG.
- ❌ Tidak mendeteksi gerakan secara otomatis (tanpa sensor gerak/accelerometer/mikrofon). **Semua pencatatan berasal dari ketukan manual ibu.**
- ❌ Tidak pernah menyatakan kondisi bayi "aman/baik-baik saja".
- ❌ Tidak ada akun/login, tidak ada cloud sync (v1 **lokal-only**, privasi maksimal).
- ❌ Tidak ada fitur berbagi ke pasangan/dokter di v1 (dijadwalkan fase lanjutan).
- ❌ Tidak mendukung kehamilan kembar / multi-janin — satu profil kehamilan.
- ❌ Tidak ada ekspor data (PDF/CSV).

---

## 5. Konsep & Definisi

| Istilah | Definisi |
|---|---|
| **Episode Gerakan** | Satu periode bayi bergerak: punya waktu mulai, waktu berhenti, dan durasi. |
| **Terakhir Bergerak** | Waktu berakhirnya episode terakhir (atau tendangan terakhir). Ditampilkan sebagai "X menit/jam lalu". |
| **Sesi Hitung Tendangan** | Sesi terfokus: ibu menghitung sampai 10 gerakan, aplikasi mencatat berapa lama tercapai. |
| **Jendela Aktif** | Rentang jam ketika ibu biasanya terjaga & memperhatikan gerakan (dipakai untuk logika pengingat). |
| **Peringatan Kelambatan** | Notifikasi lembut jika belum ada gerakan yang **dicatat** dalam sekian jam (bukan berarti bayi tidak bergerak). |

---

## 6. Fitur & Kebutuhan Fungsional

### F1 — Widget Layar Utama (fitur utama)
Widget interaktif dengan **1-tap logging**, tidak perlu membuka aplikasi.

**State widget:**
- **Idle:** menampilkan "Terakhir bergerak: X menit/jam lalu" + tombol besar **"Bayi mulai gerak"**.
- **Merekam episode:** menampilkan timer berjalan (mm:ss) + tombol besar **"Bayi berhenti gerak"**.
- Ketukan langsung menulis ke database lokal (tanpa membuka app).
- Sertakan aksi sekunder kecil: shortcut **"Hitung Tendangan"** (membuka app ke layar sesi kick count).

**Catatan teknis:** di Android, gunakan App Widget interaktif (Jetpack Glance) dengan aksi menulis ke DB via coroutine/WorkManager. Widget harus tetap benar setelah reboot dan hemat baterai (tidak polling terus-menerus; refresh berbasis event + interval wajar).

**Acceptance criteria:**
- Menekan tombol pada widget mencatat waktu dalam < 2 detik tanpa membuka app.
- Setelah "mulai", widget berubah ke state merekam; setelah "stop", tersimpan sebagai satu Episode dan "Terakhir bergerak" ter-update.
- Jika app di-force-close, widget tetap berfungsi.

### F2 — Catat Gerakan (Start–Stop)
- Tap **mulai** → buat Episode dengan `startAt`.
- Tap **stop** → set `endAt`, hitung `durationSec`.
- Boleh dijalankan dari widget maupun dari dalam app.
- Guard: jika ibu lupa menekan "stop" dan episode berjalan sangat lama (mis. > 60 menit), tampilkan prompt di app "Masih bergerak? / Lupa menghentikan?" dengan opsi lanjut, hentikan sekarang, atau buang.

**Acceptance criteria:**
- Satu Episode selalu punya `startAt`; `endAt` boleh kosong hanya selama masih berjalan.
- Durasi ditampilkan dalam format ramah (mis. "3 mnt 20 dtk").

### F3 — Sesi Hitung Tendangan (Kick Count)
Mengikuti metode "hitung sampai 10" (acuan umum ACOG). **Penghitungan sepenuhnya manual: setiap gerakan bertambah HANYA karena ibu menekan tombol sendiri. Aplikasi TIDAK mendeteksi atau menghitung gerakan secara otomatis — tidak ada sensor gerak, accelerometer, mikrofon, atau deteksi otomatis apa pun.**
- Layar terfokus: tombol besar **"+1 Gerakan"** — ibu menekan sekali setiap kali ia merasakan bayi bergerak. Penghitung bertambah semata-mata dari ketukan ini (0→10…).
- Timer di layar hanya menampilkan **waktu berjalan** sejak sesi dimulai (jam biasa, untuk konteks) — bukan mendeteksi atau menghitung gerakan.
- Saat penghitung mencapai target, tampilkan waktu yang dibutuhkan (mis. "10 gerakan dalam 24 menit"). Ibu tetap bisa terus menekan bila mau; **sesi tidak menutup paksa**.
- Ibu mengakhiri sesi secara manual lewat tombol **"Selesai"**.
- **JANGAN** menampilkan pesan "aman/normal". Sebagai gantinya tampilkan pesan edukatif netral + tautan ke panduan kapan menghubungi tenaga medis (lihat F8/Bagian 12).
- Simpan `kickTimestamps[]` — satu timestamp per ketukan manual.
- Tampilkan catatan konteks: penghitungan tendangan paling relevan di **trimester ke-3 (~28 minggu ke atas)**; jika usia kehamilan diketahui < 28 minggu, tampilkan info bahwa pola belum tentu stabil (tidak memblokir pemakaian).

**Acceptance criteria:**
- Penghitung bertambah **hanya** ketika ibu menekan "+1 Gerakan"; tidak ada mekanisme apa pun yang menambah hitungan secara otomatis.
- Setiap ketukan tersimpan dengan timestamp-nya sendiri.
- Saat target tercapai, sesi **tidak** berhenti paksa — ibu yang memutuskan kapan mengakhiri lewat "Selesai".
- Tidak ada klaim medis di hasil.

### F4 — Tampilan "Terakhir Bergerak"
- Di widget dan di beranda app: "Terakhir bergerak **X** lalu" (relatif), plus waktu absolut kecil (mis. "14:32").
- Sumber = `endAt` episode terakhir ATAU tendangan terakhir, mana yang terbaru.

### F5 — Riwayat & Grafik Pola Gerak
- **Daftar riwayat:** kronologis, memisahkan Episode dan Sesi Tendangan, dikelompokkan per hari.
- **Grafik/pola** (minimal 2 visual):
  1. **Pola harian (heatmap jam)** — kapan bayi biasanya aktif dalam sehari, membantu ibu mengenali pola *normal*-nya sendiri.
  2. **Tren harian** — jumlah episode per hari, dan/atau rata-rata waktu mencapai 10 gerakan per sesi.
- Bisa memilih rentang (7 hari / 30 hari).
- Semua perhitungan dilakukan **di perangkat** (offline).

**Acceptance criteria:**
- Grafik memuat data dari DB lokal, tetap tampil tanpa internet.
- Heatmap menunjukkan distribusi gerakan per jam-dalam-hari.

### F6 — Peringatan Kelambatan (Inactivity Awareness)
> Fitur sensitif — ikuti Bagian 12 dengan ketat.
- Ibu mengatur: **Jendela Aktif** (mis. 08:00–22:00) dan **ambang batas** (default 3 jam).
- Jika dalam Jendela Aktif tidak ada gerakan **yang dicatat** melewati ambang batas → kirim notifikasi lembut.
- **Isi notifikasi (wajib bernuansa ini):** "Belum ada gerakan yang **kamu catat** dalam X jam. Coba luangkan waktu memperhatikan gerakan bayi: berbaring miring ke kiri, minum sesuatu yang manis/dingin, di tempat tenang. **Jika gerakan terasa berkurang atau berbeda dari biasanya, segera hubungi bidan atau rumah sakit — layanan tersedia 24 jam.**"
- **Larangan keras:** jangan pernah menulis "bayi tidak bergerak/tidak terdeteksi" (aplikasi hanya tahu yang dicatat), dan jangan pernah menyatakan kondisi aman.
- Fitur **opt-in** (mati secara default), dijelaskan saat onboarding bahwa ini pengingat, bukan alarm medis.

**Acceptance criteria:**
- Notifikasi hanya muncul dalam Jendela Aktif.
- Teks notifikasi tidak pernah mengklaim status bayi & selalu memuat ajakan menghubungi tenaga medis bila ada perubahan pola.

### F7 — Pengingat Harian
- Ibu mengatur satu atau beberapa waktu (mis. pagi & malam) untuk diingatkan mencatat/menghitung gerakan.
- Notifikasi netral: "Waktunya memperhatikan gerakan bayi 💛" (tanpa klaim medis).
- Dapat dinonaktifkan; hormati mode "Jangan Ganggu" / quiet hours.

### F8 — Disclaimer & Materi Edukasi
- **Onboarding pertama kali:** layar disclaimer yang harus disetujui (lihat Bagian 12).
- Halaman "Tentang gerakan bayi" yang dapat diakses kapan saja: penjelasan singkat, netral, berbasis panduan RCOG/ACOG (bahasa awam), dan tombol jelas **"Kapan harus menghubungi tenaga medis"**.
- Sediakan slot pengaturan **nomor kontak darurat kehamilan** milik ibu sendiri (mis. nomor bidan/RS) agar bisa dihubungi cepat dari dalam app. (Aplikasi tidak menyediakan nomor default.)

### F9 — Pengaturan
- Jendela Aktif, ambang peringatan, on/off peringatan kelambatan.
- Waktu pengingat harian.
- Target tendangan (default 10).
- Perkiraan HPL / usia kehamilan (opsional) — untuk konteks trimester.
- Nomor kontak tenaga medis (opsional, disimpan lokal).
- Tema terang/gelap, haptic on/off.

### F10 — Onboarding
- 2–3 layar: (1) apa fungsi app, (2) cara memasang widget, (3) disclaimer + persetujuan.
- Pandu pemasangan widget ke layar utama.

---

## 7. Model Data (SQLite / Room)

```
MovementEpisode
  id            : UUID (PK)
  startAt       : Long (epoch ms)
  endAt         : Long? (epoch ms, null = sedang berjalan)
  durationSec   : Int? (derived saat stop)
  source        : Enum { WIDGET, APP }
  note          : String?

KickSession
  id            : UUID (PK)
  startAt       : Long
  endAt         : Long?
  targetCount   : Int (default 10)
  reachedTargetAt : Long?      // waktu mencapai target
  status        : Enum { RUNNING, COMPLETED, ABANDONED }

Kick
  id            : UUID (PK)
  sessionId     : UUID (FK -> KickSession)
  at            : Long

Settings   (single row / DataStore)
  activeWindowStart      : LocalTime (default 08:00)
  activeWindowEnd        : LocalTime (default 22:00)
  inactivityAlertEnabled : Boolean (default false)
  inactivityThresholdHrs : Int (default 3)
  dailyReminderTimes     : List<LocalTime>
  dailyReminderEnabled   : Boolean
  kickTarget             : Int (default 10)
  dueDate                : LocalDate?      // untuk hitung usia kehamilan
  emergencyContact       : String?
  theme                  : Enum { SYSTEM, LIGHT, DARK }
  hapticsEnabled         : Boolean (default true)
  onboardingCompleted    : Boolean
```

Turunan "terakhir bergerak" = `max(endAt terbaru dari MovementEpisode, at terbaru dari Kick)`.

---

## 8. Alur & UX

**Beranda app:**
- Kartu besar "Terakhir bergerak: X lalu".
- Dua tombol utama: **Catat Gerakan** dan **Hitung Tendangan**.
- Ringkasan hari ini (jumlah episode, sesi tendangan).
- Akses ke Riwayat, Pola, Pengaturan.

**Prinsip UI:**
- Target ketuk besar, ramah satu tangan, kontras tinggi, dukung mode gelap (dipakai malam).
- Haptic feedback saat menekan tombol catat.
- Bahasa lembut dan menenangkan, tapi netral secara medis.
- Aksesibilitas: dukung ukuran font besar & screen reader (contentDescription pada tombol).

---

## 9. Logika Notifikasi

- Gunakan penjadwalan tahan-reboot (WorkManager + boot receiver).
- **Peringatan kelambatan:** evaluasi terjadwal; picu hanya bila (a) fitur aktif, (b) dalam Jendela Aktif, (c) selisih waktu sekarang − "terakhir bergerak" ≥ ambang.
- **Pengingat harian:** alarm pada waktu yang diset.
- Hormati quiet hours; jangan menumpuk notifikasi (maks. 1 peringatan kelambatan aktif dalam satu waktu).

---

## 10. Kebutuhan Non-Fungsional

- **Offline-first & privasi:** semua data di perangkat (Room). Idealnya v1 **tanpa izin internet** sama sekali.
- **Performa:** aksi widget → tersimpan < 2 dtk; beranda dingin-start < 1.5 dtk.
- **Baterai:** tanpa background service yang boros; berbasis event & WorkManager.
- **Keandalan:** tidak ada kehilangan data saat app ditutup/HP reboot.
- **Aksesibilitas:** font scalable, kontras AA, screen reader.
- **Lokalisasi:** semua string di resource, default `id-ID`, struktur siap tambah bahasa.

---

## 11. Rekomendasi Tech Stack

> Ini rekomendasi, bukan keharusan — silakan sesuaikan. Prioritas Android karena fitur utama = widget layar utama interaktif (paling matang di Android) dan basis pengguna Indonesia mayoritas Android.

- **Platform:** Android native, minSdk 26+ (idealnya 31+ untuk widget modern).
- **Bahasa/UI:** Kotlin + Jetpack Compose.
- **Widget:** Jetpack Glance (App Widget interaktif).
- **Database:** Room (SQLite).
- **Preferensi:** DataStore.
- **Penjadwalan:** WorkManager (+ BootCompleted receiver).
- **Arsitektur:** MVVM, single-module untuk v1, repository pattern.

**Catatan iOS (fase lanjutan):** widget interaktif iOS memungkinkan lewat App Intents (iOS 17+), namun model interaksinya berbeda. Disarankan Android dulu untuk MVP; iOS menyusul.

---

## 12. Keselamatan & Framing Medis (WAJIB)

Bagian ini bukan opsional — implementasikan persis.

**Prinsip landasan (berbasis panduan umum RCOG & ACOG):**
- Setiap kehamilan berbeda; yang terpenting ibu mengenali **pola normal bayinya sendiri** dan menyadari **perubahan/penurunan**.
- Bukti tidak mendukung penghitungan gerakan formal sebagai alat skrining rutin untuk mencegah kematian janin — jadi aplikasi **tidak boleh** memposisikan diri sebagai penjamin keselamatan.
- Penghitungan tendangan paling bermakna di **trimester ke-3 (~28 minggu ke atas)**.
- **Penurunan atau perubahan pola gerakan memerlukan penilaian medis segera.** Ibu tidak boleh menunggu.

**Aturan implementasi:**
1. **Disclaimer onboarding (harus disetujui):** aplikasi adalah alat bantu catat/ingat, **bukan alat medis**, tidak mendiagnosa, dan tidak menggantikan bidan/dokter.
2. **Jangan pernah menyatakan status bayi** ("aman", "sehat", "normal", "tidak apa-apa").
3. **Jangan pernah menulis "bayi tidak bergerak/tidak terdeteksi"** — aplikasi hanya tahu apa yang dicatat ibu. Gunakan "belum ada gerakan yang kamu catat".
4. Setiap hasil sesi tendangan & setiap peringatan kelambatan **harus** menyertakan ajakan jelas: *jika merasa gerakan berkurang/berbeda dari biasanya, segera hubungi bidan/RS (layanan 24 jam).*
5. Sediakan tombol/halaman **"Kapan harus menghubungi tenaga medis"** yang mudah diakses dari beranda, hasil sesi, dan notifikasi.
6. Sediakan slot **nomor kontak tenaga medis milik ibu** yang bisa dihubungi cepat; jangan menaruh nomor default buatan sendiri.
7. Bahasa menenangkan namun tidak meremehkan kekhawatiran — selalu utamakan "kalau ragu, hubungi tenaga medis".

---

## 13. Lingkup & Tahapan

**Fase 1 — MVP inti**
- F1 Widget start–stop, F2 Catat Gerakan, F4 Terakhir Bergerak, F5 riwayat (daftar), F8 disclaimer/onboarding, penyimpanan lokal.

**Fase 2 — Fitur yang diminta**
- F3 Hitung Tendangan, F5 grafik/pola harian, F6 peringatan kelambatan, F7 pengingat harian, F9 pengaturan lengkap.

**Fase 3 — Lanjutan (di luar v1)**
- Backup lokal opsional, dukungan iOS, tambahan bahasa. (Ekspor data & dukungan kembar sudah diputuskan **tidak** dibuat.)

---

## 14. Asumsi & Keputusan Final

**Asumsi teknis:**
- Target platform: **Android lebih dulu**.
- Penyimpanan: **lokal-only** di v1 (tanpa akun/cloud).

**Keputusan final (v1):**
1. **Bayi kembar:** tidak didukung — satu profil kehamilan.
2. **Ekspor data (PDF/CSV):** tidak dibuat.
3. **Ambang peringatan kelambatan:** default **3 jam** (tetap bisa diubah pengguna di Pengaturan).
4. **Nama kerja (working title):** _Tiny Taps_ — final menunggu keputusan (lihat opsi branding).
