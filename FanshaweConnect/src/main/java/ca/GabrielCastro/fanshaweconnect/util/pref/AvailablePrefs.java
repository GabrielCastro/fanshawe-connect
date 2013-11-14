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

package ca.GabrielCastro.fanshaweconnect.util.pref;

import ca.GabrielCastro.betterpreferences.preftypes.BooleanPreference;
import ca.GabrielCastro.betterpreferences.preftypes.StringPreference;
import ca.GabrielCastro.fanshawelogin.CONSTANTS;

public interface AvailablePrefs {

    StringPreference  USER_NAME    = new StringPreference(CONSTANTS.KEY_USERNAME, null);
    StringPreference  PASS_WORD    = new StringPreference(CONSTANTS.KEY_PASSWD, null);
    StringPreference  FIRST_NAME   = new StringPreference(CONSTANTS.KEY_FIRST_NAME, null);
    StringPreference  LAST_NAME    = new StringPreference(CONSTANTS.KEY_LAST_NAME, null);
    BooleanPreference AUTO_CONNECT = new BooleanPreference(CONSTANTS.KEY_AUTO_CONNECT, true);

}
