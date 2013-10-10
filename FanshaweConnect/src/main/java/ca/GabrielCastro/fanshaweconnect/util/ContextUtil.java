package ca.GabrielCastro.fanshaweconnect.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class ContextUtil {

    private final Context context;
    private static ContextUtil instance;

    private ContextUtil(Context context) {
        this.context = context.getApplicationContext();
    }

    public static final ContextUtil get(Context context) {
        if (instance == null && context != null) {
            instance = new ContextUtil(context);
        }
        return instance;
    }

    public boolean isPackageAvailable(String packageName) {
        return getPackageVersion(packageName) >= 0;
    }

    public int getPackageVersion(String packageName) {
        PackageManager man = context.getPackageManager();
        try {
            PackageInfo info = man.getPackageInfo(packageName, 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

}
