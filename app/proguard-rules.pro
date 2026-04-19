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

# Retrofit necesita conservar las firmas genéricas de los métodos anotados
# para poder resolver Response<T> en release con R8.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Coroutines + Retrofit: conservar Continuation evita que R8 rompa la firma
# genérica de los suspend fun.
-keep class kotlin.coroutines.Continuation

# Reglas defensivas para interfaces de servicios Retrofit.
-keep,allowobfuscation interface com.fernanda.medialert.data.remote.*ApiService

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

# Evitar advertencias irrelevantes de Retrofit en release
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
