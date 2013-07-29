package ca.GabrielCastro.fanshaweconnect.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.GabrielCastro.fanshawelogin.CONSTANTS;
import ca.GabrielCastro.fanshawelogin.util.StateChangeService;

public class NetworkChangeStateReceiver extends BroadcastReceiver implements CONSTANTS {

    private static final String TAG = "ChSate";

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, StateChangeService.class).putExtra("", intent));
    }

}
