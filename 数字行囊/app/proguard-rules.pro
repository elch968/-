# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep data classes
-keep class com.digitalbackpack.subscription.data.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Retrofit & OkHttp (if added later)
-dontwarn okhttp3.**
-dontwarn retrofit2.**

# Security Crypto
-keep class androidx.security.crypto.** { *; }

