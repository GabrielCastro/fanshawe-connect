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
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import ca.GabrielCastro.betterpreferences.MyPreferences;
import ca.GabrielCastro.fanshaweconnect.util.pref.AvailablePrefs;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;
import ca.GabrielCastro.fanshawelogin.util.StateChangeService;

/**
 * A BroadcastReceiver for network state changes that
 * re-sends everything to {@link StateChangeService}
 *
 * @see StateChangeService
 */
public class NetworkChangeStateReceiver extends WakefulBroadcastReceiver implements CONSTANTS {

    private static final String TAG = "ChSate";

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean connect = MyPreferences.read(context, AvailablePrefs.AUTO_CONNECT);
        if (connect) {
            startWakefulService(context, StateChangeService.intentWithParent(context, intent));
        }
    }

}
