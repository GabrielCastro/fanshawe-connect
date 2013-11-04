package ca.GabrielCastro.betterpreferences.preftypes;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Set;

import ca.GabrielCastro.betterpreferences.BetterPreference;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StringSetPreference extends BetterPreference<Set<String>> {

    public StringSetPreference(String key, Set<String> defValue) {
        super(key, defValue);
    }

    @Override
    public Set<String> readFromPrefs(SharedPreferences prefs) {
        return prefs.getStringSet(this.key, this.defVal);
    }

    @Override
    public void saveToPrefs(SharedPreferences.Editor editor, Set<String> val) {
        editor.putStringSet(this.key, val);
    }
}