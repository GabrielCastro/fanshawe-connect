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

package ca.GabrielCastro.betterpreferences.preftypes;

import android.content.SharedPreferences;

import ca.GabrielCastro.betterpreferences.BetterPreference;

public class BooleanPreference extends BetterPreference<Boolean> {

    public BooleanPreference(String key, boolean defValue) {
        super(key, defValue);
    }

    @Override
    public Boolean getDefaultValue() {
        return (Boolean) defVal;
    }

    @Override
    public Boolean readFromPrefs(SharedPreferences prefs) {
        return prefs.getBoolean(key, getDefaultValue());
    }

    @Override
    public void saveToPrefs(SharedPreferences.Editor editor, Boolean val) {
        editor.putBoolean(key, val);
    }

}
