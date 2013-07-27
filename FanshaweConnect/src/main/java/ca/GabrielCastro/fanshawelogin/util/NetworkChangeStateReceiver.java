package ca.GabrielCastro.fanshawelogin.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import ca.GabrielCastro.fanshaweconnect.R;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;

public class NetworkChangeStateReceiver extends BroadcastReceiver implements CONSTANTS {

    private static final String TAG = "ChSate";
    String user, pass;

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMrg = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Tools.isDebugLevelSet(DEBUG_ALL_CON_STATE_CHANGE)) {
            String netInfo = "";
            for (NetworkInfo nI : conMrg.getAllNetworkInfo()) {
                netInfo += nI.toString() + '\n';
            }

            Log.d(TAG, "changed state to: " + netInfo);
        }

        SharedPreferences prefs = context.getSharedPreferences(CONSTANTS.PREFS_NAME, 0);
        user = prefs.getString(CONSTANTS.USER_KEY, "");
        pass = prefs.getString(CONSTANTS.PASSWORD_KEY, "");
        if (user == "" || pass == "") {
            Log.d(TAG, "no userpass");
            return;
        }

        LogOnRequest r = new LogOnRequest(null, user, pass, context);

        int result = -1;

        try {
            result = r.execute().get();
            Log.d(TAG, "result was: " + result);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification note = new Notification(R.drawable.ic_launcher, "Connected", System.currentTimeMillis());

        switch (result) {
            case LogOnRequest.RETURN_OK:
                note.setLatestEventInfo(context, "Loged in",
                        "you where automatically connected to Fanshawe's wifi useing the stored credentials", null);
                notificationManager.notify(0, note);
                break;
            case LogOnRequest.RETURN_INVALID_CRED:
                note.setLatestEventInfo(context, "Error", "Your Credetials appere to be broken, or maybe we're not at Fanshawe", null);
                notificationManager.notify(0, note);
                break;
            case LogOnRequest.RETURN_NOT_AT_FANSHAWE:
            case LogOnRequest.RETURN_AT_FANSHAWE_OK:
            case LogOnRequest.RETURN_UNABLE_TO_LOGIN:
            case LogOnRequest.RETURN_CONNECTION_ERROR:
            case LogOnRequest.RETURN_USPECIFIED_ERROR:
        }

    }

}
