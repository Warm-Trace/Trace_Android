# Gson 사용시 필요한 기본 설정
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.reflect.** { *; }

# Gson이 사용하는 TypeToken 보존
-keep class com.google.gson.internal.** { *; }
-keep class com.google.gson.TypeAdapterFactory
-keep class * extends com.google.gson.TypeAdapter

# Generic TypeToken 클래스 보존
-keep class * extends com.google.gson.reflect.TypeToken