package ca.GabrielCastro.betterpreferences.preftypes;

import android.content.SharedPreferences;

import ca.GabrielCastro.betterpreferences.BetterPreference;

public class FloatPreference extends BetterPreference<Float> {
    public FloatPreference(String key, Float defValue) {
        super(key, defValue);
    }

    @Override
    public Float readFromPrefs(SharedPreferences prefs) {
        return prefs.getFloat(this.key, this.defVal);
    }

    @Override
    public void saveToPrefs(SharedPreferences.Editor editor, Float val) {
        editor.putFloat(this.key, this.defVal);
    }
}
