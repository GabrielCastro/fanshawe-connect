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
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ca.GabrielCastro.fanshaweconnect.R;
import ca.GabrielCastro.fanshaweconnect.util.NetworkChangeStateReceiver;
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        NetworkChangeStateReceiver.completeWakefulIntent(intent);
        return ret;
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
        boolean autoConnect = prefs.getBoolean(CONSTANTS.KEY_AUTO_CONNECT, true);
        if (!autoConnect || user == "" || pass == "") {
            Log.d(TAG, "no userpass");
            return;
        }

        LogOnRequest r = new LogOnRequest(null, user, pass, context);

        LogOnRequest.Status result = r.doInThisThread();

        String ssid = "";
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            ssid = wifiInfo.getSSID();
        }

        //TODO use new API and make this cleaner in general
        NotificationManager noteMan = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //TODO get a better icon
        builder.setSmallIcon(R.drawable.ic_launcher);

        boolean doNotify = false;
        switch (result) {
            case RETURN_OK:
                builder.setTicker(context.getString(R.string.note_ticker_ok))
                    .setContentText(context.getString(R.string.note_text_ok, ssid));
                doNotify = true;
                break;
            case RETURN_INVALID_CRED:
                builder.setTicker(context.getString(R.string.note_ticker_bad))
                        .setContentText(context.getString(R.string.note_text_bad, ssid));
                doNotify = true;
                break;
            case RETURN_NOT_AT_FANSHAWE:
            case RETURN_AT_FANSHAWE_OK:
            case RETURN_UNABLE_TO_LOGIN:
            case RETURN_CONNECTION_ERROR:
            case RETURN_USPECIFIED_ERROR:
                if (Tools.isDebugLevelSet(DEBUG_NOTE_ALL)) {
                    builder.setTicker("Other")
                            .setContentText(result.toString());
                    doNotify = true;
                }
        }
        if (doNotify) {
            noteMan.notify(0, builder.build());
        }
    }

}
