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

package ca.GabrielCastro.fanshawelogin.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import ca.GabrielCastro.fanshaweconnect.R;
import ca.GabrielCastro.fanshaweconnect.util.ObfuscatedSharedPreferences;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;

/**
 * The Service in charge of handling Connection state changes
 */
public class StateChangeService extends IntentService implements CONSTANTS {

    public static final String EXTRA_PARENT_INTENT = "FanConnect.StateChangeService.parentIntent";
    private static final String TAG = "StateChangeService";

    private String user, pass;

    public StateChangeService() {
        super(TAG);
    }

    public static Intent intentWithParent(Context context, Intent parent) {
        return new Intent(context, StateChangeService.class).putExtra(EXTRA_PARENT_INTENT, parent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();

        ConnectivityManager conMrg = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Tools.isDebugLevelSet(DEBUG_ALL_CON_STATE_CHANGE)) {
            String netInfo = "";
            for (NetworkInfo nI : conMrg.getAllNetworkInfo()) {
                netInfo += nI.toString() + '\n';
            }

            Log.d(TAG, "changed state to: " + netInfo);
        }

        SharedPreferences prefs = ObfuscatedSharedPreferences.create(context, CONSTANTS.PREFS_NAME);
        user = prefs.getString(CONSTANTS.KEY_USERNAME, "");
        pass = prefs.getString(CONSTANTS.KEY_PASSWD, "");
        if (user == "" || pass == "") {
            Log.d(TAG, "no userpass");
            return;
        }

        LogOnRequest r = new LogOnRequest(null, user, pass, context);

        LogOnRequest.Status result = r.doInThisThread();

        //TODO use new API and make this cleaner in general
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //TODO get a better icon
        Notification note = new Notification(R.drawable.ic_launcher, "Connected", System.currentTimeMillis());

        switch (result) {
            case RETURN_OK:
                //TODO resource externalize
                note.setLatestEventInfo(context, "Logged in",
                        "you where automatically connected to Fanshawe's wifi using the stored credentials", null);
                notificationManager.notify(0, note);
                break;
            case RETURN_INVALID_CRED:
                note.setLatestEventInfo(context, "Error", "Your Credentials appear to be broken, or maybe we're not at Fanshawe", null);
                notificationManager.notify(0, note);
                break;
            case RETURN_NOT_AT_FANSHAWE:
            case RETURN_AT_FANSHAWE_OK:
            case RETURN_UNABLE_TO_LOGIN:
            case RETURN_CONNECTION_ERROR:
            case RETURN_USPECIFIED_ERROR:
        }
    }

}
