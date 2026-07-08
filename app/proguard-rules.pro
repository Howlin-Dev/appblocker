# Koin
-keep class org.koin.** { *; }
-keep interface org.koin.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keep class * extends androidx.room.Dao

# Keep compose functions (usually handled by compiler plugin, but good practice)
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}