package ca.GabrielCastro.betterpreferences.preftypes;

import android.content.SharedPreferences;

import ca.GabrielCastro.betterpreferences.BetterPreference;

public class IntegerPreference extends BetterPreference<Integer> {
    public IntegerPreference(String key, Integer defValue) {
        super(key, defValue);
    }

    @Override
    public Integer readFromPrefs(SharedPreferences prefs) {
        return prefs.getInt(this.key, this.defVal);
    }

    @Override
    public void saveToPrefs(SharedPreferences.Editor editor, Integer val) {
        editor.putInt(this.key, val);
    }
}
