package ca.GabrielCastro.betterpreferences.preftypes;

import android.content.SharedPreferences;

import ca.GabrielCastro.betterpreferences.BetterPreference;

public class LongPreference extends BetterPreference<Long> {
    public LongPreference(String key, Long defValue) {
        super(key, defValue);
    }

    @Override
    public Long readFromPrefs(SharedPreferences prefs) {
        return prefs.getLong(this.key, this.defVal);
    }

    @Override
    public void saveToPrefs(SharedPreferences.Editor editor, Long val) {
        editor.putLong(this.key, val);
    }
}
