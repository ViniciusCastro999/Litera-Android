# Add project specific ProGuard rules here.
# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.litera.app.**$$serializer { *; }
-keepclassmembers class com.litera.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.litera.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase

# Firestore's automatic POJO mapping (toObject()/set(dto)) reads and writes
# fields by name via reflection — without this, R8 renames/strips the
# fields on our own Dto classes (data/repository/*Impl.kt) and every
# read/write silently returns nulls/defaults instead of crashing, which is
# much harder to notice than a crash.
-keepclassmembers class com.litera.app.data.repository.*Dto {
    <fields>;
    <init>(...);
}
-keep class com.litera.app.data.repository.*Dto { *; }
