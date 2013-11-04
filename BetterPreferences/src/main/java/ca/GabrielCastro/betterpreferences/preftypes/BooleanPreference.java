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
