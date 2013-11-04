/*
 * This file is part of FanshaweConnect.
 *
 * Copyright 2013 Gabriel Castro (c)
 *
 *     FanshaweConnect is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     FanshaweConnect is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with FanshaweConnect.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.GabrielCastro.fanshaweconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import ca.GabrielCastro.betterpreferences.MyPreferences;
import ca.GabrielCastro.fanshaweconnect.util.ObfuscatedSharedPreferences;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;

/**
 * The Application Class for the app
 */
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

        MyPreferences.setFactory(new MyPreferences.SharedPreferencesFactory() {
            @Override
            public SharedPreferences create(Context context) {
                return ObfuscatedSharedPreferences.create(context, CONSTANTS.PREFS_NAME);
            }
        });
    }

    /**
     * Gets the UserAgent that should be used by all network connections
     * @return
     */
    public static String getUserAgent() {
        return userAgent;
    }
}
