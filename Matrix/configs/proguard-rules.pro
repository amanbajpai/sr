# Picasso
-dontwarn com.squareup.okhttp.**
-dontwarn org.joda.time.**

# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
-keepattributes *Annotation*,LineNumberTable,SourceFile
-useuniqueclassmembernames
-keepclassmembers public class * extends com.j256.ormlite.dao.BaseDaoImpl {
    public <init>(...);
}

# Helper
-keepclassmembers class se.comviq.app.db.ComviqDatabaseHelper { ComviqDatabaseHelper(***); }

# Models
-keep class se.comviq.app.model.** { *; }
-keep class se.comviq.app.utils.jwt.** { *; }
-keep enum se.comviq.** { *; }

# EventBus
-keepclassmembers class se.comviq.app.** { public void onEvent*(***); }

# Gson
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }

# Pull to refresh
-keep class se.comviq.app.ui.views.pull_to_refresh.CustomDrawableLoadingLayout { *; }

-keep class dk.dibs.android.library.**
-keepclassmembers class dk.dibs.android.library.** { *; }

# Webview
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}
# Vesta Javascript interface
-keep public class se.comviq.app.ui.fragments.tanka.TopUpWithNewCardFragment$ComviqJavascriptInterface
-keep public class * implements se.comviq.app.ui.fragments.tanka.TopUpWithNewCardFragment$ComviqJavascriptInterface
-keepclassmembers class se.comviq.app.ui.fragments.tanka.TopUpWithNewCardFragment$ComviqJavascriptInterface {
    <methods>;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface
#OkHttp
-keepnames class com.levelup.http.okhttp.** { *; }
-keepnames interface com.levelup.http.okhttp.** { *; }

-keepnames class com.squareup.okhttp.** { *; }
-keepnames interface com.squareup.okhttp.** { *; }

# OkHttp oddities
-dontwarn com.squareup.okhttp.internal.http.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

#Play services
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# AppDynamics
-keep class com.appdynamics.eumagent.runtime.DontObfuscate
-keep @com.appdynamics.eumagent.runtime.DontObfuscate class * { *; }


