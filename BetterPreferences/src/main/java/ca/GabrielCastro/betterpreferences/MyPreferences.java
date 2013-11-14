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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyPreferences {

    private static MyPreferences instance;

    private final static SharedPreferencesFactory DEFAULT_FACTORY = new SharedPreferencesFactory() {
        @Override
        public SharedPreferences create(Context context) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        }
    };

    private static SharedPreferencesFactory sFactory = DEFAULT_FACTORY;

    private SharedPreferences mPrefs;

    private MyPreferences(Context context) {
        mPrefs = sFactory.create(context);
    }

    public static void setFactory(SharedPreferencesFactory factory) {
        synchronized (MyPreferences.class) {
            if (instance != null) {
                throw new IllegalArgumentException("setFactory() cannot be called before get()");
            }
            if (factory == null) {
                sFactory = DEFAULT_FACTORY;
            } else {
                sFactory = factory;
            }
        }
    }

    public static MyPreferences get(Context context) {
        synchronized (MyPreferences.class) {
            if (instance == null && context != null) {
                instance = new MyPreferences(context);
            }
            return instance;
        }
    }

    public static Editor edit(Context context) {
        return new Editor(get(context).mPrefs.edit());
    }

    public static <T> T read(Context context, BetterPreference<T> pref) {
        return pref.readFromPrefs(get(context).mPrefs);
    }

    public static <T> void set(Context context, BetterPreference<T> pref, T val) {
        edit(context)
                .put(pref, val)
                .commit();
    }

    public static final class Editor extends SharedPreferenceEditorWrapper {

        public Editor(SharedPreferences.Editor delegate) {
            super(delegate);
        }

        public <T> Editor put(BetterPreference<T> pref, T val) {
            pref.saveToPrefs(this, val);
            return this;
        }
    }

    public static interface SharedPreferencesFactory {

        public SharedPreferences create(Context context);

    }
}