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

package ca.GabrielCastro.betterpreferences;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Set;


public class SharedPreferenceEditorWrapper implements SharedPreferences.Editor {

    private final SharedPreferences.Editor delegate;

    public SharedPreferenceEditorWrapper(SharedPreferences.Editor delegate) {
        this.delegate = delegate;
    }

    @Override
    public SharedPreferenceEditorWrapper putString(String key, String value) {
        delegate.putString(key, value);
        return this;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public SharedPreferenceEditorWrapper putStringSet(String key, Set<String> values) {
        delegate.putStringSet(key, values);
        return this;
    }

    @Override
    public SharedPreferenceEditorWrapper putInt(String key, int value) {
        delegate.putInt(key, value);
        return this;
    }

    @Override
    public SharedPreferenceEditorWrapper putLong(String key, long value) {
        delegate.putLong(key, value);
        return this;
    }

    @Override
    public SharedPreferenceEditorWrapper putFloat(String key, float value) {
        delegate.putFloat(key, value);
        return this;
    }

    @Override
    public SharedPreferenceEditorWrapper putBoolean(String key, boolean value) {
        delegate.putBoolean(key, value);
        return this;
    }

    @Override
    public SharedPreferenceEditorWrapper remove(String key) {
        delegate.remove(key);
        return this;
    }

    @Override
    public SharedPreferenceEditorWrapper clear() {
        delegate.clear();
        return this;
    }

    @Override
    public boolean commit() {
        return delegate.commit();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void apply() {
        delegate.apply();
    }

}
