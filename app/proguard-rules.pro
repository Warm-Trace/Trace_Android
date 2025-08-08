-keepattributes SourceFile,LineNumberTable
-keepattributes Signature, InnerClasses, EnclosingMethod, KotlinMetadata
-keepattributes AnnotationDefault, *Annotation*

-keepclassmembers class android.content.Intent {
    public java.lang.String getStringExtra(java.lang.String);
}

## 파이어베이스
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }

## 도메인 및 네트워크 모델
-keep class com.virtuous.domain.model.** { *; }
-keep class com.virtuous.network.model.** { *; }

## 네비게이션 클래스 이름 유지
-keep class com.virtuous.navigation.** { *; }

## 카카오
-keep class com.kakao.sdk.**.model.* { <fields>; }

## Gson
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.reflect.** { *; }

# Gson이 사용하는 TypeToken 보존
-keep class com.google.gson.internal.** { *; }