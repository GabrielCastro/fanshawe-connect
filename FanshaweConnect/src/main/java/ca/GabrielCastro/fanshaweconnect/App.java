package ca.GabrielCastro.fanshaweconnect;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class App extends android.app.Application {

    private static final String BASE_USER_AGENT =
            "Mozilla/5.0 " +
                    // android version, phone name
                    "(Linux; Android %1$s; %2$s) " +
                    // app version
                    "FanshaweConnect/%3$s(%4$s) Mobile";

    public static String userAgent = String.format(BASE_USER_AGENT, Build.VERSION.RELEASE, Build.MODEL, "", "");
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            PackageInfo self = getPackageManager().getPackageInfo(getPackageName(), 0);
            userAgent = String.format(BASE_USER_AGENT, Build.VERSION.RELEASE, Build.MODEL, self.versionName, self.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            Log.wtf("FanshaweConnect[App]", "Self not found", e);
        }
    }
}
