# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\java_tool\Android_Tool\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#QuickBlox
-keep class org.jivesoftware.smack.initializer.VmArgInitializer {  public *; }
-keep class org.jivesoftware.smack.ReconnectionManager { public *; }
-keep class com.quickblox.module.c.a.c { public *; }
-keep class com.quickblox.module.chat.QBChatService { public *; }
-keep class com.quickblox.module.chat.QBChatService.loginWithUser { public *; }
-keep class com.quickblox.module.chat.listeners.SessionCallback { public *; }
-keep class * extends org.jivesoftware.smack { public *; }
-keep class org.jivesoftware.smack.** { public *; }
-keep class org.jivesoftware.smackx.** { public *; }
-keep class com.quickblox.** { public *; }
-keep class com.quickblox.module** { public *; }
-keep class * extends org.jivesoftware.smack { public *; }
-keep class * implements org.jivesoftware.smack.debugger.SmackDebugger { public *; }
-keep class org.jivesoftware.** { public *; }
-keep class com.quickblox.* { public *; }
-keep class * extends org.jivesoftware.smack { public *; }
-keep class com.quickblox.chat.QBChatService.login* { public *; }
-keep class * extends com.quickblox.core.server.BaseService { public *; }
-keep class com.quickblox.chat.**
-keep class com.quickblox.chat.** { *; }
-keepnames class com.quickblox.chat.**
-keepnames class com.quickblox.chat.** { *; }
-keepclassmembers class com.quickblox.chat.** {*;}
-keepclassmembers enum com.quickblox.chat.** {*;}
-keepclassmembers interface com.quickblox.chat.** {*;}