# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-ignorewarnings                # 抑制警告

#-keep class javax.annotation.** { *; }
#-keep public class org.codehaus.**
#-keep class org.codehaus.mojo.** { *; }
#mojo.animal_sniffer.IgnoreJRERequirement
#-dontwarn javax.annotation.**
#-dontwarn org.codehaus.**
#mojo.animal_sniffer.IgnoreJRERequirement

#-libraryjars libs/litepal-1.6.0.jar
#-dontwarn org.litepal.*
#-keep class org.litepal.* { *; }
#-keep enum org.litepal.**
#-keep interface org.litepal.* { *; }
#-keep public class * extends org.litepal.**
#-keepattributes Annotation
#-keepclassmembers enum * {
#public static **[] values();
#public static ** valueOf(java.lang.String);
#}
#-keepclassmembers class * extends org.litepal.crud.DataSupport{*;}