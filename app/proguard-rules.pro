# =========================
# Retrofit + Gson
# =========================

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }

# OkHttp (por si acaso)
-keep class okhttp3.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Mantener modelos (CRÍTICO)
-keep class com.fernanda.medialert.data.remote.** { *; }

# Mantener campos de modelos
-keepclassmembers class com.fernanda.medialert.data.remote.** {
    <fields>;
}

# =========================
# Room
# =========================
-keep class com.fernanda.medialert.data.local.entity.** { *; }
-keep class com.fernanda.medialert.data.local.dao.** { *; }

# =========================
# Coroutines
# =========================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}