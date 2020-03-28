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
-keepclassmembers public interface com.lt.retrofitdemo.http.HttpFunctions {*;}#防止自定的接口方法名被混淆
-keepclasseswithmembernames public interface com.lt.retrofitdemo.http.ObserverCallBack {*;}#因为使用到了反射,所以回调的类名称也不能被混淆
-keep class kotlin.reflect.jvm.internal.impl.load.java.**{*; }#防止kt反射被混淆
-keep class kotlin.Metadata{*; }#防止kt元注解被混淆