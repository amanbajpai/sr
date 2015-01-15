# Helper
-keepclassmembers class com.ros.smartrocket.db.AppSQLiteOpenHelper { AppSQLiteOpenHelper(***); }

# Models
-keep class com.ros.smartrocket.db.entity.** { *; }

# Gson
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }

# Webview
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

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

# Picasso
-dontwarn com.squareup.okhttp.**
-dontwarn org.joda.time.**

#OkHttp
-keepnames class com.levelup.http.okhttp.** { *; }
-keepnames interface com.levelup.http.okhttp.** { *; }

-keepnames class com.squareup.okhttp.** { *; }
-keepnames interface com.squareup.okhttp.** { *; }

# OkHttp oddities
-dontwarn com.squareup.okhttp.internal.http.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**



