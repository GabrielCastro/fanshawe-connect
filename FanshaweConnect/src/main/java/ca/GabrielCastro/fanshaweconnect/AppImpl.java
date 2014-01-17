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


import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
        formUri = BuildConfig.ACRA_URI,
        formUriBasicAuthLogin = BuildConfig.ACRA_USR,
        formUriBasicAuthPassword = BuildConfig.ACRA_PWD,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        reportType = org.acra.sender.HttpSender.Type.JSON
)
public class AppImpl extends App {

    private static final String TAG = "fanshaweconnect.AppImpl.non_free";

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.ACRA_ENABLE) {
            ACRA.init(this);
            EasyTracker.getInstance(this);
            GoogleAnalytics.getInstance(this);
        }
        Log.e(TAG, "onCreate");
    }
}
