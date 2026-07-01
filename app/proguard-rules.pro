# ============================================================
# Tiny Taps — ProGuard / R8 Rules
# ============================================================

# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ---- Room ----
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# ---- Kotlin Serialization ----
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
}
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ---- Navigation (type-safe routes) ----
-keep @kotlinx.serialization.Serializable class com.giovankov.tinytaps.ui.navigation.** { *; }

# ---- Glance Widget ----
-keep class com.giovankov.tinytaps.widget.** { *; }
-keep class * extends androidx.glance.appwidget.GlanceAppWidget
-keep class * extends androidx.glance.appwidget.GlanceAppWidgetReceiver
-keep class * extends androidx.glance.appwidget.action.ActionCallback

# ---- WorkManager + Hilt Workers ----
-keep class * extends androidx.work.CoroutineWorker
-keep class * extends androidx.work.Worker

# ---- Domain models (used with Room/DataStore) ----
-keep class com.giovankov.tinytaps.data.local.db.entity.** { *; }
-keep class com.giovankov.tinytaps.domain.model.** { *; }

# ---- Enums ----
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
