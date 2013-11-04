package ca.GabrielCastro.betterpreferences;

import android.content.SharedPreferences;

public abstract class BetterPreference<T> {

    public final String key;
    public final T defVal;

    protected BetterPreference(String key, T defValue) {
        this.key = key;
        this.defVal = defValue;
    }

    public T getDefaultValue() {
        return defVal;
    }

    public abstract T readFromPrefs(SharedPreferences prefs);

    public abstract void saveToPrefs(SharedPreferences.Editor editor, T val);

}