# Technical Implementation Plan — Tiny Taps

> **Status:** Draft — menunggu review
> **Platform:** Android native
> **Referensi:** [PRD](./PRD-Pelacak-Gerakan-Bayi.md) | [UI Proposal](./ui-proposal.pdf)

---

## 1. Tech Stack

| Layer | Teknologi | Keterangan |
|---|---|---|
| Language | **Kotlin 2.x** | KSP (bukan KAPT) |
| UI | **Jetpack Compose + Material 3** | Dynamic color OFF (custom theme) |
| Navigation | **Compose Navigation** (type-safe) | Serializable route objects |
| Database | **Room** (SQLite) | Entity, DAO, migrations |
| Preferences | **DataStore** (Preferences) | Settings single-source |
| DI | **Hilt** | `@HiltViewModel`, `@Inject` |
| Widget | **Jetpack Glance** | Compose-based App Widget |
| Background | **WorkManager** | Inactivity check, daily reminder |
| Async | **Coroutines + Flow** | StateFlow di ViewModel |
| Charts | **Vico** (compose chart library) | Heatmap & trend chart |
| Build | **Gradle Kotlin DSL** + Version Catalog | `libs.versions.toml` |
| Min SDK | **26** (Android 8.0) | Target SDK **35** |

---

## 2. Arsitektur

**MVVM + Clean Architecture (single-module v1)**

```
┌─────────────────────────────────────────────┐
│  UI Layer (Compose Screens + ViewModels)     │
├─────────────────────────────────────────────┤
│  Domain Layer (UseCases + Repository Iface)  │
├─────────────────────────────────────────────┤
│  Data Layer (Room + DataStore + Impl)        │
└─────────────────────────────────────────────┘
```

Unidirectional data flow: `UI Event → ViewModel → UseCase → Repository → DB/DataStore → Flow → UI State`

---

## 3. Package Structure

```
com.tinytaps.app/
├── data/
│   ├── local/
│   │   ├── db/
│   │   │   ├── TinyTapsDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── MovementEpisodeDao.kt
│   │   │   │   ├── KickSessionDao.kt
│   │   │   │   └── KickDao.kt
│   │   │   ├── entity/
│   │   │   │   ├── MovementEpisodeEntity.kt
│   │   │   │   ├── KickSessionEntity.kt
│   │   │   │   └── KickEntity.kt
│   │   │   └── converter/
│   │   │       └── Converters.kt
│   │   └── datastore/
│   │       └── SettingsDataStore.kt
│   └── repository/
│       ├── MovementRepositoryImpl.kt
│       ├── KickSessionRepositoryImpl.kt
│       └── SettingsRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   ├── MovementEpisode.kt
│   │   ├── KickSession.kt
│   │   ├── Kick.kt
│   │   ├── AppSettings.kt
│   │   └── enums.kt          (Source, SessionStatus, AppTheme)
│   ├── repository/
│   │   ├── MovementRepository.kt
│   │   ├── KickSessionRepository.kt
│   │   └── SettingsRepository.kt
│   └── usecase/
│       ├── movement/
│       │   ├── StartMovementUseCase.kt
│       │   ├── StopMovementUseCase.kt
│       │   ├── GetActiveEpisodeUseCase.kt
│       │   └── GetLastMovementTimeUseCase.kt
│       ├── kick/
│       │   ├── StartKickSessionUseCase.kt
│       │   ├── RecordKickUseCase.kt
│       │   ├── EndKickSessionUseCase.kt
│       │   └── GetActiveKickSessionUseCase.kt
│       └── analytics/
│           ├── GetDailyStatsUseCase.kt
│           ├── GetHourlyHeatmapUseCase.kt
│           └── GetDailyTrendUseCase.kt
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   ├── Shape.kt
│   │   └── Theme.kt
│   ├── navigation/
│   │   ├── NavRoutes.kt
│   │   ├── NavGraph.kt
│   │   └── BottomNavBar.kt
│   ├── screen/
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   ├── recording/
│   │   │   ├── RecordingScreen.kt
│   │   │   └── RecordingViewModel.kt
│   │   ├── kickcount/
│   │   │   ├── KickCountScreen.kt
│   │   │   ├── KickCountResultSheet.kt
│   │   │   └── KickCountViewModel.kt
│   │   ├── history/
│   │   │   ├── HistoryScreen.kt
│   │   │   └── HistoryViewModel.kt
│   │   ├── pattern/
│   │   │   ├── PatternScreen.kt
│   │   │   ├── HeatmapChart.kt
│   │   │   ├── TrendChart.kt
│   │   │   └── PatternViewModel.kt
│   │   ├── settings/
│   │   │   ├── SettingsScreen.kt
│   │   │   └── SettingsViewModel.kt
│   │   ├── onboarding/
│   │   │   ├── OnboardingScreen.kt
│   │   │   └── OnboardingViewModel.kt
│   │   └── education/
│   │       └── EducationScreen.kt
│   └── component/
│       ├── LastMovementCard.kt
│       ├── ActionButton.kt
│       ├── TimerDisplay.kt
│       ├── CircularProgressCount.kt
│       ├── DaySectionHeader.kt
│       ├── EpisodeCard.kt
│       ├── KickSessionCard.kt
│       └── MedicalDisclaimerBanner.kt
├── widget/
│   ├── TinyTapsWidget.kt           (GlanceAppWidget)
│   ├── TinyTapsWidgetReceiver.kt   (GlanceAppWidgetReceiver)
│   ├── WidgetState.kt
│   └── WidgetActionCallback.kt
├── notification/
│   ├── NotificationHelper.kt
│   ├── InactivityCheckWorker.kt
│   ├── DailyReminderWorker.kt
│   └── BootReceiver.kt
├── di/
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
├── util/
│   ├── TimeFormatter.kt
│   └── DateUtils.kt
├── MainActivity.kt
└── TinyTapsApplication.kt
```

---

## 4. Design System (dari UI Proposal)

### 4.1 Color Palette

| Token | Light | Dark | Penggunaan |
|---|---|---|---|
| Primary | `#E8614D` (coral) | `#E8614D` | Tombol utama, accent |
| OnPrimary | `#FFFFFF` | `#FFFFFF` | Text di atas primary |
| Background | `#FFF8F4` (warm cream) | `#1A1210` (dark brown) | Background utama |
| Surface | `#FFFFFF` | `#2A2220` | Card, bottom sheet |
| SurfaceVariant | `#FFF0EA` | `#3A302A` | Secondary card bg |
| OnBackground | `#2D1F1A` (dark brown) | `#F5E8E0` | Body text |
| OnSurface | `#2D1F1A` | `#F5E8E0` | Card text |
| SecondaryContainer | `#F5E8E0` | `#3A302A` | Outlined button bg |
| Outline | `#D4C4BC` | `#5A4A42` | Border, divider |
| Success | `#4CAF50` | `#66BB6A` | Target reached |
| SurfaceTint | `#E8614D` | `#E8614D` | Elevation tint |

### 4.2 Typography

- **Display** — waktu relatif besar ("40 menit lalu")
- **Headline** — section header
- **Title** — card title
- **Body** — content text
- **Label** — metadata, caption

Font: System default (Roboto) — clean, readable.

### 4.3 Shape

- Card: `RoundedCornerShape(16.dp)`
- Button: `RoundedCornerShape(12.dp)`
- Bottom Sheet: `RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)`

### 4.4 Bottom Navigation

4 tab: **Beranda** | **Riwayat** | **Pola** | **Pengaturan**

---

## 5. Database Schema (Room)

```kotlin
// === Entities ===

@Entity(tableName = "movement_episodes")
data class MovementEpisodeEntity(
    @PrimaryKey val id: String,          // UUID
    val startAt: Long,                    // epoch ms
    val endAt: Long?,                     // null = sedang berjalan
    val durationSec: Int?,                // computed saat stop
    val source: String,                   // "WIDGET" | "APP"
    val note: String?
)

@Entity(tableName = "kick_sessions")
data class KickSessionEntity(
    @PrimaryKey val id: String,
    val startAt: Long,
    val endAt: Long?,
    val targetCount: Int,                 // default 10
    val reachedTargetAt: Long?,
    val status: String                    // "RUNNING" | "COMPLETED" | "ABANDONED"
)

@Entity(
    tableName = "kicks",
    foreignKeys = [ForeignKey(
        entity = KickSessionEntity::class,
        parentColumns = ["id"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("sessionId")]
)
data class KickEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val at: Long
)
```

### Key Queries

```kotlin
// Terakhir bergerak
@Query("""
    SELECT MAX(t) FROM (
        SELECT MAX(endAt) AS t FROM movement_episodes WHERE endAt IS NOT NULL
        UNION ALL
        SELECT MAX(at) AS t FROM kicks
    )
""")
fun getLastMovementTime(): Flow<Long?>

// Episode hari ini
@Query("SELECT * FROM movement_episodes WHERE startAt >= :dayStart ORDER BY startAt DESC")
fun getEpisodesToday(dayStart: Long): Flow<List<MovementEpisodeEntity>>

// Hourly heatmap (jumlah gerakan per jam, N hari terakhir)
@Query("""
    SELECT strftime('%H', startAt / 1000, 'unixepoch', 'localtime') AS hour,
           COUNT(*) AS count
    FROM movement_episodes
    WHERE startAt >= :since
    GROUP BY hour
""")
fun getHourlyDistribution(since: Long): Flow<List<HourCount>>

// Active episode (yang belum di-stop)
@Query("SELECT * FROM movement_episodes WHERE endAt IS NULL LIMIT 1")
fun getActiveEpisode(): Flow<MovementEpisodeEntity?>
```

---

## 6. Widget (Jetpack Glance)

### States

```
IDLE          → "Terakhir bergerak: X lalu" + tombol "Bayi mulai gerak"
RECORDING     → Timer berjalan (mm:ss) + tombol "Bayi berhenti gerak"
```

### Implementasi

- `TinyTapsWidget : GlanceAppWidget` — layout berdasarkan state
- `WidgetActionCallback : ActionCallback` — handle tap, write langsung ke Room via Repository (coroutine scope)
- Update widget via `TinyTapsWidget().update(context, glanceId)` setelah setiap perubahan data
- Periodic refresh: `updatePeriodMillis = 15 menit` (minimum Android) — untuk update "X lalu" text
- Boot receiver: re-schedule widget update setelah reboot
- Saat recording aktif: gunakan `setAlarm` per-menit untuk update timer display

### Widget Layout

```
┌──────────────────────────────┐
│  🟠 Tiny Taps                │
│                              │
│  Terakhir bergerak           │
│  40 menit lalu       14:32   │
│                              │
│  ┌──────────────────────┐    │
│  │  ▶ Bayi mulai gerak  │    │
│  └──────────────────────┘    │
│                              │
│  ⏱ Hitung Tendangan         │
└──────────────────────────────┘
```

---

## 7. Notification System

### 7.1 Inactivity Alert (F6)

```
InactivityCheckWorker (PeriodicWorkRequest, 15 min interval)
  ├── Cek: fitur aktif?
  ├── Cek: dalam Jendela Aktif?
  ├── Cek: selisih now - lastMovement >= threshold?
  ├── Cek: belum ada notif aktif sejak terakhir gerakan?
  └── Ya semua → tampilkan notifikasi (teks sesuai PRD §12)
```

- Channel: `inactivity_alert` (importance HIGH)
- Teks notifikasi sesuai PRD, menyertakan ajakan hubungi tenaga medis
- Tidak pernah mengklaim status bayi
- Reschedule setelah reboot via `BootReceiver`

### 7.2 Daily Reminder (F7)

```
DailyReminderWorker (OneTimeWorkRequest, scheduled per reminder time)
  └── Tampilkan: "Waktunya memperhatikan gerakan bayi 💛"
```

- Channel: `daily_reminder` (importance DEFAULT)
- Hormati Do Not Disturb
- Reschedule setelah reboot

### 7.3 Long Running Episode Guard (F2)

- Di `RecordingViewModel`: timer check setiap menit
- Jika > 60 menit → tampilkan dialog di app: "Masih bergerak? / Lupa menghentikan?"
- Opsi: Lanjut, Hentikan Sekarang, Buang

---

## 8. Layar-per-Layar Implementasi

### 8.1 Onboarding (F10)

**3 layar swipeable (HorizontalPager):**
1. **Selamat datang** — logo, nama app, deskripsi singkat
2. **Cara pakai** — ilustrasi widget, cara pasang
3. **Disclaimer** — teks medis (§12), checkbox "Saya mengerti", tombol "Mulai"

State: `OnboardingViewModel` → set `onboardingCompleted = true` di DataStore

### 8.2 Home / Beranda (F4)

**Komponen:**
- Greeting: "Halo, Bunda" + tanggal
- `LastMovementCard` — waktu relatif besar + waktu absolut kecil
- 2 tombol: "Catat Gerakan" (filled coral) + "Hitung Tendangan" (outlined)
- Ringkasan hari ini: jumlah episode, jumlah sesi tendangan
- Banner medical disclaimer kecil di bawah (link ke halaman edukasi)

**ViewModel:**
```kotlin
data class HomeUiState(
    val lastMovementTime: Long?,
    val activeEpisode: MovementEpisode?,    // null jika idle
    val todayEpisodeCount: Int,
    val todayKickSessionCount: Int,
    val gestationalWeeks: Int?              // dari HPL
)
```

### 8.3 Recording / Catat Gerakan (F2)

**2 state:**
- **Belum mulai** → tombol besar "Mulai Catat"
- **Merekam** → timer berjalan (elapsed time), tombol "Berhenti", waktu mulai

Timer: `LaunchedEffect` + `delay(1000)` loop, baca dari `startAt`

### 8.4 Kick Count / Hitung Tendangan (F3)

**Komponen:**
- Circular progress: count/target (mis. 7/10)
- Elapsed time display
- Tombol besar "+1 Gerakan" (dengan haptic)
- Tombol "Selesai"
- Info trimester jika usia kehamilan < 28 minggu
- Saat target tercapai → bottom sheet hasil (tanpa klaim medis) + link edukasi

**ViewModel:**
```kotlin
data class KickCountUiState(
    val session: KickSession?,
    val kickCount: Int,
    val targetCount: Int,
    val elapsedSeconds: Long,
    val targetReached: Boolean,
    val timeToReachTarget: Long?,   // durasi sampai target (dtk)
    val showTrimesterInfo: Boolean
)
```

### 8.5 History / Riwayat (F5)

- `LazyColumn` grouped by date (`DaySectionHeader`)
- 2 tipe card: `EpisodeCard` (waktu + durasi) dan `KickSessionCard` (waktu + jumlah + durasi)
- Tab atau chip filter: Semua / Episode / Tendangan

### 8.6 Pattern / Pola (F5)

**2 chart:**
1. **Heatmap jam** — grid 24 kolom × 7 baris (hari), warna intensitas = jumlah gerakan
2. **Tren harian** — bar/line chart, jumlah episode per hari atau rata-rata waktu kick count

- Chip selector: 7 hari / 30 hari
- Library: **Vico** (compose-native chart library, well-maintained)

### 8.7 Settings / Pengaturan (F9)

Grouped sections:
- **Pengingat**: Jendela Aktif (time picker ×2), ambang peringatan, toggle peringatan kelambatan
- **Pengingat Harian**: toggle + time picker(s)
- **Tendangan**: target (number picker, default 10)
- **Kehamilan**: HPL (date picker), kontak tenaga medis (text field)
- **Tampilan**: tema (System/Terang/Gelap), haptic toggle
- **Tentang**: versi, link edukasi, disclaimer

### 8.8 Education / Edukasi (F8)

- Scrollable content: informasi gerakan bayi (bahasa awam, netral)
- Section "Kapan harus menghubungi tenaga medis" (prominent)
- Tombol panggil kontak tenaga medis (jika tersimpan)
- Accessible dari: beranda, hasil kick count, notifikasi

---

## 9. Haptic Feedback

- Tombol catat gerakan (start/stop): `HapticFeedbackType.LongPress`
- Tombol +1 kick: `HapticFeedbackType.Confirm` (medium impact)
- Widget tap: `VibrationEffect.createOneShot(50, DEFAULT_AMPLITUDE)`
- Bisa di-off dari Settings

---

## 10. Accessibility

- Semua tombol: `contentDescription` dalam Bahasa Indonesia
- Minimum touch target: `48.dp`
- Contrast ratio ≥ 4.5:1 (AA)
- Support dynamic font scaling (`sp` units)
- Timer & counter: `LiveRegion.Polite` (screen reader announces updates)

---

## 11. Lokalisasi

- Semua user-facing string di `res/values/strings.xml` (default `id`)
- Struktur siap i18n: `res/values-en/strings.xml` bisa ditambah kemudian
- Plural forms via `plurals` resource
- Date/time format: `DateTimeFormatter` with locale

---

## 12. Implementasi Keselamatan Medis (§12 PRD)

Checklist wajib — setiap item HARUS terimplementasi:

- [ ] Disclaimer onboarding (harus disetujui sebelum pakai)
- [ ] Tidak ada teks "aman/sehat/normal/baik-baik saja" di seluruh app
- [ ] Tidak ada teks "bayi tidak bergerak/tidak terdeteksi" — gunakan "belum ada gerakan yang kamu catat"
- [ ] Hasil kick count: sertakan ajakan hubungi tenaga medis
- [ ] Notifikasi inactivity: sertakan ajakan hubungi tenaga medis
- [ ] Tombol "Kapan harus menghubungi tenaga medis" accessible dari beranda
- [ ] Slot kontak tenaga medis — tanpa nomor default
- [ ] Bahasa menenangkan, tidak meremehkan — "kalau ragu, hubungi tenaga medis"
- [ ] Review seluruh string sebelum release

---

## 13. Fase Implementasi

### Fase 1 — Foundation & Core (hari 1–2)

| Step | Task | Detail |
|---|---|---|
| 1.1 | Project setup | Gradle, version catalog, Hilt, Room, Compose, tema |
| 1.2 | Design system | Color, typography, shape, Theme.kt (light + dark) |
| 1.3 | Database | Entity, DAO, Database, migrations |
| 1.4 | DataStore | Settings preferences |
| 1.5 | Domain layer | Models, repository interfaces, core use cases |
| 1.6 | DI modules | Hilt modules untuk DB, repositories |
| 1.7 | Navigation | NavGraph, BottomNavBar, route definitions |
| 1.8 | Reusable components | LastMovementCard, ActionButton, TimerDisplay, dll |

### Fase 2 — MVP Screens (hari 2–3)

| Step | Task | PRD Feature |
|---|---|---|
| 2.1 | Onboarding screen | F10, F8 (disclaimer) |
| 2.2 | Home screen | F4 (terakhir bergerak) |
| 2.3 | Recording screen | F2 (catat gerakan start-stop) |
| 2.4 | History screen (list) | F5 (riwayat daftar) |
| 2.5 | Settings screen (basic) | F9 (partial) |
| 2.6 | Education screen | F8 |

### Fase 3 — Widget (hari 3–4)

| Step | Task | PRD Feature |
|---|---|---|
| 3.1 | Widget layout (Glance) | F1 |
| 3.2 | Widget actions (start/stop) | F1 |
| 3.3 | Widget state sync | F1 |
| 3.4 | Boot receiver | F1 (survive reboot) |
| 3.5 | Widget update scheduling | F1 (refresh "X lalu") |

### Fase 4 — Kick Count & Analytics (hari 4–5)

| Step | Task | PRD Feature |
|---|---|---|
| 4.1 | Kick count screen | F3 |
| 4.2 | Kick count result | F3 |
| 4.3 | Pattern screen — heatmap | F5 |
| 4.4 | Pattern screen — trend chart | F5 |
| 4.5 | History screen — kick session cards | F5 |

### Fase 5 — Notifications & Polish (hari 5–6)

| Step | Task | PRD Feature |
|---|---|---|
| 5.1 | Notification channels | F6, F7 |
| 5.2 | Inactivity check worker | F6 |
| 5.3 | Daily reminder worker | F7 |
| 5.4 | Settings screen (complete) | F9 |
| 5.5 | Long episode guard dialog | F2 |
| 5.6 | Haptic feedback | UX |
| 5.7 | Dark mode polish | UX |
| 5.8 | Accessibility audit | Non-functional |
| 5.9 | Medical safety string audit | §12 |

---

## 14. Key Dependencies (Version Catalog)

```toml
[versions]
kotlin = "2.1.21"
agp = "8.10.1"
compose-bom = "2025.06.01"
compose-compiler = "2.1.21"
hilt = "2.56.2"
room = "2.7.1"
datastore = "1.1.7"
glance = "1.1.1"
work = "2.10.1"
navigation = "2.9.0"
vico = "2.1.2"
core-ktx = "1.16.0"
lifecycle = "2.9.1"
material3 = "1.3.2"
ksp = "2.1.21-2.0.1"

[libraries]
# BOM
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }

# Compose
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }

# Navigation
navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.2.0" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# DataStore
datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

# Glance (Widget)
glance-appwidget = { group = "androidx.glance", name = "glance-appwidget", version.ref = "glance" }
glance-material3 = { group = "androidx.glance", name = "glance-material3", version.ref = "glance" }

# WorkManager
work-runtime-ktx = { group = "androidx.work", name = "work-runtime-ktx", version.ref = "work" }
hilt-work = { group = "androidx.hilt", name = "hilt-work", version = "1.2.0" }

# Charts
vico-compose-m3 = { group = "com.patrykandpatrick.vico", name = "compose-m3", version.ref = "vico" }

# Lifecycle
lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }

# Core
core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "core-ktx" }
```

---

## 15. File Manifest (estimasi)

| Category | Files | Keterangan |
|---|---|---|
| Build/Config | ~5 | build.gradle.kts (root+app), settings, version catalog, proguard |
| Theme/Design | 4 | Color, Type, Shape, Theme |
| Database | 7 | 3 entities, 3 DAOs, 1 database class |
| DataStore | 1 | SettingsDataStore |
| Domain Models | 4 | MovementEpisode, KickSession, Kick, AppSettings + enums |
| Repositories | 6 | 3 interfaces + 3 implementations |
| Use Cases | ~10 | movement, kick, analytics |
| ViewModels | 7 | per screen |
| Screens | 8 | onboarding, home, recording, kickcount, history, pattern, settings, education |
| Components | ~8 | reusable composables |
| Widget | 4 | widget, receiver, state, callback |
| Notification | 4 | helper, 2 workers, boot receiver |
| DI | 3 | app, database, repository modules |
| Navigation | 3 | routes, graph, bottom nav |
| Util | 2 | time formatter, date utils |
| App/Activity | 2 | Application, MainActivity |
| Resources | ~5 | strings.xml, colors.xml, widget layout xml, manifest |
| **Total** | **~80 files** | |

---

## 16. Risiko & Mitigasi

| Risiko | Mitigasi |
|---|---|
| Glance API terbatas (styling) | Desain widget sederhana, test di berbagai launcher |
| Widget tidak update setelah reboot | BootReceiver + re-init state dari Room |
| Timer widget tidak real-time | Gunakan AlarmManager per-menit saat recording aktif |
| Battery drain dari workers | PeriodicWorkRequest 15 min (minimum), tidak ada persistent service |
| Heatmap chart complexity | Vico library atau custom Canvas composable jika library tidak support |
| Teks medis salah nuansa | Hardcode string per PRD §12, review checklist sebelum setiap fase |

---

## 17. Keputusan Teknis

1. **Single module** — cukup untuk v1, avoid premature modularization
2. **UUID sebagai PK** — portable, tidak bocor info urutan
3. **Epoch millis untuk timestamp** — konsisten, timezone-agnostic storage
4. **Glance (bukan RemoteViews manual)** — compose-like API, lebih maintainable
5. **Vico untuk charts** — actively maintained, compose-native, M3 support
6. **Tidak ada internet permission** — privasi maksimal sesuai PRD
7. **KSP (bukan KAPT)** — lebih cepat build, Kotlin 2.x native
