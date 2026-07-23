# Regras de ProGuard/R8 para o NexaAI (release build).

# Mantém a camada JNI: métodos nativos e classes chamadas de C++ não podem ser renomeados/removidos.
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class com.nexaai.engine.** { *; }

# Room: mantém entidades e DAOs (geração de código em tempo de compilação depende dos nomes).
-keep class com.nexaai.data.local.entity.** { *; }
-keep interface com.nexaai.data.local.dao.** { *; }

# Kotlinx Serialization: mantém metadados de serialização.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Hilt / Dagger gera código em tempo de compilação; não ofuscar componentes gerados.
-keep class dagger.hilt.internal.aggregatedroot.codegen.** { *; }
-keep class hilt_aggregated_deps.** { *; }
