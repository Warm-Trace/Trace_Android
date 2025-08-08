## Retrofit
# https://github.com/square/retrofit/blob/trunk/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

## kotlin.Result
-keep class kotlin.Result { *; }
-keep class kotlin.Result$Failure { *; }

## kotlinx.serialization
# Keep `Companion` object, `serializer()` and `descriptor` fields in serializable classes
-keepclassmembers class **$$serializer {
    public static final **$$serializer INSTANCE;
    public final kotlinx.serialization.descriptors.SerialDescriptor getDescriptor();
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <init>(...);
}
-keepclassmembers class * {
    public static final kotlinx.serialization.KSerializer serializer(...);
}
-keep class *$$serializer { *; }

# Keep classes annotated with @Serializable
-keep @kotlinx.serialization.Serializable class * { *; }
