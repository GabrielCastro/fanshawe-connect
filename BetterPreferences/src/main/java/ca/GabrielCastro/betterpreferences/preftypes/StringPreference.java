package ca.GabrielCastro.betterpreferences.preftypes;

import android.content.SharedPreferences;

import ca.GabrielCastro.betterpreferences.BetterPreference;

public class StringPreference extends BetterPreference<String> {

    public StringPreference(String key, String defValue) {
        super(key, defValue);
    }

    @Override
    public String readFromPrefs(SharedPreferences prefs) {
        return prefs.getString(this.key, this.defVal);
    }

    @Override
    public void saveToPrefs(SharedPreferences.Editor editor, String val) {
        editor.putString(this.key, val);
    }
}
