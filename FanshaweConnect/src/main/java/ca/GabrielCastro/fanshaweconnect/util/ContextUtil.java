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
