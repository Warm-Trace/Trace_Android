-keepattributes SourceFile,LineNumberTable
-keepattributes Signature, InnerClasses, EnclosingMethod, KotlinMetadata
-keepattributes AnnotationDefault, *Annotation*

-keepclassmembers class android.content.Intent {
    public java.lang.String getStringExtra(java.lang.String);
}

## 파이어베이스
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }

## 카카오
-keep class com.kakao.sdk.**.model.* { <fields>; }

## 도메인 모델
-keep class com.virtuous.domain.model.** { *; }
